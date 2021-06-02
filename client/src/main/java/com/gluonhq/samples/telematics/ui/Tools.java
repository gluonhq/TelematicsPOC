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
package com.gluonhq.samples.telematics.ui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;


public class Tools {

    private Tools() {}

    public static double in2px(Double inch) {
        return Screen.getPrimary().getDpi() * inch;
    }

    public static Button buildButton(Node graphic, EventHandler<ActionEvent> handler) {
        Button button = new Button(null, graphic );
        button.setOnAction(
          Optional.ofNullable(handler).orElse( e -> System.out.println(graphic.toString()) )
        );
        return button;
    }

    public static Button buildButton(Node graphic) {
        return buildButton(graphic, null);
    }

    public static ToggleButton getToggleButton(BooleanProperty sourceProperty, Function<Boolean, Node> iconProvider) {
        ToggleButton button = new ToggleButton(null, iconProvider.apply(sourceProperty.get()));
        button.selectedProperty().addListener((o,x,selected) -> button.setGraphic(iconProvider.apply(selected)));
        button.selectedProperty().bindBidirectional(sourceProperty);
        return button;
    }

    public static Node getSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        spacer.setMinWidth(Region.USE_PREF_SIZE);
        spacer.setMinHeight(Region.USE_PREF_SIZE);
        return spacer;
    }

    private static long lastUpdate = 0;
    private static int index = 0;
    private static final double[] frameRates = new double[100];
    private static final DoubleProperty fpsInstProperty = new SimpleDoubleProperty();
    private static final DoubleProperty fpsAveProperty = new SimpleDoubleProperty();
    static {
        AnimationTimer frameRateMeter = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate > 0) {
                    long nanosElapsed = now - lastUpdate;
                    double frameRate = 1_000_000_000.0 / nanosElapsed;
                    index %= frameRates.length;
                    frameRates[index++] = frameRate;
                    fpsInstProperty.set(getInstantFPS());
                    fpsAveProperty.set(getAverageFPS());
                }
                lastUpdate = now;
            }
        };
        frameRateMeter.start();
    }
    /**
     * Returns the instantaneous FPS for the last frame rendered.
     *
     * @return
     */
    public static double getInstantFPS() {
        return frameRates[index % frameRates.length];
    }
    /**
     * Returns the average FPS for the last 100 frames rendered.
     * @return
     */
    public static double getAverageFPS() {
        return Arrays.stream(frameRates)
                .average()
                .orElse(0d);
    }
    public static DoubleProperty getFpsInstProperty() { return fpsInstProperty; }
    public static DoubleProperty getFpsAveProperty() { return fpsAveProperty; }

}
