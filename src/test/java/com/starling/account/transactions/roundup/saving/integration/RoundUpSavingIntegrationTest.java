package com.starling.account.transactions.roundup.saving.integration;

import com.starling.account.transactions.roundup.saving.TestData;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Header;
import org.mockserver.model.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author shahbazhussain
 */
public class RoundUpSavingIntegrationTest extends BaseIntegrationTest {

    @Test
    public void shouldGetExpectedText_forWorkingExample() throws Exception {

        registerStarlingMockRequests();
        var sampleRoundUpRequestAsString = TestData.getSampleRoundUpRequestAsString();
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/roundup/saving/transfer-money")
                        .content(sampleRoundUpRequestAsString)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].accountUid", is(TestData.TEST_ACCOUNT_UID)))
                .andExpect(jsonPath("$.[0].accountCurrency", is(TestData.TEST_CURRENCY.getCurrencyCode())))
                .andExpect(jsonPath("$.[0].categoryUid", is(TestData.TEST_DEFAULT_CATEGORY_UID)))
                .andExpect(jsonPath("$.[0].savingUid", is(TestData.TEST_SAVING_GOAL_UID)))
                .andExpect(jsonPath("$.[0].transferUid").exists())
                .andExpect(jsonPath("$.[0].transferredAmount.minorUnits", is(TestData.TEST_TRANSACTION_ROUND_UP_SUM)))
                .andExpect(jsonPath("$.[0].transferredAmount.currency", is(TestData.TEST_CURRENCY.getCurrencyCode())))
                .andExpect(jsonPath("$.[0].success", is(true)))
                .andExpect(jsonPath("$.[0].accountUid", is(TestData.TEST_ACCOUNT_UID)));
    }

    @Test
    public void shouldGetBadRequestError_forMissingFieldInJson() throws Exception {

        var sampleRoundUpRequest = TestData.getSampleRoundUpRequest();
        // Explicitly remove startingTime field in Json payload
        // startingTime field cannot be null/empty/invalid date
        sampleRoundUpRequest.setStartingTime(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/roundup/saving/transfer-money")
                        .content(TestData.OBJECT_MAPPER.writeValueAsString(sampleRoundUpRequest))
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message", is("startingTime mustn't be null")));
    }

    @Test
    public void shouldGetBadRequestError_forEarlierEndingTime() throws Exception {

        var sampleRoundUpRequest = TestData.getSampleRoundUpRequest();
        // Explicitly remove endingTime before one day to startingTime
        sampleRoundUpRequest.setEndingTime(Instant.parse(sampleRoundUpRequest.getStartingTime()).minus(1, ChronoUnit.DAYS).toString());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/roundup/saving/transfer-money")
                        .content(TestData.OBJECT_MAPPER.writeValueAsString(sampleRoundUpRequest))
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].message", is("Starting time mustn't be equal or after ending time.")));
    }


    @Test
    public void shouldGet403Status_whenStarlingApiGive403Status() throws Exception {

        //Mocking starling api too return 403 on our first request
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/v2/accounts")
                        .withHeader(Header.header("Authorization", TestData.TEST_REQUEST_AUTH))
                        .withHeader(Header.header("Accept", MediaType.APPLICATION_JSON_VALUE)))
                .respond(response()
                        .withStatusCode(HttpStatusCode.FORBIDDEN_403.code())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON));

        var sampleRoundUpRequestAsString = TestData.getSampleRoundUpRequestAsString();
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/roundup/saving/transfer-money")
                        .content(sampleRoundUpRequestAsString)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors[0].message", is("Couldn't authenticate customer token. Please contact the bank.")));
    }
}
