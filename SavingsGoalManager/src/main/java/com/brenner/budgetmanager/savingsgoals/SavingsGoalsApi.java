/**
 * 
 */
package com.brenner.budgetmanager.savingsgoals;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author dbrenner
 * 
 */
@RestController
@Slf4j
public class SavingsGoalsApi {

	@Autowired
	SavingsGoalRepository savingsGoalRepo;
	
	@PutMapping(path="/savingsGoal/{id}")
	public void updateSavingsGoal(@PathVariable Integer id, @RequestParam(name = "amount", required=true) Float amount) {
		
		log.debug("Call to update SavingsGoal: " + amount);
		
		Optional<SavingsGoal> optGoal = this.savingsGoalRepo.findById(id);
		if (! optGoal.isEmpty()) {
			SavingsGoal sg = optGoal.get();
			sg.setCurrentBalance(sg.getCurrentBalance() + amount);
			this.savingsGoalRepo.save(sg);
		}
	}

}
