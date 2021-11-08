const path = require('path');
const aliasPath = '../vendor/';

const commonEntry = {
    'default':                      './src/scripts/govscot/format.default.js',
    'global':                       './src/scripts/govscot/global.js',

    // format-specific entry points
    'complex-document':             './src/scripts/govscot/format.complex-document.js',
    'home':                         './src/scripts/govscot/format.home.js',
    'filtered-list-page':           './src/scripts/govscot/format.filtered-list-page.js',
    'search':                       './src/scripts/govscot/format.search.js',
    'publication':                  './src/scripts/govscot/format.publication.js',
    'aboutstats':                   './src/scripts/govscot/format.aboutstats.js',
    'covid-lookup':                 './src/scripts/govscot/format.covid-lookup.js'
};

const commonMode = 'development';

const commonExternals = {
    jquery: 'jQuery'
};

const commonResolve = {
    modules: [
        './src/main/resources/site/assets/scripts',
        'node_modules'
    ],

    extensions: ['.js'],

    // equivalent to requirejs paths
    alias: {
        'jquery': aliasPath + 'jquery.min'
    }
};


// Packs JS files & deps into bundles
module.exports = [{
    mode: commonMode,
    entry: commonEntry,
    externals: commonExternals,
    resolve: commonResolve,

    output: {
        path: path.resolve(__dirname, 'src/main/resources/site/assets/scripts'),
        filename: '[name].js'
    }
}, {
    mode: commonMode,
    entry: commonEntry,
    externals: commonExternals,
    resolve: commonResolve,

    output: {
        path: path.resolve(__dirname, 'src/main/resources/site/assets/scripts'),
        filename: '[name].es5.js'
    },

    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components)/,
                loader: 'babel-loader',
                query: {
                    presets: ['@babel/env'],
                    plugins: ['@babel/plugin-transform-classes']
                }
            }
        ]
    }
}];
