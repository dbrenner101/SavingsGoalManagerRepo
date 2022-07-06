/**
 * 
 */
package com.brenner.budgetmanager.savingsgoals;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

/**
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
	
	@GetMapping(path = "/savingsgoals")
	public List<SavingsGoal> getAllSavingsGoals() {
		List<SavingsGoal> goals = this.service.getAllSavingsGoals();
		return goals;
	}
	
	@PostMapping(path="/savingsgoals")
	public SavingsGoal addSavingsGoal(@RequestBody SavingsGoal savingsGoal) {
		if (savingsGoal.getCurrentBalance() == null) {
			savingsGoal.setCurrentBalance(0F);
		}
		SavingsGoal newGoal = this.service.addSavingsGoal(savingsGoal);
		return newGoal;
	}
	
	@PutMapping(path="/savingsgoals/{id}")
	public SavingsGoal updateSavingsGoal(@PathVariable Integer id, @RequestBody SavingsGoal savingsGoal) {
		
		log.debug("Call to update SavingsGoal: " + savingsGoal);
		
		SavingsGoal sg = this.service.getSavingsGoalById(id);
		sg.setCurrentBalance(savingsGoal.getCurrentBalance());
		sg.setGoalName(savingsGoal.getGoalName());
		sg.setInitialBalance(savingsGoal.getInitialBalance());
		sg.setTargetAmount(savingsGoal.getTargetAmount());
		sg.setSavingsStartDate(savingsGoal.getSavingsStartDate());
		sg.setSavingsEndDate(savingsGoal.getSavingsEndDate());
		return this.service.updateSavingsGoal(sg);
	}
	
	@DeleteMapping(path="/savingsgoals/{id}")
	public void deleteSavingsGoal(@PathVariable Integer id) {
		
		SavingsGoal savingsGoal = this.service.getSavingsGoalById(id);
		if (savingsGoal == null) {
			///
		}
		this.service.deleteSavingsGoal(id);
	}

}
