package com.foo.hazelcast.pnlAggregator.aggregators.attrBased;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.foo.hazelcast.pnlAggregator.model.Pnl;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.hazelcast.aggregation.Aggregator;

public class DayLocalSumGroupByBookAggregator extends Aggregator<Map.Entry<Long, Pnl>, Map<Integer, Double>> {

    private static final long serialVersionUID = 1L;

    Map<Integer, Double> bookToSumOfDayLocal = new HashMap<>();;

    @Override
    public void accumulate(Entry<Long, Pnl> input) {
        Integer bookId = input.getValue().getPnlKey().getBookId();
        Double sum = bookToSumOfDayLocal.get(bookId);
        if (sum == null) {
            sum = 0d;
        }
        if(input.getValue().getDayLocal() != null ){
            sum += input.getValue().getDayLocal();
        }
        bookToSumOfDayLocal.put(bookId, sum);
        System.out.println("--------------ACCUMULATE------------------------------");
        System.out.println("Current :"+bookToSumOfDayLocal.get(bookId));
        System.out.println("Input :"+input.getValue());
        System.out.println("Sum: "+sum);
        System.out.println("Current state : "+bookToSumOfDayLocal);
        System.out.println("--------------------------------------------");
    }

    @Override
    public void combine(Aggregator aggregator) {
        DayLocalSumGroupByBookAggregator other = (DayLocalSumGroupByBookAggregator) aggregator;
        // Careful about storing the keyset to a local variable. later when you are getting the intersection, the
        // keyset, which is a view is getting updated because we do a putall for adding the entries for symmetric
        // difference
        Set<Integer> otherKeys = new HashSet<>(other.bookToSumOfDayLocal.keySet());
        Set<Integer> thisKeys = new HashSet<>(this.bookToSumOfDayLocal.keySet());
        System.out.println("================COMBINE=======================");
        System.out.println("THIS :"+this.bookToSumOfDayLocal);
        System.out.println("OTHER :"+other.bookToSumOfDayLocal);
        
        System.out.println("This keys "+thisKeys);
        System.out.println("Other keys "+otherKeys);
        
        SetView<Integer> symmetricDifference = Sets.symmetricDifference(thisKeys, otherKeys);
        
        System.out.println("symm diff : "+symmetricDifference);
        
        Map<Integer, Double> entriesToBeAdded = other.bookToSumOfDayLocal.entrySet().stream()
                .filter(e -> symmetricDifference.contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        this.bookToSumOfDayLocal.putAll(entriesToBeAdded);
        
        System.out.println(entriesToBeAdded);
        System.out.println("This keys "+thisKeys);
        System.out.println("Other keys "+otherKeys);
        SetView<Integer> keysInBoth = Sets.intersection(thisKeys, otherKeys);
        System.out.println("Keys in both "+keysInBoth);
        for (Integer key : keysInBoth) {
            Double value = this.bookToSumOfDayLocal.get(key) + other.bookToSumOfDayLocal.get(key);
            this.bookToSumOfDayLocal.put(key, value);
        }
        System.out.println("THIS :"+this.bookToSumOfDayLocal);
        System.out.println("OTHER :"+other.bookToSumOfDayLocal);
        System.out.println("===============================================");
    }

    @Override
    public Map<Integer, Double> aggregate() {
        return bookToSumOfDayLocal;
    }

}