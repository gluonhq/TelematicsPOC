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

import com.gluonhq.samples.telematics.ui.event.ToggleThemeEvent;
import com.gluonhq.samples.telematics.games.Game2048FX;
import com.gluonhq.samples.telematics.ui.Icons;
import com.gluonhq.samples.telematics.ui.Tools;
import com.gluonhq.samples.telematics.ui.UITheme;
import io.micronaut.runtime.event.annotation.EventListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public class MainScreen  extends BorderPane {

    public static final double SCREEN_WIDTH  = Tools.in2px(12.5);
    public static final double SCREEN_HEIGHT = Tools.in2px(8.0);

    // themeProperty
    private final ObjectProperty<UITheme> themeProperty = new SimpleObjectProperty<>(this, "theme", UITheme.DARK);
    public final ObjectProperty<UITheme> themeProperty() {
       return themeProperty;
    }
    public final UITheme getTheme() {
       return themeProperty.get();
    }
    public final void setTheme(UITheme theme) {
        themeProperty.set(theme);
        Objects.requireNonNull(theme).assignTo(getScene());
    }

    @Inject
    private ContentView mainContentView;

    @Inject
    private NavigationView navigationView;

    @Inject
    private GamesView games;

    @Inject
    private Game2048FX game2048FX;

    @PostConstruct
    void init() {

        setMaxSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setMinSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        // TODO abstract out the toolbar
        ToolBar mainMenu = new ToolBar();
        mainMenu.getStyleClass().add("main-menu");

        mainMenu.getItems().addAll(
            Tools.buildButton(Icons.CAR.asGraphic(), e -> navigationView.showWindow( new Label("Car settings"))),
            Tools.buildButton(Icons.MUSIC.asGraphic()),
            Tools.buildButton(Icons.GAME.asGraphic(), e -> {
                processGameService();
                navigationView.showWindow(games);
            }),
            Tools.buildButton(Icons.MENU_UP.asGraphic()),
            Tools.buildButton(Icons.VOLUME_UP.asGraphic())
        );

        setCenter(mainContentView);
        setBottom(mainMenu);

        sceneProperty().addListener( (o, x, scene) -> {
            if (scene != null) {
                setTheme(getTheme());
            }
        });

    }

    public void toggleTheme() {
        setTheme( getTheme().reverse() );
    }

    @EventListener
    void onToggleTheme(ToggleThemeEvent event) {
        toggleTheme();
    }

    private void processGameService() {
        games.getPlaceHolder(0)
            .ifPresent(box -> box.getChildren().set(0, game2048FX));
    }
}
