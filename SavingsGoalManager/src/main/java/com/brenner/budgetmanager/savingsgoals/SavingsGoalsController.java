package com.brenner.budgetmanager.savingsgoals;

import com.brenner.budgetmanager.deposit.Deposit;
import com.brenner.budgetmanager.deposit.DepositBusinessService;
import com.brenner.budgetmanager.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * MVC controller implementation to bridge the browser interface with the SavingsGoals business service.
 */
@Controller
public class SavingsGoalsController {

    private static final Logger log = LoggerFactory.getLogger(SavingsGoalsController.class);
    
    @Autowired
    SavingsGoalsBusinessService savingsGoalService;
    
    @Autowired
    DepositBusinessService depositsService;
    
    /**
     * Entry point for allocating a deposit with a set of goals. This method also retrieved the default goal and will
     * generate a 404 if it's not found.
     *
     * @param depositId The deposit unique identifier
     * @param model The model to contain the data for the view
     * @return The path to the allocate deposit template.
     */
    @RequestMapping("listGoalsAndDeposit")
    public String getGoalsAndDeposits(@RequestParam(name="depositId") Long depositId, Model model) {
    	
    	List<SavingsGoal> goals = this.savingsGoalService.getAllSavingsGoalsButDefault();
        
        Optional<SavingsGoal> optionalSavingsGoal = this.savingsGoalService.findDefaultGoal();
        if (optionalSavingsGoal.isEmpty()) {
            throw new NotFoundException("A default goal could not be identified.");
        }
        
    	SavingsGoal unplannedGoal = optionalSavingsGoal.get();
     
    	Optional<Deposit> optDeposit = this.depositsService.getDeposit(depositId);
        if (optDeposit.isEmpty()) {
            throw new NotFoundException("Deposit with id " + depositId + " does not exist.");
        }
    	
    	model.addAttribute("deposit", optDeposit.get());
    	model.addAttribute("savingsGoals", goals);
    	model.addAttribute("unplannedGoal", unplannedGoal);
    	
    	return "savingsgoals/associateDepositWithGoals";
    }
    
    /**
     * Method called when allocations are complete and need to be persisted.
     *
     * @param depositId The unique deposit the allocations are derived from.
     * @param savingsGoalIds The List of savings goal ids to allocate against
     * @param amountTowardsGoal The List of amounts to allocate
     * @return A redirect to the list of deposits not yet allocated
     */
    @RequestMapping("allocateDeposit")
    public String allocatedDepositToGoals(
    		@RequestParam(name="depositId") Long depositId,
    		@RequestParam(name="savingsGoalId") List<Integer> savingsGoalIds,
    		@RequestParam(name="amountTowardsGoal") List<BigDecimal> amountTowardsGoal) {
    	
    	this.savingsGoalService.allocateDepositToGoals(depositId, savingsGoalIds, amountTowardsGoal);
    	
    	return "redirect:getUnallocatedDeposits";
    }
    
    /**
     * Entry point to creating a new savings goal. This method also retrieves the defaul goal and will generate a 404
     * if it can't be found.
     *
     * @param savingsGoal A container for the model
     * @param model Model in the MVC
     * @return Path to the add savings goal template.
     */
    @RequestMapping("startAddSavingsGoalWorkflow")
    public String startAddSavingsGoalWorkflow(@ModelAttribute("savingsGoal") SavingsGoal savingsGoal, Model model) {
        log.info("Entered startAddSavingsGoalWorkflow()");
        
        List<SavingsGoal> goals = this.savingsGoalService.getAllSavingsGoalsButDefault();
        model.addAttribute("savingsGoals", goals);
        
        Optional<SavingsGoal> optionalSavingsGoal = this.savingsGoalService.findDefaultGoal();
        if (optionalSavingsGoal.isEmpty()) {
            throw new NotFoundException("A default savings goal could not be found.");
        }
        
        SavingsGoal defaultGoal = optionalSavingsGoal.get();
        model.addAttribute("defaultGoal", defaultGoal);
        
        log.info("Exiting startAddSavingsGoalWorkflow()");
        return "savingsgoals/addSavingsGoal";
    }
    
