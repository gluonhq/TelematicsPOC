/*
 * Copyright (c) 2021 Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.samples.telematics.model;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import javafx.concurrent.Task;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class GeoTools {

    private static final double MILLIS_TO_HOURS = 1000*60*60;

    private GeoTools() {}

    //Calculate bearing in degrees
    public static Optional<Double> calculateBearing(Location a, Location b) {

        if (a == null || b == null) return Optional.empty();

        double dlon = a.getLongitude()-b.getLongitude();
        double y = Math.sin(dlon) * Math.cos(b.getLatitude());
        double x = Math.cos(a.getLatitude()) * Math.sin(b.getLatitude()) - Math.sin(a.getLatitude()) * Math.cos(b.getLatitude()) * Math.cos(dlon) ;
        double q = Math.atan2(y, x);
        double bearing = Math.toDegrees(q);
        return Optional.of( (bearing >= 0.0f && bearing <= 180.0f)? bearing: 180 + (180 + bearing));

    }

    public static Optional<Double> calculateDistance(Location a, Location b, MeasurementUnits units) {

        if (a == null || b == null) return Optional.empty();

        double alon = Math.toRadians(a.getLongitude());
        double blon = Math.toRadians(b.getLongitude());
        double alat = Math.toRadians(a.getLatitude());
        double blat = Math.toRadians(b.getLatitude());

        double dlon = blon-alon;
        double dlat = blat-alat;
        double ax = Math.pow(Math.sin(dlat/2), 2) + Math.cos(alat) * Math.cos(blat) * Math.pow(Math.sin(dlon/2),2);
        double cx = 2 * Math.asin(Math.sqrt(ax));

//        double r = 3958.756; // in miles or 6371km
        return  Optional.of(cx*units.getGlobeRadius());

    }

    public static Optional<Integer> calculateSpeed(Location a, Location b, MeasurementUnits units) {
        return calculateDistance(a,b, units)
                .map( d -> {
//                    System.out.printf( "Distance: %f\n", d );
                    double millis = ChronoUnit.MILLIS.between(b.getDateTime(), a.getDateTime());
                    double hours = Math.abs(millis)/MILLIS_TO_HOURS;
//                    System.out.printf( "Seconds: %f\n", Math.abs(millis)/1000 );
                    return (int) (d / hours);
                });
    }

    private static final AtomicBoolean isSimulating = new AtomicBoolean(false);

    public static void simulateTrack( String gpxResourceName, Consumer<Location> onLocationChange) {

        if ( isSimulating.get() ) return;

        Thread thread = new Thread( new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    isSimulating.set(true);
                    GPX.read(GeoTools.class.getResourceAsStream(gpxResourceName))
                            .tracks()
                            .flatMap(Track::segments)
                            .flatMap(TrackSegment::points)
                            .forEach( wp -> onLocationChange.accept(new Location(wp)));
                } catch (IOException e) {
                    throw new RuntimeException( "Cannot read the GPX track: " + gpxResourceName, e);
                } finally {
                    isSimulating.set(false);
                }
                return null;
            };
        });
        thread.setDaemon(true);
        thread.start();

    }

}
