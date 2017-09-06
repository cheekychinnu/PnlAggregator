package com.foo.hazelcast.pnlAggregator.extractable;

import java.lang.reflect.Field;
import java.util.Arrays;

import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.AttributeType;

public abstract class AbstractCustomExtractable implements CustomExtractable {

	protected static Class<?> getFieldType(String attributeName, Class<?> candidateClass) throws QueryException {

		String[] split = attributeName.split("\\.");
		int length = split.length;

		try {
			if (length == 1) {
				Field field = candidateClass.getDeclaredField(attributeName);
				return field.getType();
			} else {
				Field field = candidateClass.getDeclaredField(split[0]);
				field.setAccessible(true);
				Class<?> type = field.getType();
				if (CustomExtractable.class.isAssignableFrom(type)) {
					return getFieldType(String.join(".", Arrays.copyOfRange(split, 1, length)), type);
				} else {
					throw new IllegalAccessException(split[0] + " is not Extractable");
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new QueryException(
					"There is no such field " + attributeName + " in " + candidateClass + " Got: " + e.getMessage());
		}

	}

	public void setAttributeValue(String attributeName, Object valueToSet, Class<?> candidateClass, Object obj)
			throws QueryException {

		String[] split = attributeName.split("\\.");
		int length = split.length;

		try {
			if (length == 1) {
				Field field = candidateClass.getDeclaredField(attributeName);
				field.setAccessible(true);
				field.set(this, valueToSet);
			} else {
				Field field = candidateClass.getDeclaredField(split[0]);
				field.setAccessible(true);
				Object nestedObject = field.get(obj);
				if (nestedObject == null) {
					nestedObject = field.getType().newInstance();
					field.set(obj, nestedObject);
				}
				if (nestedObject instanceof CustomExtractable) {
					AbstractCustomExtractable extractable = (AbstractCustomExtractable) nestedObject;
					extractable.setAttributeValue(String.join(".", Arrays.copyOfRange(split, 1, length)), valueToSet);
				} else {
					throw new IllegalAccessException(split[0] + " is not Extractable");
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new QueryException(
					"There is no such field " + attributeName + " in " + candidateClass + " Got: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new QueryException(e.getMessage());
		}

	}

	protected Object getAttributeValue(String attributeName, Class<?> candidateClass, Object obj)
			throws QueryException {

		String[] split = attributeName.split("\\.");
		int length = split.length;

		try {
			if (length == 1) {
				Field field = candidateClass.getDeclaredField(attributeName);
				field.setAccessible(true);
				return field.get(obj);
			} else {
				Field field = candidateClass.getDeclaredField(split[0]);
				field.setAccessible(true);
				Object object = field.get(obj);
				if (object == null) {
					return null;
				}
				if (object instanceof CustomExtractable) {
					CustomExtractable extractable = (CustomExtractable) object;
					return extractable.getAttributeValue(String.join(".", Arrays.copyOfRange(split, 1, length)));
				} else {
					throw new IllegalAccessException(split[0] + " is not Extractable");
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new QueryException(
					"There is no such field " + attributeName + " in " + candidateClass + " Got: " + e.getMessage());
		}
	}

	protected static AttributeType getAttributeType(String attributeName, Class<?> candidateClass)
			throws QueryException {
		String[] split = attributeName.split("\\.");
		int length = split.length;

		try {
			if (length == 1) {
				Field field = candidateClass.getDeclaredField(attributeName);
				return getAttributeType(field);
			} else {
				Field field = candidateClass.getDeclaredField(split[0]);
				field.setAccessible(true);
				Class<?> type = field.getType();
				if (CustomExtractable.class.isAssignableFrom(type)) {
					return getAttributeType(String.join(".", Arrays.copyOfRange(split, 1, length)), type);
				}
				throw new IllegalArgumentException("Attribute  " + split[0] + " is not extractable");
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new QueryException(
					"There is no such field " + attributeName + " in " + candidateClass + " Got: " + e.getMessage());
		}
	}

	private static AttributeType getAttributeType(Field field) throws IllegalAccessException {
		Class<?> type = field.getType();
		if (type == Double.class) {
			return AttributeType.DOUBLE;
		}
		if (type == Integer.class) {
			return AttributeType.INTEGER;
		}
		if (type == Long.class) {
			return AttributeType.LONG;
		}
		throw new IllegalAccessException(field.getName() + "'s type is not supported in aggregation as of now");
	}
}
