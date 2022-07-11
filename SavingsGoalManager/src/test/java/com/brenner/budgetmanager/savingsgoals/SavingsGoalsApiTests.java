package com.brenner.budgetmanager.savingsgoals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest (classes = {
        SavingsGoalsBusinessService.class,
        SavingsGoalsApi.class,
        ObjectMapper.class
})
@AutoConfigureMockMvc
@EnableWebMvc
public class SavingsGoalsApiTests {
    
    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    SavingsGoalsBusinessService service;
    
    @Autowired
    ObjectMapper mapper;
    
    SavingsGoal sg1 = new SavingsGoal(1, "Goal 1", new Date(), new Date(),
            BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(25), false);
    SavingsGoal sg2 = new SavingsGoal(2, "Goal 2", new Date(), new Date(),
            BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(25), false);
    SavingsGoal sg3 = new SavingsGoal(3, "Goal 3", new Date(), new Date(),
            BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(25), false);
    
    SavingsGoal defaultGoal = new SavingsGoal(3, "Default Goal", new Date(), new Date(),
            BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(25), true);
    
    @Test
    public void testGetAllSavingsGoals_Success() throws Exception {
    
        List<SavingsGoal> goals = Arrays.asList(sg1, sg2, sg3);
        Mockito.when(this.service.getAllSavingsGoals()).thenReturn(goals);
        
        this.mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/savingsgoals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].savingsGoalId", is(sg1.getSavingsGoalId())));
    }
    
    @Test
    public void testGetDefaultGoal_Success() throws Exception {
        Mockito.when(this.service.findDefaultGoal()).thenReturn(Optional.of(defaultGoal));
        
        this.mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/savingsgoals/defaultgoal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.goalName", is(defaultGoal.getGoalName())));
    }
    
    @Test
    public void testGetDefaultGoalNotFound_Fail() throws Exception {
        Mockito.when(this.service.findDefaultGoal()).thenReturn(Optional.empty());
    
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/savingsgoals/defaultgoal"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testAddNewGoal_Success() throws Exception {
        Mockito.when(this.service.addSavingsGoal(sg2)).thenReturn(sg2);
        
        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/savingsgoals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sg2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.savingsGoalId", is(sg2.getSavingsGoalId())));
    }
    
    @Test
    public void testUpdateSavingsGoal_Success() throws Exception {
        Mockito.when(this.service.getSavingsGoalById(sg3.getSavingsGoalId())).thenReturn(Optional.of(sg3));
        Mockito.when(this.service.updateSavingsGoal(sg3)).thenReturn(sg3);
        
        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/savingsgoals/" + sg3.getSavingsGoalId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sg3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.goalName", is(sg3.getGoalName())));
    }
    
    @Test
    public void testUpdateSavingsGoalNotFound_Fail() throws Exception {
        Mockito.when(this.service.getSavingsGoalById(sg3.getSavingsGoalId())).thenReturn(Optional.empty());
    
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/savingsgoals/" + sg3.getSavingsGoalId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sg3)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testDeleteSavingsGoal_Success() throws Exception {
        Mockito.when(this.service.getSavingsGoalById(sg2.getSavingsGoalId())).thenReturn(Optional.of(sg2));
        
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/savingsgoals/" + sg2.getSavingsGoalId()))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteSavingsGoalNotFound_Fail() throws Exception {
        Mockito.when(this.service.getSavingsGoalById(sg2.getSavingsGoalId())).thenReturn(Optional.empty());
        
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/savingsgoals/" + sg2.getSavingsGoalId()))
                .andExpect(status().isNotFound());
    }
}
