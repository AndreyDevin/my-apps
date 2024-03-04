package com.example.alarm_clock.util.get_time_zone;

import com.example.alarm_clock.util.get_time_zone.stores.TimeZoneListStore;

public class GetTimeZone {
    public IConverter iconv = Converter.getInstance(TimeZoneListStore.class);
}
