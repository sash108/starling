package com.starling.account.transactions.roundup.saving.client.starling;

import com.starling.account.transactions.roundup.saving.api.request.RoundUpRequest;
import com.starling.account.transactions.roundup.saving.exception.HttpAuthenticationException;
import com.starling.account.transactions.roundup.saving.model.Account;
import com.starling.account.transactions.roundup.saving.model.FeedItem;
import com.starling.account.transactions.roundup.saving.model.SavingsGoal;
import com.starling.account.transactions.roundup.saving.client.starling.model.request.SavingGoalTransferRequest;
import com.starling.account.transactions.roundup.saving.client.starling.model.request.SavingsGoalCreateRequest;
import com.starling.account.transactions.roundup.saving.client.starling.model.response.SavingGoalsResponse;
import com.starling.account.transactions.roundup.saving.client.starling.model.response.TransactionsResponse;
import com.starling.account.transactions.roundup.saving.client.starling.model.response.AccountsResponse;
import com.starling.account.transactions.roundup.saving.client.starling.model.response.SavingsGoalCreateResponse;
import com.starling.account.transactions.roundup.saving.client.starling.model.response.SavingsGoalTransferResponse;
import com.starling.account.transactions.roundup.saving.exception.NotFoundException;
import com.starling.account.transactions.roundup.saving.exception.OperationFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author shahbazhussain
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StarlingRestClient {

    @Value("${external.starling.base_url}")
    private String baseUrl;
    private final RestClient restClient;


    public List<Account> getAccounts(final String customerKey) {
        val customerAccountsResponseEntity = restClient
                .get()
                .uri(baseUrl + "/api/v2/accounts")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(customerKey))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 401 || res.getStatusCode().value() == 403) {
                        throw new HttpAuthenticationException("Couldn't authenticate customer token. Please contact the bank.");
                    } else if (res.getStatusCode().value() == 404) {
                        throw new NotFoundException("No accounts has been found.");
                    }
                })
                .toEntity(AccountsResponse.class);

        if (customerAccountsResponseEntity.getStatusCode().value() != 200) {
            throw new NotFoundException("No accounts has been found.");
        }
        return Optional.ofNullable(customerAccountsResponseEntity.getBody())
                .map(AccountsResponse::getAccounts)
                .orElseThrow(() -> new NotFoundException("No accounts has been found."));

    }

    public List<FeedItem> getTransactions(final RoundUpRequest roundUpRequest, final UUID accountId, final UUID categoryId) {

        val accountTransactionsResponseEntity = restClient
                .get()
                .uri(baseUrl + "/api/v2/feed/account/" + accountId + "/category/" + categoryId + "/transactions-between",
                        uriBuilder -> uriBuilder.queryParam("minTransactionTimestamp", roundUpRequest.getStartingTime())
                                .queryParam("maxTransactionTimestamp", roundUpRequest.getEndingTime())
                                .build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(roundUpRequest.getCustomerToken()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 401 || res.getStatusCode().value() == 403) {
                        throw new HttpAuthenticationException("Couldn't authenticate customer token. Please contact the bank.");
                    } else if (res.getStatusCode().value() == 404) {
                        throw new NotFoundException("No transactions has been found.");
                    }
                })
                .toEntity(TransactionsResponse.class);

        if (accountTransactionsResponseEntity.getStatusCode().value() != 200) {
            throw new NotFoundException("No transactions has been found.");
        }
        return Optional.ofNullable(accountTransactionsResponseEntity.getBody())
                .map(TransactionsResponse::getFeedItems)
                .orElseThrow(() -> new NotFoundException("No transactions has been found."));
    }

    public List<SavingsGoal> getAllAccountSavingGoals(final RoundUpRequest roundUpRequest, final UUID accountId) {

        val accountSavingGoalsResponseEntity = restClient
                .get()
                .uri(baseUrl + "/api/v2/account/" + accountId + "/savings-goals")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(roundUpRequest.getCustomerToken()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 401 || res.getStatusCode().value() == 403) {
                        throw new HttpAuthenticationException("Couldn't authenticate customer token. Please contact the bank.");
                    } else if (res.getStatusCode().value() == 404) {
                        throw new NotFoundException("No saving goals has been found.");
                    }
                })
                .toEntity(SavingGoalsResponse.class);

        if (accountSavingGoalsResponseEntity.getStatusCode().value() != 200) {
            throw new NotFoundException("No saving goals has been found.");
        }
        return Optional.ofNullable(accountSavingGoalsResponseEntity.getBody())
                .map(SavingGoalsResponse::getSavingsGoalList)
                .orElseThrow(() -> new NotFoundException("No saving goals has been found."));
    }

    public SavingsGoalCreateResponse createNewSavingGoal(final RoundUpRequest roundUpRequest, final UUID accountId,
                                                         final SavingsGoalCreateRequest savingsGoalCreateRequest) {

        val savingsGoalCreateResponseEntity = restClient
                .put()
                .uri(baseUrl + "/api/v2/account/" + accountId + "/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(roundUpRequest.getCustomerToken()))
                .accept(MediaType.APPLICATION_JSON)
                .body(savingsGoalCreateRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 401 || res.getStatusCode().value() == 403) {
                        throw new HttpAuthenticationException("Couldn't authenticate customer token. Please contact the bank.");
                    } else if (res.getStatusCode().value() == 404) {
                        // Even though, the chances are very less to get 404 in this call, since account will already be retrieved.
                        throw new NotFoundException("No matching account has been found.");
                    }
                })
                .toEntity(SavingsGoalCreateResponse.class);

        if (savingsGoalCreateResponseEntity.getStatusCode().value() != 200) {
            throw new OperationFailureException("Failed to create new saving goal.");
        }
        return Optional.ofNullable(savingsGoalCreateResponseEntity.getBody())
                .filter(SavingsGoalCreateResponse::isSuccess)
                .orElseThrow(() -> new OperationFailureException("Failed to create new saving goal."));
    }


    public SavingsGoalTransferResponse transferSavingToSavingGoal(final RoundUpRequest roundUpRequest, final UUID accountId,
                                                                  final UUID savingGoalUid, final UUID transferUid,
                                                                  final SavingGoalTransferRequest savingGoalTransferRequest) {

        val savingsGoalTransferResponseEntity = restClient
                .put()
                .uri(baseUrl + "/api/v2/account/" + accountId + "/savings-goals/" + savingGoalUid + "/add-money/" + transferUid)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(roundUpRequest.getCustomerToken()))
                .accept(MediaType.APPLICATION_JSON)
                .body(savingGoalTransferRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    if (res.getStatusCode().value() == 401 || res.getStatusCode().value() == 403) {
                        throw new HttpAuthenticationException("Couldn't authenticate customer token. Please contact the bank.");
                    } else if (res.getStatusCode().value() == 404) {
                        // Even though, the chances are very less to get 404 in this call, since it will be last call.
                        throw new NotFoundException("Either accounts or saving goals hasn't been found.");
                    }
                })
                .toEntity(SavingsGoalTransferResponse.class);
        if (savingsGoalTransferResponseEntity.getStatusCode().value() != 200) {
            throw new OperationFailureException("Failed to transfer money to saving account.");
        }
        return Optional.ofNullable(savingsGoalTransferResponseEntity.getBody())
                .filter(SavingsGoalTransferResponse::isSuccess)
                .orElseThrow(() -> new OperationFailureException("Failed to transfer money to saving account."));

    }


}
