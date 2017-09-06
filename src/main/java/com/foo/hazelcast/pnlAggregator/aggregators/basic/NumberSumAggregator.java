package com.foo.hazelcast.pnlAggregator.aggregators.basic;

public interface NumberSumAggregator<T extends Number> extends SumAggregator<T>{
    T sum(T a, T b);
}