package com.foo.hazelcast.pnlAggregator.model;


import java.io.Serializable;
import java.util.Arrays;

import com.foo.hazelcast.pnlAggregator.extractable.AbstractCustomExtractable;
import com.foo.hazelcast.pnlAggregator.extractable.FieldExtractable;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.AttributeType;

public class Pnl extends  AbstractCustomExtractable implements Serializable {

	public static enum Field implements FieldExtractable {

		DATE_FIELD(PnlKey.Field.DATE_FIELD.getFullyQualifiedFieldName()), 
		BOOK_ID_FIELD(PnlKey.Field.BOOK_ID_FIELD.getFullyQualifiedFieldName()), 
		CUSTODIAN_ACCOUNT_FIELD(PnlKey.Field.CUSTODIAN_ACCOUNT_FIELD.getFullyQualifiedFieldName()), 
		BUNDLE_ID_FIELD(PnlKey.Field.BUNDLE_ID_FIELD.getFullyQualifiedFieldName()), 
		DAY_LOCAL_FIELD("dayLocal"), 
		LAST_DAY_LOCAL_FIELD("lastDayLocal"),
		DAY_USD("dayUsd"),
		LAST_DAY_USD("lastDayUsd"),
		LAST_DAY_MONTH_LOCAL_FIELD("lastDayMonthLocal"),
		LAST_DAY_MONTH_USD("lastDayMonthUsd"),
		LAST_YEAR_LOCAL_FIELD("lastYearLocal"),
		LAST_YEAR_USD("lastYearUsd");
		
		private final String fieldName;

		Field(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return this.fieldName;
		}

		public String getClassName() {
			return "pnl";
		}
	}
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

//    public static String DATE_FIELD = String.join(".", "pnlKey", PnlKey.DATE_FIELD);

//    public static String BOOK_ID_FIELD = String.join(".", "pnlKey", PnlKey.BOOK_ID_FIELD);

//    public static String CUSTODIAN_ACCOUNT_FIELD = String.join(".", "pnlKey", PnlKey.CUSTODIAN_ACCOUNT_FIELD);

//    public static String BUNDLE_ID_FIELD = String.join(".", "pnlKey", PnlKey.BUNDLE_ID_FIELD);

//    public static String DAY_LOCAL_FIELD = "dayLocal";
//    
//    public static String DAY_USD = "dayUsd";
//    
//    public static String LAST_DAY_LOCAL_FIELD = "lastDayLocal";
//    
//    public static String LAST_DAY_USD = "lastDayUsd";
//    
//    public static String LAST_DAY_MONTH_LOCAL_FIELD = "lastDayMonthLocal";
    
//    public static String LAST_DAY_MONTH_USD = "lastDayMonthUsd";
//    
//    public static String LAST_YEAR_LOCAL_FIELD = "lastYearLocal";
//    
//    public static String LAST_YEAR_USD = "lastYearUsd";

    private long id;

    // Key contributor
    private PnlKey pnlKey;

    // Values
    private Double dayLocal;

    private Double dayUsd;

    private Double lastDayLocal;

    private Double lastDayUsd;

    private Double lastDayMonthLocal;

    private Double lastDayMonthUsd;

    private Double[] monthsUsd = new Double[12];

    private Double[] monthsLocal = new Double[12];

    private Double lastYearLocal;

    private Double lastYearUsd;

    public Double getDayLocal() {
        return dayLocal;
    }

    public void setDayLocal(Double dayLocal) {
        this.dayLocal = dayLocal;
    }

    public Double getDayUsd() {
        return dayUsd;
    }

    public void setDayUsd(Double dayUsd) {
        this.dayUsd = dayUsd;
    }

    public Double getLastDayLocal() {
        return lastDayLocal;
    }

    public void setLastDayLocal(Double lastDayLocal) {
        this.lastDayLocal = lastDayLocal;
    }

    public Double getLastDayUsd() {
        return lastDayUsd;
    }

    public void setLastDayUsd(Double lastDayUsd) {
        this.lastDayUsd = lastDayUsd;
    }

    public Double getLastDayMonthLocal() {
        return lastDayMonthLocal;
    }

    public void setLastDayMonthLocal(Double lastDayMonthLocal) {
        this.lastDayMonthLocal = lastDayMonthLocal;
    }

    public Double getLastDayMonthUsd() {
        return lastDayMonthUsd;
    }

    public void setLastDayMonthUsd(Double lastDayMonthUsd) {
        this.lastDayMonthUsd = lastDayMonthUsd;
    }

    public Double[] getMonthsUsd() {
        return monthsUsd;
    }

    public void setMonthsUsd(Double[] monthsUsd) {
        this.monthsUsd = monthsUsd;
    }

    public Double[] getMonthsLocal() {
        return monthsLocal;
    }

