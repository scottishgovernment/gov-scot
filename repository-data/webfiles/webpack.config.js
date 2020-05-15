const path = require('path');
const aliasPath = '../vendor/';

const commonEntry = {
    'default':                      './src/scripts/govscot/format.default.js',
    'global':                       './src/scripts/govscot/global.js',

    // format-specific entry points
    'collection':                   './src/scripts/govscot/format.collection.js',
    'complex-document':             './src/scripts/govscot/format.complex-document.js',
    'home':                         './src/scripts/govscot/format.home.js',
    'issue-hub':                    './src/scripts/govscot/format.issue-hub.js',
    'filtered-list-page':           './src/scripts/govscot/format.filtered-list-page.js',
    'topic':                        './src/scripts/govscot/format.topic.js',
    'publication':                  './src/scripts/govscot/format.publication.js',
    'policy':                       './src/scripts/govscot/format.policy.js',
    'aboutstats':                   './src/scripts/govscot/format.aboutstats.js'
    
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
        'jquery': aliasPath + 'jquery.min',
        'jquery.dotdotdot': aliasPath + 'jquery.dotdotdot.min',
        'moment': aliasPath + 'moment',
        'hammer': aliasPath + 'hammer.min',
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
                test: /(govscot|shared|utils)\/.*\.js$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: 'babel-loader',
                    query: {
                        presets: ['@babel/env']
                    }
                }
            }
        ]
    }
}];
