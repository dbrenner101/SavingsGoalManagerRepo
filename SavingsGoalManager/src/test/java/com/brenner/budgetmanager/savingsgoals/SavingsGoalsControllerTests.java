/**
 * 
 */
package com.brenner.budgetmanager.savingsgoals;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.brenner.budgetmanager.deposit.Deposit;
import com.brenner.budgetmanager.deposit.DepositRepository;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SavingsGoalsControllerTests {

	@MockBean
	SavingsGoalRepository savingsGoalRepo;
	
	@MockBean
	DepositRepository depositRepo;
	
	@Autowired
	MockMvc mockMvc;
	
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
	public void testListGoalsAndDeposits_Success() throws Exception {
		
		List<SavingsGoal> goals = new ArrayList<>(Arrays.asList(this.sg1, this.sg2));
		
		Mockito.when(this.depositRepo.findById(1L)).thenReturn(Optional.of(this.d1));
		Mockito.when(this.savingsGoalRepo.findAllByIsDefaultFalse(Sort.by(Sort.Direction.ASC, "goalName"))).thenReturn(goals);
		Mockito.when(this.savingsGoalRepo.findByIsDefault(true)).thenReturn(Optional.of(this.sg3));
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/listGoalsAndDeposit")
				.param("depositId", "1"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("deposit"))
			.andExpect(model().attribute("deposit", this.d1))
			.andExpect(model().attributeExists("savingsGoals"))
			.andExpect(model().attribute("savingsGoals", goals))
			.andExpect(model().attributeExists("unplannedGoal"))
			.andExpect(model().attribute("unplannedGoal", this.sg3))
			.andExpect(view().name("savingsgoals/associateDepositWithGoals"));
	}
	
	@Test
	public void testAllocateDepositToGoals_Success() throws Exception {
		
		Mockito.when(this.depositRepo.findById(2L)).thenReturn(Optional.of(this.d2));
		Mockito.when(this.savingsGoalRepo.findById(1)).thenReturn(Optional.of(this.sg1));
		Mockito.when(this.savingsGoalRepo.findById(2)).thenReturn(Optional.of(this.sg2));
		Mockito.when(this.savingsGoalRepo.findByIsDefault(true)).thenReturn(Optional.of(this.sg3));
		
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/allocateDeposit")
				.param("depositId", "2")
				.param("savingsGoalId", "1")
				.param("savingsGoalId", "2")
				.param("amountTowardsGoal", "10.5")
				.param("amountTowardsGoal", "100"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:getUnallocatedDeposits"));
			
		
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
