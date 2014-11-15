README for respondeco
==========================

# run project

In order to run the application you have to start the backend and the frontend seperately.

## prerequisites

In order to run the application you first need the following software

* [node.js](http://nodejs.org/)
* [maven](http://maven.apache.org/)

Installed via npm (node.js package manager - have a look at commands!)

* [grunt](http://gruntjs.com/)
* [bower](http://bower.io/)

to run the following commands!

### commands

```
npm install -g grunt-cli      # installs grunt globally so you can use it in the terminal (task runner)
npm install -g bower          # the same for bower (dependency management)
npm install                   # in the applications root directory!
bower install                 # in the applications root directory!
```

After those three steps you should be able to run the backend and the frontend.

## backend

Open a terminal, go to the root directory and enter:

```
mvn spring-boot:run
```

## frontend

Open a terminal, go to the root directory and enter:

```
grunt server
```

This will startup a server for the frontend and also launch a browser window with the application. It also enables the live reload feature - so if you change some style definitions they will be live injected into the browser. If you change some javascript code the livereload will automatically reload the page with the new scripts.

## details

For more details visit [jHipster development](https://jhipster.github.io/development.html).

# git commits

When you commit always add the key of the regarding JIRA issue as this will automatically connect the commit with the issue. In JIRA the commit will now show up under the development section.
