package com.brenner.budgetmanager.savingsgoals;

import com.brenner.budgetmanager.deposit.Deposit;

import java.util.List;

/**
 * A special data transfer object that encapsulates the allocation of a part of a deposit to a specific goal.
 */
public class SavingsGoalDepositAllocation {
    
    List<Deposit> deposits;
    List<SavingsGoalAllocation> savingsGoalAllocations;
    
    public SavingsGoalDepositAllocation() {}
    
    public SavingsGoalDepositAllocation(List<Deposit> deposits, List<SavingsGoalAllocation> savingsGoalAllocations) {
        this.deposits = deposits;
        this.savingsGoalAllocations = savingsGoalAllocations;
    }
    
    public List<Deposit> getDeposits() {
        return deposits;
    }
    
    public void setDeposits(List<Deposit> deposits) {
        this.deposits = deposits;
    }
    
    public List<SavingsGoalAllocation> getSavingsGoalAllocations() {
        return savingsGoalAllocations;
    }
    
    public void setSavingsGoalAllocations(List<SavingsGoalAllocation> savingsGoalAllocations) {
        this.savingsGoalAllocations = savingsGoalAllocations;
    }
}
