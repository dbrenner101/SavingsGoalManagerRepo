package com.brenner.budgetmanager.savingsgoals;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.brenner.budgetmanager.deposit.Deposit;
import com.brenner.budgetmanager.deposit.DepositRepository;
import com.brenner.budgetmanager.exception.InvalidRequestException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SavingsGoalsBusinessService {

    @Autowired
    SavingsGoalRepository savingsGoalRepo;
    
    @Autowired
    DepositRepository depositRepo;
    
    public SavingsGoal findDefaultGoal() {
    	
    	Optional<SavingsGoal> optGoal = this.savingsGoalRepo.findByIsDefault(true);
    	
    	return optGoal.get();
    }
    
    public SavingsGoal addSavingsGoal(SavingsGoal goal) {
    	
    	if (goal == null) {
    		throw new InvalidRequestException("Savings Goal must not be null.");
    	}
        log.info("Entered saveSavingsGoal()");
        log.debug("Param: savingsGoal {}", goal);
        
        goal = decorateSavingsGoal(goal);
        log.debug("Decorated savingsGoal: {}", goal);
        
        SavingsGoal savingsGoal = this.savingsGoalRepo.save(goal);
        log.debug("Saved goal: {}", savingsGoal);
        
        log.info("Exiting saveSavingsGoal()");
        return savingsGoal;
    }
    
    public SavingsGoal updateSavingsGoal(SavingsGoal goal) {
        log.info("Entered updateSavingsGoal()");
        log.debug("Param: savingsGoal: {}", goal);
        
        goal = this.decorateSavingsGoal(goal);
        log.debug("Decorated savingsGoal: {}", goal);
        
        log.info("Exiting updateSavingsGoal()");
        
        return this.savingsGoalRepo.save(goal);
    }
    
    protected void allocateDepositToGoals(Long depositId, List<Integer> savingGoalIds, List<Float> amountsToAllocate) {
    	
    	Deposit deposit = this.depositRepo.findById(depositId).get();
    	Float depositAmount = deposit.getAmount();
    	Float remainingDeposit = depositAmount;
    	
    	for (int i=0; i<savingGoalIds.size(); i++) {
    		Integer goalId = savingGoalIds.get(i);
    		SavingsGoal goal = this.savingsGoalRepo.findById(goalId).get();
    		Float allocation = amountsToAllocate.get(i);
    		goal.setCurrentBalance(goal.getCurrentBalance() == null ? allocation : goal.getCurrentBalance() + allocation);
    		updateSavingsGoal(goal);
    		remainingDeposit -= allocation;
    	}
    	
    	log.debug("Remaining deposit: " + remainingDeposit);
    	
    	SavingsGoal defaultGoal = this.savingsGoalRepo.findByIsDefault(true).get();
    	defaultGoal.setCurrentBalance(defaultGoal.getCurrentBalance() == null ? remainingDeposit : defaultGoal.getCurrentBalance() + remainingDeposit);
    	updateSavingsGoal(defaultGoal);
    	
    	deposit.setAllocated(true);
    	this.depositRepo.save(deposit);
    }
    
    public void deleteSavingsGoal(Integer savingsGoalId) {
    	
    	Optional<SavingsGoal> optSavingsGoal = this.savingsGoalRepo.findById(savingsGoalId);
    	
    	if (optSavingsGoal.isEmpty()) {
    		throw new RuntimeException("Savings not found - can't delete.");
    	}
    	this.savingsGoalRepo.delete(optSavingsGoal.get());
    }
    
    public SavingsGoal decorateSavingsGoal(SavingsGoal goal) {
    	
    	if (goal == null) {
    		throw new InvalidRequestException("Goal must not be null.");
    	}
    	
    	if (goal.isDefault() == false) {
    		
    		if (goal.getCurrentBalance() == null) {
    			throw new InvalidRequestException("Goal must have a non-null current balance.");
    		}
    		
	        DateTime startDate = new DateTime(goal.getSavingsStartDate().getTime());
	        DateTime endDate = new DateTime(goal.getSavingsEndDate().getTime());
	        
	        Float targetAmount = goal.getTargetAmount();
	        
	        if (goal.getCurrentBalance() >= targetAmount) {
	        	goal.setSavingsPerDay(null);
	        	goal.setSavingsPerMonth(null);
	        	goal.setSavingsPerWeek(null);
	        }
	        else {
		        Float initialBalance = goal.getInitialBalance() != null ? goal.getInitialBalance() : 0;
		        Float targetBalance = targetAmount - initialBalance;
		        
		        Months months = Months.monthsBetween(startDate, endDate);
		        log.debug("months between: {}", months.getMonths());
		        goal.setMonthsTillPayment(months.getMonths());
		        if (months.getMonths() > 0) {
		        	Float savingsPerMonth = targetBalance > 0 ? targetBalance / months.getMonths() : 0;
		        	log.debug("savingsPerMonth: {}", savingsPerMonth);;
		        	goal.setSavingsPerMonth(savingsPerMonth);
		        }
		        
		        Weeks weeks = Weeks.weeksBetween(startDate, endDate);
		        log.debug("Weeks between: {}", weeks.getWeeks());
		        goal.setWeeksTillPayment(weeks.getWeeks());
		        Float savingsPerWeek = targetBalance > 0 ? targetBalance / weeks.getWeeks() : 0;
		        log.debug("savingsPerWeek: {}", savingsPerWeek);
		        goal.setSavingsPerWeek(savingsPerWeek);
		        
		        Days days = Days.daysBetween(startDate, endDate);
		        log.debug("days between: {}", days.getDays());
		        goal.setDaysTillPayment(days.getDays());
		        Float savingsPerDay = targetBalance > 0 ? targetBalance / days.getDays() : 0;
		        log.debug("savingsPerDay: {}", savingsPerDay);
		        goal.setSavingsPerDay(savingsPerDay);
	        }
    	}
        
        return goal;
    }
    
    public List<SavingsGoal> getAllSavingsGoalsButDefault() {
        log.info("Entered getAllSavingsGoalsButDefault()");
        
        List<SavingsGoal> goals = this.savingsGoalRepo.findAllByIsDefaultFalse(Sort.by(Sort.Direction.ASC, "goalName"));
        log.debug("Returing {} savingsGoals", goals != null ? goals.size() : 0);
        
        log.info("Exiting getAllSavingsGoalsButDefault()");
        return goals;
    }
    
    public SavingsGoal getSavingsGoalById(Integer goalId) {
    	
    	if (goalId == null) {
    		throw new InvalidRequestException("Goal id must be non-null.");
    	}
    	
        log.info("Entered getSavingsGoalById()");
        log.debug("Param: savingsGoalId: {}", goalId);
        
        Optional<SavingsGoal> optSavingsGoal = this.savingsGoalRepo.findById(goalId);
        
        SavingsGoal goal = optSavingsGoal.get();
        log.debug("Returing savingsGoal: {}", goal);
        
        log.info("Exiting getSavingsGoalById()");
        return goal;
    }
    
    public List<SavingsGoal> getAllSavingsGoals() {
    	
    	return this.savingsGoalRepo.findAll(Sort.by(Sort.Direction.ASC, "goalName"));
    }
}
