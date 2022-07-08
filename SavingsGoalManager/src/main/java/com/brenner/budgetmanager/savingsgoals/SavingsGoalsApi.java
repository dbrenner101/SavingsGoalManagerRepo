/**
 * 
 */
package com.brenner.budgetmanager.savingsgoals;

import com.brenner.budgetmanager.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * API to the Savings Goal business service
 *
 * @author dbrenner
 * 
 */
@RestController
@RequestMapping(path = "/api")
@Slf4j
public class SavingsGoalsApi {

	@Autowired
	SavingsGoalsBusinessService service;
	
	/**
	 * Access to the list of savings goals.
	 *
	 * @return An unordered list of all savings goals.
	 */
	@GetMapping(path = "/savingsgoals")
	public List<SavingsGoal> getAllSavingsGoals() {
		List<SavingsGoal> goals = this.service.getAllSavingsGoals();
		return goals;
	}
	
	/**
	 * Access to retrieve the specific default goal. A 404 is generated if there is no default goal.
	 *
	 * @return The goal marked as a default.
	 */
	@GetMapping(path="/savingsgoals/defaultgoal")
	public SavingsGoal getDefaultGoal() {
		Optional<SavingsGoal> optGoal = this.service.findDefaultGoal();
		if (optGoal.isEmpty()) {
			throw new NotFoundException("Default goal does not exist.");
		}
		
		return optGoal.get();
	}
	
	/**
	 * Access to save a new goal
	 *
	 * @param savingsGoal The goal data to persist
	 * @return The persisted object including unique identifier
	 */
	@PostMapping(path="/savingsgoals")
	public SavingsGoal addSavingsGoal(@RequestBody SavingsGoal savingsGoal) {
		if (savingsGoal.getCurrentBalance() == null) {
			savingsGoal.setCurrentBalance(BigDecimal.valueOf(0));
		}
		SavingsGoal newGoal = this.service.addSavingsGoal(savingsGoal);
		return newGoal;
	}
	
	/**
	 * Access to allocate amounts to goals associated with a specific deposit.
	 *
	 * @param savingsGoalDepositAllocations The list of allocations
	 */
	@PutMapping(path="/savingsgoals/allocateDeposit")
	public void allocateDepositToGoals(@RequestBody List<SavingsGoalDepositAllocation> savingsGoalDepositAllocations) {
		this.service.allocateDepositToGoals(savingsGoalDepositAllocations);
	}
	
	/**
	 * Access to update a goal. If the goal to update doesn't exist a 404 will be generated.
	 *
	 * @param id The goal's unique identifier
	 * @param savingsGoal The goal data to persist
	 * @return The goal after persistence
	 */
	@PutMapping(path="/savingsgoals/{id}")
	public SavingsGoal updateSavingsGoal(@PathVariable Integer id, @RequestBody SavingsGoal savingsGoal) {
		
		log.debug("Call to update SavingsGoal: " + savingsGoal);
		
		Optional<SavingsGoal> optionalSavingsGoal = this.service.getSavingsGoalById(id);
		if (optionalSavingsGoal.isEmpty()) {
			throw new NotFoundException("Savings goal with id " + id + " does not exist.");
		}
		
		SavingsGoal sg = optionalSavingsGoal.get();
		sg.setCurrentBalance(savingsGoal.getCurrentBalance());
		sg.setGoalName(savingsGoal.getGoalName());
		sg.setInitialBalance(savingsGoal.getInitialBalance());
		sg.setTargetAmount(savingsGoal.getTargetAmount());
		sg.setSavingsStartDate(savingsGoal.getSavingsStartDate());
		sg.setSavingsEndDate(savingsGoal.getSavingsEndDate());
		return this.service.updateSavingsGoal(sg);
	}
	
	/**
	 * Access to delete a specific goal. A 404 will be generated if the goal is not found.
	 *
	 * @param id The goal's unique identifier.
	 */
	@DeleteMapping(path="/savingsgoals/{id}")
	public void deleteSavingsGoal(@PathVariable Integer id) {
		
		Optional<SavingsGoal> optionalSavingsGoal = this.service.getSavingsGoalById(id);
		if (optionalSavingsGoal.isEmpty()){
			throw new NotFoundException("Savings goal with id " + id + " does not exist.");
		}
		
		SavingsGoal savingsGoal = optionalSavingsGoal.get();
		this.service.deleteSavingsGoal(id);
	}

}
