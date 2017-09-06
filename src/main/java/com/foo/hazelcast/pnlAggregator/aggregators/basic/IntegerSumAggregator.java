package com.foo.hazelcast.pnlAggregator.aggregators.basic;


public class IntegerSumAggregator implements NumberSumAggregator<Integer> {

    @Override
    public Integer sum(Integer a, Integer b) {
        if (a == null) {
            a = 0;
        }
        if (b == null) {
            b = 0;
        }
        return a + b;
    }

}
