/**
 * 
 */
package com.brenner.budgetmanager.deposit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author dbrenner
 * 
 */
public interface DepositRepository extends JpaRepository<Deposit, Long> {
	
	List<Deposit> findByAllocated(boolean allocated);

}
