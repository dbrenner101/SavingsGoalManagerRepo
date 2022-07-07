/**
 * 
 */
package com.brenner.budgetmanager.deposit;

import com.brenner.budgetmanager.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class to handle all "business" logic for managing deposits.
 *
 * Deposits are either allocated (applied towards goals) or unallocated (waiting to be applied).
 *
 * Deposits can only be applied once.
 *
 * @author dbrenner
 */
@Service
@Slf4j
public class DepositBusinessService {

	/** JPA interface to deposits */
	@Autowired
	DepositRepository depositRepo;
	
	/**
	 * Saves the deposit to the repository.
	 *
	 * @param deposit Deposit data to save
	 * @return The saved entity with unique identifier
	 */
	public Deposit saveDeposit(Deposit deposit) {
		
		if (deposit == null) {
			throw new InvalidRequestException("Deposit is a required parameter.");
		}
		
		log.debug("Saving Deposit: " + deposit);
		
		return this.depositRepo.save(deposit);
	}
	
	/**
	 * Retrieves the list of deposits that have not been allocated towards goals.
	 *
	 * @return A List of Deposit objects.
	 */
	@Transactional()
	public List<Deposit> getUnallocatedDeposits() {
		
		log.debug("Getting unallocated deposits");
		
		return this.depositRepo.findByAllocated(false);
	}
	
	/**
	 * Retrieves a single Deposit object based on the unique identifier.
	 *
	 * @param depositId The Deposit unique identifier
	 * @return An Optional<Deposit> instance
	 */
	public Optional<Deposit> getDeposit(Long depositId) {
		
		if (depositId == null) {
			throw new InvalidRequestException("Deposit id is required.");
		}
		
		log.debug("Getting Deposit for id: " + depositId);
		
		return this.depositRepo.findById(depositId);
	}
	
	/**
	 * Deletes a specific deposit after verifying that it exists.
	 *
	 * @param depositId Deposit unique identifier
	 */
	public void deleteDeposit(Long depositId) {
		
		if (depositId == null) {
			throw new InvalidRequestException("Deposit id is required.");
		}
		
		log.debug("Getting Deposit for id: " + depositId);
		
		Optional<Deposit> optDeposit = this.depositRepo.findById(depositId);
		
		if (optDeposit.isPresent()) {
			this.depositRepo.delete(optDeposit.get());
		}
	}

}
