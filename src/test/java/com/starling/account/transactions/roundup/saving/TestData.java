package com.starling.account.transactions.roundup.saving;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.starling.account.transactions.roundup.saving.api.request.RoundUpRequest;
import com.starling.account.transactions.roundup.saving.client.starling.model.request.SavingGoalTransferRequest;
import com.starling.account.transactions.roundup.saving.client.starling.model.request.SavingsGoalCreateRequest;
import com.starling.account.transactions.roundup.saving.client.starling.model.response.AccountsResponse;
import com.starling.account.transactions.roundup.saving.client.starling.model.response.TransactionsResponse;
import com.starling.account.transactions.roundup.saving.model.Account;
import com.starling.account.transactions.roundup.saving.model.CurrencyAmount;
import com.starling.account.transactions.roundup.saving.model.FeedItem;
import com.starling.account.transactions.roundup.saving.model.SavingsGoal;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Currency;
import java.util.List;

/**
 * @author shahbazhussain
 */
public class TestData {

    public static final String TEST_CUSTOMER_TOKEN = "test-token";
    public static final String TEST_REQUEST_AUTH = "Bearer " + TEST_CUSTOMER_TOKEN;
    public static final String TEST_ACCOUNT_UID = "e2181806-1baf-44a4-8098-1b4f23badad8";
    public static final String TEST_DEFAULT_CATEGORY_UID = "e218f8c9-1fbe-424d-9d52-ff9bd029ccd9";
    public static final String TEST_SAVING_GOAL_UID = "f6f7ebed-0f05-456b-ac80-949fa3c0fa9c";
    public static final String TEST_ROUND_UP_STARTING_TIME = "2024-01-01T12:08:08.641Z";
    public static final String TEST_ROUND_UP_ENDING_TIME = "2024-01-30T12:08:08.641Z";
    public static final Currency TEST_CURRENCY = Currency.getInstance("GBP");
    public static final int TEST_TRANSACTION_ROUND_UP_SUM = 282; // all transactions(ItemFeeds) sum
    public static final String TEST_NEW_SAVING_GOAL_NAME = "saving_goal_2024-01-01T12:08:08.641Z";
    public static final int TEST_NEW_SAVING_GOAL_TARGET_AMOUNT = 14100; // TEST_TRANSACTION_SUM * 50= 14100
    public static final String TEST_UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String readJsonFileAsString(final String fileName) throws IOException {
        return new String(Files.readAllBytes(new ClassPathResource(fileName).getFile().toPath()));
    }


    public static String getSampleRoundUpRequestAsString() throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(getSampleRoundUpRequest());
    }

    public static RoundUpRequest getSampleRoundUpRequest() {
        return RoundUpRequest.builder()
                .customerToken(TEST_CUSTOMER_TOKEN)
                .startingTime(TEST_ROUND_UP_STARTING_TIME)
                .endingTime(TEST_ROUND_UP_ENDING_TIME)
                .build();
    }

//    public static String getSampleRoundUpResponseAsString() throws JsonProcessingException {
//        return OBJECT_MAPPER.writeValueAsString(RoundUpResponse.builder()
//                .savingGoalTransfers(List.of(RoundUpResponse.SavingGoalTransfer.builder()
//                        .accountUid(TEST_ACCOUNT_UID)
//                        .categoryUid(TEST_DEFAULT_CATEGORY_UID)
//                        .savingUid(TEST_SAVING_GOAL_UID)
//                        .transferUid(TestTra)
//                        .transferredAmount(roundUpAmount)
//                        .success(true)
//                        .build()))
//                .build());
//    }

    public static List<Account> testCustomerAccounts() throws Exception {
        return OBJECT_MAPPER.readValue(testCustomerAccountsGetResponse(), AccountsResponse.class)
                .getAccounts();
    }

    public static String testCustomerAccountsGetResponse() throws Exception {
        return readJsonFileAsString("/data/mockserver/response/accounts_get_response.json");
    }

    public static String testAccountTransactionsGetResponse() throws Exception {
        return readJsonFileAsString("/data/mockserver/response/transactions_get_response.json");
    }

    public static List<FeedItem> testFeedItems() throws Exception {
        return OBJECT_MAPPER.readValue(testAccountTransactionsGetResponse(), TransactionsResponse.class)
                .getFeedItems();
    }

    public static String testAllSavingGoalsGetResponse() throws Exception {
        return readJsonFileAsString("/data/mockserver/response/all_saving_goals_get_response.json");
    }

    public static String testSavingGoalsPutRequest() throws Exception {
        final SavingsGoalCreateRequest savingsGoalCreateRequest = SavingsGoalCreateRequest.builder()
                .name(TEST_NEW_SAVING_GOAL_NAME)
                .currency(TEST_CURRENCY)
                .target(CurrencyAmount.builder().currency(TEST_CURRENCY).minorUnits(TEST_NEW_SAVING_GOAL_TARGET_AMOUNT).build())
                .build();
        return OBJECT_MAPPER.writeValueAsString(savingsGoalCreateRequest);
    }

    public static String testSavingGoalsPutResponse() throws Exception {
        return readJsonFileAsString("/data/mockserver/response/saving_goal_put_response.json");
    }

    public static String testSavingGoalsByIdGetResponse() throws Exception {
        return readJsonFileAsString("/data/mockserver/response/saving_goal_by_id_get_response.json");
    }

    public static SavingsGoal testSavingGoal() throws Exception {
        return OBJECT_MAPPER.readValue(testSavingGoalsByIdGetResponse(), SavingsGoal.class);
    }

    public static String testAddMoneyPutRequest() throws Exception {
        return OBJECT_MAPPER.writeValueAsString(SavingGoalTransferRequest.builder()
                .amount(CurrencyAmount.builder().currency(TEST_CURRENCY).minorUnits(TEST_TRANSACTION_ROUND_UP_SUM).build())
                .build());
//        return readJsonFileAsString("/data/mockserver/response/saving_goal_by_id_get_response.json");
    }

    public static String testAddMoneyPutResponse() throws Exception {
        return readJsonFileAsString("/data/mockserver/response/add_money_put_response.json");
    }


}
