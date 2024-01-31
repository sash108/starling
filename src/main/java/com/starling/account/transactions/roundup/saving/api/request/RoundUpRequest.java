package com.starling.account.transactions.roundup.saving.api.request;

import lombok.Builder;
import lombok.Data;

/**
 * @author shahbazhussain
 */
@Data
@Builder
public class RoundUpRequest {
    private String customerToken;
    private String startingTime;
    private String endingTime;
}
