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

import io.jenetics.jpx.Length;
import io.jenetics.jpx.WayPoint;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Location {

    private float latitude;
    private float longitude;
    private ZonedDateTime dateTime;

    private float elevation;

    Location(float latitude, float longitude, ZonedDateTime dateTime, float elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.dateTime = Objects.requireNonNull(dateTime);
    }

    public Location(WayPoint wayPoint) {
        Objects.requireNonNull(wayPoint);
        this.latitude = wayPoint.getLatitude().floatValue();
        this.longitude = wayPoint.getLongitude().floatValue();
        this.elevation = wayPoint.getElevation().map(Length::floatValue).orElse(0.0f);
        this.dateTime = wayPoint.getTime().orElse(ZonedDateTime.now());
    }

//    public Location(float latitude, float longitude) {
//        this(latitude, longitude, LocalDateTime.now());
//    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public float getElevation() {
        return elevation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Float.compare(location.latitude, latitude) == 0 &&
                Float.compare(location.longitude, longitude) == 0 &&
                dateTime.equals(location.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, dateTime);
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", dateTime=" + dateTime +
                '}';
    }
}
