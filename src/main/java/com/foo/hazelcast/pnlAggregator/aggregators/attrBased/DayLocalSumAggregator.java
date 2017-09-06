package com.foo.hazelcast.pnlAggregator.aggregators.attrBased;

import java.util.Map;
import java.util.Map.Entry;

import com.foo.hazelcast.pnlAggregator.model.Pnl;
import com.hazelcast.aggregation.Aggregator;

public class DayLocalSumAggregator extends Aggregator<Map.Entry<Long, Pnl>, Double> {

    private static final long serialVersionUID = 1L;
    
    private Double sum = 0d;

    @Override
    public void accumulate(Entry<Long, Pnl> input) {
        sum += input.getValue().getDayLocal();

    }

    @Override
    public void combine(Aggregator aggregator) {
        DayLocalSumAggregator otherAggregator = (DayLocalSumAggregator) aggregator;
        sum += otherAggregator.sum;
    }

    @Override
    public Double aggregate() {
        return sum;
    }
    
}