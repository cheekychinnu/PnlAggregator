package com.foo.hazelcast.pnlAggregator.config;

import static com.foo.hazelcast.pnlAggregator.util.DateUtil.getModifiedDate;
import static com.foo.hazelcast.pnlAggregator.util.DateUtil.getPreviousMonthEndDate;
import static com.foo.hazelcast.pnlAggregator.util.DateUtil.getToday;
import static com.foo.hazelcast.pnlAggregator.util.DateUtil.getYearStartDate;

import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.foo.hazelcast.pnlAggregator.model.PnlKey;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinition;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.api.Randomizer;

@Configuration
public class PnlRandomizerConfiguration {

	private static Date today;

	private static Date previousMonthEndDate;

	private static Date yearStartDate;

	private static Date todayMinusOne;

	private static Date todayMinusTwo;

	private static Date todayMinusThree;

	private static Date todayMinusFour;

	private static Date todayMinusFive;

	private static Date[] dates;

	private static String[] custodianAccounts;

	static {
		today = getToday();
		previousMonthEndDate = getPreviousMonthEndDate(today);
		yearStartDate = getYearStartDate(today);
		todayMinusOne = getModifiedDate(today, -1);
		todayMinusTwo = getModifiedDate(today, -2);
		todayMinusThree = getModifiedDate(today, -3);
		todayMinusFour = getModifiedDate(today, -4);
		todayMinusFive = getModifiedDate(today, -5);
		dates = new Date[] { today, previousMonthEndDate, yearStartDate, todayMinusOne, todayMinusTwo, todayMinusThree,
				todayMinusFour, todayMinusFive };

		custodianAccounts = IntStream.range(1, 11).mapToObj(t -> "CA" + t).toArray(String[]::new);
	}

	@Bean
	public EnhancedRandom getEnhancedRandomForPnl() {
		Randomizer<Date> dateRandomizer = () -> {
			int max = dates.length;
			Random random = new Random();
			return dates[random.nextInt(max)];
		};
		Randomizer<Integer> bookRandomizer = () -> {
			int min = 1;
			int max = 10;
			Random random = new Random();
			return min + random.nextInt((max - min) + 1);
		};
		Randomizer<Integer> bundleRandomizer = () -> {
			int min = 11;
			int max = 20;
			Random random = new Random();
			return min + random.nextInt((max - min) + 1);
		};
		Randomizer<String> custodianAccountRandomizer = () -> {
			Random random = new Random();
			return custodianAccounts[random.nextInt(custodianAccounts.length)];
		};
		EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
				.randomize(Date.class, dateRandomizer)
				.randomize(new FieldDefinition<>("bookId", Integer.class, PnlKey.class), bookRandomizer)
				.randomize(new FieldDefinition<>("bundleId", Integer.class, PnlKey.class), bundleRandomizer)
				.randomize(new FieldDefinition<>("custodianAccount", String.class, PnlKey.class),
						custodianAccountRandomizer)
				.build();
		return enhancedRandom;
	}

}