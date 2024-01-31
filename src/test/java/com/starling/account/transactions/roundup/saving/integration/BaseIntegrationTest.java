package com.starling.account.transactions.roundup.saving.integration;

import com.starling.account.transactions.roundup.saving.StarlingRoundUpSavingApplication;
import com.starling.account.transactions.roundup.saving.TestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.StringBody.exact;

/**
 * @author shahbazhussain
 */
@MockServerTest({"server.url=http://localhost:${mockServerPort}"})
@SpringBootTest(classes = StarlingRoundUpSavingApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;
    protected MockServerClient mockServerClient;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
//        registerStarlingMockRequests();
    }

    @SneakyThrows
    public void registerStarlingMockRequests() {
        registerAccountsGetRequest();
        registerTransactionsGetRequest();
        registerAllSavingGoalsGetRequest();
        registerSavingGoalPutRequest();
        registerSavingGoalByIdGetRequest();
        registerAddMoneyPutRequest();
    }

    private void registerAccountsGetRequest() throws Exception {
        var customerAccountsResponse = TestData.testCustomerAccountsGetResponse();
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/v2/accounts")
                        .withHeader(Header.header("Authorization", TestData.TEST_REQUEST_AUTH))
                        .withHeader(Header.header("Accept", MediaType.APPLICATION_JSON_VALUE)))
                .respond(response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(customerAccountsResponse));
    }

    private void registerTransactionsGetRequest() throws Exception {
        var accountTransactionsResponse = TestData.testAccountTransactionsGetResponse();
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/v2/feed/account/{accountUid}/category/{categoryUid}/transactions-between")
                        .withPathParameter("accountUid", TestData.TEST_ACCOUNT_UID)
                        .withPathParameter("categoryUid", TestData.TEST_DEFAULT_CATEGORY_UID)
                        .withQueryStringParameter("minTransactionTimestamp", TestData.TEST_ROUND_UP_STARTING_TIME)
                        .withQueryStringParameter("maxTransactionTimestamp", TestData.TEST_ROUND_UP_ENDING_TIME)
                        .withHeader(Header.header("Authorization", TestData.TEST_REQUEST_AUTH))
                        .withHeader(Header.header("Accept", MediaType.APPLICATION_JSON_VALUE)))
                .respond(response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(accountTransactionsResponse));
    }

    private void registerAllSavingGoalsGetRequest() throws Exception {
        var allSavingGoalsGetResponse = TestData.testAllSavingGoalsGetResponse();
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/v2/account/{accountUid}/savings-goals")
                        .withPathParameter("accountUid", TestData.TEST_ACCOUNT_UID)
                        .withHeader(Header.header("Authorization", TestData.TEST_REQUEST_AUTH))
                        .withHeader(Header.header("Accept", MediaType.APPLICATION_JSON_VALUE)))
                .respond(response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(allSavingGoalsGetResponse));
    }

    private void registerSavingGoalPutRequest() throws Exception {
        var savingGoalsPutRequest = TestData.testSavingGoalsPutRequest();
        var savingGoalsPutResponse = TestData.testSavingGoalsPutResponse();
        mockServerClient
                .when(request().withMethod("PUT").withPath("/api/v2/account/{accountUid}/savings-goals")
                        .withPathParameter("accountUid", TestData.TEST_ACCOUNT_UID)
                        .withHeader(Header.header("Authorization", TestData.TEST_REQUEST_AUTH))
                        .withHeader(Header.header("Accept", MediaType.APPLICATION_JSON_VALUE))
                        .withHeader(Header.header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                        .withBody(exact(savingGoalsPutRequest)))
                .respond(response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(exact(savingGoalsPutResponse)));
    }

    private void registerSavingGoalByIdGetRequest() throws Exception {
        var savingGoalByIdGetResponse = TestData.testSavingGoalsByIdGetResponse();
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/v2/account/{accountUid}/savings-goals/{savingGoalUid}")
                        .withPathParameter("accountUid", TestData.TEST_ACCOUNT_UID)
                        .withPathParameter("savingGoalUid", "f6f7ebed-0f05-456b-ac80-949fa3c0fa9c")
                        .withHeader(Header.header("Authorization", TestData.TEST_REQUEST_AUTH))
                        .withHeader(Header.header("Accept", MediaType.APPLICATION_JSON_VALUE)))
                .respond(response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(savingGoalByIdGetResponse));
    }

    private void registerAddMoneyPutRequest() throws Exception {
        var addMoneyPutRequest = TestData.testAddMoneyPutRequest();
        var addMoneyPutResponse = TestData.testAddMoneyPutResponse();
        final UUID transferUid = UUID.randomUUID();
        mockServerClient
                .when(request().withMethod("PUT").withPath("/api/v2/account/{accountUid}/savings-goals/{savingGoalUid}/add-money/{transferUid}")
                        .withPathParameter("accountUid", TestData.TEST_ACCOUNT_UID)
                        .withPathParameter("savingGoalUid", TestData.TEST_SAVING_GOAL_UID)
                        .withPathParameter("transferUid", TestData.TEST_UUID_PATTERN)
                        .withHeader(Header.header("Authorization", TestData.TEST_REQUEST_AUTH))
                        .withHeader(Header.header("Accept", MediaType.APPLICATION_JSON_VALUE))
                        .withHeader(Header.header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                        .withBody(exact(addMoneyPutRequest)))
                .respond(response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                        .withBody(exact(addMoneyPutResponse)));
    }


}
