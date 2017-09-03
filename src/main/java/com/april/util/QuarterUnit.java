package com.april.util;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

/**
 * A standard set of date periods units.
 * <p>
 * This set of units provide unit-based access to manipulate a date, time or date-time.
 * The standard set of units can be extended by implementing {@link TemporalUnit}.
 * <p>
 * These units are intended to be applicable in multiple calendar systems.
 * For example, most non-ISO calendar systems define units of years, months and days,
 * just with slightly different rules.
 * The documentation of each unit explains how it operates.
 *
 * @implSpec
 * This is a final, immutable and thread-safe enum.
 *
 * @since 1.8
 */
public enum QuarterUnit implements TemporalUnit {

    /**
     * Unit that represents the concept of a minute.
     * For the ISO calendar system, it is equal to 15 minutes.
     */
    QUARTERS("Quarters", Duration.ofSeconds(900)),
    /**
     * Unit that represents the concept of an hour.
     * For the ISO calendar system, it is equal to 60 minutes.
     */
    HOURS("Hours", Duration.ofSeconds(3600)),
    /**
     * Unit that represents the concept of a day.
     * For the ISO calendar system, it is the standard day from midnight to midnight.
     * The estimated duration of a day is {@code 24 Hours}.
     * <p>
     * When used with other calendar systems it must correspond to the day defined by
     * the rising and setting of the Sun on Earth. It is not required that days begin
     * at midnight - when converting between calendar systems, the date should be
     * equivalent at midday.
     */
    DAYS("Days", Duration.ofSeconds(86400)),
	/**
     * Artificial unit that represents the concept of forever.
     * This is primarily used with {@link TemporalField} to represent unbounded fields
     * such as the year or era.
     * The estimated duration of the era is artificially defined as the largest duration
     * supported by {@code Duration}.
     */
    FOREVER("Forever", Duration.ofSeconds(Long.MAX_VALUE, 999_999_999));
    
    private final String name;
    private final Duration duration;

    private QuarterUnit(String name, Duration estimatedDuration) {
        this.name = name;
        this.duration = estimatedDuration;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the estimated duration of this unit in the ISO calendar system.
     * <p>
     * All of the units in this class have an estimated duration.
     * Days vary due to daylight saving time, while months have different lengths.
     *
     * @return the estimated duration of this unit, not null
     */
    @Override
    public Duration getDuration() {
        return duration;
    }

    /**
     * Checks if the duration of the unit is an estimate.
     * <p>
     * All time units in this class are considered to be accurate, while all date
     * units in this class are considered to be estimated.
     * <p>
     * This definition ignores leap seconds, but considers that Days vary due to
     * daylight saving time and months have different lengths.
     *
     * @return true if the duration is estimated, false if accurate
     */
    @Override
    public boolean isDurationEstimated() {
        return this.compareTo(DAYS) >= 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this unit is a date unit.
     * <p>
     * All units from days to eras inclusive are date-based.
     * Time-based units and {@code FOREVER} return false.
     *
     * @return true if a date unit, false if a time unit
     */
    @Override
    public boolean isDateBased() {
        return this.compareTo(DAYS) >= 0 && this != FOREVER;
    }

    /**
     * Checks if this unit is a time unit.
     * <p>
     * All units from nanos to half-days inclusive are time-based.
     * Date-based units and {@code FOREVER} return false.
     *
     * @return true if a time unit, false if a date unit
     */
    @Override
    public boolean isTimeBased() {
        return this.compareTo(DAYS) < 0;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupportedBy(Temporal temporal) {
        return temporal.isSupported(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Temporal> R addTo(R temporal, long amount) {
        return (R) temporal.plus(amount, this);
    }

    //-----------------------------------------------------------------------
    @Override
    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return temporal1Inclusive.until(temporal2Exclusive, this);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return name;
    }

}
