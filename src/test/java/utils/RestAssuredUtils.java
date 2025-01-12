package utils;

import io.restassured.RestAssured;

public class RestAssuredUtils {
    public static void setUp(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }
}
