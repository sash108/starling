package com.starling.account.transactions.roundup.saving.service;

import com.starling.account.transactions.roundup.saving.api.request.RoundUpRequest;
import com.starling.account.transactions.roundup.saving.api.response.RoundUpResponse;
import com.starling.account.transactions.roundup.saving.client.starling.StarlingRestClient;
import com.starling.account.transactions.roundup.saving.client.starling.model.request.SavingGoalTransferRequest;
import com.starling.account.transactions.roundup.saving.model.Account;
import com.starling.account.transactions.roundup.saving.model.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author shahbazhussain
 */
@AllArgsConstructor
@Slf4j
@Service
public class RoundUpSavingService {

    private final StarlingRestClient starlingRestClient;
    private final RoundingService roundingService;

    private final SavingGoalService savingGoalService;

    public List<RoundUpResponse.SavingGoalTransfer> roundUpSaving(final RoundUpRequest roundUpRequest) {

        val accounts = starlingRestClient.getAccounts(roundUpRequest.getCustomerToken());
        log.info("Received total num_of_account={} for an account holder", accounts.size());
        return accounts.stream()
                .map(account -> runRoundUpAnalysisForAccount(roundUpRequest, account))
                .toList();
    }

    private RoundUpResponse.SavingGoalTransfer runRoundUpAnalysisForAccount(final RoundUpRequest roundUpRequest, final Account account) {

        val accountCurrency = account.getCurrency();
        val accountUid = account.getAccountUid();
        val defaultCategory = account.getDefaultCategory();
        val transferUid = UUID.randomUUID();

        log.info("Starting round up saving for accountUid={},defaultCategory={}, currency={} ", accountUid, defaultCategory, accountCurrency);
        val transactions = starlingRestClient.getTransactions(roundUpRequest, accountUid, defaultCategory);
        val roundUpSum = roundingService.roundUpSumByTransactions(accountCurrency, transactions);
        val roundUpAmount = CurrencyAmount.builder().currency(accountCurrency).minorUnits(roundUpSum).build();
        log.info("Total round up sum amountMinorUnits={} from numOfTransactions={}", roundUpSum, transactions.size());

        // Better to send response with success=false to avoid further unnessary HTTP calls.
        if (roundUpSum == 0) {
            RoundUpResponse.SavingGoalTransfer.builder()
                    .accountUid(accountUid)
                    .accountCurrency(accountCurrency)
                    .categoryUid(defaultCategory)
                    .savingUid(null)
                    .transferUid(transferUid)
                    .transferredAmount(roundUpAmount)
                    .success(false)
                    .build();
        }

        // Get Saving goal to save roundUpSum
        val targetedSavingsGoalUid = savingGoalService.getSavingsGoalUid(roundUpRequest, accountCurrency, accountUid, roundUpSum);

        // Transfer saving to saving goal
        val savingsGoalTransferResponse = starlingRestClient.transferSavingToSavingGoal(roundUpRequest,
                accountUid,
                targetedSavingsGoalUid,
                transferUid,
                SavingGoalTransferRequest.builder().amount(roundUpAmount).build());

        log.info("Successfully transferred amountMinorUnits={} to savingGoalUid={} with transferUid={}", roundUpSum, targetedSavingsGoalUid, savingsGoalTransferResponse.getTransferUid());

        return RoundUpResponse.SavingGoalTransfer.builder()
                .accountUid(accountUid)
                .accountCurrency(accountCurrency)
                .categoryUid(defaultCategory)
                .savingUid(targetedSavingsGoalUid)
                .transferUid(transferUid)
                .transferredAmount(roundUpAmount)
                .success(true)
                .build();

    }


}
