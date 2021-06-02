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

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.gluonhq.samples.telematics.model.Car;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Pair;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NavigationView extends StackPane {

    @Inject
    private Car car;

    @Inject
    private NavigationOverlay overlay;

    @Inject
    private NavigationMapView mapView;

    private final Node locationIcon = createLocationIcon();
    private final LocationLayer locationLayer = new LocationLayer();
    private MapPoint location = null;

    @PostConstruct
    private void init() {

        mapView.addLayer( locationLayer);
        mapView.setZoom(15);
        mapView.setCenter(new MapPoint(26.8657054,-80.0983174));

        car.locationProperty().addListener( (o,x,p) -> {
            if ( p != null) {
                if ( location == null ) {
                    location = new MapPoint( p.getLatitude(), p.getLongitude());
                    locationLayer.addPoint(location, locationIcon);
                    mapView.setCenter(location);
                } else {
                    location.update( p.getLatitude(), p.getLongitude());
                }
                mapView.flyTo(0, location, 1.0);
            }
        });

        car.bearingProperty().addListener((o,x,b) -> locationIcon.setRotate(b.doubleValue()));

        getChildren().setAll(
            mapView,
            overlay
        );

    }

    private Node createLocationIcon() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(0.0, -10.0, 10.0, 10.0, 0.0, 5.0, -10.0, 10.0);
        polygon.setFill(Color.RED);
        polygon.setStroke(Color.WHITE);
        polygon.setStrokeWidth(2);
        polygon.setEffect( new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 5, .2, 3, 3));
        return polygon;
    }

    public void showWindow( Node content ) {
       overlay.showWindow(content);
    }

}

@Singleton
class NavigationMapView extends MapView {
    public void zoomBy(int factor) {
        setZoom(getZoom() + factor);
    }
}

class LocationLayer extends MapLayer {


    private final ObservableList<Pair<MapPoint, Node>> points = FXCollections.observableArrayList();

    public void addPoint(MapPoint p, Node icon) {
        points.add(new Pair<>(p, icon));
        this.getChildren().add(icon);
        this.markDirty();
    }

    @Override
    protected void layoutLayer() {
        for (Pair<MapPoint, Node> candidate : points) {
            MapPoint point = candidate.getKey();
            Node icon = candidate.getValue();
            Point2D mapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            icon.setVisible(true);
            icon.setTranslateX(mapPoint.getX());
            icon.setTranslateY(mapPoint.getY());
        }
    }

}

