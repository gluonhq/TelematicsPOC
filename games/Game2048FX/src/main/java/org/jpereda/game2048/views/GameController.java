/*
 * Copyright (C) 2013-2019 2048FX
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
package org.jpereda.game2048.views;

import com.gluonhq.attach.vibration.VibrationService;
import io.micronaut.context.annotation.Prototype;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.jpereda.game2048.Direction;
import org.jpereda.game2048.model.GameModel;
import org.jpereda.game2048.service.Cloud;

import javax.inject.Inject;
import java.util.Optional;

@Prototype
public class GameController {

    @FXML
    private BorderPane view;

    @FXML
    private ToolBar bottomNav;

    private Button saveGame;
    private Button restoreGame;
    private Button pauseGame;
    private Button tryAgain;
    private Button share;
    private Button board;
    
    @Inject
    private Cloud cloud;

    @Inject
    private GameModel model;

    private final static int MARGIN = 36;

    private final BooleanProperty first = new SimpleBooleanProperty();
    private final BooleanProperty stop = new SimpleBooleanProperty();
    private final BooleanProperty pause = new SimpleBooleanProperty();
    private final IntegerProperty score = new SimpleIntegerProperty();
    
    private boolean lock = false;

    public void initialize() {
        view.getStyleClass().addAll("game-root");
        view.setCenter(model.getGame());

        Label labelTit = new Label("2048");
        Label labelFX = new Label("FX");
        labelFX.setStyle("-fx-font-size: 0.8em; -fx-text-fill: #f2b179; -fx-translate-y: -8;");
        final HBox hBoxTitle = new HBox(labelTit, labelFX);
        hBoxTitle.setAlignment(Pos.CENTER_LEFT);

        saveGame = new Button(null, Icons.FILE_DOWNLOAD.asGraphic());
        restoreGame = new Button(null, Icons.FILE_UPLOAD.asGraphic());
        pauseGame = new Button(null, Icons.PAUSE_CIRCLE.asGraphic());
        tryAgain = new Button(null, Icons.LOOP.asGraphic());
        share = new Button(null, Icons.SHARE.asGraphic());
        share.setDisable(true);
        board = new Button(null, Icons.BOARD.asGraphic());
        board.setDisable(true);
        bottomNav.getItems().addAll(saveGame, restoreGame, pauseGame, tryAgain, share, board);

        model.getGame().overlayVisible().addListener((obs, ov, nv) -> lock = nv);
        stop.addListener((ov, b0, b2) -> {
            if (b2) {
                model.getGame().saveRecord();
            }
        });
        pause.addListener((ov, b0, b2) -> {
            model.getGame().saveRecord();
            model.getGame().externalPause(b0, b2);
        });
        saveGame.disableProperty().bind(model.saveEnabledProperty().not().or(model.gameOverAndShareProperty()));
        restoreGame.disableProperty().bind(model.restoreEnabledProperty().not());

        model.gameShareProperty().addListener((ov, b0, b2) -> {
            if (b2) {
//                model.gameOverProperty().set(true);
//                share();
            }
        });

        model.getGame().tile2048FoundProperty().addListener((ov, b0, b2) -> {
            if (b2 && model.isVibrateModeOn()) {
                VibrationService.create().ifPresent(VibrationService::vibrate);
            }
        });

        view.sceneProperty().addListener((obs, ov, nv) -> {
                if (nv != null) {
                    if (!first.get()) {
                        ChangeListener<Number> resize = (ob, v, v1) -> gameResize();
                        view.getScene().widthProperty().addListener(resize);
                        view.getScene().heightProperty().addListener(resize);

                        Platform.runLater(this::gameResize);
                        first.set(true);
                    }

                    addKeyHandler(view);
                    addSwipeHandlers(view);
                } else {
                    // when homeview is not shown, remove handlers to avoid interacting with it
                    removeHandlers(view);
                    if (!model.isGameOverAndShare()) {
                        model.pauseGame();
                    }
                }
            });

        // sharing scores is only enabled when the game ends
        share.disableProperty().bind(model.gameOverAndShareProperty().not());

        saveGame.setOnAction(e -> {
            lock = true;
            model.saveSession();
        });
        restoreGame.setOnAction(e -> {
            lock = true;
            model.restoreSession();
        });
        pauseGame.setOnAction(e -> {
            lock = true;
            model.pauseGame();
        });
        tryAgain.setOnAction(e -> {
                lock = true;
                model.tryAgain();
        });
        share.setOnAction(e -> {
//                share();
        });
        board.setOnAction(e -> {
//                board();
        });

        model.gameModeProperty().addListener((obs, ov, nv) -> updateBoard());
        updateBoard();
    }

    private void updateBoard() {
        if (! cloud.isAuthenticated()) {
            return;
        }

        cloud.updateLeaderboard();
    }

    @FXML
    private void showMenu() {
        if (!model.isGameOverAndShare()) {
            model.pauseGame();
        }
    }

    private void share() {
        lock = true;
        if (! cloud.isAuthenticated()) {
            if (! model.isGameOverAndShare()) {
                model.pauseGame();
            }
            showSignInDialog("To share your result");
        } else {
            score.set(model.getScore());
        }
    }

    void board() {
        lock = true;
        if (! cloud.isAuthenticated()) {
            if (! model.isGameOverAndShare()) {
                model.pauseGame();
            }
            showSignInDialog("To access the leaderboard");
        }
    }

    public void stopGame() {
        model.getGame().saveRecord();
    }

    private void gameResize() {
        if (view.getScene() == null) {
            return;
        }

        double W = view.getScene().getWidth() - MARGIN;
        double H = view.getScene().getHeight()
                - bottomNav.getHeight() - MARGIN;
        model.getGame().setMinSize(W, H);
        model.getGame().setPrefSize(W, H);
        model.getGame().setMaxSize(W, H);
    }

    private void addKeyHandler(Node node) {
        node.getScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            KeyCode keyCode = ke.getCode();
            if (keyCode.equals(KeyCode.S)) {
                model.saveSession();
                return;
            }
            if (keyCode.equals(KeyCode.R)) {
                model.restoreSession();
                return;
            }
            if (keyCode.equals(KeyCode.P)) {
                model.pauseGame();
                return;
            }
            if (keyCode.equals(KeyCode.Q)) {
                model.quitGame();
                return;
            }
            if (keyCode.isArrowKey()) {
                Direction direction = Direction.valueFor(keyCode);
                model.move(direction);
            }
        });
    }

    private void addSwipeHandlers(Node node) {
        Scene scene = node.getScene();
        if (scene != null) {
            scene.setOnSwipeUp(e -> move(Direction.UP));
            scene.setOnSwipeRight(e -> move(Direction.RIGHT));
            scene.setOnSwipeLeft(e -> move(Direction.LEFT));
            scene.setOnSwipeDown(e -> move(Direction.DOWN));
        }
    }

    private void removeHandlers(Node node) {
        Scene scene = node.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(null);
            scene.setOnSwipeUp(null);
            scene.setOnSwipeRight(null);
            scene.setOnSwipeLeft(null);
            scene.setOnSwipeDown(null);
        }
    }

    private void move(Direction direction) {
        if (lock) {
            return;
        }
        model.move(direction);
    }

    private void showSignInDialog(String message) {
        // force login view
        HBox title = new HBox(10);
        title.setAlignment(Pos.CENTER_LEFT);
        title.getChildren().add(new ImageView());
        title.getChildren().add(new Label("Sign in required"));
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(new Label(message + ", you have to sign in\nwith your social network profile. \nDo you want to continue?"));
        dialog.getDialogPane().setHeader(title);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        Platform.runLater(() -> {
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                cloud.forceLogin();
            }
        });
    }

    public BooleanProperty pauseProperty() {
        return pause;
    }

    public BooleanProperty stopProperty() {
        return stop;
    }

}
