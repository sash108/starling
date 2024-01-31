package com.starling.account.transactions.roundup.saving.service;

import com.starling.account.transactions.roundup.saving.BaseUnitTest;
import com.starling.account.transactions.roundup.saving.TestData;
import com.starling.account.transactions.roundup.saving.client.starling.StarlingRestClient;
import com.starling.account.transactions.roundup.saving.client.starling.model.response.SavingsGoalCreateResponse;
import com.starling.account.transactions.roundup.saving.model.SavingsGoal;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author shahbazhussain
 */
class SavingGoalServiceTest extends BaseUnitTest {

    @InjectMocks
    private SavingGoalService savingGoalService;
    @Mock
    private StarlingRestClient starlingRestClient;

    @Test
    void shouldGetExpectedSavingGoalUid_inCorrectSavingGoalList() throws Exception {

        var savingsGoals = List.of(TestData.testSavingGoal());
        when(starlingRestClient.getAllAccountSavingGoals(any(), any())).thenReturn(savingsGoals);

        var actualSavingsGoal = savingGoalService.getSavingsGoalUid(TestData.getSampleRoundUpRequest(),
                TestData.TEST_CURRENCY,
                UUID.fromString(TestData.TEST_ACCOUNT_UID),
                TestData.TEST_TRANSACTION_ROUND_UP_SUM);

        assertThat(actualSavingsGoal).isNotNull();
        assertThat(Pattern.matches(TestData.TEST_UUID_PATTERN, actualSavingsGoal.toString())).isTrue();
    }

    @Test
    void shouldCreateNewSavingGoal_whenNoSavingGoalsAlreadyExists() {

        List<SavingsGoal> noExistingGoals = List.of(); // empty saving goal list
        var savingGoalUidToBeCreated = UUID.randomUUID();
        SavingsGoalCreateResponse newlyCreatedSavingsGoalResponse = SavingsGoalCreateResponse.builder()
                .savingsGoalUid(savingGoalUidToBeCreated)
                .success(true)
                .build();
        when(starlingRestClient.getAllAccountSavingGoals(any(), any())).thenReturn(noExistingGoals);
        when(starlingRestClient.createNewSavingGoal(any(), any(), any())).thenReturn(newlyCreatedSavingsGoalResponse);

        var newlyCreatedSavingsGoal = savingGoalService.getSavingsGoalUid(TestData.getSampleRoundUpRequest(),
                TestData.TEST_CURRENCY,
                UUID.fromString(TestData.TEST_ACCOUNT_UID),
                TestData.TEST_TRANSACTION_ROUND_UP_SUM);

        assertThat(newlyCreatedSavingsGoal).isNotNull();
        assertThat(Pattern.matches(TestData.TEST_UUID_PATTERN, newlyCreatedSavingsGoal.toString())).isTrue();
        assertThat(newlyCreatedSavingsGoal).isEqualTo(savingGoalUidToBeCreated);
        verify(starlingRestClient,times(1)).createNewSavingGoal(any(), any(), any());
    }

    @Test
    void shouldGetExpectedSavingGoalUid_forCorrectSavingGoals() throws Exception {

        var existingSavingsGoals = List.of(TestData.testSavingGoal());
        existingSavingsGoals.forEach(savingsGoal -> savingsGoal.setState(SavingsGoal.SavingsGoalState.ARCHIVED));
        var newlyCreatedSavingsGoalResponse = SavingsGoalCreateResponse.builder()
                .savingsGoalUid(UUID.randomUUID())
                .success(true)
                .build();
        when(starlingRestClient.getAllAccountSavingGoals(any(), any())).thenReturn(existingSavingsGoals);
        when(starlingRestClient.createNewSavingGoal(any(), any(), any())).thenReturn(newlyCreatedSavingsGoalResponse);

        var newSavingsGoal = savingGoalService.getSavingsGoalUid(TestData.getSampleRoundUpRequest(),
                TestData.TEST_CURRENCY,
                UUID.fromString(TestData.TEST_ACCOUNT_UID),
                TestData.TEST_TRANSACTION_ROUND_UP_SUM);

        assertThat(newSavingsGoal).isNotNull();
        assertThat(Pattern.matches(TestData.TEST_UUID_PATTERN, newSavingsGoal.toString())).isTrue();
        verify(starlingRestClient,times(1)).createNewSavingGoal(any(), any(), any());
    }
}