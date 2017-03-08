package com.zte.ums.oespaas.mysql.bean.hbase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10183966 on 8/22/16.
 */
public final class Range {
    private final long from;
    private final long to;

    public Range(long from, long to) {
        this.from = from;
        this.to = to;
        validate();
    }

    public Range(long from, long to, boolean check) {
        this.from = from;
        this.to = to;
        if (check) {
            validate();
        }
    }

    public static Range createUncheckedRange(long from, long to) {
        return new Range(from, to, false);
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public long getRange() {
        return to - from;
    }

    public void validate() {
        if (this.to < this.from) {
            throw new IllegalArgumentException("invalid range:" + this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        if (from != range.from) return false;
        if (to != range.to) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (from ^ (from >>> 32));
        result = 31 * result + (int) (to ^ (to >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Range{" +
                "from=" + from +
                ", to=" + to +
                ", range=" + getRange() +
                '}';
    }

    public List<Range> splitRangeForTimeRanges(int rangeCount, long halfInterval) {
        long tmpFrom = getFrom();
        long tmpTo = getTo();

        List<Range> ranges = new ArrayList<Range>();
        if (rangeCount <= 0) {
            ranges.add(new Range(tmpFrom, tmpTo));
            return ranges;
        }

        long increase = (tmpTo - tmpFrom) / rangeCount;
        for (int i = 0; i <= rangeCount; i++) {
            long timePoint = tmpFrom + i * increase;

            if (i == 0) {
                ranges.add(new Range(tmpFrom, tmpFrom + halfInterval));
            } else if (i == rangeCount) {
                ranges.add(new Range(tmpTo - halfInterval, tmpTo));
            } else {
                ranges.add(new Range(timePoint - halfInterval, timePoint + halfInterval));
            }
        }

        return ranges;
    }

    public List<Long> splitRangeForTimePoints(int rangeCount) {
        long tmpFrom = getFrom();
        long tmpTo = getTo();

        List<Long> timePoints = new ArrayList<Long>();
        if (rangeCount <= 0) {
            timePoints.add(tmpFrom);
            timePoints.add(tmpTo);

            return timePoints;
        }

        long increase = (tmpTo - tmpFrom) / rangeCount;
        for (int i = 0; i <= rangeCount; i++) {
            long timePoint = tmpFrom + i * increase;

            if (i == 0) {
                timePoints.add(tmpFrom);
            } else if (i == rangeCount) {
                timePoints.add(tmpTo);
            } else {
                timePoints.add(timePoint);
            }
        }

        return timePoints;
    }
}
