package com.brenner.budgetmanager.transactions;

import com.brenner.budgetmanager.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST interface to expose transaction methods
 */
@RestController
@RequestMapping(path = "/api")
@Slf4j
public class TransactionsApi {

    @Autowired
    TransactionBusinessService service;
    
    /**
     * Access point to retrieve all transactions
     *
     * @return The list of transactions
     */
    @GetMapping(path = "/transactions")
    public List<Transaction> getAllTransactions() {
        return this.service.getAllTransactions();
    }
    
    /**
     * Access point to retrieve a single transaction. If the transaction is not found a 404 is emitted.
     *
     * @param transactionId Transaction unique identifier
     * @return The transaction associated with the transactionId.
     */
    @GetMapping(path="/transactions/{id}")
    public Transaction getTransaction(@PathVariable(name="id") Long transactionId) {
        Optional<Transaction> optionalTransaction = this.service.getTransaction(transactionId);
        if (optionalTransaction.isEmpty()) {
            throw new NotFoundException("Transaction with id " + transactionId + " does not exist.");
        }
        
        return optionalTransaction.get();
    }
    
    /**
     * Access point to save a new transaction.
     *
     * @param transaction Transaction data to save
     * @return The transaction returned from the business layer
     */
    @PostMapping(path="/transactions")
    public Transaction saveNewTransaction(@RequestBody Transaction transaction) {
        
        Transaction newTransaction = this.service.saveTransaction(transaction);
        return newTransaction;
    }
    
    /**
     * Access point to update a transaction. A 404 will be emitted if the transaction doesn't already exist.
     *
     * @param transactionId Unique identifier for the transaction
     * @param transaction The data to update
     * @return The object returned from the business layer.
     */
    @PutMapping(path="/transactions/{id}")
    public Transaction updateTransaction(@PathVariable(name="id")Long transactionId, @RequestBody Transaction transaction) {
        
        Optional<Transaction> optionalTransaction = this.service.getTransaction(transactionId);
        if (optionalTransaction.isEmpty()) {
            throw new NotFoundException("Transaction with id " + transactionId + " does not exist.");
        }
        Transaction existingTransaction = optionalTransaction.get();
        
        return this.service.saveTransaction(transaction);
    }
    
    /**
     * Access point for deleting a transaction. A 404 is emitted if the transaction doesn't already exist.
     *
     * @param transactionId The unique identifier of the transaction to delete.
     */
    @DeleteMapping(path="/transactions/{id}")
    public void deleteTransaction(@PathVariable(name="id") Long transactionId) {
        
        Optional<Transaction> optionalTransaction = this.service.getTransaction(transactionId);
        if (optionalTransaction.isEmpty()) {
            throw new NotFoundException("Transaction with id " + transactionId + " does not exist.");
        }
        Transaction existingTransaction = optionalTransaction.get();
        this.service.deleteTransaction(existingTransaction);
    }
}
