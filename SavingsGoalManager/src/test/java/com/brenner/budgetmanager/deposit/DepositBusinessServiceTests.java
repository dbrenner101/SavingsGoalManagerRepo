/**
 * 
 */
package com.brenner.budgetmanager.deposit;


import com.brenner.budgetmanager.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
	
	Deposit d1 = new Deposit(1L, BigDecimal.valueOf(100.5), new Date(), false);
	Deposit d2 = new Deposit(2L, BigDecimal.valueOf(50), new Date(), false);
	Deposit d3 = new Deposit(3L, BigDecimal.valueOf(5500.75), new Date(), true);
	
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
		
		Optional<Deposit> optDeposit = this.service.getDeposit(this.d1.getDepositId());
		assertTrue(optDeposit.isPresent());
		
		Deposit deposit = optDeposit.get();
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
