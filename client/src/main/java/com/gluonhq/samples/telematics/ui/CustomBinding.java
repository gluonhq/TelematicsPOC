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

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

import java.util.function.Function;

public class CustomBinding {

    public static <A ,B> void bindBidirectional(Property<A> propertyA, Property<B> propertyB, Function<A,B> updateB, Function<B,A> updateA){
        addFlaggedChangeListener(propertyA, propertyB, updateB);
        addFlaggedChangeListener(propertyB, propertyA, updateA);
    }

    public static <A ,B> void bind(Property<A> propertyA, Property<B> propertyB, Function<A,B> updateB){
        addFlaggedChangeListener(propertyA, propertyB, updateB);
    }

    private static <X,Y> void addFlaggedChangeListener(ObservableValue<X> propertyX, WritableValue<Y> propertyY, Function<X,Y> updateY){
        propertyX.addListener(new ChangeListener<X>() {
            private boolean alreadyCalled = false;

            @Override
            public void changed(ObservableValue<? extends X> observable, X oldValue, X newValue) {
                if(alreadyCalled) return;
                try {
                    alreadyCalled = true;
                    propertyY.setValue(updateY.apply(newValue));
                }
                finally {alreadyCalled = false; }
            }
        });
    }
}
