package com.starling.account.transactions.roundup.saving.service;

import com.starling.account.transactions.roundup.saving.BaseUnitTest;
import com.starling.account.transactions.roundup.saving.TestData;
import com.starling.account.transactions.roundup.saving.client.starling.StarlingRestClient;
import com.starling.account.transactions.roundup.saving.client.starling.model.response.SavingsGoalTransferResponse;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author shahbazhussain
 */
class RoundUpSavingServiceTest extends BaseUnitTest {

    @Mock
    private StarlingRestClient starlingRestClient;
    @Mock
    private SavingGoalService savingGoalService;

    @Mock
    private RoundingService roundingService;
    @InjectMocks
    private RoundUpSavingService roundUpSavingService;

    @Test
    void shouldGetTransferSaving_forWorkingExample() throws Exception {

        val sampleRoundUpRequest = TestData.getSampleRoundUpRequest();
        val accounts = TestData.testCustomerAccounts();
        val feedItems = TestData.testFeedItems();
        val savingsGoalTransferResponse = SavingsGoalTransferResponse.builder().transferUid(UUID.randomUUID()).success(true).build();
        when(starlingRestClient.getAccounts(anyString())).thenReturn(accounts);
        when(starlingRestClient.getTransactions(any(), any(), any())).thenReturn(feedItems);
        when(roundingService.roundUpSumByTransactions(any(), any())).thenReturn(TestData.TEST_TRANSACTION_ROUND_UP_SUM);
        when(savingGoalService.getSavingsGoalUid(any(), any(), any(), anyInt())).thenReturn(UUID.fromString(TestData.TEST_SAVING_GOAL_UID));
        when(starlingRestClient.transferSavingToSavingGoal(any(), any(), any(), any(), any())).thenReturn(savingsGoalTransferResponse);

        val savingGoalTransfers = roundUpSavingService.roundUpSaving(sampleRoundUpRequest);

        assertThat(savingGoalTransfers.size()).isEqualTo(1);
        assertThat(savingGoalTransfers.get(0).getTransferUid()).isNotNull();
        verify(starlingRestClient, times(1)).getAccounts(any());
        verify(starlingRestClient, times(1)).getTransactions(any(), any(), any());
        verify(roundingService, times(1)).roundUpSumByTransactions(any(), any());
        verify(savingGoalService, times(1)).getSavingsGoalUid(any(), any(), any(), anyInt());
        verify(starlingRestClient, times(1)).transferSavingToSavingGoal(any(), any(), any(), any(), any());

    }


}