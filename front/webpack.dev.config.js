const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const merge = require('webpack-merge');
const webpackBaseConfig = require('./webpack.base.config.js');
const fs = require('fs');

fs.open('./src/config/env.js', 'w', function (err, fd) {
    const buf = 'export default "development";';
    fs.write(fd, buf, 0, 'utf-8', function (err, written, buffer){});
});

module.exports = merge(webpackBaseConfig, {
    devtool: '#source-map',
    output: {
        publicPath: '/dist/',
        filename: '[name].js',
        chunkFilename: '[name].chunk.js'
    },
    devServer: {
        proxy: {
            "/api": {
                target: "http://localhost:3000",
                bypass: function(req, res) {
                    if(req.headers.accept.indexOf("html") !== -1){
                        console.log("Skipping proxy for browser request.");
                        return '/index.html';
                    } else {
                        const name = req.path.split("/api/")[1].split("/").join("_");
                        const mock = require(`./mock/${name}`);
                        const result = mock(req.method);
                        delete require.cache[require.resolve(`./mock/${name}`)];
                        return res.send(result);
                    }
                }
            }
        }
    },
    plugins: [
        new ExtractTextPlugin({
            filename: '[name].css',
            allChunks: true
        }),
        new webpack.optimize.CommonsChunkPlugin({
            name: 'vendors',
            filename: 'vendors.js'
        }),
        new HtmlWebpackPlugin({
            filename: '../index.html',
            template: './src/template/index.ejs',
            inject: false
        })
    ]
});