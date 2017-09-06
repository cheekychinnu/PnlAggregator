package com.foo.hazelcast.pnlAggregator.factory;

import com.foo.hazelcast.pnlAggregator.aggregators.basic.DoubleSumAggregator;
import com.foo.hazelcast.pnlAggregator.aggregators.basic.IntegerSumAggregator;
import com.foo.hazelcast.pnlAggregator.aggregators.basic.LongSumAggregator;
import com.foo.hazelcast.pnlAggregator.aggregators.basic.SumAggregator;

public class SumAggregatorFactory {
    
    @SuppressWarnings("unchecked")
    public static <T> SumAggregator<T> getSumAggregator(Class<T> clazz){
        
        if(clazz.equals(Integer.class)){
            return (SumAggregator<T>) new IntegerSumAggregator();
        }
        
        if(clazz.equals(Long.class)) {
            return (SumAggregator<T>) new LongSumAggregator();
        }
        
        if(clazz.equals(Double.class)) {
            return (SumAggregator<T>) new DoubleSumAggregator();
        }
        
        throw new IllegalArgumentException("Type not supported for summation : "+clazz.getName());
    }
}