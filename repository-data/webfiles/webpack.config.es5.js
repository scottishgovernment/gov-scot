const webpackConfig = require('./webpack.config.js');
const path = require('path');

const babelRule = {
    test: /(govscot|shared|utils)\/.*\.js$/,
    exclude: /(node_modules|bower_components)/,
    use: {
        loader: 'babel-loader',
        query: {
            presets: ['@babel/env']
        }
    }
}

webpackConfig.module.rules.push(babelRule);

webpackConfig.output = {
    path: path.resolve(__dirname, 'src/main/resources/site/assets/scripts'),
    filename: '[name].es5.js'
};

module.exports = webpackConfig;
