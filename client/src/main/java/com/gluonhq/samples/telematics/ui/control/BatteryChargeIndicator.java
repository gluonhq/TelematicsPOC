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
package com.gluonhq.samples.telematics.ui.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Region;

public class BatteryChargeIndicator extends Region {

    private final Region batteryShell = new Region();
    private final Region batteryCharge = new Region();
    private final Region terminal = new Region();

    public BatteryChargeIndicator() {
        getStyleClass().add("battery-indicator");
        batteryShell.getStyleClass().add("shell");
        terminal.getStyleClass().add("shell");
        batteryCharge.getStyleClass().add("charge");
        getChildren().addAll(batteryShell, terminal, batteryCharge);
    }

    // chargeProperty 0.0 - 1.0
    private final DoubleProperty chargeProperty = new SimpleDoubleProperty(this, "charge", 0);
    public final DoubleProperty chargeProperty() {
       return chargeProperty;
    }
    public final double getCharge() {
       return chargeProperty.get();
    }
    public final void setCharge(double value) {
        chargeProperty.set(value);
    }

    private static final double inset = 1.5;
    private static final double inset2 = inset * 2;
    private static final double terminalWidth = 3;

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double x = snappedLeftInset();
        final double y = snappedTopInset();
        final double w = snapSizeX(getWidth()) - x - snappedRightInset() - (terminalWidth + 1);
        final double h = snapSizeY(getHeight()) - y - snappedBottomInset();
        batteryShell.resizeRelocate(x,y,w,h);
        batteryCharge.resizeRelocate(x + inset, y + inset,(w-inset2) * getCharge() , h - inset2);
        terminal.resizeRelocate(w + 1, y+ h/4, terminalWidth, h/2);
    }

}
