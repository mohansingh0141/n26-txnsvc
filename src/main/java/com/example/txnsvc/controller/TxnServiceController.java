package com.example.txnsvc.controller;

import com.example.txnsvc.contract.CreateTxnRequestData;
import com.example.txnsvc.contract.TxnStatsResponse;
import com.example.txnsvc.datastore.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/")
public class TxnServiceController {

    private final static Logger LOGGER = LoggerFactory.getLogger(TxnServiceController.class);

    private Datastore datastore;

    @Autowired
    public TxnServiceController(Datastore datastore) {
        this.datastore = datastore;
    }

    @RequestMapping(value = "transactions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void createTxn(@RequestBody CreateTxnRequestData createTxnRequestData, HttpServletResponse response){

        Datastore.DocumentState documentState = datastore.saveTxn(createTxnRequestData);

        if(documentState == Datastore.DocumentState.PAST_TIMESTAMP){
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        else {
            response.setStatus(HttpServletResponse.SC_CREATED);
        }


    }

    @RequestMapping(value = "statistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public TxnStatsResponse getStats(){
        return datastore.getStats();
    }

    @ExceptionHandler(Exception.class)
    public void exceptionHandler(Exception ex,HttpServletResponse response){

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        LOGGER.error("Exception occured",ex);

    }
}
