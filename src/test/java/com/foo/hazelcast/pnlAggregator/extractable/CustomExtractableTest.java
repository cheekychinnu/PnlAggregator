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
        
        assertNull(pnl.getAttributeValue(Pnl.Field.BOOK_ID_FIELD.getFieldName()));
        assertNull(pnl.getAttributeValue(Pnl.Field.BUNDLE_ID_FIELD.getFieldName()));
        assertNull(pnl.getAttributeValue(Pnl.Field.CUSTODIAN_ACCOUNT_FIELD.getFieldName()));
        assertNull(pnl.getAttributeValue(Pnl.Field.DATE_FIELD.getFieldName()));
        
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
        
        pnl.setAttributeValue(Pnl.Field.BOOK_ID_FIELD.getFieldName(), integerValue);
        pnl.setAttributeValue(Pnl.Field.BUNDLE_ID_FIELD.getFieldName(), integerValue);
        assertEquals(integerValue, pnl.getAttributeValue(Pnl.Field.BOOK_ID_FIELD.getFieldName()));
        assertEquals(integerValue, pnl.getAttributeValue(Pnl.Field.BUNDLE_ID_FIELD.getFieldName()));
        
        String stringValue = "dummy";
        pnl.setAttributeValue(Pnl.Field.CUSTODIAN_ACCOUNT_FIELD.getFieldName(), stringValue);
        assertEquals(stringValue, pnl.getAttributeValue(Pnl.Field.CUSTODIAN_ACCOUNT_FIELD.getFieldName()));

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

        assertEquals(AttributeType.INTEGER, pnl.getAttributeType(Pnl.Field.BOOK_ID_FIELD.getFieldName()));
        assertEquals(AttributeType.INTEGER, pnl.getAttributeType(Pnl.Field.BUNDLE_ID_FIELD.getFieldName()));
    }

    public void assertAtrributeTypeForUnsupportedTypes(Pnl pnl) {
        expectedException.expect(QueryException.class);
        expectedException.expectMessage(StringContains
                .containsString(PnlKey.Field.CUSTODIAN_ACCOUNT_FIELD.getFieldName() + "'s type is not supported in aggregation as of now"));
        pnl.getAttributeType(Pnl.Field.CUSTODIAN_ACCOUNT_FIELD.getFieldName());

        expectedException.expect(QueryException.class);
        expectedException.expect(
                StringContains.containsString(PnlKey.Field.DATE_FIELD.getFieldName() + "'s type is not supported in aggregation as of now"));
        pnl.getAttributeType(Pnl.Field.DATE_FIELD.getFieldName());

    }

    private void assertGetFieldType(Pnl pnl) {

        assertEquals(Integer.class, pnl.getFieldType(Pnl.Field.BOOK_ID_FIELD.getFieldName()));
        assertEquals(Integer.class, pnl.getFieldType(Pnl.Field.BUNDLE_ID_FIELD.getFieldName()));
        assertEquals(String.class, pnl.getFieldType(Pnl.Field.CUSTODIAN_ACCOUNT_FIELD.getFieldName()));
        assertEquals(Date.class, pnl.getFieldType(Pnl.Field.DATE_FIELD.getFieldName()));

        assertEquals(Double.class, pnl.getFieldType(Pnl.DAY_LOCAL_FIELD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.DAY_USD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.LAST_DAY_LOCAL_FIELD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.LAST_DAY_USD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.LAST_DAY_MONTH_LOCAL_FIELD));
        assertEquals(Double.class, pnl.getFieldType(Pnl.LAST_DAY_MONTH_USD));

    }

    private void assertGetAttributeValue(Pnl pnl) {

        assertEquals(pnl.getPnlKey().getBookId(), pnl.getAttributeValue(Pnl.Field.BOOK_ID_FIELD.getFieldName()));
        assertEquals(pnl.getPnlKey().getBundleId(), pnl.getAttributeValue(Pnl.Field.BUNDLE_ID_FIELD.getFieldName()));
        assertEquals(pnl.getPnlKey().getCustodianAccount(), pnl.getAttributeValue(Pnl.Field.CUSTODIAN_ACCOUNT_FIELD.getFieldName()));
        assertEquals(pnl.getPnlKey().getDate(), pnl.getAttributeValue(Pnl.Field.DATE_FIELD.getFieldName()));

        assertEquals(pnl.getDayLocal(), pnl.getAttributeValue(Pnl.DAY_LOCAL_FIELD));
        assertEquals(pnl.getDayUsd(), pnl.getAttributeValue(Pnl.DAY_USD));
        assertEquals(pnl.getLastDayLocal(), pnl.getAttributeValue(Pnl.LAST_DAY_LOCAL_FIELD));
        assertEquals(pnl.getLastDayUsd(), pnl.getAttributeValue(Pnl.LAST_DAY_USD));
        assertEquals(pnl.getLastDayMonthLocal(), pnl.getAttributeValue(Pnl.LAST_DAY_MONTH_LOCAL_FIELD));
        assertEquals(pnl.getLastDayMonthUsd(), pnl.getAttributeValue(Pnl.LAST_DAY_MONTH_USD));

    }
}