const path = require('path');
const aliasPath = '../vendor/';

// Packs JS files & deps into bundles
module.exports = {
    mode: 'development',

    entry: {
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
        'policy':                       './src/scripts/govscot/format.policy.js'
    },

    externals: {
        jquery: 'jQuery'
    },

    output: {
        path: path.resolve(__dirname, 'src/main/resources/site/assets/scripts'),
        filename: '[name].js'
    },

    resolve: {
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
    },

    module: {
        rules: []
    }

};
