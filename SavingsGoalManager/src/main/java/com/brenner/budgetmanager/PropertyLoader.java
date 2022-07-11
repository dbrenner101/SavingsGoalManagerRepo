package com.brenner.budgetmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Load properties from an external file
 */
@Configuration
@PropertySource("file:${HOME}/dev/secrets/secrets.properties")
public class PropertyLoader {
    
    @Value("${budgetmanager.datasource.password}")
    private String springDataSourcePassword;
    
    public String getSpringDataSourcePassword() {
        return springDataSourcePassword;
    }
    
    public void setSpringDataSourcePassword(String springDataSourcePassword) {
        this.springDataSourcePassword = springDataSourcePassword;
    }
}
