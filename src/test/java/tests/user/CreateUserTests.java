package tests.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.dao.CustomerAddressDao;
import model.dao.CustomerDao;
import model.dto.user.*;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
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
import static utils.ConstantUtils.*;
import static utils.DateTimeUtils.verifyDateTime;
import static utils.DateTimeUtils.verifyDateTimeDb;

public class CreateUserTests {
    static String token;
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
    void verifyCreateUserSuccessfull(){
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
        SoftAssertions softAssertions = new SoftAssertions();
        //1. Verify status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify header if needs
        softAssertions.assertThat(response.header(HEADER_CONTENT_TYPE)).isEqualTo(CONTENT_TYPE);
        softAssertions.assertThat(response.header(HEADER_POWER_BY)).isEqualTo(POWER_BY);
        //3. Verify body
        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);
        createdCustomerIds.add(createUserResponse.getId());
        softAssertions.assertThat(StringUtils.isNoneBlank(createUserResponse.getId())).isTrue();
        softAssertions.assertThat(createUserResponse.getMessage()).isEqualTo("Customer created");

        //4. Double Check that user has been stored in system
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
        //5. Verify by access to DB
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

    @Test
    void verifyCreateUserSuccessfullWithTwoAddresses(){
        long randomNumber = System.currentTimeMillis();
        String randomEmail = String.format("auto_api_%s@abc.com", randomNumber);
        UserRequest userRequest = UserRequest.getDefault();
        UserAddressRequest userAddressRequest1 = UserAddressRequest.getDefault();
        UserAddressRequest userAddressRequest2 = UserAddressRequest.getDefault();
        userAddressRequest2.setStreetNumber("456");
        userRequest.setAddresses(List.of(userAddressRequest1, userAddressRequest2));
        userRequest.setEmail(randomEmail);
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(userRequest)
                .post(CREATE_USER_API);
        SoftAssertions softAssertions = new SoftAssertions();
        //1. Verify status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify header if needs
        softAssertions.assertThat(response.header(HEADER_CONTENT_TYPE)).isEqualTo(CONTENT_TYPE);
        softAssertions.assertThat(response.header(HEADER_POWER_BY)).isEqualTo(POWER_BY);
        //3. Verify body
        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);
        createdCustomerIds.add(createUserResponse.getId());
        softAssertions.assertThat(StringUtils.isNoneBlank(createUserResponse.getId())).isTrue();
        softAssertions.assertThat(createUserResponse.getMessage()).isEqualTo("Customer created");
        softAssertions.assertAll();
    }
}
