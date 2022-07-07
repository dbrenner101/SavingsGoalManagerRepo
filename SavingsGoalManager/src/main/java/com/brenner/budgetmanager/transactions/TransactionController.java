/**
 * 
 */
package com.brenner.budgetmanager.transactions;

import com.brenner.budgetmanager.exception.NotFoundException;
import com.brenner.budgetmanager.savingsgoals.SavingsGoal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Controller for transactions in the MVC pattern.
 *
 * @author dbrenner
 */
@Controller
public class TransactionController {

	@Autowired
	TransactionBusinessService transactionService;
	
	/**
	 * Entry point to manage transactions. Puts a list of savings goals and transacations into the model
	 *
	 * @param model Object containing the response data
	 * @param transaction An empty Transaction object used to map the form fields
	 * @return The template path to display and manage transactions
	 */
	@RequestMapping(path="/manageTransactions")
	public String manageTransactions(Model model, @ModelAttribute("transaction") Transaction transaction) {
		
		List<SavingsGoal> allSavingsGoals = this.transactionService.getAllSavingsGoals();
		model.addAttribute("savingsGoals", allSavingsGoals);
		
		List<Transaction> allTransactions = this.transactionService.getAllTransactions();
		model.addAttribute("transactions", allTransactions);
		
		return "transaction/addTransaction";
	}
	
	/**
	 * Entry point for adding a new transaction.
	 *
	 * @param model The container for model data
	 * @param transaction The transaction to persist
	 * @return A redirect to the manage transaction path
	 */
	@RequestMapping(path="/addTransaction")
	public String addTransaction(Model model, @ModelAttribute("transaction") Transaction transaction) {
		
		this.transactionService.saveTransaction(transaction);
		
		return "redirect:manageTransactions";
	}
	
	/**
	 * Entry point to delete a transaction. A 404 is emitted if the transaction to delete does not exist.
	 *
	 * @param transactionId The unique identifier of the transaction to delete
	 * @return A redirect to the manage transaction path
	 */
	@RequestMapping(path="/deleteTransaction")
	public String deleteTransaction(@RequestParam(name="transactionId", required=true) Long transactionId) {
		
		Optional<Transaction> optionalTransaction = this.transactionService.getTransaction(transactionId);
		if (optionalTransaction.isEmpty()) {
			throw new NotFoundException("Transaction with id " + transactionId + " does not exist.");
			
		}
		Transaction transaction =  optionalTransaction.get();
		
		this.transactionService.deleteTransaction(transaction);
		
		return "redirect:/manageTransactions";
	}
	
	

}