    public void setMonthsLocal(Double[] monthsLocal) {
        this.monthsLocal = monthsLocal;
    }

    public Double getLastYearLocal() {
        return lastYearLocal;
    }

    public void setLastYearLocal(Double lastYearLocal) {
        this.lastYearLocal = lastYearLocal;
    }

    public Double getLastYearUsd() {
        return lastYearUsd;
    }

    public void setLastYearUsd(Double lastYearUsd) {
        this.lastYearUsd = lastYearUsd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PnlKey getPnlKey() {
        return pnlKey;
    }

    public void setPnlKey(PnlKey pnlKey) {
        this.pnlKey = pnlKey;
    }

    private String intern(String input) {
        return input == null ? null : input.intern();
    }

    @Override
    public String toString() {
        return "Pnl [id=" + id + ", pnlKey=" + pnlKey + ", dayLocal=" + dayLocal + ", dayUsd=" + dayUsd
                + ", lastDayLocal=" + lastDayLocal + ", lastDayUsd=" + lastDayUsd + ", lastDayMonthLocal="
                + lastDayMonthLocal + ", lastDayMonthUsd=" + lastDayMonthUsd + ", monthsUsd="
                + Arrays.toString(monthsUsd) + ", monthsLocal=" + Arrays.toString(monthsLocal) + ", lastYearLocal="
                + lastYearLocal + ", lastYearUsd=" + lastYearUsd + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dayLocal == null) ? 0 : dayLocal.hashCode());
        result = prime * result + ((dayUsd == null) ? 0 : dayUsd.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((lastDayLocal == null) ? 0 : lastDayLocal.hashCode());
        result = prime * result + ((lastDayMonthLocal == null) ? 0 : lastDayMonthLocal.hashCode());
        result = prime * result + ((lastDayMonthUsd == null) ? 0 : lastDayMonthUsd.hashCode());
        result = prime * result + ((lastDayUsd == null) ? 0 : lastDayUsd.hashCode());
        result = prime * result + ((lastYearLocal == null) ? 0 : lastYearLocal.hashCode());
        result = prime * result + ((lastYearUsd == null) ? 0 : lastYearUsd.hashCode());
        result = prime * result + Arrays.hashCode(monthsLocal);
        result = prime * result + Arrays.hashCode(monthsUsd);
        result = prime * result + ((pnlKey == null) ? 0 : pnlKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pnl other = (Pnl) obj;
        if (dayLocal == null) {
            if (other.dayLocal != null)
                return false;
        } else if (!dayLocal.equals(other.dayLocal))
            return false;
        if (dayUsd == null) {
            if (other.dayUsd != null)
                return false;
        } else if (!dayUsd.equals(other.dayUsd))
            return false;
        if (id != other.id)
            return false;
        if (lastDayLocal == null) {
            if (other.lastDayLocal != null)
                return false;
        } else if (!lastDayLocal.equals(other.lastDayLocal))
            return false;
        if (lastDayMonthLocal == null) {
            if (other.lastDayMonthLocal != null)
                return false;
        } else if (!lastDayMonthLocal.equals(other.lastDayMonthLocal))
            return false;
        if (lastDayMonthUsd == null) {
            if (other.lastDayMonthUsd != null)
                return false;
        } else if (!lastDayMonthUsd.equals(other.lastDayMonthUsd))
            return false;
        if (lastDayUsd == null) {
            if (other.lastDayUsd != null)
                return false;
        } else if (!lastDayUsd.equals(other.lastDayUsd))
            return false;
        if (lastYearLocal == null) {
            if (other.lastYearLocal != null)
                return false;
        } else if (!lastYearLocal.equals(other.lastYearLocal))
            return false;
        if (lastYearUsd == null) {
            if (other.lastYearUsd != null)
                return false;
        } else if (!lastYearUsd.equals(other.lastYearUsd))
            return false;
        if (!Arrays.equals(monthsLocal, other.monthsLocal))
            return false;
        if (!Arrays.equals(monthsUsd, other.monthsUsd))
            return false;
        if (pnlKey == null) {
            if (other.pnlKey != null)
                return false;
        } else if (!pnlKey.equals(other.pnlKey))
            return false;
        return true;
    }

    @Override
    public Object getAttributeValue(String attributeName) throws QueryException {
        return this.getAttributeValue(attributeName, this.getClass(), this);
    }

    @Override
    public AttributeType getAttributeType(String attributeName) throws QueryException {
        return getAttributeType(attributeName, this.getClass());
    }

    @Override
    public void setAttributeValue(String attributeName, Object value) throws QueryException {
        this.setAttributeValue(attributeName, value, this.getClass(), this);
    }

    @Override
    public Class<?> getFieldType(String attributeName) throws QueryException {
        return getFieldType(attributeName, this.getClass());
    }
    
}