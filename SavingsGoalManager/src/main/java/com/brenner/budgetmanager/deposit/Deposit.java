/**
 * 
 */
package com.brenner.budgetmanager.deposit;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

/**
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
	
	private Float amount;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = {"MM/dd/yyyy", "yy-MM-dd"})
	private Date date;
	
	private boolean allocated;

	/**
	 * 
	 */
	public Deposit() {}
	
	public Deposit(Long depositId, Float amount, Date date, Boolean allocated) {
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

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
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
	public String toString() {
		return "Deposit [depositId=" + depositId + ", amount=" + amount + ", date=" + date + ", allocated=" + allocated
				+ "]";
	}

}
