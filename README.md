## Synopsis

This is a Chess game using AngularJS (v4) as client and Spring Boot as server running a Web Socket handler.

The app is deployed here: http://cam.dynadrop.com/chess

## Dev - Client Install
Client is a simple AngularJS (v4) app using a raw Web Socket client. Works only on modern browsers.
```
cd client/
npm install
ng serve
```

## Dev - Server Install
Server is a Spring Boot app with a Web Socket handler.
Follow steps to run locally
```
cd server/
./gradlew build
./gradlew test -i #(run junit)
./gradlew bootRun
```

## Prod Deploy - Client
```
cd client/
ng build --prod
cp dist/* [the chess folder in your Apache's www html folder]
```

## Prod Deploy - Server
Edit start.sh to change java memory usage
```
cd server/
mvn package #will not build if tests failed
chmod +x start.sh
start.sh
```
