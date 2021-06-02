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
package com.gluonhq.samples.telematics.games;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jpereda.game2048.GameManager;
import org.jpereda.game2048.views.GameView;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Game2048FX extends ImageView {

    @Inject
    private GameView game;

    public Game2048FX() {
        setImage(new Image(GameManager.class.getResourceAsStream("2048fx.png")));
        setPreserveRatio(true);
        setFitHeight(80);

        // TODO: Remove once cursor is available
        if ("aarch64".equals(System.getProperty("os.arch"))) {
            Platform.runLater(this::run);
        } else {
            setOnMouseClicked(m -> run());
        }
    }

    private void run() {
        BorderPane root = game.getRoot();
        root.getStylesheets().add(GameManager.class.getResource("phone.css").toExternalForm());
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setOnHiding(e -> scene.setRoot(new Group()));
        stage.setTitle("2048FX");
        stage.setScene(scene);
        List<Screen> screen = Screen.getScreens();
        if (screen.size() > 1) {
            Screen secondScreen = screen.get(0);
            stage.setX(secondScreen.getBounds().getMinX());
            stage.setY(secondScreen.getBounds().getMinY());
            stage.setWidth(secondScreen.getBounds().getWidth());
            stage.setHeight(secondScreen.getBounds().getHeight());
        }

        stage.show();
    }

}

