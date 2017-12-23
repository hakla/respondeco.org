var path = require('path')
var webpack = require('webpack')
var utils = require('./utils')
var config = require('../config')
var vueLoaderConfig = require('./vue-loader.conf')

function resolve (dir) {
  return path.join(__dirname, '../..', dir)
}

module.exports = {
  entry: {
    admin: './app/assets/vue/admin/main.js',
    app: './app/assets/vue/app/main.js',
    vendor_admin: ['bootstrap', 'bootstrap/dist/css/bootstrap.css', 'jquery'],
    vendor_app: ['./unify/assets/vendor/bootstrap/bootstrap.js', './unify/assets/vendor/bootstrap/bootstrap.css', './unify/assets/vendor/jquery/jquery.js', 'popper.js']
  },
  output: {
    path: config.build.assetsRoot,
    filename: '[name].js',
    publicPath: process.env.NODE_ENV === 'production'
      ? config.build.assetsPublicPath
      : config.dev.assetsPublicPath
  },
  plugins: [
    new webpack.optimize.CommonsChunkPlugin({
      name: "common",
      // filename: "vendor.js"
      // (Give the chunk a different name)

      minChunks: Infinity,
      // (with more entries, this ensures that no other module
      //  goes into the vendor chunk)
    })
  ],
  resolve: {
    extensions: ['.js', '.vue', '.json'],
    alias: {
      'vue$': 'vue/dist/vue.esm.js',
      '@': resolve('src'),
      'admin': resolve('app/assets/vue/admin'),
      'app': resolve('app/assets/vue/app'),
      'assets': resolve('src/assets'),
      'common': resolve('app/assets/vue/common'),
      'components': resolve('src/components'),
      'mixins': resolve('app/assets/vue/app/mixins'),
      'unify': resolve('unify/assets')
    }
  },
  module: {
    rules: [
      {
        test: /\.vue$/,
        loader: 'vue-loader',
        options: vueLoaderConfig
      },
      {
        test: /\.js$/,
        loader: 'babel-loader',
        include: resolve(''),
        exclude: /node_modules/
      },
      {
        test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
        loader: 'url-loader',
        query: {
          limit: 10000,
          name: utils.assetsPath('/img/[name].[hash:7].[ext]')
        }
      },
      {
        test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
        loader: 'url-loader',
        query: {
          limit: 10000,
          name: utils.assetsPath('/fonts/[name].[hash:7].[ext]')
        }
      }
    ]
  }
}
