/**
 * 
 */
package com.brenner.budgetmanager.deposit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.brenner.budgetmanager.exception.InvalidRequestException;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest
public class DepositBusinessServiceTests {

	@MockBean
	DepositRepository depositRepo;
	
	@Autowired
	DepositBusinessService service;
	
	Deposit d1 = new Deposit(1L, 100.5F, new Date(), false);
	Deposit d2 = new Deposit(2L, 50F, new Date(), false);
	Deposit d3 = new Deposit(3L, 5500.75F, new Date(), true);
	
	@Test
	public void testSaveDeposit_Success() throws Exception {
		
		Mockito.when(this.depositRepo.save(this.d3)).thenReturn(this.d3);
		
		Deposit deposit = this.service.saveDeposit(this.d3);
		
		assertEquals(this.d3, deposit);
	}
	
	@Test
	public void testGetUnallocated_Success() throws Exception {
		List<Deposit> deposits = new ArrayList<>(Arrays.asList(d1, d2));
		
		Mockito.when(this.depositRepo.findByAllocated(false)).thenReturn(deposits);
		
		List<Deposit> unallocated = this.service.getUnallocatedDeposits();
		
		assertNotNull(unallocated); 
		assertEquals(2, unallocated.size());
	}
	
	@Test
	public void testGetDeposit_Success() throws Exception {
		
		Mockito.when(this.depositRepo.findById(this.d1.getDepositId())).thenReturn(Optional.of(this.d1));
		
		Deposit deposit = this.service.getDeposit(this.d1.getDepositId());
		
		assertNotNull(deposit);
		assertEquals(this.d1, deposit);
	}
	
	@Test
	public void testGetDeposit_Failure() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.getDeposit(null);
		});
		
		assertEquals("Deposit id is required.", e.getMessage());
		
	}
}