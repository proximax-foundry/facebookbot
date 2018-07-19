# Facebook Bot

This application reads messages from hooked Facebook website's messenger. Content of these messages is uploaded into ProximaX storage.

## Installing
```
git clone https://github.com/proximax-storage-foundry/facebookbot.git
cd messenger
```
Using Maven
```
mvn package
```
Now you should have .jar in target/jars/ directory.

## Configuring

## Running
App starts a webserver on configured ports. It can listen both http and https. In case of https you must provide signed SSL/TLS certificate in pkcs12 format.
