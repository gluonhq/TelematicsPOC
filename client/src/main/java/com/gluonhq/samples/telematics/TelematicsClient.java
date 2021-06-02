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
package com.gluonhq.samples.telematics;

import java.util.List;
import com.gluonhq.ignite.micronaut.FXApplication;
import com.gluonhq.samples.telematics.ui.view.Monitor;
import io.micronaut.runtime.event.annotation.EventListener;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TelematicsClient {

    @Inject
    private Monitor monitor;

    @EventListener
    void onAppStart(FXApplication.StartEvent event) {

        List<Screen> screen = Screen.getScreens();
        Stage stage = event.getStage();
        if (screen.size() > 1) {
            Screen mainScreen = screen.get(1);
            stage.setX(mainScreen.getBounds().getMinX());
            stage.setY(mainScreen.getBounds().getMinY());
            stage.setWidth(mainScreen.getBounds().getWidth());
            stage.setHeight(mainScreen.getBounds().getHeight());
        }
        Scene scene = new Scene(monitor);
        stage.setScene(scene);
        stage.setTitle("Telematics Client App");
        stage.setResizable(false);
        stage.show();
    }
}
