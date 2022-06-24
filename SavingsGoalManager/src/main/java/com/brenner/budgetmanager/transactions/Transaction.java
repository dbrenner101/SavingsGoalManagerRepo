/**
 * 
 */
package com.brenner.budgetmanager.transactions;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;

import com.brenner.budgetmanager.savingsgoals.SavingsGoal;

/**
 *
 * @author dbrenner
 * 
 */
@Entity
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;
	
	@ManyToOne
	private SavingsGoal fromGoal;
	
	@ManyToOne
	private SavingsGoal toGoal;
	
	private Float amount;
	
	private String notes;

	/**
	 * 
	 */
	public Transaction() {}
	
	public Transaction(Long transactionId, Date date, SavingsGoal fromGoal, SavingsGoal toGoal, Float amount) {
		this.transactionId = transactionId;
		this.date = date;
		this.fromGoal = fromGoal;
		this.toGoal = toGoal;
		this.amount = amount;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public SavingsGoal getFromGoal() {
		return fromGoal;
	}

	public void setFromGoal(SavingsGoal fromGoal) {
		this.fromGoal = fromGoal;
	}

	public SavingsGoal getToGoal() {
		return toGoal;
	}

	public void setToGoal(SavingsGoal toGoal) {
		this.toGoal = toGoal;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", date=" + date + ", fromGoal=" + fromGoal + ", toGoal="
				+ toGoal + ", amount=" + amount + ", notes=" + notes + "]";
	}

}
