# PnlAggregator

Experimenting Hazelcast filters and aggregators

PnlAggregator is the custom aggregator written for group by support.
It will take in key attributes and the values that are to be aggregated - This is done to aggregate data at different level - book, book-bundle, etc. Also providing multiple value attrs allows you to group the records once and do aggregations on multiple fields.
It can be enhanced to plug in aggregators as well. Currently it is just using SumAggregator

Extractable -> this enables to access the attrs of Pnl. you can mention pnl.pnlKey.bookId to access the variable.

PnlRandomizer -> this uses random beans to generate Pnl records for certain date/book/CA/bundle values. 

Each of the functionality - filters, aggregators, randomizers, Extractables are thoroughly tested 
