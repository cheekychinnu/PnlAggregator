package com.foo.hazelcast.pnlAggregator.config;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.foo.hazelcast.pnlAggregator.BaseIntegrationTest;
import com.foo.hazelcast.pnlAggregator.model.Pnl;
import com.foo.hazelcast.pnlAggregator.model.PnlKey;
import com.foo.hazelcast.pnlAggregator.service.PnlCacheService;
import com.foo.hazelcast.pnlAggregator.util.DateUtil;

import io.github.benas.randombeans.api.EnhancedRandom;

@RunWith(Enclosed.class)
public class PnlCacheServiceTest {

    @Ignore("enclosed base class")
    public static abstract class BasePnlCacheServiceTest extends BaseIntegrationTest{

        @Autowired
        protected EnhancedRandom enhancedRandomForPnl;

        @Autowired
        protected PnlCacheService pnlCacheService;

        protected static List<Pnl> pnls;

        protected static Date date = DateUtil.getToday();

        protected static Integer bookId = 1;

        protected static String custodianAccount = "CA1";

        protected static Integer bundleId = 11;

        @After
        public void clear() {
            pnlCacheService.clearPnlCache();
        }
    }

    public static class PnlSimpleAggregationTest extends BasePnlCacheServiceTest {

        private double dayLocalForGranularLevel = 9d;

        private double dayLocalForBookLevel = 5d;

        private double dayLocalForOthers = 2d;

        private Predicate<PnlKey> bookLevelPredicate = (pnlKey) -> pnlKey.getDate().equals(date)
                && pnlKey.getBookId().equals(bookId);

        private Predicate<PnlKey> granularPredicate = (pnlKey) -> bookLevelPredicate.test(pnlKey)
                && pnlKey.getCustodianAccount().equals(custodianAccount) && pnlKey.getBundleId().equals(bundleId);

        @Before
        public void setup() {
            pnls = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Pnl nextObject = enhancedRandomForPnl.nextObject(Pnl.class);
                PnlKey pnlKey = nextObject.getPnlKey();
                if (i == 0) {
                    // This is must since there must be atleast one entry for out test for the given criteria
                    pnlKey.setDate(date);
                    pnlKey.setBookId(bookId);
                    pnlKey.setCustodianAccount(custodianAccount);
                    pnlKey.setBundleId(bundleId);
                }
                if (granularPredicate.test(pnlKey)) {
                    nextObject.setDayLocal(dayLocalForGranularLevel);
                } else if (bookLevelPredicate.test(pnlKey)) {
                    nextObject.setDayLocal(dayLocalForBookLevel);
                } else {
                    nextObject.setDayLocal(dayLocalForOthers);
                }

                pnls.add(nextObject);
            }
            pnlCacheService.cachePnls(pnls);
        }

        @Test
        public void testGetSumOfDayLocalForPnlsGroupedByDateAndBookFilteredByDate() {
            long count = pnls.stream().map(p -> p.getPnlKey()).filter(k -> k.getBookId().equals(bookId)).count();
            System.out.println(count);
            Map<Integer, Double> bookIdToDayLocalMap = pnlCacheService
                    .getSumOfDayLocalForPnlsGroupedByDateAndBook(date);
            assertNotNull(bookIdToDayLocalMap);
            assertTrue(bookIdToDayLocalMap.keySet().size() > 1);
            Double sumOfDayLocalGroupedByBookId = bookIdToDayLocalMap.get(bookId);

            long bookLevelPnlsForTestData = pnls.stream().map(p -> p.getPnlKey()).filter(bookLevelPredicate).count();
            long granularLevelPnlsForTestData = pnls.stream().map(p -> p.getPnlKey()).filter(granularPredicate).count();

            System.out.println(bookLevelPnlsForTestData);
            System.out.println(granularLevelPnlsForTestData);

            Double expectedSum = (((bookLevelPnlsForTestData - granularLevelPnlsForTestData) * dayLocalForBookLevel)
                    + (granularLevelPnlsForTestData * dayLocalForGranularLevel));
            assertEquals(expectedSum, sumOfDayLocalGroupedByBookId);
        }

