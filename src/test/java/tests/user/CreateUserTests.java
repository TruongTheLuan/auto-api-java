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
import org.junit.jupiter.api.Test;
import utils.RestAssuredUtils;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTests {
    @BeforeAll
    static void setUp(){
        RestAssuredUtils.setUp();
    }

    @Test
    void verifyCreateUserSuccessfull(){
        LoginRequest loginRequest = LoginRequest.getDefault();
        LoginResponse loginResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post("/api/login")
                .as(LoginResponse.class);
        String token = String.format("Bearer %s", loginResponse.getToken());

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
}
