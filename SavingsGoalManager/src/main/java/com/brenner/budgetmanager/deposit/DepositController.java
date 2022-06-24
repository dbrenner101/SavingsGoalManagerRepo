/**
 * 
 */
package com.brenner.budgetmanager.deposit;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author dbrenner
 * 
 */
@Controller
public class DepositController {

	@Autowired
	DepositBusinessService depositBusinessService;
	
	@RequestMapping("getUnallocatedDeposits")
	public String getUnallocatedDeposits(Model model) {
		
		List<Deposit> allDeposits = this.depositBusinessService.getUnallocatedDeposits();
		model.addAttribute("deposits", allDeposits);
		
		return "deposit/deposits";
	}
	
	@RequestMapping("addDeposit")
	public String addDeposit(@ModelAttribute Deposit deposit) {
		
		if (deposit != null && deposit.getAmount() != null && deposit.getDate() != null) {
			this.depositBusinessService.saveDeposit(deposit);
		}
		
		return "redirect:getUnallocatedDeposits";
	}
	
	@RequestMapping("editDeposit")
	public String manageEditDepositRequest(@RequestParam(name="depositId", required=true) Long depositId, Model model) {
		
		Deposit deposit = this.depositBusinessService.getDeposit(depositId);
		model.addAttribute("deposit", deposit);
		
		return "deposit/editDeposit";
	}
	
	@RequestMapping("updateDeposit")
	public String updateDeposit(@ModelAttribute Deposit deposit) {
		
		this.depositBusinessService.saveDeposit(deposit);
		
		return "redirect:getUnallocatedDeposits";
	}
	
	@GetMapping(path="deleteDeposit")
	public String deleteDeposit(@RequestParam(name="depositId", required=true) Long depositId) {
		
		this.depositBusinessService.deleteDeposit(depositId);
		
		return "redirect:getUnallocatedDeposits";
	}

}
