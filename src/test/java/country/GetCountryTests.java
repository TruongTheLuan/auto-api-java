package country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static data.country.GetCountryData.GET_ALL_COUNTRIES;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetCountryTests {

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }

    @Test
    void verifyGetCountriesApiSchema(){
        RestAssured.given().log().all()
                .get("/api/v1/countries")
                .then().log().all()
                .statusCode(200)
                .assertThat().body(matchesJsonSchemaInClasspath("Data/get-country/get-country-json-schema.json"));
        ;
    }

    @Test
    void verifyGetCountriesApiResponseCorrectData(){
        Response response = RestAssured.given().log().all()
                .get("/api/v1/countries");
        //1. Verify status code
        assertThat(response.statusCode(), equalTo(200));
        //2. Verify header if needs
        assertThat(response.header("Content-Type"), equalTo("application/json; charset=utf-8"));
        assertThat(response.header("X-Powered-By"), equalTo("Express"));
        //3. Verify body
        assertThat(response.asString(), jsonEquals(GET_ALL_COUNTRIES).when(IGNORING_ARRAY_ORDER));
        System.out.println(response.asString());
    }

    static Stream<Map<String,String>> countryProvider() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String,String>> dataList = mapper.readValue(GET_ALL_COUNTRIES, new TypeReference<List<Map<String, String>>>(){});
        return dataList.stream();
    }

    @ParameterizedTest
    @MethodSource("countryProvider")
    void verifyGetCountryApiResponseCorrectData(Map<String,String> country){
        Response response = RestAssured.given().log().all()
                .get("/api/v1/countries/{code}", country.get("code"));
        //1. Verify status code
        assertThat(response.statusCode(), equalTo(200));
        //2. Verify header if needs
        assertThat(response.header("Content-Type"), equalTo("application/json; charset=utf-8"));
        assertThat(response.header("X-Powered-By"), equalTo("Express"));
        //3. Verify body
        assertThat(response.asString(), jsonEquals(country));
    }
}
