package com.foo.hazelcast.pnlAggregator.extractable;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.foo.hazelcast.pnlAggregator.config.PnlRandomizerConfiguration;
import com.foo.hazelcast.pnlAggregator.model.Pnl;
import com.foo.hazelcast.pnlAggregator.model.PnlKey;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.AttributeType;

import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PnlRandomizerConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class CustomExtractableTest {

    @Autowired
    private EnhancedRandom enhancedRandomForPnl;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCustomExtractableForPnl() {
        Pnl pnl = enhancedRandomForPnl.nextObject(Pnl.class);

        assertGetAttributeValue(pnl);
        assertGetAttributeType(pnl);
        assertGetFieldType(pnl);
        assertAtrributeTypeForUnsupportedTypes(pnl);
        assertSetAttributeValue(pnl);
    }

    @Test
    public void testCustomExtractableForPnlWherePnlKeyIsNull() {
        Pnl pnl = enhancedRandomForPnl.nextObject(Pnl.class);
        pnl.setPnlKey(null);
        
        assertNull(pnl.getAttributeValue(Pnl.BOOK_ID_FIELD));
        assertNull(pnl.getAttributeValue(Pnl.BUNDLE_ID_FIELD));
        assertNull(pnl.getAttributeValue(Pnl.CUSTODIAN_ACCOUNT_FIELD));
        assertNull(pnl.getAttributeValue(Pnl.DATE_FIELD));
        
        // attribute type should not matter if the nested object is null. since you can still query for type if the value is null
        assertGetAttributeType(pnl);
        // field type should not matter if the nested object is null. since you can still query for type if the value is null
        assertGetFieldType(pnl);
        assertAtrributeTypeForUnsupportedTypes(pnl);
        // set attribute of bookId in null pnlkey should not throw npe or any exception
        assertSetAttributeValue(pnl);
    }
    
    
    private void assertSetAttributeValue(Pnl pnl) {
        Integer integerValue = 100;
        
        pnl.setAttributeValue(Pnl.BOOK_ID_FIELD, integerValue);
        pnl.setAttributeValue(Pnl.BUNDLE_ID_FIELD, integerValue);
        assertEquals(integerValue, pnl.getAttributeValue(Pnl.BOOK_ID_FIELD));
        assertEquals(integerValue, pnl.getAttributeValue(Pnl.BUNDLE_ID_FIELD));
        
        String stringValue = "dummy";
        pnl.setAttributeValue(Pnl.CUSTODIAN_ACCOUNT_FIELD, stringValue);
        assertEquals(stringValue, pnl.getAttributeValue(Pnl.CUSTODIAN_ACCOUNT_FIELD));

        Double doubleValue = 900d;
        
        pnl.setAttributeValue(Pnl.DAY_LOCAL_FIELD,doubleValue);
        pnl.setAttributeValue(Pnl.DAY_USD,doubleValue);
        pnl.setAttributeValue(Pnl.LAST_DAY_LOCAL_FIELD,doubleValue);
        pnl.setAttributeValue(Pnl.LAST_DAY_USD,doubleValue);
        pnl.setAttributeValue(Pnl.LAST_DAY_MONTH_LOCAL_FIELD,doubleValue);
        pnl.setAttributeValue(Pnl.LAST_DAY_MONTH_USD,doubleValue);
        
        assertEquals(doubleValue, pnl.getAttributeValue(Pnl.DAY_LOCAL_FIELD));
        assertEquals(doubleValue, pnl.getAttributeValue(Pnl.DAY_USD));
        assertEquals(doubleValue, pnl.getAttributeValue(Pnl.LAST_DAY_LOCAL_FIELD));
        assertEquals(doubleValue, pnl.getAttributeValue(Pnl.LAST_DAY_USD));
        assertEquals(doubleValue, pnl.getAttributeValue(Pnl.LAST_DAY_MONTH_LOCAL_FIELD));
        assertEquals(doubleValue, pnl.getAttributeValue(Pnl.LAST_DAY_MONTH_USD));

    }

    private void assertGetAttributeType(Pnl pnl) {

        assertEquals(AttributeType.DOUBLE, pnl.getAttributeType(Pnl.DAY_LOCAL_FIELD));
        assertEquals(AttributeType.DOUBLE, pnl.getAttributeType(Pnl.DAY_USD));
        assertEquals(AttributeType.DOUBLE, pnl.getAttributeType(Pnl.LAST_DAY_LOCAL_FIELD));
        assertEquals(AttributeType.DOUBLE, pnl.getAttributeType(Pnl.LAST_DAY_USD));
        assertEquals(AttributeType.DOUBLE, pnl.getAttributeType(Pnl.LAST_DAY_MONTH_LOCAL_FIELD));
        assertEquals(AttributeType.DOUBLE, pnl.getAttributeType(Pnl.LAST_DAY_MONTH_USD));

        assertEquals(AttributeType.INTEGER, pnl.getAttributeType(Pnl.BOOK_ID_FIELD));
        assertEquals(AttributeType.INTEGER, pnl.getAttributeType(Pnl.BUNDLE_ID_FIELD));
    }

    public void assertAtrributeTypeForUnsupportedTypes(Pnl pnl) {
        expectedException.expect(QueryException.class);
        expectedException.expectMessage(StringContains
                .containsString(PnlKey.CUSTODIAN_ACCOUNT_FIELD + "'s type is not supported in aggregation as of now"));
        pnl.getAttributeType(Pnl.CUSTODIAN_ACCOUNT_FIELD);

        expectedException.expect(QueryException.class);
        expectedException.expect(
                StringContains.containsString(PnlKey.DATE_FIELD + "'s type is not supported in aggregation as of now"));
        pnl.getAttributeType(Pnl.DATE_FIELD);

    }

    private void assertGetFieldType(Pnl pnl) {

        assertEquals(Integer.class, pnl.getFieldType(Pnl.BOOK_ID_FIELD));
        assertEquals(Integer.class, pnl.getFieldType(Pnl.BUNDLE_ID_FIELD));
        assertEquals(String.class, pnl.getFieldType(Pnl.CUSTODIAN_ACCOUNT_FIELD));
        assertEquals(Date.class, pnl.getFieldType(Pnl.DATE_FIELD));

        assertEquals(Double.class, pnl.getFieldType(Pnl.DAY_LOCAL_FIELD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.DAY_USD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.LAST_DAY_LOCAL_FIELD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.LAST_DAY_USD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.LAST_DAY_MONTH_LOCAL_FIELD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.LAST_DAY_MONTH_USD));

    }

    private void assertGetAttributeValue(Pnl pnl) {

        assertEquals(pnl.getPnlKey().getBookId(), pnl.getAttributeValue(Pnl.BOOK_ID_FIELD));
        assertEquals(pnl.getPnlKey().getBundleId(), pnl.getAttributeValue(Pnl.BUNDLE_ID_FIELD));
        assertEquals(pnl.getPnlKey().getCustodianAccount(), pnl.getAttributeValue(Pnl.CUSTODIAN_ACCOUNT_FIELD));
        assertEquals(pnl.getPnlKey().getDate(), pnl.getAttributeValue(Pnl.DATE_FIELD));

        assertEquals(pnl.getDayLocal(), pnl.getAttributeValue(Pnl.DAY_LOCAL_FIELD));
        assertEquals(pnl.getDayUsd(), pnl.getAttributeValue(Pnl.DAY_USD));
        assertEquals(pnl.getLastDayLocal(), pnl.getAttributeValue(Pnl.LAST_DAY_LOCAL_FIELD));
        assertEquals(pnl.getLastDayUsd(), pnl.getAttributeValue(Pnl.LAST_DAY_USD));
        assertEquals(pnl.getLastDayMonthLocal(), pnl.getAttributeValue(Pnl.LAST_DAY_MONTH_LOCAL_FIELD));
        assertEquals(pnl.getLastDayMonthUsd(), pnl.getAttributeValue(Pnl.LAST_DAY_MONTH_USD));

    }
}