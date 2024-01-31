package com.starling.account.transactions.roundup.saving.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Currency;

/**
 * @author shahbazhussain
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public  class CurrencyAmount {
    private Currency currency;
    private int minorUnits;
}