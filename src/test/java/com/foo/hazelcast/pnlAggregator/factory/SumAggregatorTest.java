package com.foo.hazelcast.pnlAggregator.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.foo.hazelcast.pnlAggregator.aggregators.basic.DoubleSumAggregator;
import com.foo.hazelcast.pnlAggregator.aggregators.basic.IntegerSumAggregator;
import com.foo.hazelcast.pnlAggregator.aggregators.basic.LongSumAggregator;
import com.foo.hazelcast.pnlAggregator.aggregators.basic.SumAggregator;
import com.foo.hazelcast.pnlAggregator.config.PnlRandomizerConfiguration;
import com.foo.hazelcast.pnlAggregator.model.Pnl;

import io.github.benas.randombeans.api.EnhancedRandom;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PnlRandomizerConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class SumAggregatorTest {

    @Autowired
    private EnhancedRandom enhancedRandomForPnl;

    @Test
    public void testAggregatorForInteger() {
        Integer a = 10;
        Integer b = 15;
        SumAggregator<Integer> sumAggregator = SumAggregatorFactory.getSumAggregator(Integer.class);
        assertTrue(sumAggregator.getClass().equals(IntegerSumAggregator.class));
        Integer sum = sumAggregator.sum(a, b);
        assertNotNull(sum);
        assertEquals(new Integer(a + b), sum);
    }

    @Test
    public void testAggregatorForLong() {
        Long a = 10l;
        Long b = 15l;
        SumAggregator<Long> sumAggregator = SumAggregatorFactory.getSumAggregator(Long.class);
        assertTrue(sumAggregator.getClass().equals(LongSumAggregator.class));
        Long sum = sumAggregator.sum(a, b);
        assertNotNull(sum);
        assertEquals(new Long(a + b), sum);
    }

    @Test
    public void testAggregatorForDouble() {
        Double a = 10d;
        Double b = 15d;
        SumAggregator<Double> sumAggregator = SumAggregatorFactory.getSumAggregator(Double.class);
        assertTrue(sumAggregator.getClass().equals(DoubleSumAggregator.class));
        Double sum = sumAggregator.sum(a, b);
        assertNotNull(sum);
        assertEquals(new Double(a + b), sum);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAggregatorForNumber() {
        SumAggregatorFactory.getSumAggregator(Number.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAggregatorForString() {
        SumAggregatorFactory.getSumAggregator(String.class);
    }

    @Test
    public void testForPnl() {
        Pnl source = enhancedRandomForPnl.nextObject(Pnl.class);
        Pnl destination = enhancedRandomForPnl.nextObject(Pnl.class);
        
        Double expectedSum =  source.getDayLocal() + destination.getDayLocal();
                
        String attr = Pnl.Field.DAY_LOCAL_FIELD.getFieldName();
        
        Class<?> fieldType = source.getFieldType(attr);
        assertTrue(fieldType.equals(Double.class));
        
        Object a = source.getAttributeValue(attr);
        Object b = destination.getAttributeValue(attr);

        SumAggregator sumAggregator = SumAggregatorFactory.getSumAggregator(fieldType);
        assertTrue(sumAggregator.getClass().equals(DoubleSumAggregator.class));
        Object sum = sumAggregator.sum(a, b);
        assertEquals(expectedSum, sum);
    }
}