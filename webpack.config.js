var webpack = require('webpack');

module.exports = {
    entry: {
        app: './app/assets/admin/javascripts/app.js',
        vendor: ['jquery', 'angular', 'angular-ui-router']
    },
    output: {
        path: './public/javascripts/',
        filename: 'admin.bundle.js'
    },
    devtool: '#source-map',
    plugins: [
        new webpack.optimize.CommonsChunkPlugin("vendor", "vendor.bundle.js")
    ],
    module: {
        loaders: [{
            test: /\.js$/,
            exclude: 'node_modules',
            loaders: ['ng-annotate', 'babel-loader']
        }, {
            test: /\.css$/,
            loader: 'style-loader!css-loader'
        }, {
            test: /\.(gif|png|jpe?g)$/,
            loader: "url-loader?limit=100000"
        }, {
            test: /\.jpg$/,
            loader: "file-loader"
        }, {
            test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/,
            loader: 'url?limit=10000&mimetype=application/font-woff'
        }, {
            test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
            loader: 'url?limit=10000&mimetype=application/octet-stream'
        }, {
            test: /\.eot(\?v=\d+\.\d+\.\d+)?$/,
            loader: 'file'
        }, {
            test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
            loader: 'url?limit=10000&mimetype=image/svg+xml'
        }, {
            test: /\.html/,
            loader: 'html'
        }]
    }
};
