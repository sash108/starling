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
public class SavingsGoalTransferResponse {
    private UUID transferUid;
    private boolean success;
}
