gulp = require "gulp"
_ = require("gulp-load-plugins")()
wiredep = require("wiredep").stream
_gulpsrc = gulp.src
Proxy = require("proxy-middleware")
url = require("url")
del = require("del")
series = require("stream-series")

config =
  apiPath: "/app/"
  app: "src/main/webapp"
  assets: "src/main/assets"
  dist: "src/main/webapp"
  host: "localhost"
  main: "src/main"
  port: 9000
  proxyPath: "http://localhost:8081/app/"
  serverPath: "http://localhost:8081/app"

paths = {}

# application itself, one-page landing page
modules = [
  'app'
  'one-page'
].join ','

setPaths = ->
  paths =
    api: "api"
    app:
      javascripts: "#{config.assets}/#{modules}/javascripts/**/*.js"
      templates: "#{config.app}/#{modules}/templates/**/*.html"
      unify: "#{config.main}/unify/{#{modules}}/**/*"
    assets2compile:
      html: "#{config.assets}/*.html"
      javascripts: "#{config.assets}/{#{modules}}/javascripts/**/*.coffee"
      stylesheets: "#{config.assets}/{#{modules}}/stylesheets/**/*.styl"
    dist:
      app:
        html: "#{config.dist}"
        jsVendor: "#{config.dist}/javascripts/app/vendor"
        jsApp: "#{config.dist}/javascripts/app"
        stylesheets: "#{config.dist}/stylesheets/app"
        templates: "#{config.dist}/templates/app"
        unify: "#{config.dist}/unify/app"
      assets: "#{config.dist}"
      javascripts: "#{config.dist}/javascripts"
      stylesheets: "#{config.dist}/stylesheets"
      unify: "#{config.dist}/unify"

setPaths()

vendor = ->
# js sources
  vendorJS = gulp.src ["#{paths.dist.app.jsVendor}/**/*.js", "#{paths.dist.app.unify}/**/*.js"], {read: false}
  appJS = gulp.src(["#{paths.dist.app.jsApp}/**/*.js", "!#{paths.dist.app.jsApp}/vendor/**/*"]).pipe _.angularFilesort()

  # css sources
  unifyCSS = gulp.src ["#{paths.dist.app.unify}/**/*.css"], {read: false}
  appCSS = gulp.src ["#{paths.dist.app.stylesheets}/stylesheets/main.css"], {read: false}

  gulp.src "#{config.assets}/app.html"
  .pipe wiredep({
    ignorePath: "../webapp"
  })
  .pipe _.inject(appCSS, {ignorePath: "/#{config.dist}/", name: "app"})
  .pipe _.inject(unifyCSS, {ignorePath: "/#{config.dist}/", name: "unify"})
  .pipe _.inject(vendorJS, {ignorePath: "/#{config.dist}/", name: "lib"})
  .pipe _.inject(appJS, {ignorePath: "/#{config.dist}/"})
  .pipe gulp.dest(config.dist)
  .pipe _.filesize()
  .pipe _.connect.reload()

gulp.src = ->
  _gulpsrc.apply(gulp, arguments)
  .pipe(_.plumber
      errorHandler: (err) ->
        console.log err
  )

gulp.task "connect", ["vendor"], ->
  _.connect.server
    livereload: true
    port: config.port
    root: config.dist,
    middleware: (connect, opt) ->
      options = url.parse(config.proxyPath)
      options.route = config.apiPath
      [Proxy options]

# gulp.src(__filename)
# .pipe _.open
#   uri: "http://#{config.host}:#{config.port}/"


gulp.task "clean", (cb) ->
#del config.dist, cb

copy = (from, to) ->
  gulp.src from
  .pipe gulp.dest to
  .pipe _.filesize()

gulp.task "copy-html", ->
  copy paths.app.templates, paths.dist.app.templates

gulp.task "copy-javascripts", ->
  copy paths.app.javascripts, paths.dist.javascripts

gulp.task "copy-assets", ->
# copy assets without filesize
#   gulp.src paths.app.assets
#   .pipe gulp.dest paths.dist.assets
  copy paths.app.unify, paths.dist.unify

gulp.task "copy-html-watch", ["copy-html"], ->
  vendor()

gulp.task "copy", ["copy-javascripts", "copy-assets", "copy-html"]

# compile javascripts
javascripts = ->
  gulp.src paths.assets2compile.javascripts
  .pipe _.sourcemaps.init()
  .pipe _.coffee()
  .pipe _.ngAnnotate()
  .pipe _.sourcemaps.write "./"
# .pipe _.rev()
  .pipe gulp.dest paths.dist.javascripts
#  .pipe _.filesize()

# compile stylesheets
stylesheets = ->
  gulp.src paths.assets2compile.stylesheets
  .pipe _.stylus()
  .pipe gulp.dest paths.dist.stylesheets
  .pipe _.filesize()

# used to trigger livereload when an asset was recompiled
gulp.task "javascripts", -> javascripts().pipe _.connect.reload()
gulp.task "stylesheets", -> stylesheets().pipe _.connect.reload()

# used to recompile the stylesheets when the index file was changed and the assets have to be injected again
gulp.task "javascripts-watch-html", -> javascripts()
gulp.task "stylesheets-watch-html", -> stylesheets()

gulp.task "vendor", ["copy", "javascripts", "stylesheets"], vendor
gulp.task "vendor-watch", ["copy-html", "javascripts-watch-html", "stylesheets-watch-html"], vendor

gulp.task "minify", ["vendor"], ->
  gulp.src "#{config.dist}/index.html"
  .pipe _.usemin()
  .pipe gulp.dest(config.dist)

gulp.task "watch", ->
  gulp.watch [paths.assets2compile.html, paths.app.templates], ["vendor-watch"]
  gulp.watch [paths.assets2compile.javascripts], ["javascripts"]
  gulp.watch [paths.assets2compile.stylesheets], ["stylesheets"]

gulp.task "default", ["connect", "watch"]
