/**
 * 
 */
package com.brenner.budgetmanager.deposit;

import com.brenner.budgetmanager.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * HTTP controller class to handle interactions from HTML templates.
 *
 * @author dbrenner
 * 
 */
@Controller
public class DepositController {

	@Autowired
	DepositBusinessService depositBusinessService;
	
	/**
	 * Access to the list of deposits that haven't been allocated and also the entry point to viewing the
	 * list of unallocated deposits.
	 *
	 * @param model The MVC model for returning data.
	 * @return The path to the list deposits template.
	 */
	@RequestMapping("getUnallocatedDeposits")
	public String getUnallocatedDeposits(Model model) {
		
		List<Deposit> allDeposits = this.depositBusinessService.getUnallocatedDeposits();
		model.addAttribute("deposits", allDeposits);
		
		return "deposit/deposits";
	}
	
	/**
	 * Access to the save new deposit function.
	 *
	 * @param deposit Deposit data to save.
	 * @return A redirect to the getUnallocatedDeposits path
	 */
	@RequestMapping("addDeposit")
	public String addDeposit(@ModelAttribute Deposit deposit) {
		
		if (deposit != null && deposit.getAmount() != null && deposit.getDate() != null) {
			this.depositBusinessService.saveDeposit(deposit);
		}
		
		return "redirect:getUnallocatedDeposits";
	}
	
	/**
	 * Entry point to the edit deposit template. Populates the model with the details of the requested deposit.
	 *
	 * @param depositId Unique identifier for the deposit to edit.
	 * @param model MVC model
	 * @return The path to the edit deposit template.
	 */
	@RequestMapping("editDeposit")
	public String manageEditDepositRequest(@RequestParam(name="depositId", required=true) Long depositId, Model model) {
		
		Optional<Deposit> optDeposit = this.depositBusinessService.getDeposit(depositId);
		
		if (! optDeposit.isPresent()) {
			throw new NotFoundException("Deposit with id " + depositId + " does not exist.");
		}
		model.addAttribute("deposit", optDeposit.get());
		
		return "deposit/editDeposit";
	}
	
	/**
	 * Method called after editing a deposit is complete. This will pass the changes to the business service for
	 * processing. Returns a 404 if the deposit cannot be found.
	 *
	 * @param deposit The deposit object to persist
	 * @return A redirect to the deposits list path
	 */
	@RequestMapping("updateDeposit")
	public String updateDeposit(@ModelAttribute Deposit deposit) {
		
		Optional<Deposit> optionalDeposit = this.depositBusinessService.getDeposit(deposit.getDepositId());
		if (optionalDeposit.isEmpty()) {
			throw new NotFoundException("Deposit with id : " + deposit.getDepositId() + " does not exist.");
		}
		
		this.depositBusinessService.saveDeposit(deposit);
		
		return "redirect:getUnallocatedDeposits";
	}
	
	/**
	 * Entry point for deleting a specific deposit. Returns a 404 if the deposit does not exist.
	 *
	 * @param depositId Deposit unique identifier.
	 * @return A redirect to the list deposits path
	 */
	@GetMapping(path="deleteDeposit")
	public String deleteDeposit(@RequestParam(name="depositId") Long depositId) {
		
		Optional<Deposit> optionalDeposit = this.depositBusinessService.getDeposit(depositId);
		if (optionalDeposit.isEmpty()) {
			throw new NotFoundException("Deposit with id : " + depositId + " does not exist.");
		}
		
		this.depositBusinessService.deleteDeposit(depositId);
		
		return "redirect:getUnallocatedDeposits";
	}

}
