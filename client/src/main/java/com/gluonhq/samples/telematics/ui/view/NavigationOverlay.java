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

import com.gluonhq.ignite.micronaut.OnFXThread;
import com.gluonhq.samples.telematics.model.Car;
import com.gluonhq.samples.telematics.ui.Icons;
import com.gluonhq.samples.telematics.ui.Tools;
import com.gluonhq.samples.telematics.ui.control.OverlayWindow;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Singleton
class NavigationOverlay extends BorderPane {

    // TODO May need global access
    private static final DateTimeFormatter pattern = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    @Inject
    private OverlayWindow overlayWindow;

    private final Label clockLabel = new Label();

    @Inject
    private NavigationMapView mapView;

    @Inject
    private Car car;

    @PostConstruct
    private void init() {

        setPickOnBounds(false); // mouse transparent except children

        car.dateTimeProperty().addListener( o-> refreshTime());

        ToggleButton lockButton = Tools.getToggleButton(car.lockedProperty(), this::getLockIcon);
        lockButton.setMaxWidth(40);
        lockButton.setMinWidth(40);

        ToolBar controlBar = new ToolBar();
        controlBar.getStyleClass().add("nav-control-bar");
        controlBar.getItems().addAll(
            lockButton,
            clockLabel,
            new Label("80°F") // TODO use car's temperature property
        );

        ToolBar zoomToolbar = new ToolBar();
        zoomToolbar.setOrientation(Orientation.VERTICAL);
        zoomToolbar.getStyleClass().add("overlay-panel");
        zoomToolbar.getItems().addAll(
            Tools.buildButton(Icons.PLUS.asGraphic(), e -> mapView.zoomBy(1)),
            Tools.buildButton(Icons.MINUS.asGraphic(), e -> mapView.zoomBy(-1))
        );

        VBox sideToolbar = new VBox(zoomToolbar);
        sideToolbar.getStyleClass().add("nav-side-tool-bar");

        setTop(controlBar);

        BorderPane controlOverlay = new BorderPane();
        controlOverlay.setPickOnBounds(false); // mouse transparent except children
        controlOverlay.setRight(sideToolbar);

        StackPane windowStack = new StackPane( controlOverlay, overlayWindow);
        windowStack.setStyle(" -fx-padding: 0 .1in .1in .1in;");
        windowStack.setPickOnBounds(false); // mouse transparent except children

        setCenter( windowStack );

    }

    @OnFXThread
    void refreshTime() {
        clockLabel.setText(pattern.format(ZonedDateTime.now()));
    }

    private Node getLockIcon( boolean isLocked ) {
        return isLocked ? Icons.LOCK_CLOSED.asGraphic(): Icons.LOCK_OPEN.asGraphic();
    }

    public void showWindow( Node content ) {
        if ( content != null ) {
            overlayWindow.setContent(content);
            overlayWindow.setVisible(true);
        }
    }

}

