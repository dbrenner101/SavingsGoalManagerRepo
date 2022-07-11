package com.brenner.budgetmanager.savingsgoals;

import java.math.BigDecimal;

public class SavingsGoalAllocation {
    
    private Integer savingsGoalId;
    
    private BigDecimal allocationAmount;
    
    public SavingsGoalAllocation(Integer savingsGoalId, BigDecimal allocationAmount) {
        this.savingsGoalId = savingsGoalId;
        this.allocationAmount = allocationAmount;
    }
    
    public Integer getSavingsGoalId() {
        return savingsGoalId;
    }
    
    public void setSavingsGoalId(Integer savingsGoalId) {
        this.savingsGoalId = savingsGoalId;
    }
    
    public BigDecimal getAllocationAmount() {
        return allocationAmount;
    }
    
    public void setAllocationAmount(BigDecimal allocationAmount) {
        this.allocationAmount = allocationAmount;
    }
}
