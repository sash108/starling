package com.starling.account.transactions.roundup.saving.client.starling.model.response;

import com.starling.account.transactions.roundup.saving.model.SavingsGoal;
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
public class SavingGoalsResponse {

    private List<SavingsGoal> savingsGoalList;
}
