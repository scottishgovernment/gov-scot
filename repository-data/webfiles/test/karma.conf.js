/* global require module */

'use strict';

const path = require('path');
const webpackConfig = require('../webpack.config.js');
const coverageDir = 'test/coverage';

const instrumentationConfig = {
    test: /\.js$/,
    exclude: [ path.resolve(__dirname, "../src/scripts/vendor"), path.resolve(__dirname, '') ],
    enforce: 'post',
    use: {
        loader: 'istanbul-instrumenter-loader',
        options: { esModules: true }
    }
};

webpackConfig.mode = 'development';
webpackConfig.module.rules.push(instrumentationConfig);

module.exports = function (config) {
    config.set({
        basePath: '../',
        frameworks: [
            'jasmine-jquery',
            'jasmine'
        ],
        reporters: [
            'dots',
            'coverage-istanbul'
        ],
        port: 9876,
        colors: true,
        logLevel: config.LOG_ERROR,
        autoWatch: true,
        browsers: ['ChromeHeadless'],
        singleRun: true,

        files: [
            'src/scripts/vendor/jquery.min.js',
            'test/helpers/jasmine-jquery.js',
            'test/fixtures/*.*',
            'test/specs/**/*_spec.js',
        ],

        preprocessors: {
            'src/scripts/govscot/**/*.js': ['babel', 'webpack'],
            'test/specs/**/*_spec.js': ['babel', 'webpack']
        },

        coverageIstanbulReporter: {
            reports: [ 'html', 'text-summary', 'lcov', 'json' ],
            dir: coverageDir,
            fixWebpackSourcePaths: true,
            'report-config': {
                html: { outdir: 'html' },
            }
        },

        webpack: webpackConfig
    });
};
