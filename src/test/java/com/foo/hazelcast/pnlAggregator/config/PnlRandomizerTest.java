package com.foo.hazelcast.pnlAggregator.config;

import static com.foo.hazelcast.pnlAggregator.util.DateUtil.getModifiedDate;
import static com.foo.hazelcast.pnlAggregator.util.DateUtil.getPreviousMonthEndDate;
import static com.foo.hazelcast.pnlAggregator.util.DateUtil.getToday;
import static com.foo.hazelcast.pnlAggregator.util.DateUtil.getYearStartDate;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.foo.hazelcast.pnlAggregator.model.Pnl;

import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=PnlRandomizerConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class PnlRandomizerTest {
    
    @Autowired
    private EnhancedRandom enhancedRandomForPnl;
    
    @Test
    public void testPnlRandomizer(){
        List<Pnl> pnls = new ArrayList<>();
        for(int i=0; i<1000; i++){
            pnls.add(enhancedRandomForPnl.nextObject(Pnl.class));
        }
        assertPnlsForBookRange(pnls);
        assertPnlsForBundleRange(pnls);
        assertPnlsForCustodianAccountRange(pnls);
        assertPnlsForDateRange(pnls);
    }

    private void assertPnlsForDateRange(List<Pnl> pnls) {
        Date today = getToday();
        Date previousMonthEndDate = getPreviousMonthEndDate(today);
        Date yearStartDate = getYearStartDate(today);
        Date todayMinusOne = getModifiedDate(today,  -1);
        Date todayMinusTwo = getModifiedDate(today,  -2);
        Date todayMinusThree = getModifiedDate(today, -3);
        Date todayMinusFour = getModifiedDate(today,  -4);
        Date todayMinusFive = getModifiedDate(today,  -5);
        Set<Date> dates = Stream.of(today, previousMonthEndDate, yearStartDate, todayMinusOne, todayMinusTwo, todayMinusThree,
                todayMinusFour, todayMinusFive).collect(Collectors.toSet());
        Set<Date> datesInPnl = pnls.stream().map(p->p.getPnlKey()).map(p -> p.getDate()).collect(Collectors.toSet());
        List<Date> datesNotExpected = datesInPnl.stream().filter(d->!dates.contains(d)).collect(Collectors.toList());
        
        assertEquals("not expecting anything out of the expected list", new ArrayList<>(), datesNotExpected);
        assertEquals(datesInPnl.size(), dates.size());
    }

    private void assertPnlsForBundleRange(List<Pnl> pnls) {
        Set<Integer> expectedBundleIds =  new HashSet<>();
        Set<Integer> bundlesFromPnls = pnls.stream().map(p->p.getPnlKey()).map(p->p.getBundleId()).collect(Collectors.toSet());
        for (int i=11;i<=20;i++){
            expectedBundleIds.add(i);
        }
        
        List<Integer> bundlesNotExpected = bundlesFromPnls.stream().filter(b->!expectedBundleIds.contains(b)).collect(Collectors.toList());
        assertEquals(new ArrayList<>(), bundlesNotExpected);
        assertEquals(expectedBundleIds.size(), bundlesFromPnls.size());
    }

    private void assertPnlsForCustodianAccountRange(List<Pnl> pnls) {

        Set<String> expectedCustodianAccounts =  new HashSet<>();
        Set<String> custodianAccountsFromPnls = pnls.stream().map(p->p.getPnlKey()).map(p->p.getCustodianAccount()).collect(Collectors.toSet());
        for (int i=1;i<=10;i++){
            expectedCustodianAccounts.add("CA"+i);
        }
        
        List<String> custodianAccountsNotExpected = custodianAccountsFromPnls.stream().filter(b->!expectedCustodianAccounts.contains(b)).collect(Collectors.toList());
        assertEquals(new ArrayList<>(), custodianAccountsNotExpected);
        assertEquals(expectedCustodianAccounts.size(), custodianAccountsFromPnls.size());
    
    }

    private void assertPnlsForBookRange(List<Pnl> pnls) {
        Set<Integer> expectedBookIds =  new HashSet<>();
        Set<Integer> booksFromPnls = pnls.stream().map(p->p.getPnlKey()).map(p->p.getBookId()).collect(Collectors.toSet());
        for (int i=1;i<=10;i++){
            expectedBookIds.add(i);
        }
        
        List<Integer> booksNotExpected = booksFromPnls.stream().filter(b->!expectedBookIds.contains(b)).collect(Collectors.toList());
        assertEquals(new ArrayList<>(), booksNotExpected);
        assertEquals(expectedBookIds.size(), booksFromPnls.size());
    }
}