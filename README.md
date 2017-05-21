README for respondeco
==========================

[ ![Codeship Status for hakla/respondeco.org](https://app.codeship.com/projects/ad40b220-1485-0135-3518-024468c76a36/status?branch=develop)](https://app.codeship.com/projects/217787)

# run project

In order to run the application you have to start the backend and the frontend seperately.

## prerequisites

In order to run the application you first need the following software

* [node.js](http://nodejs.org/)
* [sbt](http://www.scala-sbt.org/)

Run `npm install`.

## backend

Open a terminal, go to the root directory and enter:

```
sbt run
```

## frontend

Open a terminal, go to the root directory and enter:

```
npm run dev
```

This will startup a server for the frontend (running on port 8080).

## deployment

```
npm run build
sbt dist
```

This will create a deployable archive under $PROJECT_ROOT/target/universal/respondeco-$VERSION.zip
