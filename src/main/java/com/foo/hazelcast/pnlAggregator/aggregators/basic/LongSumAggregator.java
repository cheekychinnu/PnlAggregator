package com.foo.hazelcast.pnlAggregator.aggregators.basic;

public class LongSumAggregator implements NumberSumAggregator<Long> {

    @Override
    public Long sum(Long a, Long b) {
        if (a == null) {
            a = 0l;
        }
        if (b == null) {
            b = 0l;
        }
        return a + b;
    }

}