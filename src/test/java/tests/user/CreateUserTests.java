package tests.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.java.Log;
import model.login.LoginFailResponse;
import model.login.LoginRequest;
import model.login.LoginResponse;
import model.user.*;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.LoginUtils;
import utils.RestAssuredUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class CreateUserTests {
    static String token;
    static final String CREATE_USER_API = "/api/user";
    static final String GET_USER_API = "/api/user/%s";
    static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final String HEADER_AUTHORIZATION = "Authorization";
    static final String HEADER_CONTENT_TYPE = "Content-Type";
    static final String CONTENT_TYPE = "application/json; charset=utf-8";
    static final String HEADER_POWER_BY = "X-Powered-By";
    static final String POWER_BY = "Express";
    @BeforeAll
    static void setUp(){
        RestAssuredUtils.setUp();
    }

    @BeforeEach
    void beforeEach(){
        token = LoginUtils.getToken();
    }

    @Test
    void verifyCreateUserSuccessfull(){
        long randomNumber = System.currentTimeMillis();
        String randomEmail = String.format("auto_api_%s@abc.com", randomNumber);
        UserRequest userRequest = UserRequest.getDefault();
        userRequest.setEmail(randomEmail);
        LocalDateTime timeBeforeCreateUser = LocalDateTime.now(ZoneId.of("Z"));
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
        for (GetUserAddressResponse addressResponse : getUserResponse.getAddresses()){
            softAssertions.assertThat(addressResponse.getCustomerId()).isEqualTo(createUserResponse.getId());
            verifyDateTime(softAssertions, addressResponse.getCreatedAt(),timeBeforeCreateUser, timeAfterCreateUser);
            verifyDateTime(softAssertions, addressResponse.getUpdatedAt(),timeBeforeCreateUser, timeAfterCreateUser);
        }
        verifyDateTime(softAssertions, getUserResponse.getCreatedAt(),timeBeforeCreateUser, timeAfterCreateUser);
        verifyDateTime(softAssertions, getUserResponse.getUpdatedAt(),timeBeforeCreateUser, timeAfterCreateUser);
        softAssertions.assertAll();
    }

    void verifyDateTime(SoftAssertions softAssertions, String targetDateTime, LocalDateTime timeBefore, LocalDateTime timeAfter){
        LocalDateTime userUpdatedAt = LocalDateTime.parse(targetDateTime, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        softAssertions.assertThat(userUpdatedAt.isAfter(timeBefore)).isTrue();
        softAssertions.assertThat(userUpdatedAt.isBefore(timeAfter)).isTrue();
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
        softAssertions.assertThat(StringUtils.isNoneBlank(createUserResponse.getId())).isTrue();
        softAssertions.assertThat(createUserResponse.getMessage()).isEqualTo("Customer created");
        softAssertions.assertAll();
    }
}
