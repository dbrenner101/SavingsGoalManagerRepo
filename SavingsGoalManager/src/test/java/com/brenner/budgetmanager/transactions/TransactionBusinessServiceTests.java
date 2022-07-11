/**
 * 
 */
package com.brenner.budgetmanager.transactions;

import com.brenner.budgetmanager.deposit.DepositRepository;
import com.brenner.budgetmanager.exception.InvalidRequestException;
import com.brenner.budgetmanager.savingsgoals.SavingsGoal;
import com.brenner.budgetmanager.savingsgoals.SavingsGoalRepository;
import com.brenner.budgetmanager.savingsgoals.SavingsGoalsBusinessService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		TransactionRepository.class,
		SavingsGoalRepository.class,
		SavingsGoalsBusinessService.class,
		TransactionBusinessService.class,
		DepositRepository.class
})
public class TransactionBusinessServiceTests {

	@MockBean
	TransactionRepository repo;
	
	@MockBean
	DepositRepository depositRepository;

	@MockBean
    SavingsGoalRepository savingsGoalRepo;
    
    @Autowired
    SavingsGoalsBusinessService savingsGoalsService;
	
	@Autowired
	TransactionBusinessService service;
	
	SavingsGoal sg1 = new SavingsGoal(1, "Goal One", convertStringToDate("1/1/2021"), convertStringToDate("1/31/2021"),
			BigDecimal.valueOf(100), BigDecimal.valueOf(0), BigDecimal.valueOf(10), false);
	SavingsGoal sg2 = new SavingsGoal(2, "Goal Two", convertStringToDate("9/1/2022"), convertStringToDate("9/1/2023"),
			BigDecimal.valueOf(500), BigDecimal.valueOf(0), BigDecimal.valueOf(0), false);
	
	Transaction t1 = new Transaction(1L, convertStringToDate("01/05/2020"), sg1, sg2, BigDecimal.valueOf(100));
	Transaction t2 = new Transaction(2L, convertStringToDate("09/01/2021"), sg2, sg1, BigDecimal.valueOf(1));
	Transaction t3 = new Transaction(3L, convertStringToDate("04/21/2022"), sg1, sg2, BigDecimal.valueOf(99));
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
	
	@Test
	public void testSaveTransactionToAndFromGoals_Success() throws Exception {
		
		SavingsGoal fromGoal = new SavingsGoal();
		BeanUtils.copyProperties(this.sg1, fromGoal);
		this.savingsGoalsService.decorateSavingsGoal(fromGoal);
		SavingsGoal toGoal = new SavingsGoal();
		BeanUtils.copyProperties(this.sg2, toGoal);
		this.savingsGoalsService.decorateSavingsGoal(toGoal);
		
		Transaction testTransaction = new Transaction();
		BeanUtils.copyProperties(this.t1, testTransaction);
		testTransaction.setFromGoal(fromGoal);
		testTransaction.setToGoal(toGoal);

		
		Mockito.when(this.repo.save(testTransaction)).thenReturn(testTransaction);
		Mockito.when(this.savingsGoalRepo.save(fromGoal)).thenReturn(fromGoal);
		Mockito.when(this.savingsGoalRepo.save(toGoal)).thenReturn(toGoal);
		
		Transaction t = this.service.saveTransaction(testTransaction);
		
		assertNotNull(t);
		assertNotNull(fromGoal.getDaysTillPayment());
		
		assertEquals(sg1.getCurrentBalance().subtract(t1.getAmount()), fromGoal.getCurrentBalance());
		assertEquals(sg2.getCurrentBalance().add(t1.getAmount()), toGoal.getCurrentBalance());
		
	}
	
	@Test
	public void testSaveTransactionFromGoalOnly_Success() throws Exception {
		
		SavingsGoal fromGoal = new SavingsGoal();
		BeanUtils.copyProperties(this.sg1, fromGoal);
		this.savingsGoalsService.decorateSavingsGoal(fromGoal);
		
		Transaction testTransaction = new Transaction();
		BeanUtils.copyProperties(this.t1, testTransaction);
		testTransaction.setFromGoal(fromGoal);
		testTransaction.setToGoal(null);

		
		Mockito.when(this.repo.save(testTransaction)).thenReturn(testTransaction);
		Mockito.when(this.savingsGoalRepo.save(fromGoal)).thenReturn(fromGoal);
		
		Transaction t = this.service.saveTransaction(testTransaction);
		
		assertNotNull(t);
		assertNotNull(fromGoal.getDaysTillPayment());
		
		assertEquals(sg1.getCurrentBalance().subtract(t1.getAmount()), fromGoal.getCurrentBalance());
		
	}
	
