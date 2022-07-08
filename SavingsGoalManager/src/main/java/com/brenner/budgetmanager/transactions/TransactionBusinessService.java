/**
 * 
 */
package com.brenner.budgetmanager.transactions;

import com.brenner.budgetmanager.exception.InvalidRequestException;
import com.brenner.budgetmanager.savingsgoals.SavingsGoal;
import com.brenner.budgetmanager.savingsgoals.SavingsGoalsBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Business service that acts as a bridge between controllers and persistence
 *
 * @author dbrenner
 * 
 */
@Service
@Slf4j
public class TransactionBusinessService {
	
	@Autowired
	TransactionRepository transactionRepo;
	
	@Autowired
	SavingsGoalsBusinessService savingsGoalService;
	
	/**
	 * Method to prepare a transaction for persistence.
	 *
	 * @param transaction The transaction to save
	 * @return The object after persistence including the assigned unique identifier.
	 */
	public Transaction saveTransaction(Transaction transaction) {
		
		if (transaction == null || transaction.getAmount() == null 
				|| transaction.getFromGoal() == null) {
			throw new InvalidRequestException("Transaction must have non-null properties (amount, from goal).");
		}
		
		log.debug("Saving transaction: " + transaction);
		
		SavingsGoal fromGoal = transaction.getFromGoal();
		log.debug("fromGoal: " + fromGoal);
		
		SavingsGoal toGoal = transaction.getToGoal();
		log.debug("toGoal: " + toGoal);
		
		BigDecimal amount = transaction.getAmount();
		log.debug("amount: " + amount);
		
		if (toGoal == null || toGoal.getSavingsGoalId().equals(fromGoal.getSavingsGoalId())) {
			fromGoal.setCurrentBalance(fromGoal.getCurrentBalance().subtract(amount));
			this.savingsGoalService.updateSavingsGoal(fromGoal);
		}
		else {
			fromGoal.setCurrentBalance(fromGoal.getCurrentBalance().subtract(amount));
			toGoal.setCurrentBalance(toGoal.getCurrentBalance().add(amount));
			this.savingsGoalService.updateSavingsGoal(fromGoal);
			this.savingsGoalService.updateSavingsGoal(toGoal);
		}
		
		return this.transactionRepo.save(transaction);
	}
	
	/**
	 * Method to prepare a transaction for deletion. The values applied during the original transaction persistence are
	 * reversed.
	 *
	 * @param transaction The object to delete
	 */
	public void deleteTransaction(Transaction transaction) {
		
		if (transaction == null || transaction.getAmount() == null || transaction.getFromGoal() == null) {
			throw new InvalidRequestException("Transaction must have non-null properties (amount, from goal).");
		}
		
		log.debug("Saving transaction: " + transaction);
		
		SavingsGoal fromGoal = transaction.getFromGoal();
		log.debug("fromGoal: " + fromGoal);
		
		SavingsGoal toGoal = transaction.getToGoal();
		log.debug("toGoal: " + toGoal);
		
		BigDecimal amount = transaction.getAmount();
		log.debug("amount: " + amount);
		
		if (toGoal == null || toGoal.getSavingsGoalId().equals(fromGoal.getSavingsGoalId())) {
			fromGoal.setCurrentBalance(fromGoal.getCurrentBalance().add(amount));
			this.savingsGoalService.updateSavingsGoal(fromGoal);
		}
		else {
			fromGoal.setCurrentBalance(fromGoal.getCurrentBalance().add(amount));
			toGoal.setCurrentBalance(toGoal.getCurrentBalance().subtract(amount));
			this.savingsGoalService.updateSavingsGoal(fromGoal);
			this.savingsGoalService.updateSavingsGoal(toGoal);
		}
		
		this.transactionRepo.delete(transaction);
	}
	
	/**
	 * Method to retrieve the list of transactions, sorted by date, descending
	 *
	 * @return A sorted list of transactions
	 */
	public List<Transaction> getAllTransactions() {
		
		return this.transactionRepo.findAll(Sort.by(Sort.Direction.DESC, "date"));
	}
	
	/**
	 * Method to retrieve a specific transaction.
	 *
	 * @param transactionId The unique identifier for the transaction to retrieve.
	 * @return An Optional wrapper around the transaction or Optional.empty if not found.
	 */
	public Optional<Transaction> getTransaction(Long transactionId) {
		
		return this.transactionRepo.findById(transactionId);
	}
	
	/**
	 * Access to the savings goal service and method that return a list of the goals.
	 *
	 * @return The list of SavingsGoals
	 */
	public List<SavingsGoal> getAllSavingsGoals() {
		return this.savingsGoalService.getAllSavingsGoals();
	}

}
