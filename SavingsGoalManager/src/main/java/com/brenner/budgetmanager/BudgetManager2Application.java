package com.brenner.budgetmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BudgetManager2Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BudgetManager2Application.class, args);
	}
	
	protected SpringApplicationBuilder
    configure(SpringApplicationBuilder application)
    {
        return application.sources(BudgetManager2Application.class);
    }

}
