## Angular2 Spring Boot Chess

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
Follow steps to run locally.
```
cd server/
./gradlew build
./gradlew test -i #to run junit
./gradlew bootRun
```

## Prod - Client Deploy
Edit index.html changing base href to "/chess" or whatever you deployed.
Ignore this if deployed in the root of domain.
```
cd client/
ng build --prod
cp dist/* [the chess folder in your Apache's www html folder]
```

## Prod - Server Deploy
Edit start.sh to change java memory usage.
mvn package will not build if tests fail.
```
cd server/
mvn package
chmod +x start.sh
start.sh
```
