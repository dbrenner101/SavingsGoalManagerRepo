/**
 * 
 */
package com.brenner.budgetmanager.deposit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DepositControllerTests {

	@MockBean
	DepositRepository depositRepo;
	
	@Autowired
    MockMvc mockMvc;
	
	Deposit d1 = new Deposit(1L, 100.5F, new Date(), false);
	Deposit d2 = new Deposit(2L, 50F, new Date(), false);
	Deposit d3 = new Deposit(3L, 5500.75F, new Date(), true);
	
	@Test
	public void testGetUnallocatedDeposits_Success() throws Exception {
		
		List<Deposit> deposits = new ArrayList<>(Arrays.asList(d1, d2));
		
		Mockito.when(this.depositRepo.findByAllocated(false)).thenReturn(deposits);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getUnallocatedDeposits"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("deposits"))
			.andExpect(model().attribute("deposits", deposits))
			.andExpect(model().size(1))
			.andExpect(view().name("deposit/deposits"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void testAddDeposit_Success() throws Exception {
		
		Mockito.when(this.depositRepo.save(d2)).thenReturn(d2);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/addDeposit")
				.contentType("application/x-www-form-urlencoded")
				.flashAttr("deposit", d2))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:getUnallocatedDeposits"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void testUpdateDeposit_Success() throws Exception {
		
		Mockito.when(this.depositRepo.save(d2)).thenReturn(d2);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/updateDeposit")
				.contentType("application/x-www-form-urlencoded")
				.flashAttr("deposit", d2))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:getUnallocatedDeposits"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void testBeginEditDeposit_Success() throws Exception {
		
		Mockito.when(this.depositRepo.findById(this.d1.getDepositId())).thenReturn(Optional.of(this.d1));
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/editDeposit?depositId=" + this.d1.getDepositId()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("deposit"))
			.andExpect(model().attribute("deposit", this.d1))
			.andExpect(view().name("deposit/editDeposit"));
	}

}
