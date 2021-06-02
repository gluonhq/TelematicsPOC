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
package com.gluonhq.samples.telematics.cloudlink;

import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.cloudlink.client.data.OperationMode;
import com.gluonhq.cloudlink.client.data.RemoteFunctionBuilder;
import com.gluonhq.cloudlink.client.data.RemoteFunctionObject;
import com.gluonhq.cloudlink.client.data.SyncFlag;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.ObjectDataReader;
import com.gluonhq.samples.telematics.model.Car;
import com.gluonhq.samples.telematics.model.Location;
import com.gluonhq.samples.telematics.ui.event.VinDetectedEvent;

import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CloudLinkClient {

    private final static Logger logger = Logger.getLogger(CloudLinkClient.class.getName());

    @Inject
    private Car car;

    private DataClient dataClient;
    private Vehicle remoteVehicle;

    @PostConstruct
    private void init() {
        dataClient = DataClientBuilder.create()
                .operationMode(OperationMode.CLOUD_FIRST)
                .build();
    }

    @EventListener
    @Async
    public void onVinDetected(VinDetectedEvent event) {
        retrieveRemoteVehicle(event.getVin());
    }

    private void retrieveRemoteVehicle(String vin) {
        String vehicleId = "loc-" + vin;
        ObjectDataReader<Vehicle> vehicleDataReader = dataClient
                .createObjectDataReader(vehicleId, Vehicle.class, SyncFlag.OBJECT_WRITE_THROUGH);

        GluonObservableObject<Vehicle> remoteVehicleObservable = DataProvider.retrieveObject(vehicleDataReader);
        remoteVehicleObservable.initializedProperty().addListener((observable, ov, nv) -> {
            if (nv) {
                if (remoteVehicleObservable.get() == null) {
                    logger.log(Level.INFO, "Vehicle with VIN '" + vin + "' does not yet exist. Creating a new remote object.");
                    addVehicle(vehicleId, vin);
                } else {
                    logger.log(Level.INFO, "Vehicle with VIN '" + vin + "' found.");
                    remoteVehicle = remoteVehicleObservable.get();
                    initializeListeners();
                }
            }
        });
    }

    private void addVehicle(String vehicleId, String vin) {
        Location location = car.getLocation();
        RemoteFunctionObject rfAddVehicle = RemoteFunctionBuilder.create("addVehicle")
                .cachingEnabled(false)
                .param("vehicleIdentifier", vehicleId)
                .param("vin", vin)
                .param("latitude", String.valueOf(location != null ? location.getLatitude() : 0.0f))
                .param("longitude", String.valueOf(location != null ? location.getLongitude() : 0.0f))
                .param("bearing", String.valueOf(car.getBearing()))
                .param("speed", String.valueOf(car.getSpeed()))
                .object();

        GluonObservableObject<CloudLinkObject> response = rfAddVehicle.call(CloudLinkObject.class);
        response.addListener((o, p, n) -> {
            if (n != null && vehicleId.equals(n.getUid())) {
                retrieveRemoteVehicle(vin);
            }
        });
    }

    private void initializeListeners() {
        car.locationProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                try {
                    Location orig = car.getLocation();
                    remoteVehicle.setLat(orig.getLatitude());
                    remoteVehicle.setLon(orig.getLongitude());
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        });
        car.bearingProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                remoteVehicle.setBearing(car.getBearing());
            }
        });
        car.speedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                remoteVehicle.setSpeed(car.getSpeed());
            }
        });
    }
}
