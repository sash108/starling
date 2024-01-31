package com.starling.account.transactions.roundup.saving.exception.handler;

import com.starling.account.transactions.roundup.saving.exception.HttpAuthenticationException;
import com.starling.account.transactions.roundup.saving.exception.NotFoundException;
import com.starling.account.transactions.roundup.saving.exception.OperationFailureException;
import com.starling.account.transactions.roundup.saving.exception.ValidationException;
import com.starling.account.transactions.roundup.saving.model.ErrorResponse;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Central place to handle exception to rest response mapping in the application.
 *
 * @author shahbazhussain
 */
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({ValidationException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationException(RuntimeException ex) {
        return new ResponseEntity<>(getErrors(List.of(ex.getMessage())), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpAuthenticationException.class})
    public ResponseEntity<?> handleForbiddenException(RuntimeException ex) {
        return new ResponseEntity<>(getErrors(List.of(ex.getMessage())), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(RuntimeException ex) {
        return new ResponseEntity<>(getErrors(List.of(ex.getMessage())), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    // Downstream operation failure cover up
    @ExceptionHandler(OperationFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleAllUncaughtException(RuntimeException ex) {
        return new ResponseEntity<>(getErrors(List.of(ex.getMessage())), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // All rest exception handling if any.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleAllUncaughtException(Exception ex) {
        return new ResponseEntity<>(getErrors(List.of("Something wrong on our side. Please try later.")), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse getErrors(List<String> errors) {
        val errorDetails = errors.stream()
                .map(errorMsg -> ErrorResponse.ErrorDetail.builder().message(errorMsg).build())
                .toList();

        return ErrorResponse.builder()
                .errors(errorDetails)
                .success(false)
                .build();
    }
}
