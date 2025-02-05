package tests.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.dao.CustomerAddressDao;
import model.dao.CustomerDao;
import model.dto.user.*;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AbstractBigDecimalAssert;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.DbUtils;
import utils.LoginUtils;
import utils.RestAssuredUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UpdateUserTests {
    static String token;
    static final String CREATE_USER_API = "/api/user";
    static final String GET_USER_API = "/api/user/%s";
    static final String DELETE_USER_API = "/api/user/%s";
    static final String UPDATE_USER_API = "/api/user/%s";
    static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final String HEADER_AUTHORIZATION = "Authorization";
    static final String HEADER_CONTENT_TYPE = "Content-Type";
    static final String CONTENT_TYPE = "application/json; charset=utf-8";
    static final String HEADER_POWER_BY = "X-Powered-By";
    static final String POWER_BY = "Express";
    static List<String> createdCustomerIds = new ArrayList<>();
    @BeforeAll
    static void setUp(){
        RestAssuredUtils.setUp();
    }

    @BeforeEach
    void beforeEach(){
        token = LoginUtils.getToken();
    }

    @AfterAll
    static void afterAll(){
        for(String id : createdCustomerIds){
            RestAssured.given().log().all()
                    .header(HEADER_AUTHORIZATION, token)
                    .delete(String.format(DELETE_USER_API,id));
        }
    }

    @Test
    void verifyUpdateUserSuccessful(){
        //1. Prepare data
        long randomNumber = System.currentTimeMillis();
        String randomEmail = String.format("auto_api_%s@abc.com", randomNumber);
        UserRequest userRequest = UserRequest.getDefault();
        userRequest.setEmail(randomEmail);
        LocalDateTime timeBeforeCreateUser = LocalDateTime.now(ZoneId.of("Z"));
        LocalDateTime timeBeforeCreateUserForDb = LocalDateTime.now();
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(userRequest)
                .post(CREATE_USER_API);
        assertThat(response.statusCode()).isEqualTo(200);
        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);
        createdCustomerIds.add(createUserResponse.getId());
        //2. Perform updating
        UserRequest updateUserRequest = UserRequest.updateUserInformation();
        updateUserRequest.setEmail(randomEmail);
        Response updateResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(userRequest)
                .put(UPDATE_USER_API);
        SoftAssertions softAssertions = new SoftAssertions();
        //3. Verify status code
        softAssertions.assertThat(updateResponse.statusCode()).isEqualTo(200);
        //4. Verify header if needs
        softAssertions.assertThat(response.header(HEADER_CONTENT_TYPE)).isEqualTo(CONTENT_TYPE);
        softAssertions.assertThat(response.header(HEADER_POWER_BY)).isEqualTo(POWER_BY);
        //4. Verify body
        UpdateUserResponse updateUserResponse = updateResponse.as(UpdateUserResponse.class);
        softAssertions.assertThat(updateUserResponse.getId()).isEqualTo(createUserResponse.getId());
        softAssertions.assertThat(updateUserResponse.getMessage()).isEqualTo("User Updated");

        //5. Double Check that user has been stored in system
        Response getResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .get(String.format(GET_USER_API,createUserResponse.getId()));
        softAssertions.assertThat(getResponse.statusCode()).isEqualTo(200);
        GetUserResponse getUserResponse = getResponse.as(GetUserResponse.class);
        assertThatJson(getUserResponse).whenIgnoringPaths("$..id", "$..createdAt", "$..updatedAt", "$..customerId").isEqualTo(userRequest);
        softAssertions = new SoftAssertions();
        softAssertions.assertThat(getUserResponse.getId()).isEqualTo(createUserResponse.getId());
        LocalDateTime timeAfterCreateUser = LocalDateTime.now(ZoneId.of("Z"));
        LocalDateTime timeAfterCreateUserForDb = LocalDateTime.now();
        for (GetUserAddressResponse addressResponse : getUserResponse.getAddresses()){
            softAssertions.assertThat(addressResponse.getCustomerId()).isEqualTo(createUserResponse.getId());
            verifyDateTime(softAssertions, addressResponse.getCreatedAt(),timeBeforeCreateUser, timeAfterCreateUser);
            verifyDateTime(softAssertions, addressResponse.getUpdatedAt(),timeBeforeCreateUser, timeAfterCreateUser);
        }
        verifyDateTime(softAssertions, getUserResponse.getCreatedAt(),timeBeforeCreateUser, timeAfterCreateUser);
        verifyDateTime(softAssertions, getUserResponse.getUpdatedAt(),timeBeforeCreateUser, timeAfterCreateUser);
        softAssertions.assertAll();
        //6. Verify by access to DB
        CustomerDao customerDao = DbUtils.getCustomerFromDb(createUserResponse.getId());
        assertThatJson(customerDao).whenIgnoringPaths("$..id", "$..createdAt", "$..updatedAt", "$..customerId").isEqualTo(userRequest);
        softAssertions = new SoftAssertions();
        softAssertions.assertThat(UUID.fromString(getUserResponse.getId())).isEqualTo(customerDao.getId());

        for (CustomerAddressDao address : customerDao.getAddresses()){
            softAssertions.assertThat(address.getCustomerId()).isEqualTo(UUID.fromString(createUserResponse.getId()));
            verifyDateTimeDb(softAssertions, address.getCreatedAt(),timeBeforeCreateUserForDb, timeAfterCreateUserForDb);
            verifyDateTimeDb(softAssertions, address.getUpdatedAt(),timeBeforeCreateUserForDb, timeAfterCreateUserForDb);
        }
        verifyDateTimeDb(softAssertions, customerDao.getCreatedAt(),timeBeforeCreateUserForDb, timeAfterCreateUserForDb);
        verifyDateTimeDb(softAssertions, customerDao.getUpdatedAt(),timeBeforeCreateUserForDb, timeAfterCreateUserForDb);
        softAssertions.assertAll();
    }

    void verifyDateTime(SoftAssertions softAssertions, String targetDateTime, LocalDateTime timeBefore, LocalDateTime timeAfter){
        LocalDateTime userUpdatedAt = LocalDateTime.parse(targetDateTime, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        softAssertions.assertThat(userUpdatedAt.isAfter(timeBefore)).isTrue();
        softAssertions.assertThat(userUpdatedAt.isBefore(timeAfter)).isTrue();
    }

    void verifyDateTimeDb(SoftAssertions softAssertions, LocalDateTime targetDateTime, LocalDateTime timeBefore, LocalDateTime timeAfter){
        softAssertions.assertThat(targetDateTime.isAfter(timeBefore)).isTrue();
        softAssertions.assertThat(targetDateTime.isBefore(timeAfter)).isTrue();
    }
}
