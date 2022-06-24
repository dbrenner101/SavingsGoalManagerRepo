/**
 * 
 */
package com.brenner.budgetmanager.transactions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
