package com.brenner.budgetmanager.deposit;

import com.brenner.budgetmanager.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@Slf4j
public class DepositApi {
    
    @Autowired
    DepositBusinessService service;
    
    @GetMapping(path="/deposits")
    public List<Deposit> getAllDeposits() {
        return service.getUnallocatedDeposits();
    }
    
    @GetMapping(path="/deposits/{depositId}")
    public Deposit getDepositById(@PathVariable(name="depositId", required = true) Long depositId) {
        Deposit deposit = this.service.getDeposit(depositId);
        
        if (deposit == null) {
            throw new NotFoundException("Deposit with id " + deposit + " was not found.");
        }
        
        return deposit;
    }
    
    @PostMapping(path="/deposits")
    public Deposit addNewDeposit(@RequestBody Deposit deposit) {
        return this.service.saveDeposit(deposit);
    }
    
    @PutMapping(path="/deposits/{depositId}")
    public Deposit updateDeposit(@PathVariable(name="depositId", required = true) Long depositId, @RequestBody Deposit deposit) {
        
        Deposit existingDeposit = this.service.getDeposit(depositId);
        if (existingDeposit == null) {
            throw new NotFoundException("Deposit with id " + depositId + " was not found.");
        }
        
        return this.service.saveDeposit(deposit);
    }
    
    @DeleteMapping(path="/deposits/{depositId}")
    public void deleteDeposit(@PathVariable(name="depositId") Long depositId) {
        
        Deposit d = this.service.getDeposit(depositId);
    
        if (d == null) {
            throw new NotFoundException("Deposit with id " + depositId + " was not found.");
        }
        
        this.service.deleteDeposit(depositId);
    }
}
