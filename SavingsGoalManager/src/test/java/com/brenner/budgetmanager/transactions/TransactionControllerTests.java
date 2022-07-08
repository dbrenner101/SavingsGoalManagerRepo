/**
 * 
 */
package com.brenner.budgetmanager.transactions;

import com.brenner.budgetmanager.savingsgoals.SavingsGoal;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTests {
	
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
	public void testManageTransactions_Success() throws Exception {
		List<Transaction> transactions = Arrays.asList(t1, t2, t3);
		List<SavingsGoal> savingsGoals = Arrays.asList(sg1, sg2, sg3);
		
		Mockito.when(this.service.getAllSavingsGoals()).thenReturn(savingsGoals);
		Mockito.when(this.service.getAllTransactions()).thenReturn(transactions);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/manageTransactions"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("transactions"))
				.andExpect(model().attribute("transactions", transactions))
				.andExpect(model().attributeExists("savingsGoals"))
				.andExpect(model().attribute("savingsGoals", savingsGoals))
				.andExpect(view().name("transaction/addTransaction"));
	}
	
	@Test
	public void testAddTransaction_Success() throws Exception {
		Mockito.when(this.service.saveTransaction(t1)).thenReturn(t1);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/addTransaction")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.flashAttr("transaction", t1))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:manageTransactions"));
	}
	
	@Test
	public void testDeleteTransaction_Success() throws Exception {
		Mockito.when(this.service.getTransaction(Mockito.anyLong())).thenReturn(Optional.of(t1));
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.delete("/deleteTransaction")
						.param("transactionId", t1.getTransactionId().toString()))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/manageTransactions"));
	}
}
