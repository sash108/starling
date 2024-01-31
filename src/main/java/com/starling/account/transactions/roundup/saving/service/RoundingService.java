package com.starling.account.transactions.roundup.saving.service;

import com.starling.account.transactions.roundup.saving.model.FeedItem;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

/**
 * @author shahbazhussain
 */
@Service
public class RoundingService {

    private static final List<FeedItem.Status> ACCEPTED_FEED_ITEM_STATUSES = List.of(FeedItem.Status.SETTLED,
            FeedItem.Status.UPCOMING,
            FeedItem.Status.PENDING);

    public int roundUpSumByTransactions(final Currency accountCurrency, final List<FeedItem> feedItems) {

        return feedItems.stream()
                .filter(feedItem -> FeedItem.Direction.OUT == feedItem.getDirection())  // only consider spending(OUT) transactions , not incoming transactions(IN)
                .filter(feedItem -> ACCEPTED_FEED_ITEM_STATUSES.contains(feedItem.getStatus()))  // only consider settled transactions or transactions which could settled in future like UPCOMING(Future automatic bill payment),PENDING(Confirmation waiting from PSP may be)
                .filter(feedItem -> accountCurrency == feedItem.getAmount().getCurrency()) // reconfirm accountCurrency in case some feedItem got wrong accountCurrency
                .mapToInt(feedItem -> feedItem.getAmount().getMinorUnits() % 100)
                .filter(roundUpAmount -> roundUpAmount > 0) // Remove unwanted transaction e.g. 9800 bill which 98 pound
                .sum();
    }
}
