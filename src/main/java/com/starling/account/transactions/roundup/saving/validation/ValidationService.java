package com.starling.account.transactions.roundup.saving.validation;

import com.starling.account.transactions.roundup.saving.api.request.RoundUpRequest;
import com.starling.account.transactions.roundup.saving.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * @author shahbazhussain
 */
@Slf4j
@Service
public class ValidationService {
    public void validateRequest(final RoundUpRequest roundUpRequest) {

        final String customerToken = roundUpRequest.getCustomerToken();
        final String startingTime = roundUpRequest.getStartingTime();
        String endingTime = roundUpRequest.getEndingTime();
        Instant startingTimeInstant, endingTimeInstant;

        // Validate customer token
        Assert.notNull(customerToken, "customerToken mustn't be absent in json payload.");
        Assert.isTrue(!customerToken.isBlank(), "customerToken mustn't be blank");

        // Validate starting time
        Assert.notNull(startingTime, "startingTime mustn't be null");
        startingTimeInstant = validateAndGetInstantTimeStamp(startingTime);
        Assert.isTrue(startingTimeInstant.isBefore(Instant.now()), "startingTime mustn't be future time");

        // Validate ending time
        if (endingTime == null) {
            endingTimeInstant = startingTimeInstant.plus(7, ChronoUnit.DAYS); // Consider endingTime after next 7 days in absence of endingTime[Optional field]
            roundUpRequest.setEndingTime(endingTimeInstant.toString()); // setting endingTime to roundUpRequest
        } else {
            endingTimeInstant = validateAndGetInstantTimeStamp(endingTime);
        }

        // Validate bott
        Assert.isTrue(startingTimeInstant.isBefore(endingTimeInstant), "Starting time mustn't be equal or after ending time.");
    }

    private Instant validateAndGetInstantTimeStamp(String timeStamp) {
        try {
            return Instant.parse(timeStamp);
        } catch (DateTimeParseException ex) {
            throw new ValidationException(String.format("Timestamp=%s isn't valid timestamp.", timeStamp));
        }
    }

}
