package com.starling.account.transactions.roundup.saving.api;

import com.starling.account.transactions.roundup.saving.api.request.RoundUpRequest;
import com.starling.account.transactions.roundup.saving.api.response.RoundUpResponse;
import com.starling.account.transactions.roundup.saving.service.RoundUpSavingService;
import com.starling.account.transactions.roundup.saving.validation.ValidationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author shahbazhussain
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/api/v1/roundup/saving")
public class RoundUpSavingController {

    private final ValidationService validationService;
    private final RoundUpSavingService roundUpSavingService;

    @PutMapping(value = "/transfer-money",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RoundUpResponse.SavingGoalTransfer>> customerAccountRoundUpSaving(
            @Valid @RequestBody @NotNull(message = "The request json payload is required.") final RoundUpRequest roundUpRequest) {

        log.info("Received query to transfer round-up saving, startingTime={},endingTime={}",
                roundUpRequest.getStartingTime(), roundUpRequest.getEndingTime());

        validationService.validateRequest(roundUpRequest);

        val savingGoalTransfers = roundUpSavingService.roundUpSaving(roundUpRequest);
        return ResponseEntity.ok(savingGoalTransfers);
    }


    @GetMapping(value = "/ping")
    public ResponseEntity<?> heartBeat() {
        return ResponseEntity.ok("Pong");
    }
}
