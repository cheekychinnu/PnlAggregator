package com.foo.hazelcast.pnlAggregator.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.foo.hazelcast.pnlAggregator.aggregators.attrBased.DayLocalSumAggregator;
import com.foo.hazelcast.pnlAggregator.aggregators.attrBased.DayLocalSumGroupByBookAggregator;
import com.foo.hazelcast.pnlAggregator.aggregators.attrBased.PnlAggregator;
import com.foo.hazelcast.pnlAggregator.model.Pnl;
import com.foo.hazelcast.pnlAggregator.model.PnlKey;
import com.foo.hazelcast.pnlAggregator.service.PnlCacheService;
import com.hazelcast.aggregation.impl.DoubleSumAggregator;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

@SuppressWarnings("rawtypes")
@Component
public class PnlCacheServiceImpl implements PnlCacheService {

    private static final String PNL_MAP = "PnlMap";
    
    Function<Date, Predicate<Long, Pnl>> dateEqualsPredicate = (d) ->  Predicates.equal(Pnl.Field.DATE_FIELD.getFieldName(), d);
    Function<Integer, Predicate<Long, Pnl>> bundleIdEqualsPredicate = (b) ->  Predicates.equal(Pnl.Field.BUNDLE_ID_FIELD.getFieldName(), b);
    Function<Integer, Predicate<Long, Pnl>> bookIdEqualsPredicate = (b) ->  Predicates.equal(Pnl.Field.BOOK_ID_FIELD.getFieldName(), b);
    Function<String, Predicate<Long, Pnl>> custodianAccountEqualsPredicate = (c) ->  Predicates.equal(Pnl.Field.CUSTODIAN_ACCOUNT_FIELD.getFieldName(), c);
    
    @Autowired
    private HazelcastInstance hazelcastInstance;

    private IMap<Long, Pnl> idToPnlMap;

    @PostConstruct
    public void afterConstruct() {
        idToPnlMap = hazelcastInstance.getMap(PNL_MAP);
    }


    @Override
    public void cachePnls(List<Pnl> pnls) {
        Map<Long, Pnl> idToPnlMap = pnls.stream().collect(Collectors.toMap(p -> p.getId(), Function.identity()));
        // every put on the hazelcast map is a call to all clusters to update data. so always use putAll. 
        this.idToPnlMap.putAll(idToPnlMap);
    }


    @Override
    public List<Pnl> getPnlsForDate(Date date) {
        Predicate datePredicate = dateEqualsPredicate.apply(date);
        Collection<Pnl> filteredPnls = idToPnlMap.values(datePredicate);
        return new ArrayList<>(filteredPnls);
    }


    @Override
    public List<Pnl> getPnlsForBookAndDate(Date date, Integer bookId) {
        Predicate datePredicate = dateEqualsPredicate.apply(date);
        Predicate bookPredicate = bookIdEqualsPredicate.apply(bookId);
        Predicate<Long, Pnl> finalPredicate =  Predicates.and(datePredicate, bookPredicate);
        Collection<Pnl> filteredPnls = idToPnlMap.values(finalPredicate);
        return new ArrayList<>(filteredPnls);
    }


    @Override
    public List<Pnl> getPnlsForBookAndDateAndCustodianAccount(Date date, Integer bookId, String custodianAccount) {
        Predicate datePredicate = dateEqualsPredicate.apply(date);
        Predicate bookPredicate = bookIdEqualsPredicate.apply(bookId);
        Predicate custodianAccountPredicate = custodianAccountEqualsPredicate.apply(custodianAccount);
        Predicate<Long, Pnl> finalPredicate =  Predicates.and(datePredicate, bookPredicate, custodianAccountPredicate);
        Collection<Pnl> filteredPnls = idToPnlMap.values(finalPredicate);
        return new ArrayList<>(filteredPnls);
    }


