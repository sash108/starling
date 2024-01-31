package com.starling.account.transactions.roundup.saving.client.starling.model.request;

import com.starling.account.transactions.roundup.saving.model.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Currency;

/**
 * @author shahbazhussain
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsGoalCreateRequest {
    private String name;
    private Currency currency;
    private CurrencyAmount target;
    private String base64EncodedPhoto;
}
