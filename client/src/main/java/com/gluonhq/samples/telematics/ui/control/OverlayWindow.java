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

import com.gluonhq.samples.telematics.ui.Icons;
import com.gluonhq.samples.telematics.ui.Tools;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;

import javax.inject.Singleton;

@Singleton
public class OverlayWindow extends Region {

    private Node content;
    private final Button cancelButton = Tools.buildButton(Icons.WINDOW_CLOSE.asGraphic());

    public OverlayWindow() {
        getStyleClass().addAll("overlay-panel", "window");
        setVisible(false);
        cancelButton.setOnAction(e -> OverlayWindow.this.setVisible(false));
        visibleProperty().addListener((o, x, visible) -> {
            if (visible) {
                OverlayWindow.this.requestLayout();
            }
        });
        setContent(null);
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        this.content = content;
        if (content != null) {
            getChildren().setAll(content, cancelButton);
        } else {
            getChildren().add(cancelButton);
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double x = snappedLeftInset();
        final double y = snappedTopInset();
        final double w = snapSizeX(getWidth()) - x - snappedRightInset();
        final double h = snapSizeY(getHeight()) - y - snappedBottomInset();
        if (content != null) {
            content.resizeRelocate(x, y, w, h);
        }
        cancelButton.relocate(x, y);
    }

}
