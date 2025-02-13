package tests.graphql;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.RestAssuredUtils;

import java.util.HashMap;
import java.util.Map;

import static data.graphql.CountriesQuery.GET_COUNTRIES_QUERY;
import static data.graphql.CountriesQuery.GET_COUNTRIES_QUERY_DATA;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GraphQlTest {
    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "https://countries.trevorblades.com/";
    }
    @Test
    void verifyQueryCountriesSuccessful(){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", GET_COUNTRIES_QUERY);
        Map<String, String> variables = new HashMap<>();
        variables.put("caCode", "CA");
        requestBody.put("variables", variables);
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post();
        SoftAssertions softAssertions = new SoftAssertions();
        //1. Verify status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify header if needs
        assertThat(response.header("Content-Type"), equalTo("application/json; charset=utf-8"));
        assertThat(response.header("X-Powered-By"), equalTo("Stellate"));
        //3. Verify body schema
        //4. Verify body value
        assertThatJson(response.asString()).isEqualTo(GET_COUNTRIES_QUERY_DATA);
    }
}
