package com.starling.account.transactions.roundup.saving.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author shahbazhussain
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedItem {
    private String feedItemUid;
    private String categoryUid;
    private CurrencyAmount amount;
    private CurrencyAmount sourceAmount;
    private Direction direction;
    private String updatedAt;
    private String transactionTime;
    private String settlementTime;
    private String source;
    private Status status;
    private String transactingApplicationUserUid;
    private String counterPartyType;
    private String counterPartyUid;
    private String counterPartyName;
    private String counterPartySubEntityUid;
    private String counterPartySubEntityName;
    private String counterPartySubEntityIdentifier;
    private String counterPartySubEntitySubIdentifier;
    private String reference;
    private String country;
    private String spendingCategory;
    private boolean hasAttachment;
    private boolean hasReceipt;
    private boolean batchPaymentDetails;

    public enum Direction {
        IN, OUT
    }

    public enum Status {
        UPCOMING, UPCOMING_CANCELLED, PENDING, REVERSED, SETTLED, DECLINED, REFUNDED, RETRYING, ACCOUNT_CHECK
    }
}

