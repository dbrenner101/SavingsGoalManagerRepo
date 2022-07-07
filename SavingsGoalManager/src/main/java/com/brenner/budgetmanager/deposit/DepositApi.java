package com.brenner.budgetmanager.deposit;

import com.brenner.budgetmanager.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * RESTful API to the deposit business service.
 */
@RestController
@RequestMapping(path = "/api")
@Slf4j
public class DepositApi {
    
    @Autowired
    DepositBusinessService service;
    
    /**
     * API interface to retrieve all (inallocated) deposits. A full list of deposits is not exposed.
     *
     * @return The List<Deposit> that have not been allocated.
     */
    @GetMapping(path="/deposits")
    public List<Deposit> getAllDeposits() {
        return service.getUnallocatedDeposits();
    }
    
    /**
     * API interface to retrieve a specific deposit instance. Throws a 404 if the instance does not exist.
     *
     * @param depositId Unique identifier for the Deposit instance.
     * @return The Deposit object associated with the indentifier.
     */
    @GetMapping(path="/deposits/{depositId}")
    public Deposit getDepositById(@PathVariable(name="depositId", required = true) Long depositId) {
        Optional<Deposit> optDeposit = this.service.getDeposit(depositId);
        
        if (optDeposit.isEmpty()) {
            throw new NotFoundException("Deposit with id " + depositId + " was not found.");
        }
        
        return optDeposit.get();
    }
    
    /**
     * API interface to persist a new Deposit instance.
     *
     * @param deposit Data object to persist
     * @return The persisted object including its unique identifier.
     */
    @PostMapping(path="/deposits")
    public Deposit addNewDeposit(@RequestBody Deposit deposit) {
        Deposit d = this.service.saveDeposit(deposit);
        return d;
    }
    
    /**
     * API interface to update a deposit instance. Returns a 404 if the instance cannot be found.
     *
     * @param depositId Deposit instance unique identifier.
     * @param deposit The Deposit data to persist
     * @return The deposit after persistence.
     */
    @PutMapping(path="/deposits/{depositId}")
    public Deposit updateDeposit(@PathVariable(name="depositId", required = true) Long depositId, @RequestBody Deposit deposit) {
        
        Optional<Deposit> optDeposit = this.service.getDeposit(depositId);
        if (optDeposit.isEmpty()) {
            throw new NotFoundException("Deposit with id " + depositId + " was not found.");
        }
        
        return this.service.saveDeposit(deposit);
    }
    
    /**
     * API interface to delete a Deposit instance. Returns a 404 if the instance cannot be found.
     *
     * @param depositId Unique identifier for the Deposit instance.
     */
    @DeleteMapping(path="/deposits/{depositId}")
    public void deleteDeposit(@PathVariable(name="depositId") Long depositId) {
        
        Optional<Deposit> optDeposit = this.service.getDeposit(depositId);
    
        if (optDeposit.isEmpty()) {
            throw new NotFoundException("Deposit with id " + depositId + " was not found.");
        }
        
        this.service.deleteDeposit(depositId);
    }
}
