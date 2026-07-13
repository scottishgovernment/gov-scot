const path = require('path');

const commonItems = {
    mode: 'development',

    entry: {
        'global':                       path.resolve(__dirname, './src/scripts/govscot/global.js'),
        'gtm':                          path.resolve(__dirname, './src/scripts/govscot/gtm.js'),
        'datalayer':                    path.resolve(__dirname, './src/scripts/govscot/datalayer.js'),
        'datalayer-search':             path.resolve(__dirname, './src/scripts/govscot/datalayer-search.js'),
        'js-enabled':                   path.resolve(__dirname, './src/scripts/govscot/js-enabled.js'),

        // format-specific entry point)s
        'cookie-preferences':           path.resolve(__dirname, './src/scripts/govscot/format.cookie-preferences.js'),
        'default':                      path.resolve(__dirname, './src/scripts/govscot/format.default.js'),
        'filtered-list-page':           path.resolve(__dirname, './src/scripts/govscot/format.filtered-list-page.js'),
        'home':                         path.resolve(__dirname, './src/scripts/govscot/format.home.js'),
        'payment-form':                 path.resolve(__dirname, './src/scripts/govscot/format.payment-form.js'),
        'publication':                  path.resolve(__dirname, './src/scripts/govscot/format.publication.js'),
        'search':                       path.resolve(__dirname, './src/scripts/govscot/format.search.js'),
        'search-page':                  path.resolve(__dirname, './src/scripts/govscot/format.searchpage.js'),
        // npf-indicator is output separately below (static webapp, not webfiles)
    },

    resolve: {
        modules: [
            './app/assets/scripts',
            'node_modules'
        ],

        extensions: ['.js', '.njk']
    },

    module: {
        rules: [
            {
                test: /\.njk/,
                loader: '@dryfeld/nunjucks-loader',
                options: {
                    config: 'src/scripts/nunjucks.config.js'
                }
            }
        ]
    }
};


const staticScriptsPath = path.resolve(__dirname, '../../site/webapp/src/main/webapp/assets/scripts');

// Packs JS files & deps into bundles
module.exports = [{
    mode: commonItems.mode,
    entry: commonItems.entry,
    externals: commonItems.externals,
    resolve: commonItems.resolve,
    module: commonItems.module,

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
            commonItems.module.rules[0],
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
}, {
    // npf-indicator: output to static webapp so it is served directly by Tomcat,
    // not via the webfiles JCR bundle
    mode: commonItems.mode,
    entry: { 'npf-indicator': path.resolve(__dirname, './src/scripts/govscot/format.npf-indicator.js') },
    resolve: commonItems.resolve,
    module: commonItems.module,
    output: { path: staticScriptsPath, filename: '[name].js' }
}, {
    mode: commonItems.mode,
    entry: { 'npf-indicator': path.resolve(__dirname, './src/scripts/govscot/format.npf-indicator.js') },
    resolve: commonItems.resolve,
    module: {
        rules: [
            commonItems.module.rules[0],
            { test: /\.js$/, use: { loader: 'babel-loader' } }
        ]
    },
    output: { path: staticScriptsPath, filename: '[name].es5.js' }
}];
