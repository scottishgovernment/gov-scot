// Karma configuration
// Generated on Fri May 04 2018 13:06:35 GMT+0100 (BST)

module.exports = function(config) {
  console.log('karma-conf')

  var coverageDir = 'test/coverage';
  
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine', 'requirejs'],


    // list of files / patterns to load in the browser
    files: [
      'test-main.js',
      'test/helpers/jasmine-jquery.js',
      {pattern: 'shared/*.js', included: false},
      {pattern: 'vendor/*.js', included: false},
      {pattern: 'govscot/*.js', included: false},
      {pattern: 'utils/*.js', included: false},
      {pattern: 'test/specs/*_spec.js', included: false},
      {pattern: 'test/fixtures/*.html', included: false}
    ],


    // list of files to exclude
    exclude: [
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
      'govscot/*.js': ['coverage'],
      'shared/*.js': ['coverage'],
      'utils/*.js': ['coverage']
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['dots', 'coverage'],


    // optionally, configure the reporter
    coverageReporter: {
      reporters: [{
          type: 'html',
          dir: coverageDir
      }, {
          type: 'json',
          dir: coverageDir
      }, {
          type: 'lcov',
          dir: coverageDir
      }, {
          type: 'cobertura',
          dir: coverageDir,
          file: 'cobertura.xml'
      }, {
          type: 'text-summary'
      }],
      subdir: function(browser) {
          // Map "PhantomJS 1.9.x (Linux)" to "phantomjs" so that output directory is stable across
          // PhantomJS versions and operating systems.
          return browser.replace(/ .*/, "").toLowerCase();
      }
    },


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_ERROR,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: [
        //'Chrome',
        'ChromeHeadless'
    ],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity
  });
};