    /**
     * Method to persist a new goal
     *
     * @param savingsGoal The goal data to persist
     * @return A redirect to the start template for adding a savings goal
     */
    @RequestMapping("addSavingsGoal")
    public String addSavingsGoal(@ModelAttribute("savingsGoal") SavingsGoal savingsGoal) {
        log.info("Entered addSavingsGoal(): " + savingsGoal);
        
        this.savingsGoalService.addSavingsGoal(savingsGoal);
        
        log.info("Exiting addSavingsGoal()");
        return "redirect:startAddSavingsGoalWorkflow";
    }
    
    /**
     * Entry point for the edit goal process
     *
     * @param savingsGoalIdStr The goal's unique identifier
     * @param model The model for transmitted data to the view
     * @return The path to the edit goal template
     */
    @RequestMapping("editSavingsGoal")
    public String editSavingsGoal(
            @RequestParam(name="savingsGoalId", required=true) String savingsGoalIdStr, 
            Model model) {
        
        Optional<SavingsGoal> optionalSavingsGoal = this.savingsGoalService.getSavingsGoalById(Integer.valueOf(savingsGoalIdStr));
        if (optionalSavingsGoal.isEmpty()) {
            throw new NotFoundException("Savings goal with id " + savingsGoalIdStr + " does not exist.");
        }
        
        SavingsGoal goal = optionalSavingsGoal.get();
        model.addAttribute("savingsGoal", goal);
        
        return "savingsgoals/editSavingsGoal";
    }
    
    /**
     * Method to persist changes to a goal. If the goal cannot be found then a 404 is returned.
     *
     * @param savingsGoal The updated goal data
     * @return A redirect to the start of the add goal process
     */
    @RequestMapping("updateSavingsGoal")
    public String updateSavingsGoal(@ModelAttribute("savingsGoal") SavingsGoal savingsGoal) {
    
        Optional<SavingsGoal> optionalSavingsGoal = this.savingsGoalService.getSavingsGoalById(savingsGoal.getSavingsGoalId());
        if (optionalSavingsGoal.isEmpty()) {
            throw new NotFoundException("Savings goal with id " + savingsGoal.getSavingsGoalId() + " does not exist.");
        }
        
        this.savingsGoalService.updateSavingsGoal(savingsGoal);
        
        return "redirect:startAddSavingsGoalWorkflow";
    }
    
    /**
     * Entry ppint to delete a goal. If the goal to delete cannot be found then a 404 is returned.
     *
     * @param savingsGoalId The goal's unique identifier.
     * @return A redirect to the start of the add goal process
     */
    @RequestMapping("deleteSavingsGoal")
    public String deleteSavingsGoal(@RequestParam(name="savingsGoalId", required=true) Integer savingsGoalId) {
    
        Optional<SavingsGoal> optionalSavingsGoal = this.savingsGoalService.getSavingsGoalById(savingsGoalId);
        if (optionalSavingsGoal.isEmpty()) {
            throw new NotFoundException("Savings goal with id " + savingsGoalId + " does not exist.");
        }
        
        Optional<SavingsGoal> optDefaultGoal = this.savingsGoalService.findDefaultGoal();
        if (optDefaultGoal.isEmpty()) {
            throw new NotFoundException("A default savings goal could not be found.");
        }
    	
    	SavingsGoal goal = optionalSavingsGoal.get();
    	BigDecimal currentBalance = goal.getCurrentBalance();
    	if (currentBalance.floatValue() > 0) {
    		SavingsGoal defaultGoal = optDefaultGoal.get();
    		defaultGoal.setCurrentBalance(defaultGoal.getCurrentBalance().add(currentBalance));
    		this.savingsGoalService.updateSavingsGoal(defaultGoal);
    	}
    	
    	this.savingsGoalService.deleteSavingsGoal(savingsGoalId);
    	
    	return "redirect:startAddSavingsGoalWorkflow";
    }
    
}
