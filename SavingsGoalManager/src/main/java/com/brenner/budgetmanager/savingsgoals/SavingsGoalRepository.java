/**
 * 
 */
package com.brenner.budgetmanager.savingsgoals;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Integer> {
	
	Optional<SavingsGoal> findByIsDefault(boolean isDefault);
	
	List<SavingsGoal> findAllByIsDefaultFalse(Sort sort);

}
