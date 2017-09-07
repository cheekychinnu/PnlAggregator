package com.foo.hazelcast.pnlAggregator.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.foo.hazelcast.pnlAggregator.model.Pnl;
import com.foo.hazelcast.pnlAggregator.model.PnlKey;

public interface PnlCacheService {

    void cachePnls(List<Pnl> pnls);

    void clearPnlCache();

    // use of filters
    List<Pnl> getPnlsForDate(Date date);

    List<Pnl> getPnlsForBookAndDate(Date date, Integer bookId);

    List<Pnl> getPnlsForBookAndDateAndCustodianAccount(Date date, Integer bookId, String custodianAccount);

    List<Pnl> getPnlsForBookAndDateAndCustodianAccountAndBundle(Date date, Integer bookId, String custodianAccount,
            Integer bundleId);

    // use of aggregation - no group by
    Double getSumOfDayLocalForPnlsGroupedByDateAndBook(Date date, Integer bookId);

    Double getSumOfDayLocalForPnlsGroupedByDateAndBook(Date date, Integer bookId, String custodianAccount,
            Integer bundleId);

    // use of aggregation - grouping by - only one attribute
    Map<Integer, Double> getSumOfDayLocalForPnlsGroupedByDateAndBook(Date date);

    // use of aggregation - grouping by - multiple attribute - CUSTOM AGGREGATION
    Map<PnlKey, Pnl> aggregatePnlGroupedByBookBundleAndCustodianAccount(Date date, Pnl.Field[] attrbutesToAggregate);
    Map<PnlKey, Pnl> aggregatePnlGroupedByBook(Date date, Pnl.Field[] attrbutesToAggregate);

}
