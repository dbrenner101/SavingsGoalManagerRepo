/**
 * 
 */
package com.brenner.budgetmanager.savingsgoals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import com.brenner.budgetmanager.deposit.Deposit;
import com.brenner.budgetmanager.deposit.DepositRepository;
import com.brenner.budgetmanager.exception.InvalidRequestException;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest
public class SavingsGoalBusinessServiceTests {

	@MockBean
	SavingsGoalRepository repo;
	
	@MockBean
	DepositRepository depositRepo;
	
	@Autowired
	SavingsGoalsBusinessService service;
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
	
	SavingsGoal sg1 = new SavingsGoal(1, "Goal One", convertStringToDate("1/1/2021"), convertStringToDate("1/31/2021"), 
			100F, 0F, 10F, false);
	SavingsGoal sg2 = new SavingsGoal(2, "Goal Two", convertStringToDate("9/1/2022"), convertStringToDate("9/1/2023"), 
			500F, 0F, 0F, false);
	SavingsGoal sg3 = new SavingsGoal(3, "Goal Three", convertStringToDate("5/1/2022"), convertStringToDate("6/30/2022"), 
			150F, 100F, 100F, true);
	
	Deposit d1 = new Deposit(1L, 100.5F, new Date(), false);
	Deposit d2 = new Deposit(2L, 50F, new Date(), false);
	Deposit d3 = new Deposit(3L, 5500.75F, new Date(), true);
	
	
	@Test
	public void testFindDefaultGoal_Success() throws Exception {
		
		Mockito.when(this.repo.findByIsDefault(true)).thenReturn(Optional.of(this.sg3));
		
		SavingsGoal defaultGoal = this.service.findDefaultGoal();
		
		assertNotNull(defaultGoal);
		assertEquals(this.sg3, defaultGoal);
		assertTrue(defaultGoal.isDefault());
	}
	
	@Test
	public void testSaveGoal_Success() throws Exception {
		
		Mockito.when(this.repo.save(this.sg1)).thenReturn(this.sg1);
		
		SavingsGoal savedGoal = this.repo.save(this.sg1);
		
		assertNotNull(savedGoal);
		assertEquals(this.sg1, savedGoal);
	}
	
	@Test
	public void testSaveNullGoal_Failure() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.addSavingsGoal(null);
		});
		
		assertEquals("Savings Goal must not be null.", e.getMessage());
	}
	
	@Test
	public void testUpdateGoal_Success() throws Exception {
		
		Mockito.when(this.repo.save(this.sg2)).thenReturn(this.sg2);
		
		SavingsGoal goal = this.service.updateSavingsGoal(this.sg2);
		
		assertNotNull(goal);
		assertEquals(this.sg2.getGoalName(), goal.getGoalName());
		assertNotNull(goal.getMonthsTillPayment());
		assertEquals(12, goal.getMonthsTillPayment());
		assertEquals(this.sg2.getTargetAmount()/12, goal.getSavingsPerMonth());
		assertNotNull(goal.getWeeksTillPayment());
		assertEquals(52, goal.getWeeksTillPayment());
		assertEquals(this.sg2.getTargetAmount()/52, goal.getSavingsPerWeek());
		assertNotNull(goal.getDaysTillPayment());
		assertEquals(365, goal.getDaysTillPayment());
		assertEquals(this.sg2.getTargetAmount()/365, goal.getSavingsPerDay());
		
	}
	
	@Test
	public void testUpdateGoalMetTarget_Success() throws Exception {
		
		SavingsGoal testGoal = new SavingsGoal();
		BeanUtils.copyProperties(this.sg1, testGoal);
		
		testGoal.setCurrentBalance(testGoal.getTargetAmount());
		
		Mockito.when(this.repo.save(testGoal)).thenReturn(testGoal);
		
		SavingsGoal goal = this.service.updateSavingsGoal(testGoal);
		
		assertNotNull(goal);
		assertNull(goal.getMonthsTillPayment());
		assertNull(goal.getWeeksTillPayment());
		assertNull(goal.getDaysTillPayment());
		assertNull(goal.getSavingsPerMonth());
		assertNull(goal.getSavingsPerWeek());
		assertNull(goal.getSavingsPerDay());
	}
	
	@Test
	public void testUpdateGoalNullGoal_Failure() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.updateSavingsGoal(null);
		});
		
		assertEquals("Goal must not be null.", e.getMessage());
	}
	
	@Test
	public void testUpdateGoalNullCurrentBalance_Failure() throws Exception {
		
		SavingsGoal testGoal = new SavingsGoal();
		BeanUtils.copyProperties(this.sg2, testGoal);
		
		testGoal.setCurrentBalance(null);
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.updateSavingsGoal(testGoal);
		});
		
		assertEquals("Goal must have a non-null current balance.", e.getMessage());
	}
	
	@Test
	public void testGetAllGoalsButDefault_Success() throws Exception {
		
		List<SavingsGoal> goals = new ArrayList<>(Arrays.asList(this.sg1, this.sg2));
		Mockito.when(this.repo.findAllByIsDefaultFalse(Sort.by(Sort.Direction.ASC, "goalName"))).thenReturn(goals);
		
		List<SavingsGoal> allGoals = this.service.getAllSavingsGoalsButDefault();
		
		assertNotNull(allGoals);
		assertEquals(2, allGoals.size());
	}
	
	@Test
	public void testGetOneGoal_Success() throws Exception {
		
		Mockito.when(this.repo.findById(this.sg3.getSavingsGoalId())).thenReturn(Optional.of(this.sg3));
		
		SavingsGoal goal = this.service.getSavingsGoalById(this.sg3.getSavingsGoalId());
		
		assertNotNull(goal);
		assertEquals(goal.getGoalName(), this.sg3.getGoalName());
		assertEquals(goal.getSavingsGoalId(), this.sg3.getSavingsGoalId());
	}
	
	@Test
	public void testGetOneGoalNullId_Failure() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.getSavingsGoalById(null);
		});
		
		assertEquals("Goal id must be non-null.", e.getMessage());
	}
	
	@Test
	public void testAllocateToGoals_Success() throws Exception {
		
		Mockito.when(this.depositRepo.findById(2L)).thenReturn(Optional.of(this.d2));
		Mockito.when(this.repo.findById(1)).thenReturn(Optional.of(this.sg1));
		Mockito.when(this.repo.findById(2)).thenReturn(Optional.of(this.sg2));
		Mockito.when(this.repo.findByIsDefault(true)).thenReturn(Optional.of(this.sg3));
		
		this.service.allocateDepositToGoals(
				this.d2.getDepositId(), 
				Arrays.asList(this.sg1.getSavingsGoalId(), this.sg2.getSavingsGoalId()), 
				Arrays.asList(100F, 50.55F));
		
		
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
