/**
 * 
 */
package com.brenner.budgetmanager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author dbrenner
 * 
 */
@Controller
public class DefaultController {

	@RequestMapping("/")
	public String handleDefaultRequest() {
		return "index";
	}

}