	@Test
	public void testSaveTransactionInvalid_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.saveTransaction(null);
		});
		
		assertEquals("Transaction must have non-null properties (amount, from goal).", e.getMessage());
		
		Transaction t = new Transaction();
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.service.saveTransaction(t);
		});
		
		assertEquals("Transaction must have non-null properties (amount, from goal).", e.getMessage());
		
		t.setAmount(BigDecimal.valueOf(100));
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.service.saveTransaction(t);
		});
		
		assertEquals("Transaction must have non-null properties (amount, from goal).", e.getMessage());
		
		t.setAmount(null);
		t.setFromGoal(this.sg1);
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.service.saveTransaction(t);
		});
		
		assertEquals("Transaction must have non-null properties (amount, from goal).", e.getMessage());
		
	}
	
	@Test
	public void testDeleteTransactionFromAndToGoals_Success() throws Exception {
		
		SavingsGoal fromGoal = new SavingsGoal();
		BeanUtils.copyProperties(this.sg1, fromGoal);
		this.savingsGoalsService.decorateSavingsGoal(fromGoal);
		SavingsGoal toGoal = new SavingsGoal();
		BeanUtils.copyProperties(this.sg2, toGoal);
		this.savingsGoalsService.decorateSavingsGoal(toGoal);
		
		Transaction testTransaction = new Transaction();
		BeanUtils.copyProperties(this.t1, testTransaction);
		testTransaction.setFromGoal(fromGoal);
		testTransaction.setToGoal(toGoal);

		
		//Mockito.when(this.repo.delete(testTransaction)).thenReturn(testTransaction);
		Mockito.when(this.savingsGoalRepo.save(fromGoal)).thenReturn(fromGoal);
		Mockito.when(this.savingsGoalRepo.save(toGoal)).thenReturn(toGoal);
		
		this.service.deleteTransaction(testTransaction);
		
		assertEquals(sg1.getCurrentBalance().add(t1.getAmount()), fromGoal.getCurrentBalance());
		assertEquals(sg2.getCurrentBalance().subtract(t1.getAmount()), toGoal.getCurrentBalance());
		
	}
	
	@Test
	public void testDeleteTransactionFromGoalOnly_Success() throws Exception {
		
		SavingsGoal fromGoal = new SavingsGoal();
		BeanUtils.copyProperties(this.sg1, fromGoal);
		this.savingsGoalsService.decorateSavingsGoal(fromGoal);
		
		Transaction testTransaction = new Transaction();
		BeanUtils.copyProperties(this.t1, testTransaction);
		testTransaction.setFromGoal(fromGoal);

		
		//Mockito.when(this.repo.delete(testTransaction)).thenReturn(testTransaction);
		Mockito.when(this.savingsGoalRepo.save(fromGoal)).thenReturn(fromGoal);
		
		this.service.deleteTransaction(testTransaction);
		
		assertEquals(sg1.getCurrentBalance().add(t1.getAmount()), fromGoal.getCurrentBalance());
		
	}
	
	@Test
	public void testDeleteTransactionInvalid_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.deleteTransaction(null);
		});
		
		assertEquals("Transaction must have non-null properties (amount, from goal).", e.getMessage());
		
		Transaction t = new Transaction();
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.service.deleteTransaction(t);
		});
		
		assertEquals("Transaction must have non-null properties (amount, from goal).", e.getMessage());
		
		t.setAmount(BigDecimal.valueOf(100));
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.service.deleteTransaction(t);
		});
		
		assertEquals("Transaction must have non-null properties (amount, from goal).", e.getMessage());
		
		t.setAmount(null);
		t.setFromGoal(this.sg1);
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.service.deleteTransaction(t);
		});
		
		assertEquals("Transaction must have non-null properties (amount, from goal).", e.getMessage());
		
	}
	
	
	private Date convertStringToDate(String dateStr) {
		try {
			return DATE_FORMATTER.parse(dateStr);
		}
		catch (ParseException pe) {
			pe.printStackTrace();
		}
		
		return null;
	}

}
