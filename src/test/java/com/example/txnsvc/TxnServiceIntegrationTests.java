package com.example.txnsvc;

import com.example.txnsvc.contract.CreateTxnRequestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TxnServiceIntegrationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    public void return201OnSuccess() throws Exception {

        this.mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(getContent().toString()) )
                .andExpect(status().isCreated());


    }

    @Test
    public void return204OnWhenRecordIsOlderThan60Secs() throws Exception {

        CreateTxnRequestData requestData = getContent();

        Thread.sleep(60000);

        this.mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestData.toString()) )
                .andExpect(status().isNoContent());


    }


    @Test
    public void recordShouldBeAvailableViaStatsApi() throws Exception {

        //Thread sleep  for 60 seconds to make sure records inserted by other tests won't affect this test
        Thread.sleep(60000);

        CreateTxnRequestData requestData = getContent();
        this.mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestData.toString()) )
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/statistics"))
                .andExpect(content().json(
                        "{" +
                                "\"sum\" : 12.3,"+
                                "\"avg\" : 12.3,"+
                                "\"max\" : 12.3,"+
                                "\"min\" : 12.3,"+
                                "\"count\" : 1"+

                                "}"
                ));



    }

    @Test
    public void recordsOlderThan60SecondsShouldNotBeAvailableViaStatsApi() throws Exception {

        //Thread sleep  for 60 seconds to make sure records inserted by other tests won't affect this test
        Thread.sleep(60000);


        this.mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent(15.5).toString()) )
                .andExpect(status().isCreated());

        //sleep for 60 seconds, this is for verification that above records won't affect stats calculation as it's older than 60 secs
        Thread.sleep(60000);


        this.mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent().toString()) )
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent(7.7).toString()) )
                .andExpect(status().isCreated());



        this.mockMvc.perform(get("/statistics"))
                .andExpect(content().json(
                        "{" +
                                "\"sum\" : 20.0,"+
                                "\"avg\" : 10.0,"+
                                "\"max\" : 12.3,"+
                                "\"min\" : 7.7,"+
                                "\"count\" : 2"+
                                "}"
                ));



    }

    private CreateTxnRequestData getContent(double amount){
            return new CreateTxnRequestData(amount,Instant.now(Clock.systemUTC()).toEpochMilli());
    }

    private CreateTxnRequestData getContent(){
       return getContent(12.3);
    }
}
