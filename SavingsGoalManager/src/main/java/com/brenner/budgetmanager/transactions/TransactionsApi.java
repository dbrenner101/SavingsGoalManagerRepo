package com.brenner.budgetmanager.transactions;

import com.brenner.budgetmanager.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@Slf4j
public class TransactionsApi {

    @Autowired
    TransactionBusinessService service;
    
    @GetMapping(path = "/transactions")
    public List<Transaction> getAllTransactions() {
        return this.service.getAllTransactions();
    }
    
    @GetMapping(path="/transactions/{id}")
    public Transaction getTransaction(@PathVariable(name="id") Long transactionId) {
        return this.service.getTransaction(transactionId);
    }
    
    @PostMapping(path="/transactions")
    public Transaction saveNewTransaction(@RequestBody Transaction transaction) {
        
        Transaction newTransaction = this.service.saveTransaction(transaction);
        return newTransaction;
    }
    
    @PutMapping(path="/transactions/{id}")
    public Transaction updateTransaction(@PathVariable(name="id")Long transactionId, @RequestBody Transaction transaction) {
        
        Transaction existingTransaction = this.service.getTransaction(transactionId);
        if (existingTransaction == null) {
            throw new NotFoundException("Transaction with id " + transactionId + " does not exist");
        }
        return this.service.saveTransaction(transaction);
    }
    
    @DeleteMapping(path="/transactions/{id}")
    public void deleteTransaction(@PathVariable(name="id") Long transactionId) {
        Transaction existingTransaction = this.service.getTransaction(transactionId);
        if (existingTransaction == null) {
            throw new NotFoundException("Transaction with id " + transactionId + " does not exist");
        }
        this.service.deleteTransaction(existingTransaction);
    }
}
