package com.starling.account.transactions.roundup.saving.client.starling.model.request;

import com.starling.account.transactions.roundup.saving.model.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author shahbazhussain
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingGoalTransferRequest {
    private CurrencyAmount amount;
}
