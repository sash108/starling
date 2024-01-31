package com.starling.account.transactions.roundup.saving.service;

import com.starling.account.transactions.roundup.saving.BaseUnitTest;
import com.starling.account.transactions.roundup.saving.TestData;
import com.starling.account.transactions.roundup.saving.model.CurrencyAmount;
import com.starling.account.transactions.roundup.saving.model.FeedItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author shahbazhussain
 */
class RoundingServiceTest extends BaseUnitTest {

    @Autowired
    private RoundingService roundingService;

    @Test
    void shouldGetExpectedRoundUpSum_forWorkingTransactions() throws Exception {

        final List<FeedItem> feedItems = TestData.testFeedItems();
        final int actualRoundUpSum = roundingService.roundUpSumByTransactions(TestData.TEST_CURRENCY, feedItems);
        assertThat(actualRoundUpSum).isEqualTo(TestData.TEST_TRANSACTION_ROUND_UP_SUM);
    }

    @Test
    void shouldGetZeroRoundUpSum_forAllFeedItemsWithInDirection() throws Exception {

        final List<FeedItem> feedItems = TestData.testFeedItems();
        feedItems.forEach(feedItem -> feedItem.setDirection(FeedItem.Direction.IN));

        final int actualRoundUpSum = roundingService.roundUpSumByTransactions(TestData.TEST_CURRENCY, feedItems);
        assertThat(actualRoundUpSum).isEqualTo(0);
    }

    @Test
    void shouldGetZeroRoundUpSum_forAllFeedItemsWithUnAcceptableStatusDecline() throws Exception {

        final List<FeedItem> feedItems = TestData.testFeedItems();

        final FeedItem.Status UnAcceptableTransactionStatus = FeedItem.Status.DECLINED;
        feedItems.forEach(feedItem -> feedItem.setStatus(UnAcceptableTransactionStatus));

        final int actualRoundUpSum = roundingService.roundUpSumByTransactions(TestData.TEST_CURRENCY, feedItems);
        assertThat(actualRoundUpSum).isEqualTo(0);
    }

    @Test
    void shouldGetZeroRoundUpSum_forAllFeedItemsWithUnMatchedCurrency() throws Exception {

        final List<FeedItem> feedItems = TestData.testFeedItems();

        final Currency unExpectedCurrency = Currency.getInstance("USD");
        feedItems.forEach(feedItem -> feedItem.getAmount().setCurrency(unExpectedCurrency));

        final int actualRoundUpSum = roundingService.roundUpSumByTransactions(TestData.TEST_CURRENCY, feedItems);
        assertThat(actualRoundUpSum).isEqualTo(0);
    }

    @Test
    void shouldGetZeroRoundUpSum_forFeedItemWithLastTwoDecimalZeros() {

        int lastTwoDecimalZeroNum = 8800;
        final List<FeedItem> feedItems = List.of(FeedItem.builder()
                .direction(FeedItem.Direction.OUT)
                .status(FeedItem.Status.SETTLED)
                .amount(new CurrencyAmount(TestData.TEST_CURRENCY, lastTwoDecimalZeroNum))
                .build());


        final int actualRoundUpSum = roundingService.roundUpSumByTransactions(TestData.TEST_CURRENCY, feedItems);
        assertThat(actualRoundUpSum).isEqualTo(0);
    }

}