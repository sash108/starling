package com.starling.account.transactions.roundup.saving.api.response;

import com.starling.account.transactions.roundup.saving.model.CurrencyAmount;
import lombok.Builder;
import lombok.Data;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * @author shahbazhussain
 */
@Data
@Builder
public class RoundUpResponse {

    private List<SavingGoalTransfer> savingGoalTransfers;
    @Data
    @Builder
    public static class SavingGoalTransfer{
        private UUID accountUid;
        private Currency accountCurrency;
        private UUID categoryUid;
        private UUID savingUid;
        private UUID transferUid;
        private CurrencyAmount transferredAmount;
        private boolean success;
    }
}
