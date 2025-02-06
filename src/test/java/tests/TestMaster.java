package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.LoginUtils;
import utils.RestAssuredUtils;

import java.util.ArrayList;
import java.util.List;

import static utils.ConstantUtils.DELETE_USER_API;
import static utils.ConstantUtils.HEADER_AUTHORIZATION;

public class TestMaster {
    protected static String token;
    protected static List<String> createdCustomerIds = new ArrayList<>();
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
}
