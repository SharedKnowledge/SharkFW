package net.sharkfw.knowledgeBase.inmemory;

import java.util.Vector;

/**
 *
 * @author mfi
 */
public abstract class SpatialAlgebra {

//    /**
//     * Check if two tags are overlapping by checking their starting- and endpoints.
//     * If either points are within the timespan of the other tags, they must cover
//     * a mutual timespan.
//     *
//     * If the tags happen to be relative (that is representing days of the week),
//     * they are checked more thoroughly to make sure, that:
//     * <ul>
//     * <li> if both have weekdays only, the weekdays must match </li>
//     * <li> if one of them has absolute timespans set, the timespan must match the other tag's weekday </li>
//     * <li> if both have timespans only, the timespans must match </li>
//     * <li> if one or both have a final end date for relative tags, it is checked against current time, if the tag is still valid </li>
//     * </ul>
//     *
//     * @param t1 A TimeSemanticTag (usually the anchor).
//     * @param t2 Another TimeSemanticTag.
//     * @return <code>true</code> if the two tags cover a mutual timespan of at least 1 millisecond. <code>false</code> otherwise.
//     */
//    public static boolean isOverlapping(TimeSemanticTag t1, TimeSemanticTag t2) {
//
//        int weekdayT1 = t1.getWeekday();
//        int weekdayT2 = t2.getWeekday();
//
//        boolean t1HasWeekday = false;
//        boolean t2HasWeekday = false;
//
//        // Determine if weekdays are set
//        if (weekdayT1 != TimeSemanticTag.WEEKDAY_NONE) {
//            // Weekday on T1
//            t1HasWeekday = true;
//        }
//
//        if (weekdayT2 != TimeSemanticTag.WEEKDAY_NONE) {
//            // Weekday on T2
//            t2HasWeekday = true;
//        }
//
//
//        if (t2HasWeekday && !t1HasWeekday) {
//            // Check if absolute timespan of t1 is in weekday of t2
//            return KBUtil.sameWeekday(t1.getFrom(), t1.getTo(), weekdayT2, t2.getFirstStartDate(), t2.getFinalEndDate());
//        }
//
//        if (t1HasWeekday && !t2HasWeekday) {
//            // Check if absolute timespan of t2 is in weekday of t1
//            return KBUtil.sameWeekday(t2.getFrom(), t2.getTo(), weekdayT1, t2.getFirstStartDate(), t1.getFinalEndDate());
//        }
//
//        // Both have weekdays set.
//        if (t1HasWeekday && t2HasWeekday) {
//
//            // Check if the two tags are still valid
//            if (t1.getFinalEndDate() != TimeSemanticTag.TIME_FOREVER && t1.getFinalEndDate() < System.currentTimeMillis()) {
//                // T1 has expired already. Can't match!
//                return false;
//            }
//
//            if (t2.getFinalEndDate() != TimeSemanticTag.TIME_FOREVER && t2.getFinalEndDate() < System.currentTimeMillis()) {
//                // T2 has expired already. Can't match!
//            }
//
//            if (weekdayT1 == weekdayT2) {
//                // Same weekday and not unset.
//                if (isAny(t1)) {
//                    // Tag 1 is covering the whole day, as the days themself match: return true.
//                    return true;
//                } else if (isAny(t2)) {
//                    // Tag 1 is not Any, but Tag 2 is. As the days themself match: return true.
//                    return true;
//                } else {
//                    // Neiter of them are any. Check if the timespans overlap.
//                    return KBUtil.isOverlappingAbsoluteTimespan(t1.getFrom(), t1.getTo(), t2.getFrom(), t2.getTo());
//                }
//            } else {
//                // The weekdays themself don't match. Can't be true!
//                return false;
//            }
//        }
//
//        // If neither of them has a weekday set, try to match the absolute values
//        return KBUtil.isOverlappingAbsoluteTimespan(t1.getFrom(), t1.getTo(), t2.getFrom(), t2.getTo());
//
//    }
//

//    /**
//     * Check if the values span all the time
//     * @param tag The tag to check
//     * @return <code>true</code> if the tag has from and to set to "0", and if weeday == -1, <code>false</code> otherwise.
//     */
//    private static boolean isAny(TimeSemanticTag tag) {
//        long from = tag.getFrom();
//        long to = tag.getTo();
//        int weekday = tag.getWeekday();
//
//        if (from == TimeSemanticTag.TIME_FIRST_KNOWN_TIME && to == TimeSemanticTag.TIME_FOREVER && weekday == TimeSemanticTag.WEEKDAY_NONE) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * Determine if one of the two long values passed are
//     * part of the weekday.
//     *
//     * @param from Milliseconds for startingtime
//     * @param to Milliseconds for endingtime
//     * @param weekday int value for weekday.
//     * @return <code>true</code> if any of the two long values are describing a time on a <code>weekday</code>, <code>false</code> otherwise.
//     */
//    private static boolean sameWeekday(long from, long to, int weekday, long firstStartDate, long finalEndDate) {
//        Calendar t1Cal = Calendar.getInstance();
//        Calendar t2Cal = Calendar.getInstance();
//
//        // Determine if from value is part of the weekday of t2
//        t1Cal.setTimeInMillis(from);
//        int dayOfWeekFrom = t1Cal.get(Calendar.DAY_OF_WEEK);
//
//        t2Cal.setTimeInMillis(to);
//        int dayOfWeekTo = t2Cal.get(Calendar.DAY_OF_WEEK);
//
//        if (dayOfWeekFrom == weekday || dayOfWeekTo == weekday) {
//            // Either of the two points in time are on the said weekday.
//            if ((finalEndDate == TimeSemanticTag.TIME_FOREVER || finalEndDate > from) && (firstStartDate == TimeSemanticTag.TIME_FIRST_KNOWN_TIME || firstStartDate < to)) {
//                // The end of the series
//                return true;
//            } else {
//                // Final end date is before from. The tag is expired then.
//                return false;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Determine if the two timespan (from/to) overkap each other or not.
//     * @param from1
//     * @param to1
//     * @param from2
//     * @param to2
//     * @return
//     */
//    private static boolean isOverlappingAbsoluteTimespan(long from1, long to1, long from2, long to2) {
//        // For absolute timespans:
//        if ((from1 >= from2) && (from1 <= to2)) {
//            return true;
//        }
//
//        if ((to1 <= to2) && (to1 >= from2)) {
//            return true;
//        }
//
//        if ((from2 >= from1) && (from2 <= to1)) {
//            return true;
//        }
//
//        if ((to2 <= to1) && (to2 >= from1)) {
//            return true;
//        }
//
//        return false;
//    }

