package country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import net.javacrumbs.jsonunit.core.Option;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static data.country.GetCountryData.GET_ALL_COUNTRIES;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    static Stream<?> verifyGetCountriesWithFilter(){
        List<Map<String,String>> inputs = new ArrayList<>();
        inputs.add(Map.of("gdp", "1868", "operator", ">"));
        inputs.add(Map.of("gdp", "1868", "operator", "<"));
        inputs.add(Map.of("gdp", "1868", "operator", ">="));
        inputs.add(Map.of("gdp", "1868", "operator", "<="));
        inputs.add(Map.of("gdp", "1868", "operator", "=="));
        inputs.add(Map.of("gdp", "1868", "operator", "!="));
        return inputs.stream();
    }
    @ParameterizedTest
    @MethodSource("verifyGetCountriesWithFilter")
    void verifyGetCountriesWithFilterGreaterThan(Map<String,String> queryParams){
        Response response = RestAssured.given().log().all()
                .queryParams(queryParams)
                .get("/api/v3/countries");
        //1. Verify status code
        assertThat(response.statusCode(), equalTo(200));
        //2. Verify header if needs
        assertThat(response.header("Content-Type"), equalTo("application/json; charset=utf-8"));
        assertThat(response.header("X-Powered-By"), equalTo("Express"));
        //3. Verify body
        List<Map<String, String>> countries = response.as(new TypeRef<List<Map<String, String>>>() {
        });
        for(Map<String, String> country : countries){
            float actualGdp = Float.parseFloat(queryParams.get("gdp"));
            Matcher<Float> matcher = switch (queryParams.get("operator")){
                case ">" -> greaterThan(actualGdp);
                case "<" -> lessThan(actualGdp);
                case ">=" -> greaterThanOrEqualTo(actualGdp);
                case "<=" -> lessThanOrEqualTo(actualGdp);
                case "!=" -> not(equalTo(actualGdp));
                default -> equalTo(actualGdp);
            };
            assertThat(Float.parseFloat(country.get("gdp")), matcher);
        }
    }
}
