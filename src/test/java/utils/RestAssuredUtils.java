package utils;

import io.restassured.RestAssured;

import static utils.ConfigUtils.getDotEnv;

public class RestAssuredUtils {
    public static void setUp(){
        RestAssured.baseURI = getDotEnv().get("API_HOST");
        RestAssured.port = 3000;
    }
}
