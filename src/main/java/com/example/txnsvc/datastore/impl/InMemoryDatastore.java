package com.example.txnsvc.datastore.impl;

import com.example.txnsvc.contract.CreateTxnRequestData;
import com.example.txnsvc.contract.TxnStatsResponse;
import com.example.txnsvc.datastore.Datastore;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.Comparator;

@Component
public class InMemoryDatastore implements Datastore {

    private volatile ArrayDeque<CreateTxnRequestData> requestDataQueue = new ArrayDeque<>(60);

    @Override
    public DocumentState saveTxn(CreateTxnRequestData requestData) {

        if(createTxn(requestData)){
            return DocumentState.CREATED_SUCCESS;
        }
        else {
            return DocumentState.PAST_TIMESTAMP;
        }
    }

    @Override
    public TxnStatsResponse getStats() {
        purge();

        ArrayDeque<CreateTxnRequestData> cloneDeque = requestDataQueue.clone();

        Double sum = cloneDeque.stream().mapToDouble(CreateTxnRequestData::getAmount).sum();
        Double avg = cloneDeque.stream().mapToDouble(CreateTxnRequestData::getAmount).average().getAsDouble();
        return new TxnStatsResponse(sum,avg,cloneDeque.stream().max(new TxnComparator()).get().getAmount(),
                cloneDeque.stream().min(new TxnComparator()).get().getAmount(),Long.valueOf(cloneDeque.size()));
    }

    private boolean createTxn(CreateTxnRequestData requestData){

        ZonedDateTime currentTime = ZonedDateTime.now(Clock.systemUTC());
        ZonedDateTime txnTime  = ZonedDateTime.from(Instant.ofEpochMilli(requestData.getTimestamp()).atOffset(ZoneOffset.UTC));

        if(txnTime.isBefore(currentTime.minusSeconds(60))){
            return false;
        }
        else {

            synchronized (this){
                requestDataQueue.addFirst(requestData);
            }
        }
        return true;
    }

    private void purge(){

        ZonedDateTime currentTime = ZonedDateTime.now(Clock.systemUTC());

        while (ZonedDateTime.from(Instant.ofEpochMilli(requestDataQueue.getLast().getTimestamp()).atOffset(ZoneOffset.UTC)).isBefore(currentTime.minusSeconds(60))){
            requestDataQueue.removeLast();
        }

    }

    class TxnComparator implements Comparator<CreateTxnRequestData>   {

        @Override
        public int compare(CreateTxnRequestData o1, CreateTxnRequestData o2) {

            if(o1.getAmount() < o2.getAmount()){
                return -1;
            }
            else if(o1.getAmount() == o2.getAmount()) {
                return 0;
            }
            else {
                return 1;
            }
        }
    }
}
