package tests.card;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.dto.card.CreateCardRequest;
import model.dto.card.CreateCardResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import tests.TestMaster;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static utils.ConstantUtils.*;
import static utils.ConstantUtils.POWER_BY;

public class CreateCardTests extends TestMaster {
    @Test
    void verifyCreateCardSuccessful(){
        WireMockServer refDataServer = new WireMockServer(options()
                .port(7777).notifier(new ConsoleNotifier(true))); //No-args constructor will start on port 8080, no HTTPS
        refDataServer.start();

        WireMockServer cardDataServer = new WireMockServer(options()
                .port(7778).notifier(new ConsoleNotifier(true))); //No-args constructor will start on port 8080, no HTTPS
        cardDataServer.start();

        CreateCardRequest cardRequest = new CreateCardRequest("1615fd5f-0c8f-4069-88df-e7a2336f73ed","SILVER");
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(cardRequest)
                .post(CREATE_CARD_API);
        SoftAssertions softAssertions = new SoftAssertions();
        //1. Verify status code
        softAssertions.assertThat(response.statusCode()).isEqualTo(200);
        //2. Verify header if needs
        softAssertions.assertThat(response.header(HEADER_CONTENT_TYPE)).isEqualTo(CONTENT_TYPE);
        softAssertions.assertThat(response.header(HEADER_POWER_BY)).isEqualTo(POWER_BY);
        //3. Verify body
        CreateCardResponse createCardResponse = response.as(CreateCardResponse.class);
        CreateCardResponse createCardResponseExpected = new CreateCardResponse("John Doe", "1111 2222 3333 4444", "01-23-2028");
        softAssertions.assertThat(createCardResponse.equals(createCardResponseExpected)).isTrue();
        softAssertions.assertAll();
    }
}
