package com.foo.hazelcast.pnlAggregator.aggregators.basic;

public interface SumAggregator <T extends Object>{
    
    T sum(T a, T b);

}