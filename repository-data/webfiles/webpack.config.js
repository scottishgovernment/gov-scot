const path = require('path');
const aliasPath = '../vendor/';

const commonItems = {
    mode: 'development',

    entry: {
        'default':                      './src/scripts/govscot/format.default.js',
        'global':                       './src/scripts/govscot/global.js',

        // format-specific entry points
        'aboutstats':                   './src/scripts/govscot/format.aboutstats.js',
        'cookie-preferences':           './src/scripts/govscot/format.cookie-preferences.js',
        'filtered-list-page':           './src/scripts/govscot/format.filtered-list-page.js',
        'home':                         './src/scripts/govscot/format.home.js',
        'payment-form':                 './src/scripts/govscot/format.payment-form.js',
        'publication':                  './src/scripts/govscot/format.publication.js',
        'search':                       './src/scripts/govscot/format.search.js'
    },

    externals: {
        jquery: 'jQuery'
    },

    resolve: {
        modules: [
            './src/main/resources/site/assets/scripts',
            'node_modules'
        ],

        extensions: ['.js'],

        // equivalent to requirejs paths
        alias: {
            'jquery': aliasPath + 'jquery.min'
        }
    },

    module: {
        rules: []
    }
};


// Packs JS files & deps into bundles
module.exports = [{
    mode: commonItems.mode,
    entry: commonItems.entry,
    externals: commonItems.externals,
    resolve: commonItems.resolve,

    output: {
        path: path.resolve(__dirname, 'src/main/resources/site/assets/scripts'),
        filename: '[name].js'
    }
}, {
    mode: commonItems.mode,
    entry: commonItems.entry,
    externals: commonItems.externals,
    resolve: commonItems.resolve,

    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /(node_modules)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env']
                        // plugins: [
                        //     '@babel/plugin-transform-classes',
                        //     // '@babel/plugin-transform-runtime',
                        // ]
                    }
                }
            }
        ]
    },

    output: {
        path: path.resolve(__dirname, 'src/main/resources/site/assets/scripts'),
        filename: '[name].es5.js'
    }
}];
