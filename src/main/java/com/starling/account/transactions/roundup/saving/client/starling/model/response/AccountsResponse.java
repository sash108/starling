package com.starling.account.transactions.roundup.saving.client.starling.model.response;

import com.starling.account.transactions.roundup.saving.model.Account;
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
public class AccountsResponse {

    private List<Account> accounts;
}