    public static Double getDistanceOfPoint(Double[] p1, Double[] p2, double earthR) {
        if (earthR == 0.0) {
            earthR = 6378.137; // WGS'84
        }
        if (p1 == null || p2 == null) {
            int a = 42;
        }
        Double lat1 = p1[0] / 180 * Math.PI, lat2 = p2[0] / 180 * Math.PI;
        Double lon1 = p1[1] / 180 * Math.PI, lon2 = p2[1] / 180 * Math.PI;

        Double dist = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.cos(lon2 - lon1));

        return dist * earthR * 1000; // we will compute meters, not km
    }

    public static boolean isInRange(Double[] p1, Double[] p2, Double range) {
        double dist = SpatialAlgebra.getDistanceOfPoint(p1, p2, 0.0);
        Double radius;
        if(range == null && p1.length > 3 && p1[3] != null) {
            radius = p1[3];
        } else {
            radius = range;
        }
        if (dist <= (radius + p2[3])) {
            return true;
        }
        return false;        
    }

    public static Double[] getCenterPoint(Vector mPoints) {
        if (mPoints.size() == 1) {
            return (Double[]) mPoints.elementAt(0);
        }
        if (mPoints.size() < 1) {
            return null;
        }
        Double[] lastPoint = (Double[]) mPoints.elementAt(0);
        Double left = lastPoint[0], top = lastPoint[1], right = lastPoint[0], bot = lastPoint[1];
        for (int i = 0; i < mPoints.size(); i++) {
            Double[] point = (Double[]) mPoints.elementAt(i);

            if (point[0] < left) {
                left = point[0];
            }
            if (point[0] > right) {
                right = point[0];
            }
            if (point[1] < bot) {
                bot = point[1];
            }
            if (point[1] > top) {
                top = point[1];
            }
        }

        Double[] center = new Double[4];
        center[0] = ((right + left) / 2);
        center[1] = ((top + bot) / 2);
        center[2] = 0.0;
        center[3] = 0.0;

        Double shortestDist = null;
        for (int i = 0; i < mPoints.size(); i++) {
            Double[] point = (Double[]) mPoints.elementAt(i);
            Double dist = SpatialAlgebra.getDistanceOfPoint(center, point, 0.0);
            if (shortestDist == null || dist < shortestDist) {
                shortestDist = dist;
            }
        }
        center[3] = shortestDist;


        return center;
    }

    public static Vector pointArray2Vector(Double[][] array) {
        Vector vPoints = new Vector();
        for (int i = 0; i < array.length; i++) {
            vPoints.add(array[i]);
        }
        return vPoints;
    }

}
