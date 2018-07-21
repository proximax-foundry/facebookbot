# Facebook Bot

This application reads messages from hooked Facebook website's messenger. Content of these messages is uploaded into ProximaX storage.

## Installing
```
git clone https://github.com/proximax-storage-foundry/facebookbot.git
cd facebookbot
```
Using Maven
```
mvn package
```
Now you should have .jar in target/jars/ directory. This JAR is statically linked.

## Configuring
Application is configured by environmental variables.

```
ACCESS_TOKEN - Access token for FB webhook
VERIFY_TOKEN - Verification token for FB webhook
CERT_PATH - Path to pkcs12 keystore file
CERT_PASSWORD - Password to keystore
PROXIMAX_ADDR - URL address of ProximaX gateway
PRIVATE_KEY - Private key of sender's NEM account
PUBLIC_KEY - Public key of receiver's NEM account
SERVER_HOST - Local address of a web server
SERVER_HTTP_PORT - Port for listen to HTTP
SERVER_HTTPS_PORT - Port for listen to HTTPS
```

You can chose to set just one of the ports or both of them. In case of https you must provide signed SSL/TLS certificate in pkcs12 format. If no port is set, the app will exit immediately.

## Running
App starts a webserver on configured ports. 

```
java -jar target/jars/messenger-1.0-SNAPSHOT-jar-with-dependencies.jar
```
## Useful links
You can follow this guide to start with Facebook messenger webhooks  
https://x-team.com/blog/how-to-get-started-with-facebook-messenger-bots/

Application is using RestFB library for Facebook API  
https://restfb.com/documentation/