        @Test
        public void testGetSumOfDayLocalForPnlsGroupedByDateAndBookFilteredByDateAndBook() {
            Double sumOfDayLocalForPnlsGroupedByDateAndBook = pnlCacheService
                    .getSumOfDayLocalForPnlsGroupedByDateAndBook(date, bookId);
            assertNotNull(sumOfDayLocalForPnlsGroupedByDateAndBook);

            long bookLevelPnlsForTestData = pnls.stream().map(p -> p.getPnlKey()).filter(bookLevelPredicate).count();
            long granularLevelPnlsForTestData = pnls.stream().map(p -> p.getPnlKey()).filter(granularPredicate).count();

            Double expectedSum = (((bookLevelPnlsForTestData - granularLevelPnlsForTestData) * dayLocalForBookLevel)
                    + (granularLevelPnlsForTestData * dayLocalForGranularLevel));
            assertEquals(expectedSum, sumOfDayLocalForPnlsGroupedByDateAndBook);
        }

        @Test
        public void testGetSumOfDayLocalForPnlsGroupedByDateAndBookFilteredByDateAndBookFilteredByDateBookcustodianAccountAndBundle() {
            Double sumOfDayLocalForPnlsGroupedByDateAndBook = pnlCacheService
                    .getSumOfDayLocalForPnlsGroupedByDateAndBook(date, bookId, custodianAccount, bundleId);

            long granularLevelPnlsForTestData = pnls.stream().map(p -> p.getPnlKey()).filter(granularPredicate).count();

            Double expectedSum = granularLevelPnlsForTestData * dayLocalForGranularLevel;
            assertEquals(expectedSum, sumOfDayLocalForPnlsGroupedByDateAndBook);
        }
    }

    public static class PnlCacheFilterTest extends BasePnlCacheServiceTest {

        @Before
        public void setup() {
            pnls = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                Pnl nextObject = enhancedRandomForPnl.nextObject(Pnl.class);
                if (i == 0) {
                    // This is must since there must be atleast one entry for out test for the given criteria
                    nextObject.getPnlKey().setDate(date);
                    nextObject.getPnlKey().setBookId(bookId);
                    nextObject.getPnlKey().setCustodianAccount(custodianAccount);
                    nextObject.getPnlKey().setBundleId(bundleId);
                }
                pnls.add(nextObject);
            }
            pnlCacheService.cachePnls(pnls);
        }

        @Test
        public void testGetPnlsForDate() {
            long expectedCount = pnls.stream().filter(p -> p.getPnlKey().getDate().equals(date)).count();
            List<Pnl> filteredPnls = pnlCacheService.getPnlsForDate(date);
            assertNotNull(filteredPnls);
            assertFalse(filteredPnls.isEmpty());
            assertEquals(expectedCount, filteredPnls.size());
            assertTrue(filteredPnls.stream().allMatch(p -> p.getPnlKey().getDate().equals(date)));
        }

        @Test
        public void testGetPnlsForBookAndDate() {
            Predicate<PnlKey> predicate = p -> p.getDate().equals(date) && p.getBookId().equals(bookId);

            long expectedCount = pnls.stream().map(p -> p.getPnlKey()).filter(predicate).count();

            List<Pnl> filteredPnls = pnlCacheService.getPnlsForBookAndDate(date, bookId);
            assertNotNull(filteredPnls);
            assertFalse(filteredPnls.isEmpty());
            assertEquals(expectedCount, filteredPnls.size());
            assertTrue(filteredPnls.stream().map(p -> p.getPnlKey()).allMatch(predicate));
        }

        @Test
        public void testGetPnlsForBookAndDateAndCustodianAccount() {
            Predicate<PnlKey> predicate = p -> p.getDate().equals(date) && p.getBookId().equals(bookId)
                    && p.getCustodianAccount().equals(custodianAccount);

            long expectedCount = pnls.stream().map(p -> p.getPnlKey()).filter(predicate).count();

            List<Pnl> filteredPnls = pnlCacheService.getPnlsForBookAndDateAndCustodianAccount(date, bookId,
                    custodianAccount);
            assertNotNull(filteredPnls);
            assertFalse(filteredPnls.isEmpty());
            assertEquals(expectedCount, filteredPnls.size());
            assertTrue(filteredPnls.stream().map(p -> p.getPnlKey()).allMatch(predicate));
        }

        @Test
        public void testGetPnlsForBookAndDateAndCustodianAccountAndBundle() {
            Predicate<PnlKey> predicate = p -> p.getDate().equals(date) && p.getBookId().equals(bookId)
                    && p.getCustodianAccount().equals(custodianAccount) && p.getBundleId().equals(bundleId);

            long expectedCount = pnls.stream().map(p -> p.getPnlKey()).filter(predicate).count();
            List<Pnl> filteredPnls = pnlCacheService.getPnlsForBookAndDateAndCustodianAccountAndBundle(date, bookId,
                    custodianAccount, bundleId);
            assertNotNull(filteredPnls);
            assertFalse(filteredPnls.isEmpty());
            assertEquals(expectedCount, filteredPnls.size());
            assertTrue(filteredPnls.stream().map(p -> p.getPnlKey()).allMatch(predicate));
        }
    }

    public static class PnlAggregatorTest extends BasePnlCacheServiceTest {

        private double dayLocalForGranularLevel = 9d;

        private double dayLocalForBookLevel = 5d;

        private double dayLocalForOthers = 2d;

        private double dayUsdForGranularLevel = 10d;

        private double dayUsdForBookLevel = 15d;

        private double dayUsdForOthers = 17d;

        private Predicate<PnlKey> bookLevelPredicate = (pnlKey) -> pnlKey.getDate().equals(date)
                && pnlKey.getBookId().equals(bookId);

        private Predicate<PnlKey> granularPredicate = (pnlKey) -> bookLevelPredicate.test(pnlKey)
                && pnlKey.getCustodianAccount().equals(custodianAccount) && pnlKey.getBundleId().equals(bundleId);

        @Before
        public void setup() {
            pnls = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Pnl nextObject = enhancedRandomForPnl.nextObject(Pnl.class);
                PnlKey pnlKey = nextObject.getPnlKey();
                if (i == 0) {
                    // This is must since there must be atleast one entry for out test for the given criteria
                    pnlKey.setDate(date);
                    pnlKey.setBookId(bookId);
                    pnlKey.setCustodianAccount(custodianAccount);
                    pnlKey.setBundleId(bundleId);
                }
                if (granularPredicate.test(pnlKey)) {
                    nextObject.setDayLocal(dayLocalForGranularLevel);
                    nextObject.setDayUsd(dayUsdForGranularLevel);
                } else if (bookLevelPredicate.test(pnlKey)) {
                    nextObject.setDayLocal(dayLocalForBookLevel);
                    nextObject.setDayUsd(dayUsdForBookLevel);
                } else {
                    nextObject.setDayLocal(dayLocalForOthers);
                    nextObject.setDayUsd(dayUsdForOthers);
                }

                pnls.add(nextObject);
            }
            pnlCacheService.cachePnls(pnls);
        }

        @Test
        public void testAggregatePnlAtBookLevel() {

            Map<PnlKey, Pnl> aggregatedPnlMap = pnlCacheService.aggregatePnlGroupedByBook(date, new String[]{Pnl.DAY_LOCAL_FIELD,
                    Pnl.DAY_USD});

            assertNotNull(aggregatedPnlMap);
            assertTrue(aggregatedPnlMap.keySet().size() > 1);
            assertPnlKeyforBookLevel(aggregatedPnlMap);

            PnlKey pnlKey = new PnlKey();
            pnlKey.setBookId(bookId);

            Pnl aggregatedPnl = aggregatedPnlMap.get(pnlKey);
            assertNotNull(aggregatedPnl);

            long bookLevelPnlsForTestData = pnls.stream().map(p -> p.getPnlKey()).filter(bookLevelPredicate).count();
            long granularLevelPnlsForTestData = pnls.stream().map(p -> p.getPnlKey()).filter(granularPredicate).count();

            Double expectedSumForDayLocal = (((bookLevelPnlsForTestData - granularLevelPnlsForTestData)
                    * dayLocalForBookLevel) + (granularLevelPnlsForTestData * dayLocalForGranularLevel));

            Double expectedSumForDayUsd = (((bookLevelPnlsForTestData - granularLevelPnlsForTestData)
                    * dayUsdForBookLevel) + (granularLevelPnlsForTestData * dayUsdForGranularLevel));

            Pnl expectedAggregatedPnl = new Pnl();
            expectedAggregatedPnl.setPnlKey(pnlKey);
            expectedAggregatedPnl.setDayLocal(expectedSumForDayLocal);
            expectedAggregatedPnl.setDayUsd(expectedSumForDayUsd);
            assertEquals(expectedAggregatedPnl, aggregatedPnl);

        }

        @Test
        public void testAggregatePnlAtGranularLevel() {

            Map<PnlKey, Pnl> aggregatedPnlMap = pnlCacheService.aggregatePnlGroupedByBookBundleAndCustodianAccount(date, new String[]{Pnl.DAY_LOCAL_FIELD,
                    Pnl.DAY_USD});

            assertNotNull(aggregatedPnlMap);
            assertTrue(aggregatedPnlMap.keySet().size() > 1);
            assertPnlKeyforGranularLevel(aggregatedPnlMap);

            PnlKey pnlKey = new PnlKey();
            pnlKey.setBookId(bookId);
            pnlKey.setBundleId(bundleId);
            pnlKey.setCustodianAccount(custodianAccount);

            Pnl aggregatedPnl = aggregatedPnlMap.get(pnlKey);
            assertNotNull(aggregatedPnl);

            long granularLevelPnlsForTestData = pnls.stream().map(p -> p.getPnlKey()).filter(granularPredicate).count();

            Double expectedSumForDayLocal = granularLevelPnlsForTestData * dayLocalForGranularLevel;

            Double expectedSumForDayUsd = granularLevelPnlsForTestData * dayUsdForGranularLevel;

            Pnl expectedAggregatedPnl = new Pnl();
            expectedAggregatedPnl.setPnlKey(pnlKey);
            expectedAggregatedPnl.setDayLocal(expectedSumForDayLocal);
            expectedAggregatedPnl.setDayUsd(expectedSumForDayUsd);
            assertEquals(expectedAggregatedPnl, aggregatedPnl);

        }
        
        private void assertPnlKeyforGranularLevel(Map<PnlKey, Pnl> aggregatedPnlMap) {
            Predicate<PnlKey> everyAttrNullExpectBookId = (key) -> {
                return !Objects.isNull(key.getBundleId()) && !Objects.isNull(key.getCustodianAccount())
                        && Objects.isNull(key.getDate()) && Objects.isNull(key.getPnlSpn())
                        && Objects.isNull(key.getDenomination()) && Objects.isNull(key.getFinancialAccountId())
                        && Objects.isNull(key.getBasketInvestment()) && Objects.isNull(key.getRoletrackingInv())
                        && Objects.isNull(key.getInventoryState()) && Objects.isNull(key.getKnowledgeDate())
                        && Objects.isNull(key.getTaxlotType()) && !Objects.isNull(key.getBookId());
            };
            List<PnlKey> faultyPnlKeys = aggregatedPnlMap.keySet().stream().filter(everyAttrNullExpectBookId.negate())
                    .collect(Collectors.toList());
            assertEquals(Collections.emptyList(), faultyPnlKeys);
        }
        
        private void assertPnlKeyforBookLevel(Map<PnlKey, Pnl> aggregatedPnlMap) {
            Predicate<PnlKey> everyAttrNullExpectBookId = (key) -> {
                return Objects.isNull(key.getBundleId()) && Objects.isNull(key.getCustodianAccount())
                        && Objects.isNull(key.getDate()) && Objects.isNull(key.getPnlSpn())
                        && Objects.isNull(key.getDenomination()) && Objects.isNull(key.getFinancialAccountId())
                        && Objects.isNull(key.getBasketInvestment()) && Objects.isNull(key.getRoletrackingInv())
                        && Objects.isNull(key.getInventoryState()) && Objects.isNull(key.getKnowledgeDate())
                        && Objects.isNull(key.getTaxlotType()) && !Objects.isNull(key.getBookId());
            };
            List<PnlKey> faultyPnlKeys = aggregatedPnlMap.keySet().stream().filter(everyAttrNullExpectBookId.negate())
                    .collect(Collectors.toList());
            assertEquals(Collections.emptyList(), faultyPnlKeys);
        }
    }
}