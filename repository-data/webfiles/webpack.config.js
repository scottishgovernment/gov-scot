const path = require('path');
const aliasPath = '../vendor/';

const commonEntry = {
    'default':                      './src/scripts/govscot/format.default.js',
    'global':                       './src/scripts/govscot/global.js',

    // format-specific entry points
    'aboutstats':                   './src/scripts/govscot/format.aboutstats.js',
    'cookie-preferences':           './src/scripts/govscot/format.cookie-preferences.js',
    'covid-lookup':                 './src/scripts/govscot/format.covid-lookup.js',
    'filtered-list-page':           './src/scripts/govscot/format.filtered-list-page.js',
    'home':                         './src/scripts/govscot/format.home.js',
    'payment-form':                 './src/scripts/govscot/format.payment-form.js',
    'publication':                  './src/scripts/govscot/format.publication.js',
    'search':                       './src/scripts/govscot/format.search.js'
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
