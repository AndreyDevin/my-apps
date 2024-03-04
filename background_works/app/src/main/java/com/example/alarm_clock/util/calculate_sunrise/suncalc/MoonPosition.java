package com.example.alarm_clock.util.calculate_sunrise.suncalc;

import static com.example.alarm_clock.util.calculate_sunrise.suncalc.util.ExtendedMath.equatorialToHorizontal;
import static com.example.alarm_clock.util.calculate_sunrise.suncalc.util.ExtendedMath.refraction;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;

import com.example.alarm_clock.util.calculate_sunrise.suncalc.param.Builder;
import com.example.alarm_clock.util.calculate_sunrise.suncalc.param.GenericParameter;
import com.example.alarm_clock.util.calculate_sunrise.suncalc.param.LocationParameter;
import com.example.alarm_clock.util.calculate_sunrise.suncalc.param.TimeParameter;
import com.example.alarm_clock.util.calculate_sunrise.suncalc.util.BaseBuilder;
import com.example.alarm_clock.util.calculate_sunrise.suncalc.util.JulianDate;
import com.example.alarm_clock.util.calculate_sunrise.suncalc.util.Moon;
import com.example.alarm_clock.util.calculate_sunrise.suncalc.util.Vector;

/**
 * Calculates the position of the moon.
 */
public class MoonPosition {

    private final double azimuth;
    private final double altitude;
    private final double distance;
    private final double parallacticAngle;

    private MoonPosition(double azimuth, double altitude, double distance, double parallacticAngle) {
        this.azimuth = (toDegrees(azimuth) + 180.0) % 360.0;
        this.altitude = toDegrees(altitude);
        this.distance = distance;
        this.parallacticAngle = toDegrees(parallacticAngle);
    }

    /**
     * Starts the computation of {@link MoonPosition}.
     *
     * @return {@link MoonPosition.Parameters} to set.
     */
    public static MoonPosition.Parameters compute() {
        return new MoonPosition.MoonPositionBuilder();
    }

    /**
     * Collects all parameters for {@link MoonPosition}.
     */
    public interface Parameters extends
            GenericParameter<Parameters>,
            LocationParameter<Parameters>,
            TimeParameter<Parameters>,
            Builder<MoonPosition> {
    }

    /**
     * Builder for {@link MoonPosition}. Performs the computations based on the
     * parameters, and creates a {@link MoonPosition} object that holds the result.
     */
    private static class MoonPositionBuilder extends BaseBuilder<Parameters> implements MoonPosition.Parameters {
        @Override
        public MoonPosition execute() {
            JulianDate t = getJulianDate();

            double phi = getLatitudeRad();
            double lambda = getLongitudeRad();

            Vector mc = Moon.position(t);
            double h = t.getGreenwichMeanSiderealTime() + lambda - mc.getPhi();

            Vector horizontal = equatorialToHorizontal(h, mc.getTheta(), mc.getR(), phi);

            double hRef = refraction(horizontal.getTheta());

            double pa = atan2(sin(h), tan(phi) * cos(mc.getTheta()) - sin(mc.getTheta()) * cos(h));

            return new MoonPosition(horizontal.getPhi(), horizontal.getTheta() + hRef, mc.getR(), pa);
        }
    }

    /**
     * Moon altitude above the horizon, in degrees.
     * <p>
     * {@code 0.0} means the moon's center is at the horizon, {@code 90.0} at the zenith
     * (straight over your head).
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Moon azimuth, in degrees, north-based.
     * <p>
     * This is the direction along the horizon, measured from north to east. For example,
     * {@code 0.0} means north, {@code 135.0} means southeast, {@code 270.0} means west.
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * Distance to the moon in kilometers.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Parallactic angle of the moon, in degrees.
     */
    public double getParallacticAngle() {
        return parallacticAngle;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonPosition[azimuth=").append(azimuth);
        sb.append("°, altitude=").append(altitude);
        sb.append("°, distance=").append(distance);
        sb.append(" km, parallacticAngle=").append(parallacticAngle);
        sb.append("°]");
        return sb.toString();
    }

}/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2018 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
