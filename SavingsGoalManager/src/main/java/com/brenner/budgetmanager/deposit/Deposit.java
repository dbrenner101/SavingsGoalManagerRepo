/**
 * 
 */
package com.brenner.budgetmanager.deposit;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Class represents a total sum of money to be applied towards one or more goals.
 *
 * @author dbrenner
 * 
 */
@Entity
@Table(name="deposits")
public class Deposit {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long depositId;
	
	/** totale deposit amount */
	private BigDecimal amount;
	
	/** Date of the deposit */
	@DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"MM/dd/yyyy", "yy-MM-dd"})
	private Date date;
	
	/** Flag to identify of the deposit has been allocated or not */
	private boolean allocated;

	/** Default constructor. */
	public Deposit() {}
	
	/**
	 * Constructor to populate fields.
	 *
	 * @param depositId
	 * @param amount
	 * @param date
	 * @param allocated
	 */
	public Deposit(Long depositId, BigDecimal amount, Date date, Boolean allocated) {
		this.depositId = depositId;
		this.amount = amount;
		this.date = date;
		this.allocated = allocated;
	}

	public Long getDepositId() {
		return depositId;
	}

	public void setDepositId(Long depositId) {
		this.depositId = depositId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean getAllocated() {
		return allocated;
	}

	public void setAllocated(boolean allocated) {
		this.allocated = allocated;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Deposit deposit = (Deposit) o;
		
		if (allocated != deposit.allocated) return false;
		if (depositId != null ? !depositId.equals(deposit.depositId) : deposit.depositId != null) return false;
		if (amount != null ? !amount.equals(deposit.amount) : deposit.amount != null) return false;
		return date != null ? date.equals(deposit.date) : deposit.date == null;
	}
	
	@Override
	public int hashCode() {
		int result = depositId != null ? depositId.hashCode() : 0;
		result = 31 * result + (amount != null ? amount.hashCode() : 0);
		result = 31 * result + (date != null ? date.hashCode() : 0);
		result = 31 * result + (allocated ? 1 : 0);
		return result;
	}
	
	@Override
	public String toString() {
		return "Deposit [depositId=" + depositId + ", amount=" + amount + ", date=" + date + ", allocated=" + allocated
				+ "]";
	}

}
