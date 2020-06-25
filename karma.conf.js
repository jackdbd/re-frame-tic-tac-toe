module.exports = function (config) {
  config.set({
    basePath: 'target',
    browsers: ['ChromeHeadless'],
    client: {
      args: ['shadow.test.karma.init'],
      singleRun: true
    },
    colors: true,
    files: ['karma-test.js'],
    frameworks: ['cljs-test'],
    logLevel: config.LOG_INFO,
    plugins: [
        'karma-cljs-test',
        'karma-chrome-launcher',
        'karma-verbose-reporter'
    ],
  })
}
