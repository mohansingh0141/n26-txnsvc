package com.example.txnsvc.contract;

public class TxnStatsResponse {

    private final Double sum;
    private final Double avg;
    private final Double max;
    private final Double min;
    private final Long count;

    public TxnStatsResponse(Double sum, Double avg, Double max, Double min, Long count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public Double getSum() {
        return sum;
    }

    public Double getAvg() {
        return avg;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public Long getCount() {
        return count;
    }
}
