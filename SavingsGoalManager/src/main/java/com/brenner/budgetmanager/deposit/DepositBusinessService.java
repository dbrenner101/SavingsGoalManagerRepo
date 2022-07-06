/**
 * 
 */
package com.brenner.budgetmanager.deposit;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brenner.budgetmanager.exception.InvalidRequestException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author dbrenner
 * 
 */
@Service
@Slf4j
public class DepositBusinessService {

	@Autowired
	DepositRepository depositRepo;
	
	public Deposit saveDeposit(Deposit deposit) {
		
		log.debug("Saving Deposit: " + deposit);
		
		return this.depositRepo.save(deposit);
	}
	
	@Transactional()
	public List<Deposit> getUnallocatedDeposits() {
		
		log.debug("Getting unallocated deposits");
		
		return this.depositRepo.findByAllocated(false);
	}
	
	public Deposit getDeposit(Long depositId) throws InvalidRequestException {
		
		if (depositId == null) {
			throw new InvalidRequestException("Deposit id is required.");
		}
		
		log.debug("Getting Depsoit for id: " + depositId);
		
		Optional<Deposit> optDeposit = this.depositRepo.findById(depositId);
		
		return optDeposit.get();
	}
	
	public void deleteDeposit(Long depositId) throws InvalidRequestException {
		
		if (depositId == null) {
			throw new InvalidRequestException("Deposit id is required.");
		}
		
		log.debug("Getting Depsoit for id: " + depositId);
		
		Optional<Deposit> optDeposit = this.depositRepo.findById(depositId);
		
		if (optDeposit.isPresent()) {
			this.depositRepo.delete(optDeposit.get());
		}
	}

}
