package com.starling.account.transactions.roundup.saving.validation;

import com.starling.account.transactions.roundup.saving.BaseUnitTest;
import com.starling.account.transactions.roundup.saving.TestData;
import com.starling.account.transactions.roundup.saving.exception.ValidationException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author shahbazhussain
 */
class ValidationServiceTest extends BaseUnitTest {

    @Autowired
    private ValidationService validationService;

    @Test
    void shouldNotThrowAnyException_forNormalRequest() {
        val sampleRoundUpRequest = TestData.getSampleRoundUpRequest();
        assertDoesNotThrow(() -> validationService.validateRequest(sampleRoundUpRequest));
    }

    @Test
    void shouldThrowIllegalArgumentException_whenCustomerTokenIsNotCorrect() {

        val sampleRoundUpRequest = TestData.getSampleRoundUpRequest();
        sampleRoundUpRequest.setCustomerToken(null); // when token=null
        var expectedIllegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> validationService.validateRequest(sampleRoundUpRequest));
        assertThat(expectedIllegalArgumentException.getMessage()).isEqualTo("customerToken mustn't be absent in json payload.");

        sampleRoundUpRequest.setCustomerToken("      "); // when token hss empty spaces
        expectedIllegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> validationService.validateRequest(sampleRoundUpRequest));
        assertThat(expectedIllegalArgumentException.getMessage()).isEqualTo("customerToken mustn't be blank");
    }

    @Test
    void shouldThrowIllegalArgumentException_whenStartingTimeIsNotCorrect() {

        val sampleRoundUpRequest = TestData.getSampleRoundUpRequest();
        sampleRoundUpRequest.setStartingTime(null); // when startingTime=null
        var expectedIllegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> validationService.validateRequest(sampleRoundUpRequest));
        assertThat(expectedIllegalArgumentException.getMessage()).isEqualTo("startingTime mustn't be null");

        sampleRoundUpRequest.setEndingTime(Instant.now().toString()); // when endingTime=now
        sampleRoundUpRequest.setStartingTime(Instant.now().plus(1, ChronoUnit.DAYS).toString()); // when startingTime is one day after
        expectedIllegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> validationService.validateRequest(sampleRoundUpRequest));
        assertThat(expectedIllegalArgumentException.getMessage()).isEqualTo("startingTime mustn't be future time");
    }

    @Test
    void shouldAssignEndingTimeInRequest_whenEndingTimeIsNull() {

        val sampleRoundUpRequest = TestData.getSampleRoundUpRequest();
        sampleRoundUpRequest.setEndingTime(null); // when endingTime=null

        validationService.validateRequest(sampleRoundUpRequest);

        assertThat(sampleRoundUpRequest.getEndingTime())
                .isEqualTo(Instant.parse(sampleRoundUpRequest.getStartingTime()).plus(7, ChronoUnit.DAYS).toString());

    }

    @Test
    void shouldThrowValidateException_whenEndingTimeIsNotCorrectTime() {

        val sampleRoundUpRequest = TestData.getSampleRoundUpRequest();
        sampleRoundUpRequest.setEndingTime("wrong-date-time"); // when wrong endingTime

        val expectedValidationException = assertThrows(ValidationException.class,
                () -> validationService.validateRequest(sampleRoundUpRequest));

        assertThat(expectedValidationException.getMessage())
                .isEqualTo(String.format("Timestamp=%s isn't valid timestamp.", sampleRoundUpRequest.getEndingTime()));

    }

    @Test
    void shouldThrowIllegalArgumentException_whenEndingTimeIsBeforeStartingTime() {

        val sampleRoundUpRequest = TestData.getSampleRoundUpRequest();
        sampleRoundUpRequest.setEndingTime(Instant.parse(sampleRoundUpRequest.getStartingTime()).minus(1, ChronoUnit.DAYS).toString());

        val expectedIllegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> validationService.validateRequest(sampleRoundUpRequest));

        assertThat(expectedIllegalArgumentException.getMessage())
                .isEqualTo("Starting time mustn't be equal or after ending time.");

    }
}