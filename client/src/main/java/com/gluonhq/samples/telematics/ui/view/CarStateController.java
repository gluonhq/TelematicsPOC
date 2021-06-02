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
package com.gluonhq.samples.telematics.ui.view;

import com.gluonhq.ignite.micronaut.view.FXMLView;
import com.gluonhq.samples.telematics.model.Car;
import com.gluonhq.samples.telematics.ui.Tools;
import com.gluonhq.samples.telematics.ui.control.BatteryChargeIndicator;
import com.gluonhq.samples.telematics.ui.event.ToggleThemeEvent;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.context.event.ApplicationEventPublisher;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javax.inject.Inject;

@Prototype
class CarStateView extends FXMLView<GridPane> {}

@Prototype
public class CarStateController {

    private final BatteryChargeIndicator chargeIndicator = new BatteryChargeIndicator();
    private final Button chargeState = new Button("", chargeIndicator);

    @Inject
    private Car car;

    @Inject
    private ApplicationEventPublisher eventPublisher;

    @FXML
    private Label speedLabel;

    @FXML
    private HBox states;

    @FXML
    private ImageView carImage;

    @FXML
    private void initialize() {

        chargeState.setContentDisplay(ContentDisplay.RIGHT);
        carImage.setFitHeight(Tools.in2px(3.0));
        carImage.setImage( new Image("/teslaM3-top.png"));

        ContextMenu menu = new ContextMenu(
            buildMenuItem("Toggle Theme", e -> eventPublisher.publishEvent( new ToggleThemeEvent())),
           new Menu( "Simulate Track", null,
               buildMenuItem( "Track 1", e -> car.move("/track1.gpx")),
               buildMenuItem( "Track 2", e -> car.move("/track2.gpx")),
               buildMenuItem( "Track 3", e -> car.move("/track3.gpx"))
           )
        );

        carImage.setOnContextMenuRequested( e -> {
            Point2D p = carImage.localToScreen( e.getX(), e.getY());
            menu.show(carImage, p.getX(), p.getY());
        });

        refreshSpeedState();
        car.speedProperty().addListener(o -> refreshSpeedState());

        refreshButteryState();
        car.batteryChargeProperty().addListener((o, x, charge) -> refreshButteryState());

        states.getChildren().addAll(Tools.getSpacer(), chargeState);
	Thread t = new Thread() {
	    @Override public void run() {
	        try {
	            Thread.sleep(5000);
	            car.move("/track1.gpx");
	        } catch (Exception e) {
		    e.printStackTrace();
	        }
	    }
	};
	if (System.getProperty("autostart", "false").equals("true")) {
	    t.start();
        }
    }

    private MenuItem buildMenuItem(String title, EventHandler<ActionEvent> action) {
       MenuItem item = new MenuItem( title );
       item.setOnAction(action);
       return item;
    }

    private void refreshSpeedState() {
        speedLabel.setText( Integer.toString(car.getSpeed()));
    }
    private void refreshButteryState() {
        chargeIndicator.setCharge(car.getBatteryCharge());
        chargeState.setText(String.format("%.0f%%", car.getBatteryCharge() * 100));
    }

}
