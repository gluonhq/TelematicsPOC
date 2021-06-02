# Telematics POC

This is a POC of a vehicle telematics app that can run on desktop and embedded devices.

This POC currently contains two apps:

 - The Telematics Client app 

 - The Telematics Game 2048FX app

## Basic Requirements

A list of the basic requirements can be found online in the [Gluon documentation](https://docs.gluonhq.com/#_requirements).

## Quick instructions

### Install the project

    mvn clean install
    
### Run the Client app

    mvn javafx:run -f client

### Run the Game app

As standalone app:

    mvn javafx:run -f games/Game2048FX
    
It can be run as well from the Client app.

### Run the Client app as a native image (desktop)

Create the config files:

     mvn client:runagent -f client

 Build image and run:
 
    mvn client:build client:run -f client

## Embedded

### HotSpot

On a Raspberry Pi (32/64 bits), follow the [documentation](https://docs.gluonhq.com/#_javafx) to add the JavaFX SDK.

The application works on HotSpot directly from the Pi:

    mvn javafx:run -f client

Note: configure the properties defined under the Maven Pi profile accordingly.

### Native image

On Linux, build the native image:

    mvn -Ppi client:build -f client
    
Deploy it to a Raspberry Pi (64 bits) and run: 

    mvn -Ppi client:install client:run -f client

Note: configure the properties defined under the Maven Pi profile accordingly.

## Selected features

This is a list of all the features that were selected when creating the sample:

### JavaFX 17 Modules

 - javafx-base
 - javafx-graphics
 - javafx-controls
 - javafx-fxml
 - javafx-media

### Gluon Features

 - Maps: cross-device map interfacing
 - Attach display
 - Attach lifecycle
 - Attach settings
 - Attach statusbar
 - Attach storage
