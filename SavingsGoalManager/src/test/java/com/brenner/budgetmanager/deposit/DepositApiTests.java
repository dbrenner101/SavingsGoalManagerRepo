package com.brenner.budgetmanager.deposit;

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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DepositApiTests {
    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    DepositBusinessService depositService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    Deposit d1 = new Deposit(1L, 100.5F, new Date(), false);
    Deposit d2 = new Deposit(2L, 200.5F, new Date(), false);
    Deposit d3 = new Deposit(3L, 300.5F, new Date(), false);
    
    @Test
    public void testGetAllDeposits_Success() throws Exception {
        List<Deposit> deposits = Arrays.asList(d1, d2, d3);
    
        Mockito.when(this.depositService.getUnallocatedDeposits()).thenReturn(deposits);
    
        this.mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].depositId", is(2)));
    }
    
    @Test
    public void testGetDepositById_Success() throws Exception {
        
        Mockito.when(this.depositService.getDeposit(d3.getDepositId())).thenReturn(Optional.of(d3));
        
        this.mockMvc.perform(MockMvcRequestBuilders
                     .get("/api/deposits/" + d3.getDepositId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.depositId", is(d3.getDepositId().intValue())));
    }
    
    @Test
    public void testGetDepositByIdNotFound_Failure() throws Exception {
        
        Mockito.when(this.depositService.getDeposit(1L)).thenReturn(Optional.empty());
    
        this.mockMvc.perform(MockMvcRequestBuilders
                     .get("/api/deposits/1"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testAddNewDeposit_Success() throws Exception {
        
        Mockito.when(this.depositService.saveDeposit(d2)).thenReturn(d2);
        
        this.mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/deposits")
                    .content(objectMapper.writeValueAsString(d2))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.depositId", is(d2.getDepositId().intValue())));
    }
    
    @Test
    public void testUpdateDeposit_Success() throws Exception {
        Mockito.when(this.depositService.getDeposit(d1.getDepositId())).thenReturn(Optional.of(d1));
        Mockito.when(this.depositService.saveDeposit(d1)).thenReturn(d1);
        
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/deposits/" + d1.getDepositId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.depositId", is(d1.getDepositId().intValue())));
    }
    
    @Test
    public void testUpdateDepositNotFound_Fail() throws Exception {
        Mockito.when(this.depositService.getDeposit(Mockito.anyLong())).thenReturn(Optional.empty());
    
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/deposits/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testDeleteDeposit_Success() throws Exception {
        Mockito.when(this.depositService.getDeposit(d3.getDepositId())).thenReturn(Optional.of(d3));
        
        this.mockMvc.perform(MockMvcRequestBuilders
                    .delete("/api/deposits/" + d3.getDepositId()))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteDepositNotFound_Fail() throws Exception {
        Mockito.when(this.depositService.getDeposit(Mockito.anyLong())).thenReturn(Optional.empty());
    
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/deposits/" + d3.getDepositId()))
                .andExpect(status().isNotFound());
    }
}
