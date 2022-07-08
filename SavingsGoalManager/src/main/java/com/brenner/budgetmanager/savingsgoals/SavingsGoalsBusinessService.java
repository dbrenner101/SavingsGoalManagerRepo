package com.brenner.budgetmanager.savingsgoals;

import com.brenner.budgetmanager.deposit.Deposit;
import com.brenner.budgetmanager.deposit.DepositRepository;
import com.brenner.budgetmanager.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Class to abstract the data persistence routines and manage any business logic required.
 */
@Service
@Slf4j
public class SavingsGoalsBusinessService {

    @Autowired
    SavingsGoalRepository savingsGoalRepo;
    
    @Autowired
    DepositRepository depositRepo;
	
	/**
	 * Method to retrieve the goal that is flagged as default.
	 *
	 * @return An Optional<SavingsGoal> or Optional.empty() if there is no default goal.
	 */
	public Optional<SavingsGoal> findDefaultGoal() {
    	
    	return this.savingsGoalRepo.findByIsDefault(true);
    }
	
	/**
	 * Handles calling persistance for a new goal. The goal is decorated before saving
	 * @see #decorateSavingsGoal
	 *
	 * @param goal The goal data to persist
	 * @return The persisted object including unique identifier.
	 */
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
	
	/**
	 * Handles updating a SavingsGoal. The goal is decorated before persistence.
	 * @see #decorateSavingsGoal
	 *
	 * @param goal The goal data to persist
	 * @return The object after persistence
	 */
    public SavingsGoal updateSavingsGoal(SavingsGoal goal) {
        log.info("Entered updateSavingsGoal()");
        log.debug("Param: savingsGoal: {}", goal);
        
        goal = this.decorateSavingsGoal(goal);
        log.debug("Decorated savingsGoal: {}", goal);
        
        log.info("Exiting updateSavingsGoal()");
        
        return this.savingsGoalRepo.save(goal);
    }
	
	/**
	 * Method to take a list of goal ids and a list of amounts and update their current balance. This is a fragile method and
	 * assumes the lists members are in alignment.
	 *
	 * This method will retrieve each goal and update its current balance based on the allocated amount. After updates
	 * are complete the deposit will be marked as allocated.
	 *
	 * @param depositId The deposit that allocations are derived from
	 * @param savingGoalIds List of goal unique identifiers.
	 * @param amountsToAllocate The amounts to allocate towards each goal.
	 */
    protected void allocateDepositToGoals(Long depositId, List<Integer> savingGoalIds, List<BigDecimal> amountsToAllocate) {
    	
    	Deposit deposit = this.depositRepo.findById(depositId).get();
    	BigDecimal depositAmount = deposit.getAmount();
    	BigDecimal remainingDeposit = depositAmount;
    	
    	for (int i=0; i<savingGoalIds.size(); i++) {
    		Integer goalId = savingGoalIds.get(i);
    		SavingsGoal goal = this.savingsGoalRepo.findById(goalId).get();
    		BigDecimal allocation = amountsToAllocate.get(i);
    		goal.setCurrentBalance(goal.getCurrentBalance() == null ? allocation : goal.getCurrentBalance().add(allocation));
    		updateSavingsGoal(goal);
    		remainingDeposit = remainingDeposit.subtract(allocation);
    	}
    	
    	log.debug("Remaining deposit: " + remainingDeposit);
	
		updateDefaultGoalBalance(remainingDeposit);
    	
    	deposit.setAllocated(true);
    	this.depositRepo.save(deposit);
    }
	
	/**
	 * Method to take a list of allocation objects
	 *
	 * 	 This method will retrieve each goal and update its current balance based on the allocated amount. After updates
	 * 	 are complete the deposit will be marked as allocated.
	 * @param amountsToAllocate The list of goal ids and allocation amounts.
	 */
	protected void allocateDepositToGoals(List<SavingsGoalDepositAllocation> amountsToAllocate) {
		
		Long depositId = amountsToAllocate.get(0).getDepositId();
		Deposit deposit = this.depositRepo.findById(depositId).get();
		BigDecimal depositAmount = deposit.getAmount();
		BigDecimal remainingDeposit = depositAmount;
		
		for (SavingsGoalDepositAllocation allocation : amountsToAllocate) {
			Integer goalId = allocation.getSavingsGoalId();
			SavingsGoal goal = this.savingsGoalRepo.findById(goalId).get();
			BigDecimal amount = allocation.getAllocationAmount();
			goal.setCurrentBalance(goal.getCurrentBalance() == null ? amount : goal.getCurrentBalance().add(amount));
			updateSavingsGoal(goal);
			remainingDeposit = remainingDeposit.subtract(amount);
		}
		
		log.debug("Remaining deposit: " + remainingDeposit);
		
		updateDefaultGoalBalance(remainingDeposit);
		
		deposit.setAllocated(true);
		this.depositRepo.save(deposit);
	}
	
