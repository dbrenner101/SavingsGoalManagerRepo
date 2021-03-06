package com.brenner.budgetmanager.transactions;

import com.brenner.budgetmanager.savingsgoals.SavingsGoal;
import com.brenner.budgetmanager.savingsgoals.SavingsGoalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        TransactionBusinessService.class,
        TransactionsApi.class,
        SavingsGoalRepository.class,
        ObjectMapper.class
})
@AutoConfigureMockMvc
@EnableWebMvc
public class TransactionsApiTests {
    
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ObjectMapper mapper;
    
    @MockBean
    TransactionBusinessService service;
    
    SavingsGoal sg1 = new SavingsGoal(1, "Goal 1", new Date(), new Date(),
            BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(25), false);
    SavingsGoal sg2 = new SavingsGoal(2, "Goal 2", new Date(), new Date(),
            BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(25), false);
    SavingsGoal sg3 = new SavingsGoal(3, "Goal 3", new Date(), new Date(),
            BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(25), false);
    
    Transaction t1 = new Transaction(1L, new Date(), sg1, sg1, BigDecimal.valueOf(100));
    Transaction t2 = new Transaction(2L, new Date(), sg2, sg2, BigDecimal.valueOf(100));
    Transaction t3 = new Transaction(3L, new Date(), sg3, sg3, BigDecimal.valueOf(100));
    
    @Test
    public void testGetAllTransactions_Success() throws Exception {
        List<Transaction> transactions = Arrays.asList(t1, t2, t3);
        Mockito.when(this.service.getAllTransactions()).thenReturn(transactions);
        
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].transactionId", is(t2.getTransactionId().intValue())));
    }
    
    @Test
    public void testGetTransaction_Success() throws Exception {
        Mockito.when(this.service.getTransaction(t3.getTransactionId())).thenReturn(Optional.of(t3));
        
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/transactions/" + t3.getTransactionId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.transactionId", is(t3.getTransactionId().intValue())));
    }
    
    @Test
    public void testGetTransactionNotFound_Fail() throws Exception {
        Mockito.when(this.service.getTransaction(Mockito.anyLong())).thenReturn(Optional.empty());
    
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/transactions" + t3.getTransactionId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testSaveTransaction_Success() throws Exception {
        Mockito.when(this.service.saveTransaction(t1)).thenReturn(t1);
        
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(t1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.transactionId", is(t1.getTransactionId().intValue())));
    }
    
    @Test
    public void testUpdateTransaction_Success() throws Exception {
        Mockito.when(this.service.getTransaction(t3.getTransactionId())).thenReturn(Optional.of(t3));
        Mockito.when(this.service.saveTransaction(t3)).thenReturn(t3);
        
        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/transactions/" + t3.getTransactionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(t3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.transactionId", is(t3.getTransactionId().intValue())));
    }
    
    @Test
    public void testUpdateTransactionNotFound_Fail() throws Exception {
        Mockito.when(this.service.getTransaction(Mockito.anyLong())).thenReturn(Optional.empty());
    
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/transactions/" + t3.getTransactionId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(t3)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testDeleteTransaction_Success() throws Exception {
        Mockito.when(this.service.getTransaction(t3.getTransactionId())).thenReturn(Optional.of(t3));
        
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/transactions/" + t3.getTransactionId()))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteTransactionNotFound_Fail() throws Exception {
        Mockito.when(this.service.getTransaction(Mockito.anyLong())).thenReturn(Optional.empty());
        
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/transactions/" + t3.getTransactionId()))
                .andExpect(status().isNotFound());
    }
}
