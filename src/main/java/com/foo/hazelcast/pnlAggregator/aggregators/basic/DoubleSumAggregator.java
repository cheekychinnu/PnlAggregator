package com.foo.hazelcast.pnlAggregator.aggregators.basic;

public class DoubleSumAggregator implements NumberSumAggregator<Double> {

    @Override
    public Double sum(Double a, Double b) {
        if (a == null) {
            a = 0d;
        }
        if (b == null) {
            b = 0d;
        }
        return a + b;
    }

}