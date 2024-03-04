package com.example.alarm_clock.util.calculate_sunrise;

import com.example.alarm_clock.util.calculate_sunrise.suncalc.SunTimes;

import java.time.ZonedDateTime;

public class GetSunriseTime {
    public SunTimes getSunriseTime(
            ZonedDateTime dateTime,
            Double lat,
            Double lng
    ) {
        return SunTimes.compute()
                .on(dateTime)
                .at(lat, lng)
                .execute();
    }
}
