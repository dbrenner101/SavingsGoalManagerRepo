package com.brenner.budgetmanager.savingsgoals;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.brenner.budgetmanager.deposit.Deposit;
import com.brenner.budgetmanager.deposit.DepositBusinessService;

@Controller
public class SavingsGoalsController {

    private static final Logger log = LoggerFactory.getLogger(SavingsGoalsController.class);
    
    @Autowired
    SavingsGoalsBusinessService savingsGoalService;
    
    @Autowired
    DepositBusinessService depositsService;
    
    @RequestMapping("listGoalsAndDeposit")
    public String getGoalsAndDeposits(@RequestParam(name="depositId", required=true) Long depositId, Model model) {
    	
    	List<SavingsGoal> goals = this.savingsGoalService.getAllSavingsGoalsButDefault();
    	SavingsGoal unplannedGoal = this.savingsGoalService.findDefaultGoal();
    	Deposit deposit = this.depositsService.getDeposit(depositId);
    	
    	model.addAttribute("deposit", deposit);
    	model.addAttribute("savingsGoals", goals);
    	model.addAttribute("unplannedGoal", unplannedGoal);
    	
    	return "savingsgoals/associateDepositWithGoals";
    }
    
    @RequestMapping("allocateDeposit")
    public String allocatedDepositToGoals(
    		@RequestParam(name="depositId", required=true) Long depositId, 
    		@RequestParam(name="savingsGoalId", required=true) List<Integer> savingsGoalIds,
    		@RequestParam(name="amountTowardsGoal", required=true) List<Float> amountTowardsGoal) {
    	
    	this.savingsGoalService.allocateDepositToGoals(depositId, savingsGoalIds, amountTowardsGoal);
    	
    	return "redirect:getUnallocatedDeposits";
    }
    
    @RequestMapping("startAddSavingsGoalWorkflow")
    public String startAddSavingsGoalWorkflow(@ModelAttribute("savingsGoal") SavingsGoal savingsGoal, Model model) {
        log.info("Entered startAddSavingsGoalWorkflow()");
        
        List<SavingsGoal> goals = this.savingsGoalService.getAllSavingsGoalsButDefault();
        model.addAttribute("savingsGoals", goals);
        
        SavingsGoal defaultGoal = this.savingsGoalService.findDefaultGoal();
        model.addAttribute("defaultGoal", defaultGoal);
        
        log.info("Exiting startAddSavingsGoalWorkflow()");
        return "savingsgoals/addSavingsGoal";
    }
    
    @RequestMapping("addSavingsGoal")
    public String addSavingsGoal(@ModelAttribute("savingsGoal") SavingsGoal savingsGoal) {
        log.info("Entered addSavingsGoal(): " + savingsGoal);
        
        this.savingsGoalService.addSavingsGoal(savingsGoal);
        
        log.info("Exiting addSavingsGoal()");
        return "redirect:startAddSavingsGoalWorkflow";
    }
    
    @RequestMapping("editSavingsGoal")
    public String editSavingsGoal(
            @RequestParam(name="savingsGoalId", required=true) String savingsGoalIdStr, 
            Model model) {
        
        SavingsGoal goal = this.savingsGoalService.getSavingsGoalById(Integer.valueOf(savingsGoalIdStr));
        model.addAttribute("savingsGoal", goal);
        
        return "savingsgoals/editSavingsGoal";
    }
    
    @RequestMapping("updateSavingsGoal")
    public String updateSavingsGoal(@ModelAttribute("savingsGoal") SavingsGoal savingsGoal) {
        
        SavingsGoal goal = new SavingsGoal();
        goal.setGoalName(savingsGoal.getGoalName());
        goal.setSavingsGoalId(Integer.valueOf(savingsGoal.getSavingsGoalId()));
        goal.setSavingsStartDate(savingsGoal.getSavingsStartDate());
        goal.setSavingsEndDate(savingsGoal.getSavingsEndDate());
        goal.setTargetAmount(savingsGoal.getTargetAmount());
        goal.setInitialBalance(savingsGoal.getInitialBalance());
        goal.setCurrentBalance(savingsGoal.getCurrentBalance());
        goal.setNotes(savingsGoal.getNotes());
        
        this.savingsGoalService.updateSavingsGoal(goal);
        
        return "redirect:startAddSavingsGoalWorkflow";
    }
    
    @RequestMapping("deleteSavingsGoal")
    public String deleteSavingsGoal(@RequestParam(name="savingsGoalId", required=true) Integer savingsGoalId) {
    	
    	SavingsGoal goal = this.savingsGoalService.getSavingsGoalById(savingsGoalId);
    	Float currentBalance = goal.getCurrentBalance();
    	if (currentBalance > 0) {
    		SavingsGoal defaultGoal = this.savingsGoalService.findDefaultGoal();
    		defaultGoal.setCurrentBalance(defaultGoal.getCurrentBalance() + currentBalance);
    		this.savingsGoalService.updateSavingsGoal(defaultGoal);
    	}
    	
    	this.savingsGoalService.deleteSavingsGoal(savingsGoalId);
    	
    	return "redirect:startAddSavingsGoalWorkflow";
    }
    
}
