package tests.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.java.Log;
import model.login.LoginFailResponse;
import model.login.LoginRequest;
import model.login.LoginResponse;
import model.user.CreateUserResponse;
import model.user.UserAddressRequest;
import model.user.UserRequest;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.LoginUtils;
import utils.RestAssuredUtils;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateUserTests {
    static String token;
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
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(userRequest)
                .post("/api/user");
        SoftAssertions softAssertions = new SoftAssertions();
        //1. Verify status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify header if needs
        softAssertions.assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8");
        softAssertions.assertThat(response.header("X-Powered-By")).isEqualTo("Express");
        //3. Verify body
        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);
        softAssertions.assertThat(StringUtils.isNoneBlank(createUserResponse.getId())).isTrue();
        softAssertions.assertThat(createUserResponse.getMessage()).isEqualTo("Customer created");
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
                .header("Authorization", token)
                .body(userRequest)
                .post("/api/user");
        SoftAssertions softAssertions = new SoftAssertions();
        //1. Verify status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify header if needs
        softAssertions.assertThat(response.header("Content-Type")).isEqualTo("application/json; charset=utf-8");
        softAssertions.assertThat(response.header("X-Powered-By")).isEqualTo("Express");
        //3. Verify body
        CreateUserResponse createUserResponse = response.as(CreateUserResponse.class);
        softAssertions.assertThat(StringUtils.isNoneBlank(createUserResponse.getId())).isTrue();
        softAssertions.assertThat(createUserResponse.getMessage()).isEqualTo("Customer created");
        softAssertions.assertAll();
    }
}