    @Override
    public List<Pnl> getPnlsForBookAndDateAndCustodianAccountAndBundle(Date date, Integer bookId,
            String custodianAccount, Integer bundleId) {
        Predicate datePredicate = dateEqualsPredicate.apply(date);
        Predicate bookPredicate = bookIdEqualsPredicate.apply(bookId);
        Predicate custodianAccountPredicate = custodianAccountEqualsPredicate.apply(custodianAccount);
        Predicate bundlePredicate = bundleIdEqualsPredicate.apply(bundleId);
        
        Predicate<Long, Pnl> finalPredicate =  Predicates.and(datePredicate, bookPredicate, custodianAccountPredicate, bundlePredicate);
        Collection<Pnl> filteredPnls = idToPnlMap.values(finalPredicate);
        return new ArrayList<>(filteredPnls);
    }

    

    @Override
    public Double getSumOfDayLocalForPnlsGroupedByDateAndBook(Date date, Integer bookId) {
        Predicate<Long, Pnl> bookPredicate = (entry) -> bookId.equals(entry.getValue().getPnlKey().getBookId());
        Predicate<Date, Pnl> datePredicate = (entry) -> date.equals(entry.getValue().getPnlKey().getDate());
        // using in-built aggregator
        return idToPnlMap.aggregate(new DoubleSumAggregator<Map.Entry<Long, Pnl>>(Pnl.DAY_LOCAL_FIELD), Predicates.and(datePredicate, bookPredicate));
        /*
         * Deprecated:
         *  return idToPnlMap.aggregate(Supplier.fromPredicate(bookPredicate,Supplier.all(Pnl::getDayLocal)), Aggregations.doubleSum());
         */
    }

    @Override
    public Map<Integer, Double> getSumOfDayLocalForPnlsGroupedByDateAndBook(Date date) {
        Predicate<Long, Pnl> datePredicate = (entry) -> date.equals(entry.getValue().getPnlKey().getDate());
        return idToPnlMap.aggregate(new DayLocalSumGroupByBookAggregator(), datePredicate);
    }
    

    @Override
    public Double getSumOfDayLocalForPnlsGroupedByDateAndBook(Date date, Integer bookId, String custodianAccount,
            Integer bundleId) {
        Predicate<Date, Pnl> datePredicate = (entry) -> date.equals(entry.getValue().getPnlKey().getDate());
        Predicate<Long, Pnl> bookPredicate = (entry) -> bookId.equals(entry.getValue().getPnlKey().getBookId());
        Predicate<Long, Pnl> bundlePredicate = (entry) -> bundleId.equals(entry.getValue().getPnlKey().getBundleId());
        Predicate<String, Pnl> custodianAccountPredicate = (entry) -> custodianAccount.equals(entry.getValue().getPnlKey().getCustodianAccount());
        Predicate finalPredicate = Predicates.and(bookPredicate, bundlePredicate, custodianAccountPredicate, datePredicate);
        // using custom aggregator
        return idToPnlMap.aggregate(new DayLocalSumAggregator(), finalPredicate);
    }
    
    @Override
    public Map<PnlKey, Pnl> aggregatePnlGroupedByBookBundleAndCustodianAccount(Date date, String[] attrbutesToAggregate) {
        Predicate<Long, Pnl> datePredicate = (entry) -> date.equals(entry.getValue().getPnlKey().getDate());
        List<Pnl.Field> keys = Stream.of(Pnl.Field.BOOK_ID_FIELD, Pnl.Field.BUNDLE_ID_FIELD, Pnl.Field.CUSTODIAN_ACCOUNT_FIELD).collect(Collectors.toList());
        return idToPnlMap.aggregate(new PnlAggregator(keys, attrbutesToAggregate), datePredicate);
    }


    @Override
    public void clearPnlCache() {
        this.idToPnlMap.clear();        
    }


    @Override
    public Map<PnlKey, Pnl> aggregatePnlGroupedByBook(Date date, String[] attrbutesToAggregate) {
        Predicate<Long, Pnl> datePredicate = (entry) -> date.equals(entry.getValue().getPnlKey().getDate());
        List<Pnl.Field> keys = Stream.of(Pnl.Field.BOOK_ID_FIELD).collect(Collectors.toList());
        return idToPnlMap.aggregate(new PnlAggregator(keys, attrbutesToAggregate), datePredicate);
    }

}