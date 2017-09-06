package com.foo.hazelcast.pnlAggregator.extractable;

public interface FieldExtractable {
	
	String getFieldName();
	String getClassName();
	default String concat(String a, String b){
		return String.join(".", a, b);
	}
	default String getFullyQualifiedFieldName() {
		return concat(getClassName(), getFieldName());
	}

}
