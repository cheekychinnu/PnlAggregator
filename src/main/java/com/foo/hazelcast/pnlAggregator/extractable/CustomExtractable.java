package com.foo.hazelcast.pnlAggregator.extractable;

import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.Extractable;

public interface CustomExtractable extends Extractable{

    void setAttributeValue(String attributeName, Object value) throws QueryException;
    
    Class<?> getFieldType(String attributeName) throws QueryException;
}
