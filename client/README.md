# Telematics Client

The Telematics client app is a simulation of an in-vehicle UI application. You can simulate a moving car by
right clicking on the image of the car and selecting one of the available tracks.

By default, it creates a random VIN during the first run and stores it for future use. For testing purposes,
you can [provide a custom VIN](#run-the-sample-with-a-custom-vin) to launch extra vehicles.

## Basic Requirements

A list of the basic requirements can be found online in the [Gluon Client documentation](https://docs.gluonhq.com/client/#_requirements).

## Quick instructions

### Run the sample

    mvn javafx:run

### Run the sample with a custom VIN

    mvn javafx:run -Dvin=VIN123abc

### Run the sample as a native image

Create the config files:

     mvn client:runagent

 Build image and run:
 
    mvn client:build client:run

### Run the sample as a native android image

    mvn -Pandroid client:build client:package client:install client:run

### Run the sample as a native iOS image

    mvn -Pios client:build client:run

## Selected features

This is a list of all the features that were selected when creating the sample:

### JavaFX 17 Modules

 - javafx-base
 - javafx-graphics
 - javafx-controls

### Gluon Features

 - Maps: cross-device map interfacing
 - Attach display
 - Attach lifecycle
 - Attach statusbar
 - Attach storage
