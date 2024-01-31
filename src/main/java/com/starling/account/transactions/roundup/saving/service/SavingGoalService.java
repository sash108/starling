package com.starling.account.transactions.roundup.saving.service;

import com.starling.account.transactions.roundup.saving.api.request.RoundUpRequest;
import com.starling.account.transactions.roundup.saving.client.starling.StarlingRestClient;
import com.starling.account.transactions.roundup.saving.client.starling.model.request.SavingsGoalCreateRequest;
import com.starling.account.transactions.roundup.saving.model.CurrencyAmount;
import com.starling.account.transactions.roundup.saving.model.SavingsGoal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

/**
 * @author shahbazhussain
 */
@AllArgsConstructor
@Slf4j
@Service
public class SavingGoalService {

    private StarlingRestClient starlingRestClient;
    public UUID getSavingsGoalUid(RoundUpRequest roundUpRequest, Currency accountCurrency, UUID accountUid, int roundUpSum) {
        UUID targetedSavingsGoalUid;
        val existingSavingGoals = starlingRestClient.getAllAccountSavingGoals(roundUpRequest, accountUid);
        final Optional<SavingsGoal> maybeExistingSavingGoal = existingSavingGoals.stream()
                .filter(savingsGoal -> SavingsGoal.SavingsGoalState.ACTIVE == savingsGoal.getState())
                .findFirst();

        if (maybeExistingSavingGoal.isPresent()) {
            targetedSavingsGoalUid = maybeExistingSavingGoal.get().getSavingsGoalUid();
            log.info("Existing saving goal savingGoalUid={} will be used.", targetedSavingsGoalUid);
        } else {
            val targetAmountMinorUnits = roundUpSum != 0 ? roundUpSum * 50 : 10000; // In given time period, targetSavingGoalAmount =  (50 times of round up amount ) otherwise amount= 100 pounds
            targetedSavingsGoalUid = starlingRestClient.createNewSavingGoal(roundUpRequest, accountUid,
                            toSavingGoalCreateRequest(roundUpRequest, accountCurrency, targetAmountMinorUnits))
                    .getSavingsGoalUid();
            log.info("Created new saving goal savingGoalUid={}, targetAmountMinorUnits={}", targetedSavingsGoalUid, targetAmountMinorUnits);
        }
        return targetedSavingsGoalUid;
    }


    private SavingsGoalCreateRequest toSavingGoalCreateRequest(RoundUpRequest roundUpRequest, Currency accountCurrency, int targetAmountMinorUnits) {
        return SavingsGoalCreateRequest.builder()
                .name("saving_goal_" + roundUpRequest.getStartingTime()) // Arbitrary naming convention is used.
                .currency(accountCurrency)
                .target(CurrencyAmount.builder().currency(accountCurrency).minorUnits(targetAmountMinorUnits).build())
                .build();
    }
}
