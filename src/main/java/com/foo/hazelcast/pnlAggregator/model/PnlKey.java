package com.foo.hazelcast.pnlAggregator.model;


import java.io.Serializable;
import java.util.Date;

import com.foo.hazelcast.pnlAggregator.extractable.AbstractCustomExtractable;
import com.foo.hazelcast.pnlAggregator.extractable.FieldExtractable;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.AttributeType;

public class PnlKey extends AbstractCustomExtractable implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static enum Field implements FieldExtractable {
		
		DATE_FIELD("date"),
		BOOK_ID_FIELD("bookId"),
		CUSTODIAN_ACCOUNT_FIELD("custodianAccount"),
		BUNDLE_ID_FIELD("bundleId");
		
		private final String fieldName;
		
		Field(String fieldName){
			this.fieldName = fieldName;
		}
		
		public String getFieldName(){
			return this.fieldName;
		}
		
		public String getClassName() {
			return "pnlKey";
		}
	}
//    public static String DATE_FIELD = "date";

//    public static String BOOK_ID_FIELD = "bookId";

//    public static String CUSTODIAN_ACCOUNT_FIELD = "custodianAccount";

//    public static String BUNDLE_ID_FIELD = "bundleId";
    
    private String custodianAccount;

    private Integer bookId;

    private Integer pnlSpn;

    private Date date;

    private Integer bundleId;

    private String denomination;

    private String financialAccountId;

    private String basketInvestment;

    private String roletrackingInv;

    private String inventoryState;

    private Date knowledgeDate;

    private Short taxlotType;

    public String getCustodianAccount() {
        return custodianAccount;
    }

    public void setCustodianAccount(String custodianAccount) {
        this.custodianAccount = custodianAccount;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getPnlSpn() {
        return pnlSpn;
    }

    public void setPnlSpn(Integer pnlSpn) {
        this.pnlSpn = pnlSpn;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getBundleId() {
        return bundleId;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getFinancialAccountId() {
        return financialAccountId;
    }

    public void setFinancialAccountId(String financialAccountId) {
        this.financialAccountId = financialAccountId;
    }

    public String getBasketInvestment() {
        return basketInvestment;
    }

    public void setBasketInvestment(String basketInvestment) {
        this.basketInvestment = basketInvestment;
    }

    public String getRoletrackingInv() {
        return roletrackingInv;
    }

    public void setRoletrackingInv(String roletrackingInv) {
        this.roletrackingInv = roletrackingInv;
    }

    public String getInventoryState() {
        return inventoryState;
    }

    public void setInventoryState(String inventoryState) {
        this.inventoryState = inventoryState;
    }

    public Date getKnowledgeDate() {
        return knowledgeDate;
    }

    public void setKnowledgeDate(Date knowledgeDate) {
        this.knowledgeDate = knowledgeDate;
    }

    public Short getTaxlotType() {
        return taxlotType;
    }

    public void setTaxlotType(Short taxlotType) {
        this.taxlotType = taxlotType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((basketInvestment == null) ? 0 : basketInvestment.hashCode());
        result = prime * result + ((bookId == null) ? 0 : bookId.hashCode());
        result = prime * result + ((bundleId == null) ? 0 : bundleId.hashCode());
        result = prime * result + ((custodianAccount == null) ? 0 : custodianAccount.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((denomination == null) ? 0 : denomination.hashCode());
        result = prime * result + ((financialAccountId == null) ? 0 : financialAccountId.hashCode());
        result = prime * result + ((inventoryState == null) ? 0 : inventoryState.hashCode());
        result = prime * result + ((knowledgeDate == null) ? 0 : knowledgeDate.hashCode());
        result = prime * result + ((pnlSpn == null) ? 0 : pnlSpn.hashCode());
        result = prime * result + ((roletrackingInv == null) ? 0 : roletrackingInv.hashCode());
        result = prime * result + ((taxlotType == null) ? 0 : taxlotType.hashCode());
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
        PnlKey other = (PnlKey) obj;
        if (basketInvestment == null) {
            if (other.basketInvestment != null)
                return false;
        } else if (!basketInvestment.equals(other.basketInvestment))
            return false;
        if (bookId == null) {
            if (other.bookId != null)
                return false;
        } else if (!bookId.equals(other.bookId))
            return false;
        if (bundleId == null) {
            if (other.bundleId != null)
                return false;
        } else if (!bundleId.equals(other.bundleId))
            return false;
        if (custodianAccount == null) {
            if (other.custodianAccount != null)
                return false;
        } else if (!custodianAccount.equals(other.custodianAccount))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (denomination == null) {
            if (other.denomination != null)
                return false;
        } else if (!denomination.equals(other.denomination))
            return false;
        if (financialAccountId == null) {
            if (other.financialAccountId != null)
                return false;
        } else if (!financialAccountId.equals(other.financialAccountId))
            return false;
        if (inventoryState == null) {
            if (other.inventoryState != null)
                return false;
        } else if (!inventoryState.equals(other.inventoryState))
            return false;
        if (knowledgeDate == null) {
            if (other.knowledgeDate != null)
                return false;
        } else if (!knowledgeDate.equals(other.knowledgeDate))
            return false;
        if (pnlSpn == null) {
            if (other.pnlSpn != null)
                return false;
        } else if (!pnlSpn.equals(other.pnlSpn))
            return false;
        if (roletrackingInv == null) {
            if (other.roletrackingInv != null)
                return false;
        } else if (!roletrackingInv.equals(other.roletrackingInv))
            return false;
        if (taxlotType == null) {
            if (other.taxlotType != null)
                return false;
        } else if (!taxlotType.equals(other.taxlotType))
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

    @Override
    public String toString() {
        return "PnlKey [custodianAccount=" + custodianAccount + ", bookId=" + bookId + ", pnlSpn=" + pnlSpn + ", date="
                + date + ", bundleId=" + bundleId + ", denomination=" + denomination + ", financialAccountId="
                + financialAccountId + ", basketInvestment=" + basketInvestment + ", roletrackingInv=" + roletrackingInv
                + ", inventoryState=" + inventoryState + ", knowledgeDate=" + knowledgeDate + ", taxlotType="
                + taxlotType + "]";
    }
    
}