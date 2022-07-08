package com.brenner.budgetmanager.savingsgoals;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity object representing a savings goal. A savings goal consists of some financial target in the future that is
 * specifically bounded by a start date, end date and target amount. Optionally an initial balance and current balance can
 * be included.
 */
@Entity
@Table(name = "savings_goals")
public class SavingsGoal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer savingsGoalId;
    
    private String goalName;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date savingsStartDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date savingsEndDate;
    
    private BigDecimal targetAmount;
    
    private Integer monthsTillPayment;
    
    private Integer weeksTillPayment;
    
    private Integer daysTillPayment;
    
    private BigDecimal savingsPerMonth;
    
    private BigDecimal savingsPerWeek;
    
    private BigDecimal savingsPerDay;
    
    private BigDecimal initialBalance;
    
    private BigDecimal currentBalance;
    
    private boolean isDefault;
    
    private String notes;
    
    public SavingsGoal() {}
    
    public SavingsGoal(Integer savingsGoalId, String goalName, Date savingsStartDate, Date savingsEndDate,
                       BigDecimal targetAmount, BigDecimal initialBalance, BigDecimal currentBalance, boolean isDefault) {
    	this.savingsGoalId = savingsGoalId;
    	this.goalName = goalName;
    	this.savingsStartDate = savingsStartDate;
    	this.savingsEndDate = savingsEndDate;
    	this.targetAmount = targetAmount;
    	this.initialBalance = initialBalance;
    	this.currentBalance = currentBalance;
    	this.isDefault = isDefault;
    }
    
    public Integer getSavingsGoalId() {
        return savingsGoalId;
    }

    public void setSavingsGoalId(Integer savingsGoalId) {
        this.savingsGoalId = savingsGoalId;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public Date getSavingsStartDate() {
        return savingsStartDate;
    }

    public void setSavingsStartDate(Date savingsStartDate) {
        this.savingsStartDate = savingsStartDate;
    }

    public Date getSavingsEndDate() {
        return savingsEndDate;
    }

    public void setSavingsEndDate(Date savingsEndDate) {
        this.savingsEndDate = savingsEndDate;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Integer getMonthsTillPayment() {
        return monthsTillPayment;
    }

    public void setMonthsTillPayment(Integer monthsTillPayment) {
        this.monthsTillPayment = monthsTillPayment;
    }

    public Integer getWeeksTillPayment() {
        return weeksTillPayment;
    }

    public void setWeeksTillPayment(Integer weeksTillPayment) {
        this.weeksTillPayment = weeksTillPayment;
    }

    public Integer getDaysTillPayment() {
        return daysTillPayment;
    }

    public void setDaysTillPayment(Integer daysTillPayment) {
        this.daysTillPayment = daysTillPayment;
    }

    public BigDecimal getSavingsPerMonth() {
        return savingsPerMonth;
    }

    public void setSavingsPerMonth(BigDecimal savingsPerMonth) {
        this.savingsPerMonth = savingsPerMonth;
    }

    public BigDecimal getSavingsPerWeek() {
        return savingsPerWeek;
    }

    public void setSavingsPerWeek(BigDecimal savingsPerWeek) {
        this.savingsPerWeek = savingsPerWeek;
    }

    public BigDecimal getSavingsPerDay() {
        return savingsPerDay;
    }

    public void setSavingsPerDay(BigDecimal savingsPerDay) {
        this.savingsPerDay = savingsPerDay;
    }

    public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance(BigDecimal initialBalance) {
		this.initialBalance = initialBalance;
	}
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        SavingsGoal that = (SavingsGoal) o;
        
        if (isDefault != that.isDefault) return false;
        if (savingsGoalId != null ? !savingsGoalId.equals(that.savingsGoalId) : that.savingsGoalId != null)
            return false;
        if (goalName != null ? !goalName.equals(that.goalName) : that.goalName != null) return false;
        if (savingsStartDate != null ? !savingsStartDate.equals(that.savingsStartDate) : that.savingsStartDate != null)
            return false;
        if (savingsEndDate != null ? !savingsEndDate.equals(that.savingsEndDate) : that.savingsEndDate != null)
            return false;
        if (targetAmount != null ? !targetAmount.equals(that.targetAmount) : that.targetAmount != null) return false;
        if (initialBalance != null ? !initialBalance.equals(that.initialBalance) : that.initialBalance != null)
            return false;
        if (currentBalance != null ? !currentBalance.equals(that.currentBalance) : that.currentBalance != null)
            return false;
        return notes != null ? notes.equals(that.notes) : that.notes == null;
    }
    
    @Override
    public int hashCode() {
        int result = savingsGoalId != null ? savingsGoalId.hashCode() : 0;
        result = 31 * result + (goalName != null ? goalName.hashCode() : 0);
        result = 31 * result + (savingsStartDate != null ? savingsStartDate.hashCode() : 0);
        result = 31 * result + (savingsEndDate != null ? savingsEndDate.hashCode() : 0);
        result = 31 * result + (targetAmount != null ? targetAmount.hashCode() : 0);
        result = 31 * result + (initialBalance != null ? initialBalance.hashCode() : 0);
        result = 31 * result + (currentBalance != null ? currentBalance.hashCode() : 0);
        result = 31 * result + (isDefault ? 1 : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        return result;
    }
    
    @Override
	public String toString() {
		return "SavingsGoal [savingsGoalId=" + savingsGoalId + ", goalName=" + goalName + ", savingsStartDate="
				+ savingsStartDate + ", savingsEndDate=" + savingsEndDate + ", targetAmount=" + targetAmount
				+ ", monthsTillPayment=" + monthsTillPayment + ", weeksTillPayment=" + weeksTillPayment
				+ ", daysTillPayment=" + daysTillPayment + ", savingsPerMonth=" + savingsPerMonth + ", savingsPerWeek="
				+ savingsPerWeek + ", savingsPerDay=" + savingsPerDay + ", initialBalance=" + initialBalance
				+ ", currentBalance=" + currentBalance + ", isDefault=" + isDefault + ", notes=" + notes + "]";
	}
}
