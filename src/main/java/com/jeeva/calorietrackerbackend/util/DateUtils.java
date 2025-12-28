package com.jeeva.calorietrackerbackend.util;

import java.time.*;
import java.util.Date;

//public class DateUtils {
//
//    public static Date startOfToday() {
//        return Date.from(
//            LocalDate.now()
//                     .atStartOfDay(ZoneId.systemDefault())
//                     .toInstant()
//        );
//    }
//
//    public static Date startOfTomorrow() {
//        return Date.from(
//            LocalDate.now()
//                     .plusDays(1)
//                     .atStartOfDay(ZoneId.systemDefault())
//                     .toInstant()
//        );
//    }
//
//    public static Date startOfLast7Days() {
//        return Date.from(
//                LocalDate.now()
//                        .minusDays(6)   // include today → total 7 days
//                        .atStartOfDay(ZoneId.systemDefault())
//                        .toInstant()
//        );
//    }
//
//    public static Date startOfLast30Days() {
//        return Date.from(
//                LocalDate.now()
//                        .minusDays(29)  // include today → total 30 days
//                        .atStartOfDay(ZoneId.systemDefault())
//                        .toInstant()
//        );
//    }
//}

public class DateUtils {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    public static Date startOfToday() {
        return toDate(LocalDate.now());
    }

    public static Date startOfTomorrow() {
        return toDate(LocalDate.now().plusDays(1));
    }

    public static Date startOfLast7Days() {
        return toDate(LocalDate.now().minusDays(6));
    }

    public static Date startOfLast30Days() {
        return toDate(LocalDate.now().minusDays(29));
    }

    private static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZONE).toInstant());
    }
}

