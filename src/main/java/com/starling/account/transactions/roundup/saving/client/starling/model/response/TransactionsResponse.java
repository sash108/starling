package com.starling.account.transactions.roundup.saving.client.starling.model.response;

import com.starling.account.transactions.roundup.saving.model.FeedItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shahbazhussain
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionsResponse {

    private List<FeedItem> feedItems;
}
