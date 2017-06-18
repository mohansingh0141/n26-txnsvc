package com.example.txnsvc.contract;

public class CreateTxnRequestData {

    private Double amount;

    private Long timestamp;

    public CreateTxnRequestData() {
        //for spring
    }

    public CreateTxnRequestData(Double amount, Long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Double getAmount() {
        return amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "{" +
                "\"amount\":" + amount  +
                ", \"timestamp\":" + timestamp +
                '}';
    }
}
