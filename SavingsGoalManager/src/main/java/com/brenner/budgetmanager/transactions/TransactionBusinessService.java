/**
 * 
 */
package com.brenner.budgetmanager.transactions;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.brenner.budgetmanager.exception.InvalidRequestException;
import com.brenner.budgetmanager.savingsgoals.SavingsGoal;
import com.brenner.budgetmanager.savingsgoals.SavingsGoalsBusinessService;

import lombok.extern.slf4j.Slf4j;

/**
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
		
		Float amount = transaction.getAmount();
		log.debug("amount: " + amount);
		
		if (toGoal == null || toGoal.getSavingsGoalId().equals(fromGoal.getSavingsGoalId())) {
			fromGoal.setCurrentBalance(fromGoal.getCurrentBalance() - amount);
			this.savingsGoalService.updateSavingsGoal(fromGoal);
		}
		else {
			fromGoal.setCurrentBalance(fromGoal.getCurrentBalance() - amount);
			toGoal.setCurrentBalance(toGoal.getCurrentBalance() + amount);
			this.savingsGoalService.updateSavingsGoal(fromGoal);
			this.savingsGoalService.updateSavingsGoal(toGoal);
		}
		
		return this.transactionRepo.save(transaction);
	}
	
	public void deleteTransaction(Transaction transaction) {
		
		if (transaction == null || transaction.getAmount() == null || transaction.getFromGoal() == null) {
			throw new InvalidRequestException("Transaction must have non-null properties (amount, from goal).");
		}
		
		log.debug("Saving transaction: " + transaction);
		
		SavingsGoal fromGoal = transaction.getFromGoal();
		log.debug("fromGoal: " + fromGoal);
		
		SavingsGoal toGoal = transaction.getToGoal();
		log.debug("toGoal: " + toGoal);
		
		Float amount = transaction.getAmount();
		log.debug("amount: " + amount);
		
		if (toGoal == null || toGoal.getSavingsGoalId().equals(fromGoal.getSavingsGoalId())) {
			fromGoal.setCurrentBalance(fromGoal.getCurrentBalance() + amount);
			this.savingsGoalService.updateSavingsGoal(fromGoal);
		}
		else {
			fromGoal.setCurrentBalance(fromGoal.getCurrentBalance() + amount);
			toGoal.setCurrentBalance(toGoal.getCurrentBalance() - amount);
			this.savingsGoalService.updateSavingsGoal(fromGoal);
			this.savingsGoalService.updateSavingsGoal(toGoal);
		}
		
		this.transactionRepo.delete(transaction);
	}
	
	public List<Transaction> getAllTransactions() {
		
		return this.transactionRepo.findAll(Sort.by(Sort.Direction.DESC, "date"));
	}
	
	public Transaction getTransaction(Long transactionId) {
		
		Optional<Transaction> optTrans = this.transactionRepo.findById(transactionId);
		
		return optTrans.get();
	}
	
	public List<SavingsGoal> getAllSavingsGoals() {
		return this.savingsGoalService.getAllSavingsGoals();
	}

}