	/**
	 * Any excess or shorfall in the allocation is assigned to the default goal.
	 *
	 * @param balanceChange Balance to assign to the default goal.
	 */
	private void updateDefaultGoalBalance(BigDecimal balanceChange) {
		SavingsGoal defaultGoal = this.savingsGoalRepo.findByIsDefault(true).get();
		defaultGoal.setCurrentBalance(defaultGoal.getCurrentBalance() == null ? balanceChange : defaultGoal.getCurrentBalance().add(balanceChange));
		updateSavingsGoal(defaultGoal);
	}
	
	/**
	 * Method to delete a specific goal. A RuntimeException is produced if the goal does not exist.
	 *
	 * @param savingsGoalId Goal unique identifier.
	 */
	public void deleteSavingsGoal(Integer savingsGoalId) {
    	
    	Optional<SavingsGoal> optSavingsGoal = this.savingsGoalRepo.findById(savingsGoalId);
    	
    	if (optSavingsGoal.isEmpty()) {
    		throw new RuntimeException("Savings not found - can't delete.");
    	}
    	this.savingsGoalRepo.delete(optSavingsGoal.get());
    }
	
	/**
	 * Method to complete derived fields like weeks till goal and dollars per month, etc.
	 *
	 * @param goal The base goal
	 * @return A completed entity graph.
	 */
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
	        
	        BigDecimal targetAmount = goal.getTargetAmount();
	        
	        if (goal.getCurrentBalance().compareTo(targetAmount) >= 0) {
	        	goal.setSavingsPerDay(BigDecimal.valueOf(0));
	        	goal.setSavingsPerMonth(BigDecimal.valueOf(0));
	        	goal.setSavingsPerWeek(BigDecimal.valueOf(0));
	        }
	        else {
				BigDecimal initialBalance = goal.getInitialBalance() != null ? goal.getInitialBalance() : BigDecimal.valueOf(0);
				BigDecimal targetBalance = targetAmount.subtract(initialBalance);
		        
		        Months months = Months.monthsBetween(startDate, endDate);
		        log.debug("months between: {}", months.getMonths());
		        goal.setMonthsTillPayment(months.getMonths());
		        if (months.getMonths() > 0) {
					BigDecimal savingsPerMonth = targetBalance.floatValue() > 0 ?
							targetBalance.divide(BigDecimal.valueOf(months.getMonths()), 2, RoundingMode.HALF_UP) : BigDecimal.valueOf(0);
		        	log.debug("savingsPerMonth: {}", savingsPerMonth);;
		        	goal.setSavingsPerMonth(savingsPerMonth);
		        }
		        
		        Weeks weeks = Weeks.weeksBetween(startDate, endDate);
		        log.debug("Weeks between: {}", weeks.getWeeks());
		        goal.setWeeksTillPayment(weeks.getWeeks());
				BigDecimal savingsPerWeek = targetBalance.floatValue() > 0 ?
						targetBalance.divide(BigDecimal.valueOf(weeks.getWeeks()), 2, RoundingMode.HALF_UP) : BigDecimal.valueOf(0);
		        log.debug("savingsPerWeek: {}", savingsPerWeek);
		        goal.setSavingsPerWeek(savingsPerWeek);
		        
		        Days days = Days.daysBetween(startDate, endDate);
		        log.debug("days between: {}", days.getDays());
		        goal.setDaysTillPayment(days.getDays());
				BigDecimal savingsPerDay = targetBalance.floatValue() > 0 ?
						targetBalance.divide(BigDecimal.valueOf(days.getDays()), 2, RoundingMode.HALF_UP) : BigDecimal.valueOf(0);
		        log.debug("savingsPerDay: {}", savingsPerDay);
		        goal.setSavingsPerDay(savingsPerDay);
	        }
    	}
        
        return goal;
    }
	
	/**
	 * Retrieves all of the savings goals except the default goal which needs special handling.
	 *
	 * @return The list of Savings Goals
	 */
	public List<SavingsGoal> getAllSavingsGoalsButDefault() {
        log.info("Entered getAllSavingsGoalsButDefault()");
        
        List<SavingsGoal> goals = this.savingsGoalRepo.findAllByIsDefaultFalse(Sort.by(Sort.Direction.ASC, "goalName"));
        log.debug("Returing {} savingsGoals", goals != null ? goals.size() : 0);
        
        log.info("Exiting getAllSavingsGoalsButDefault()");
        return goals;
    }
	
	/**
	 * Attempts to retrieve the goal identified by the id.
	 *
	 * @param goalId The goal's unique identifier
	 * @return An Optional instance of the goal or Optional.empty
	 */
    public Optional<SavingsGoal> getSavingsGoalById(Integer goalId) {
    	
    	if (goalId == null) {
    		throw new InvalidRequestException("Goal id must be non-null.");
    	}
    	
        log.info("Entered getSavingsGoalById()");
        log.debug("Param: savingsGoalId: {}", goalId);
        
        log.info("Exiting getSavingsGoalById()");
        return this.savingsGoalRepo.findById(goalId);
    }
	
	/**
	 * Access to a list of all goals sorted by goal name.
	 *
	 * @return The sorted list of goals
	 */
	public List<SavingsGoal> getAllSavingsGoals() {
    	
    	return this.savingsGoalRepo.findAll(Sort.by(Sort.Direction.ASC, "goalName"));
    }
}
