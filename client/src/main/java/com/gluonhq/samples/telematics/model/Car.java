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

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.ignite.micronaut.FXApplication;
import com.gluonhq.samples.telematics.ui.event.VinDetectedEvent;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Scheduled;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.application.Platform;
import javafx.beans.property.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;


@Singleton
public class Car {

    @Inject
    private CarConfig config;

    @Inject
    private ApplicationEventPublisher eventPublisher;

    private String vin;

    public String getVin() {
        return this.vin;
    }

    // lockedProperty
    private final BooleanProperty lockedProperty = new SimpleBooleanProperty(this, "locked", false);
    public final BooleanProperty lockedProperty() {
        return lockedProperty;
    }
    public final boolean isLocked() {
        return lockedProperty.get();
    }
    public final void setLocked(boolean value) {
        lockedProperty.set(value);
    }

    // dateTimeProperty
    private final ReadOnlyObjectWrapper<ZonedDateTime> dateTimeProperty = new ReadOnlyObjectWrapper<>(this, "dateTime", ZonedDateTime.now());
    public final ReadOnlyObjectProperty<ZonedDateTime> dateTimeProperty() {
        return dateTimeProperty.getReadOnlyProperty();
    }
    public final ZonedDateTime getDateTime() {
        return dateTimeProperty.get();
    }

    // outsideTemperatureProperty
    private final FloatProperty outsideTemperatureProperty = new SimpleFloatProperty(this, "outsideTemperature", 80);
    public final FloatProperty outsideTemperatureProperty() {
        return outsideTemperatureProperty;
    }
    public final float getOutsideTemperature() {
        return outsideTemperatureProperty.get();
    }
    public final void setOutsideTemperature(float value) {
        outsideTemperatureProperty.set(value);
    }

    // locationProperty
    private final ReadOnlyObjectWrapper<Location> locationProperty = new ReadOnlyObjectWrapper<>(this, "Location", null);
    public final ReadOnlyObjectProperty<Location> locationProperty() {
        return locationProperty.getReadOnlyProperty();
    }
    public final Location getLocation() {
        return locationProperty.get();
    }

    // bearingProperty  IN DEGREES
    private final ReadOnlyDoubleWrapper bearingProperty = new ReadOnlyDoubleWrapper(this, "bearing");
    public final ReadOnlyDoubleProperty bearingProperty() {
        return bearingProperty.getReadOnlyProperty();
    }
    public final double getBearing() {
        return bearingProperty.get();
    }

    // speedProperty
    private final ReadOnlyIntegerWrapper speedProperty = new ReadOnlyIntegerWrapper(this, "speed", 0);
    public final ReadOnlyIntegerProperty speedProperty() {
        return speedProperty.getReadOnlyProperty();
    }
    public final int getSpeed() {
        return speedProperty.get();
    }

    // batteryChargeProperty 0.0 to 1.0
    private final ReadOnlyDoubleWrapper batteryChargeProperty = new ReadOnlyDoubleWrapper(this, "batteryCharge", .5);
    public final ReadOnlyDoubleProperty batteryChargeProperty() {
        return batteryChargeProperty.getReadOnlyProperty();
    }
    public final double getBatteryCharge() {
        return batteryChargeProperty.get();
    }

    @Scheduled(fixedDelay = "1s")
    void refreshTime() {
        dateTimeProperty.set(ZonedDateTime.now());
        //TODO add temperature updates here
    }

    @PostConstruct
    void init() {
        locationProperty.addListener( (o, prevLoc, nextLoc) -> {
            GeoTools.calculateBearing(prevLoc, nextLoc).ifPresent(bearingProperty::set);
            GeoTools.calculateSpeed(prevLoc, nextLoc, config.getMeasurementUnits()).ifPresent(speedProperty::set);
        });
    }

    @EventListener
    void onAppStart(FXApplication.StartEvent event) {
        String vinProperty = System.getProperty("tesla.m3.vin");
        if (vinProperty != null && !vinProperty.trim().isEmpty()) {
            this.vin = vinProperty;
        } else {
            StorageService storageService = StorageService.create().get();
            File privateStorage = storageService.getPrivateStorage().get();
            Path path = privateStorage.toPath().resolve("vin.txt");
            try {
                if (Files.exists(path)) {
                    this.vin = Files.readString(path);
                } else {
                    this.vin = "VIN"+ (long) (Math.random()* 1e12d);
                    Files.writeString(path, vin);
                }
            } catch (IOException ex) {
                Logger.getLogger(Car.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.err.println("VIN = " + vin);
        eventPublisher.publishEvent(new VinDetectedEvent(vin));
    }

    public void move( String resourceName ) {
        GeoTools.simulateTrack(resourceName, p -> {
            try {
                Platform.runLater( () -> locationProperty.set(p));
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}

