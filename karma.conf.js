// https://karma-runner.github.io/latest/intro/configuration.html

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
    // http://karma-runner.github.io/6.4/config/plugins.html
    plugins: [
      'karma-cljs-test',
      'karma-chrome-launcher',
      'karma-verbose-reporter'
    ]
  })

  console.log('=== Karma config ===', config)
}
