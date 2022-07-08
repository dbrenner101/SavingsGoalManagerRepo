/**
 * 
 */
package com.brenner.budgetmanager.transactions;

import com.brenner.budgetmanager.savingsgoals.SavingsGoal;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 *Entity object that represents a transaction. A transaction is the decrementing or transferring of funds from one
 * savings goal to another.
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
	
	private BigDecimal amount;
	
	private String notes;
	
	private boolean applied;

	/**
	 * 
	 */
	public Transaction() {}
	
	public Transaction(Long transactionId, Date date, SavingsGoal fromGoal, SavingsGoal toGoal, BigDecimal amount) {
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public boolean getApplied() {
		return applied;
	}
	
	public void setApplied(boolean applied) {
		this.applied = applied;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Transaction that = (Transaction) o;
		
		if (applied != that.applied) return false;
		if (transactionId != null ? !transactionId.equals(that.transactionId) : that.transactionId != null)
			return false;
		if (date != null ? !date.equals(that.date) : that.date != null) return false;
		if (fromGoal != null ? !fromGoal.equals(that.fromGoal) : that.fromGoal != null) return false;
		if (toGoal != null ? !toGoal.equals(that.toGoal) : that.toGoal != null) return false;
		if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
		return notes != null ? notes.equals(that.notes) : that.notes == null;
	}
	
	@Override
	public int hashCode() {
		int result = transactionId != null ? transactionId.hashCode() : 0;
		result = 31 * result + (date != null ? date.hashCode() : 0);
		result = 31 * result + (fromGoal != null ? fromGoal.hashCode() : 0);
		result = 31 * result + (toGoal != null ? toGoal.hashCode() : 0);
		result = 31 * result + (amount != null ? amount.hashCode() : 0);
		result = 31 * result + (notes != null ? notes.hashCode() : 0);
		result = 31 * result + (applied ? 1 : 0);
		return result;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Transaction{");
		sb.append("transactionId=").append(transactionId);
		sb.append(", date=").append(date);
		sb.append(", fromGoal=").append(fromGoal);
		sb.append(", toGoal=").append(toGoal);
		sb.append(", amount=").append(amount);
		sb.append(", notes='").append(notes).append('\'');
		sb.append(", applied=").append(applied);
		sb.append('}');
		return sb.toString();
	}
}
