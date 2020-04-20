package com.xman.admin.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class DateUtils {
    public LocalDateTime getToday() {
        return LocalDateTime.now();
    }

    public long getLeftMinuteBetween(LocalDateTime first, LocalDateTime second) {
        if(!ObjectUtils.allNotNull(first, second)) return 0;
        return ChronoUnit.MINUTES.between(first, second);
    }

    public long getLeftMinuteBetweenNowDateAnd(LocalDateTime loginFailDt) {
        if (ObjectUtils.isEmpty(loginFailDt)) return 0;

        LocalDateTime loginDateTime = loginFailDt;
        LocalDateTime nowDateTime = LocalDateTime.now();

        return ChronoUnit.MINUTES.between(loginDateTime, nowDateTime);
    }
}
