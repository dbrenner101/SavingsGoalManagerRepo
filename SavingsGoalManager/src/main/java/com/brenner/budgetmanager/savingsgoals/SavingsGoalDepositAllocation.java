package com.brenner.budgetmanager.savingsgoals;

import java.math.BigDecimal;

/**
 * A special data transfer object that encapsulates the allocation of a part of a deposit to a specific goal.
 */
public class SavingsGoalDepositAllocation {
    Integer savingsGoalId;
    Long depositId;
    BigDecimal allocationAmount;
    
    public SavingsGoalDepositAllocation(Integer savingsGoalId, Long depositId, BigDecimal allocationAmount) {
        this.savingsGoalId = savingsGoalId;
        this.depositId = depositId;
        this.allocationAmount = allocationAmount;
    }
    
    public Integer getSavingsGoalId() {
        return savingsGoalId;
    }
    
    public void setSavingsGoalId(Integer savingsGoalId) {
        this.savingsGoalId = savingsGoalId;
    }
    
    public Long getDepositId() {
        return depositId;
    }
    
    public void setDepositId(Long depositId) {
        this.depositId = depositId;
    }
    
    public BigDecimal getAllocationAmount() {
        return allocationAmount;
    }
    
    public void setAllocationAmount(BigDecimal allocationAmount) {
        this.allocationAmount = allocationAmount;
    }
}
