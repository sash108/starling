package com.starling.account.transactions.roundup.saving.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * @author shahbazhussain
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsGoal {
    private UUID savingsGoalUid;
    private String name;
    private CurrencyAmount target;
    private CurrencyAmount totalSaved;
    private Integer savedPercentage;
    private SavingsGoalState state;

    public enum SavingsGoalState {
        CREATING, ACTIVE, ARCHIVING, ARCHIVED, RESTORING, PENDING
    }
}

