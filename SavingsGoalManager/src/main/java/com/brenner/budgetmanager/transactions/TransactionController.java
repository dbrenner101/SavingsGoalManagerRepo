/**
 * 
 */
package com.brenner.budgetmanager.transactions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.brenner.budgetmanager.savingsgoals.SavingsGoal;

/**
 *
 * @author dbrenner
 * 
 */
@Controller
public class TransactionController {

	@Autowired
	TransactionBusinessService transactionService;
	
	@RequestMapping(path="/manageTransactions")
	public String manageTransactions(Model model, @ModelAttribute("transaction") Transaction transaction) {
		
		List<SavingsGoal> allSavingsGoals = this.transactionService.getAllSavingsGoals();
		model.addAttribute("savingsGoals", allSavingsGoals);
		
		List<Transaction> allTransactions = this.transactionService.getAllTransactions();
		model.addAttribute("transactions", allTransactions);
		
		return "transaction/addTransaction";
	}
	
	@RequestMapping(path="/addTransaction")
	public String addTransaction(Model model, @ModelAttribute("transaction") Transaction transaction) {
		
		this.transactionService.saveTransaction(transaction);
		
		return "redirect:manageTransactions";
	}
	
	@RequestMapping(path="/deleteTransaction")
	public String deleteTransaction(@RequestParam(name="transactionId", required=true) Long transactionId) {
		
		Transaction transaction = this.transactionService.getTransaction(transactionId);
		
		this.transactionService.deleteTransaction(transaction);
		
		return "redirect:/manageTransactions";
	}
	
	

}
