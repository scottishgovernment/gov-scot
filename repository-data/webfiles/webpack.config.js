const path = require('path');

const commonItems = {
    mode: 'development',

    entry: {
        'global':                       path.resolve(__dirname, './src/scripts/govscot/global.js'),
        'gtm':                          path.resolve(__dirname, './src/scripts/govscot/gtm.js'),
        'datalayer':                    path.resolve(__dirname, './src/scripts/govscot/datalayer.js'),

        // format-specific entry point)s
        'cookie-preferences':           path.resolve(__dirname, './src/scripts/govscot/format.cookie-preferences.js'),
        'default':                      path.resolve(__dirname, './src/scripts/govscot/format.default.js'),
        'filtered-list-page':           path.resolve(__dirname, './src/scripts/govscot/format.filtered-list-page.js'),
        'home':                         path.resolve(__dirname, './src/scripts/govscot/format.home.js'),
        'payment-form':                 path.resolve(__dirname, './src/scripts/govscot/format.payment-form.js'),
        'publication':                  path.resolve(__dirname, './src/scripts/govscot/format.publication.js'),
        'search':                       path.resolve(__dirname, './src/scripts/govscot/format.search.js'),
        'search-page':                  path.resolve(__dirname, './src/scripts/govscot/format.searchpage.js')
    },

    resolve: {
        modules: [
            './app/assets/scripts',
            'node_modules'
        ],

        extensions: ['.js']
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
                use: {
                    loader: 'babel-loader'
                }
            }
        ]
    },

    output: {
        path: path.resolve(__dirname, 'src/main/resources/site/assets/scripts'),
        filename: '[name].es5.js'
    }
}];
