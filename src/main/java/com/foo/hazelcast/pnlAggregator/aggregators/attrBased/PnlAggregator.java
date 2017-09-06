package com.foo.hazelcast.pnlAggregator.aggregators.attrBased;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.foo.hazelcast.pnlAggregator.aggregators.basic.SumAggregator;
import com.foo.hazelcast.pnlAggregator.extractable.FieldExtractable;
import com.foo.hazelcast.pnlAggregator.factory.SumAggregatorFactory;
import com.foo.hazelcast.pnlAggregator.model.Pnl;
import com.foo.hazelcast.pnlAggregator.model.PnlKey;
import com.google.common.base.Defaults;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.hazelcast.aggregation.Aggregator;

public class PnlAggregator extends Aggregator<Map.Entry<Long, Pnl>, Map<PnlKey, Pnl>> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Map<PnlKey, Pnl> keyToPnl = new HashMap<>();

    private List<FieldExtractable> keyAttributesInPnl;
    
    private List<String> attributesToBeAggregatedInPnl;

    public PnlAggregator(List<Pnl.Field> keyAttributes, String[] attributes) {
        if(keyAttributes == null ||keyAttributes.isEmpty() || attributes == null ||attributes.length == 0) {
            throw new IllegalArgumentException("Key attributes and Aggregation attributes are mandatory for PnlAggregator");
        }
        this.keyAttributesInPnl = new ArrayList<>(keyAttributes);
        this.attributesToBeAggregatedInPnl = Stream.of(attributes).collect(Collectors.toList());
    }

    @Override
    public void accumulate(Entry<Long, Pnl> input) {
        System.out.println("--------------ACCUMULATE------------------------------");
        System.out.println("Current :"+this.keyToPnl);
        System.out.println("Input :"+input.getValue());
        
        Pnl pnl = input.getValue();
        PnlKey pnlKey = getKey(pnl);
        Pnl pnlForKey = keyToPnl.get(pnlKey);
        if (pnlForKey == null) {
            pnlForKey = new Pnl();
            pnlForKey.setPnlKey(pnlKey);
            System.out.println("Key formed : "+pnlKey);
            setInitialValuesForPnl(pnlForKey);
            System.out.println("Aggregator initial :"+pnlForKey);
            keyToPnl.put(pnlKey, pnlForKey);
        }

        try {
            mergePnl(pnl, pnlForKey);
            System.out.println("After merging :"+pnlForKey);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        
        System.out.println("--------------------------------------------");
    }

    private PnlKey getKey(Pnl pnl) {
        if(keyAttributesInPnl == null || keyAttributesInPnl.isEmpty()){
            return pnl.getPnlKey();
        }
        Pnl partialPnl = new Pnl();
        // find a solution for this.
        partialPnl.setPnlKey(new PnlKey());
        for (FieldExtractable field: this.keyAttributesInPnl) {
        	String attr = field.getFieldName();
            Object attributeValue = pnl.getAttributeValue(attr);
            partialPnl.setAttributeValue(attr, attributeValue);
        }
        return partialPnl.getPnlKey();
    }

    private void setInitialValuesForPnl(Pnl pnl) {
        for (String attr : this.attributesToBeAggregatedInPnl) {
            Class<?> fieldType = pnl.getFieldType(attr);
            // this is actually usueful when the field type is primitive. that's why in the aggregator class, we have a special null check
            Object defaultValue = Defaults.defaultValue(fieldType);
            System.out.println("Default value for "+fieldType+" is "+defaultValue+" Attr:"+attr);
            pnl.setAttributeValue(attr, defaultValue);
        }
    }

    private void mergePnl(Pnl source, Pnl destination) throws IllegalAccessException {
        for (String attr : this.attributesToBeAggregatedInPnl) {
            Object aggregateAttribute = aggregateAttribute(attr, source, destination);
            destination.setAttributeValue(attr, aggregateAttribute);
        }
    }

    @SuppressWarnings("unchecked")
    private  Object aggregateAttribute(String attr, Pnl source, Pnl destination) throws IllegalAccessException {
        // null check for values?
        Class<?> fieldType = source.getFieldType(attr);
        Object a = source.getAttributeValue(attr);
        Object b = destination.getAttributeValue(attr);
        
        SumAggregator sumAggregator = SumAggregatorFactory.getSumAggregator(fieldType);
        return sumAggregator.sum(a, b);
    }

    @Override
    public void combine(Aggregator aggregator) {
        PnlAggregator pnlAggregator = (PnlAggregator) aggregator;
        
        Set<PnlKey> otherKeys = new HashSet<>(pnlAggregator.keyToPnl.keySet());
        Set<PnlKey> thisKeys = new HashSet<>(this.keyToPnl.keySet());

        SetView<PnlKey> symmetricDifference = Sets.symmetricDifference(thisKeys, otherKeys);
        Map<PnlKey, Pnl> entriesToBeAdded = pnlAggregator.keyToPnl.entrySet().stream()
                .filter(e -> symmetricDifference.contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        this.keyToPnl.putAll(entriesToBeAdded);

        SetView<PnlKey> keysInBoth = Sets.intersection(thisKeys, otherKeys);
        for (PnlKey key : keysInBoth) {
            try {
                mergePnl(pnlAggregator.keyToPnl.get(key), this.keyToPnl.get(key));
            } catch (IllegalAccessException e1) {
                throw new RuntimeException(e1.getMessage());
            }
        }
    }

    @Override
    public Map<PnlKey, Pnl> aggregate() {
        return keyToPnl;
    }

}