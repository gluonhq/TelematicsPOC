/*
 * Copyright (C) 2013-2021 2048FX
 * Jose Pereda, Bruno Borges & Jens Deters
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jpereda.game2048;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.lifecycle.LifecycleEvent;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.ignite.micronaut.FXApplication;
import io.micronaut.runtime.event.annotation.EventListener;
import javafx.application.ConditionalFeature;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jpereda.game2048.model.GameModel;
import org.jpereda.game2048.views.GameController;
import org.jpereda.game2048.views.GameView;

/**
 * The game 2048 built using JavaFX and Java 8. 
 * This is a fork based on the Javascript version: https://github.com/gabrielecirulli/2048
 * 
 * @author Bruno Borges bruno.borges@oracle.com & Jose Pereda jperedadnr@gmail.com 
 * Based on https://github.com/brunoborges/fx2048
 * 
 * Android Platform: Jose Pereda jperedadnr@gmail.com
 * 
 * iOS Platform: Jens Deters jens.deters@codecentric.de
 */
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Game2048 {

    public static final String VERSION = "3.0.0";

    public static final String GAME_MODE = "game_mode";
    public static final String GAME_VIBRATE_MODE_ON = "vibrate_mode_on";
    public static final String GAME_LEGACY = "game_legacy";
    public static final String GAME_ID = "game_id";


    private final BooleanProperty stop = new SimpleBooleanProperty();
    private final BooleanProperty pause = new SimpleBooleanProperty();

    @Inject
    private GameController gameController;

    @Inject
    private GameModel gameModel;

    @Inject
    private GameView game;

    private static Stage stage;

    @EventListener
    void onAppInit(FXApplication.InitEvent event) {
        LifecycleService.create()
                .ifPresent(service -> {
                    service.addListener(LifecycleEvent.PAUSE, () -> pause.set(true));
                    service.addListener(LifecycleEvent.RESUME, () -> {
                        pause.set(false);
                        stop.set(false);
                    });
                });
    }

    @EventListener
    void onAppStart(FXApplication.StartEvent event) {
        // prevent this stage from showing if the event stage has already an scene
        if (event.getStage().getScene() != null) {
            return;
        } else {
            event.getStage().sceneProperty().addListener((obs, ov, nv) -> {
                if (nv != null && stage != null) {
                    stage.hide();
                }
            });
        }
        stage = new Stage();
        Scene scene = new Scene(game.getRoot());

        String display = DisplayService.create()
                .map(service -> service.isTablet() ? "tablet" : "phone")
                .orElse("phone");
        scene.getStylesheets().add(GameManager.class.getResource(display + ".css").toExternalForm());

        scene.getRoot().getStyleClass().add(gameModel.getGameMode().toString().toLowerCase());
        gameModel.gameModeProperty().addListener((obs, m, m1) -> {
            scene.getRoot().getStyleClass().remove(m.toString().toLowerCase());
            scene.getRoot().getStyleClass().add(m1.toString().toLowerCase());
        });

        if (Platform.isDesktop()) {
            DisplayService.create()
                    .ifPresent(service -> {
                        if (service.isTablet()) {
                            // tablet
                            stage.setWidth(600);
                            stage.setHeight(800);
                        }
                    });

            stage.setTitle("2048FX");
            stage.getIcons()
                    .add(new Image(GameManager.class.getResourceAsStream("Icon-60.png")));
        }

        gameController.pauseProperty().bind(pause);
        gameController.stopProperty().bind(stop);

        if (Platform.isDesktop() && isARMDevice()) {
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
        }

        if (javafx.application.Platform.isSupported(ConditionalFeature.INPUT_TOUCH)) {
            scene.setCursor(Cursor.NONE);
        }

        stage.setScene(scene);
        stage.show();
    }

    public static Stage getStage() {
        return stage;
    }

    private boolean isARMDevice() {
        return System.getProperty("os.arch").toUpperCase().contains("ARM");
    }

    @EventListener
    void onAppStop(FXApplication.StopEvent event) {
        gameController.stopGame();
    }

}
