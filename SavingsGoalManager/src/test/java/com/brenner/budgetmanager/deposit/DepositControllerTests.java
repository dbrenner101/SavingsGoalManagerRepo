/**
 * 
 */
package com.brenner.budgetmanager.deposit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		DepositController.class,
		DepositBusinessService.class
})
@AutoConfigureMockMvc
public class DepositControllerTests {

	@MockBean
	DepositBusinessService depositBusinessService;
	
	@Autowired
    MockMvc mockMvc;
	
	Deposit d1 = new Deposit(1L, BigDecimal.valueOf(100.5), new Date(), false);
	Deposit d2 = new Deposit(2L, BigDecimal.valueOf(50), new Date(), false);
	Deposit d3 = new Deposit(3L, BigDecimal.valueOf(5500.75), new Date(), true);
	
	@Test
	public void testGetUnallocatedDeposits_Success() throws Exception {
		
		List<Deposit> deposits = new ArrayList<>(Arrays.asList(d1, d2));
		
		Mockito.when(this.depositBusinessService.getUnallocatedDeposits()).thenReturn(deposits);
		
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
		
		Mockito.when(this.depositBusinessService.saveDeposit(d2)).thenReturn(d2);
		
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
		
		Mockito.when(this.depositBusinessService.getDeposit(d2.getDepositId())).thenReturn(Optional.of(d2));
		Mockito.when(this.depositBusinessService.saveDeposit(d2)).thenReturn(d2);
		
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
		
		Mockito.when(this.depositBusinessService.getDeposit(this.d1.getDepositId())).thenReturn(Optional.of(this.d1));
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/editDeposit?depositId=" + this.d1.getDepositId()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("deposit"))
			.andExpect(model().attribute("deposit", this.d1))
			.andExpect(view().name("deposit/editDeposit"));
	}

}
