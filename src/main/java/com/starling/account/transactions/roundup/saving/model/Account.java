package com.starling.account.transactions.roundup.saving.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

/**
 * @author shahbazhussain
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    private UUID accountUid;
    private String accountType;
    private UUID defaultCategory;
    private Currency currency;
    private Instant createdAt;
    private String name;
}
