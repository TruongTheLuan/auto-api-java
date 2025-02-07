package tests.card;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.dto.card.CreateCardRequest;
import model.dto.card.CreateCardResponse;
import model.dto.user.CreateUserResponse;
import model.dto.user.UserRequest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tests.TestMaster;
import utils.StubUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static utils.ConstantUtils.*;
import static utils.ConstantUtils.POWER_BY;

public class CreateCardTests extends TestMaster {
    @BeforeAll
    static void setUpForCard(){
        StubUtils.startStubForCreateCard();
    }

    @Test
    void verifyCreateCardSuccessful(){
        long randomNumber = System.currentTimeMillis();
        String randomEmail = String.format("auto_api_%s@abc.com", randomNumber);
        UserRequest userRequest = UserRequest.getDefault();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setEmail(randomEmail);
        LocalDateTime timeBeforeCreateUser = LocalDateTime.now(ZoneId.of("Z"));
        LocalDateTime timeBeforeCreateUserForDb = LocalDateTime.now();
        Response responseCreateUser = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(userRequest)
                .post(CREATE_USER_API);
        assertThat(responseCreateUser.statusCode()).isEqualTo(200);
        CreateUserResponse createUserResponse = responseCreateUser.as(CreateUserResponse.class);
        //Verify Create Card
        CreateCardRequest cardRequest = new CreateCardRequest(createUserResponse.getId(),"SILVER");
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
        CreateCardResponse createCardResponseExpected = new CreateCardResponse(String.format("%s %s", userRequest.getFirstName(),  userRequest.getLastName()), "1111 2222 3333 4444", "01-23-2028");
        softAssertions.assertThat(createCardResponse.equals(createCardResponseExpected)).isTrue();
        softAssertions.assertAll();
    }
}
