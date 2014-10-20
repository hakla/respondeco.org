README for respondeco
==========================

# run project

In order to run the application you have to start the server and the client seperately.

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
