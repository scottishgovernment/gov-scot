const webpack = require('webpack');
const path = require('path');
const aliasPath = '../../../../../site/src/main/webapp/assets/scripts/vendor/';

// Packs JS files & deps into bundles
module.exports = {
    devtool: 'source-map',

    entry: {
        'default':                      './src/scripts/govscot/format.default.js',
        'global':                       './src/scripts/govscot/global.js',

        // format-specific entry points
        'aps-publication':              './src/scripts/govscot/format.aps-publication.js',
        'atoz':                         './src/scripts/govscot/format.atoz.js',
        'default':                      './src/scripts/govscot/format.default.js',
        'home':                         './src/scripts/govscot/format.home.js',
        'issue-hub':                    './src/scripts/govscot/format.issue-hub.js',
        'non-aps-publication':          './src/scripts/govscot/format.non-aps-publication.js',
        'policy-landing-page':          './src/scripts/govscot/format.policy-landing-page.js',
        'policy':                       './src/scripts/govscot/format.policy.js',
        'policy-latest':                './src/scripts/govscot/format.policy-latest.js',
        'press-release-landing-page':   './src/scripts/govscot/format.press-release-landing-page.js',
        'publication-landing':          './src/scripts/govscot/format.publication-landing.js',
        'search':                       './src/scripts/govscot/format.search.js',
        'topic':                        './src/scripts/govscot/format.topic.js'
    },

    resolve: {
        modules: [
            './src/main/resources/site/assets/scripts',
            'node_modules'
        ],

        extensions: ['', '.js'],

        // equivalent to requirejs paths
        alias: {
            'jquery': aliasPath + 'jquery.min',
            'jquery.dotdotdot': aliasPath + 'jquery.dotdotdot.min',
            'moment': aliasPath + 'moment',
            'hammer': aliasPath + 'hammer',
        }
    },

    resolveLoader: {
        alias: {

        }
    },

    output: {
        path: path.resolve(__dirname, 'src/main/resources/site/assets/scripts'),
        filename: '[name].js'
    },

    module: {
        loaders: [
            {
                test: /(govscot|shared|utils)\/.*\.js$/,
                exclude: /(node_modules|bower_components)/,
                loader: 'babel',
                query: {
                    presets: ['es2015']
                }
            }
        ]
    },


    development: {
        plugins: [
            new webpack.DefinePlugin({
                DEBUG: true,
                PRODUCTION: false
            })
        ]
    },

    production: {
        plugins: [
            new webpack.DefinePlugin({
                DEBUG: false,
                PRODUCTION: true
            }),
            new webpack.optimize.DedupePlugin(),
            new webpack.optimize.UglifyJsPlugin()
        ]
    }

};