var webdriver = require('selenium-webdriver');
var SeleniumServer = require('selenium-webdriver/remote').SeleniumServer;
var assert = require('assert');

console.log(webdriver.Capabilities.android());

// change the next two vars to match your filesystem
var selendroidPath = '/Users/ruiqili/MobileAutomation/selendroid-standalone.jar'; // symbolic
// linked
// to
// selendroid-standalone-0.12.0-with-dependencies.jar
var appPath = '/Users/ruiqili/MobileAutomation/selendroid-test.apk';
var caps = {
    browserName : 'android',
    emulator : false,
    androidTarget : 'ANDROID21'
};

function getServer() {
    var server = new SeleniumServer(selendroidPath,
        {
            port : 4444,
            stdio : 'inherit', // remove this if you don't want to see the
            // selendroid process stdout
            args : [ '-app',
                '/Users/ruiqili/MobileAutomation/selendroid-test.apk' ]
        });
    server.start();
    return server.address();
};

var driver = new webdriver.Builder().withCapabilities(
    webdriver.Capabilities.android()).usingServer(getServer()).build();

driver.get('and-activity://io.selendroid.testapp.HomeScreenActivity');
driver.getCurrentUrl().then(function(currentUrl) {
    assert.equal(currentUrl, 'and-activity://HomeScreenActivity')
});
driver.findElement(webdriver.By.id('my_text_field')).then(
    function(myTextField) {
        myTextField.sendKeys('Hello Selendroid');
        return myTextField.getText();
    }).then(function(text) {
        assert.equal(text, 'Hello Selendroid');
    });

driver.quit();

var assert = require('assert');
suite('Array', function() {
    setup(function() {
        console.log('dddd');
    });

    test('should return -1 when not present', function() {
        assert.equal(-1, [ 1, 2, 3 ].indexOf(4));
    });

    teardown(function() {
        console.log('gggg');
    });
});