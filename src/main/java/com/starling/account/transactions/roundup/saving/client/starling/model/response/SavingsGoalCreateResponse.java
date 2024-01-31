package com.starling.account.transactions.roundup.saving.client.starling.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author shahbazhussain
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsGoalCreateResponse {
    private UUID savingsGoalUid;
    private boolean success;
}
