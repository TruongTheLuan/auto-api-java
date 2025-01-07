package country;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static data.country.GetCountryWithGdpData.GET_ALL_COUNTRIES_WITH_GDP;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetCountryWithGdpTests {
    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }
    @Test
    void verifyGetCountryWithGdpApiSchema(){
        RestAssured.given().log().all()
                .get("/api/v2/countries")
                .then().log().all()
                .statusCode(200)
                .assertThat().body(matchesJsonSchemaInClasspath("Data/get-country/get-country-json-schema.json"));
        ;
    }

    @Test
    void verifyGetCountryWithGdpApiResponseCorrectData(){
        Response response = RestAssured.given().log().all()
                .get("/api/v2/countries");
        //1. Verify status code
        assertThat(response.statusCode(), equalTo(200));
        //2. Verify header if needs
        assertThat(response.header("Content-Type"), equalTo("application/json; charset=utf-8"));
        assertThat(response.header("X-Powered-By"), equalTo("Express"));
        //3. Verify body
        assertThat(response.asString(), jsonEquals(GET_ALL_COUNTRIES_WITH_GDP).when(IGNORING_ARRAY_ORDER));
        System.out.println(response.asString());
    }
}
