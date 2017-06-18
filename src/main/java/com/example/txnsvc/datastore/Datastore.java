package com.example.txnsvc.datastore;

import com.example.txnsvc.contract.CreateTxnRequestData;
import com.example.txnsvc.contract.TxnStatsResponse;

public interface Datastore {

    enum DocumentState {

        CREATED_SUCCESS,
        PAST_TIMESTAMP
        

    }

    DocumentState saveTxn(CreateTxnRequestData requestData);

    TxnStatsResponse getStats();


}
