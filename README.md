README for respondeco
==========================

# run project

In order to run the application you have to start the backend and the frontend seperately.

## prerequisites

In order to run the application you first need the following software

* [node.js](http://nodejs.org/)
* [sbt](http://www.scala-sbt.org/)

### commands

```
npm install                   # in the applications root directory!
```

After these steps you should be able to run the backend and the frontend.

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
