package org.jpereda.game2048.views;

import javafx.scene.Node;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

public enum Icons {

    FILE_DOWNLOAD(FontAwesomeSolid.DOWNLOAD),
    FILE_UPLOAD(FontAwesomeSolid.UPLOAD),
    PAUSE_CIRCLE(FontAwesomeSolid.PAUSE_CIRCLE),
    LOOP(FontAwesomeSolid.SYNC_ALT),
    SHARE(FontAwesomeSolid.SHARE_ALT),
    BOARD(FontAwesomeSolid.LIST_OL);

    private final Ikon ikon;
    private FontIcon icon;

    Icons(Ikon ikon) {
        this.ikon = Objects.requireNonNull(ikon);
    }

    public Node asGraphic() {
        if (icon == null) {
            this.icon = FontIcon.of(ikon);
            icon.getStyleClass().setAll("font-icon");
        }
        return icon;
    }

}
