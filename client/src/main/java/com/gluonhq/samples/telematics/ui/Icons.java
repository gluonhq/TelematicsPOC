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

import javafx.scene.Node;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

public enum Icons {

    CAR(FontAwesomeSolid.CAR),
    GAME(FontAwesomeSolid.GAMEPAD),
    LOCK_CLOSED(FontAwesomeSolid.LOCK),
    LOCK_OPEN(FontAwesomeSolid.UNLOCK_ALT),
    MENU_UP(FontAwesomeSolid.CHEVRON_CIRCLE_UP),
    MINUS(FontAwesomeSolid.MINUS),
    MUSIC(FontAwesomeSolid.MUSIC),
    PLUS(FontAwesomeSolid.PLUS),
    VOLUME_UP(FontAwesomeSolid.VOLUME_UP),
    WINDOW_CLOSE(FontAwesomeSolid.TIMES);

    private final Ikon ikon;

    Icons(Ikon ikon) {
        this.ikon = Objects.requireNonNull(ikon);
    }

    public Node asGraphic() {
        FontIcon icon = FontIcon.of(ikon);
        icon.getStyleClass().setAll("font-icon");
        return icon;
    }

}
