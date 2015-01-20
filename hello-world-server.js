var webdriver = require('selenium-webdriver');
var SeleniumServer = require('selenium-webdriver/remote').SeleniumServer;
var assert = require('assert');

//change the next two vars to match your filesystem
var selendroidPath = 'selendroid-standalone.jar'; //symbolic linked to selendroid-standalone-0.12.0-with-dependencies.jar
var appPath = 'selendroid-test-app-0.10.0.apk';
var caps = {
    browserName: 'android',
    aut: 'io.selendroid.testapp:0.12.0'
};

function getServer() {
    var server = new SeleniumServer(selendroidPath, {
        port: 4444,
        stdio: 'inherit', //remove this if you don't want to see the selendroid process stdout
        args: ['-app', 'selendroid-test-app-0.10.0.apk']
    });
    server.start();
    return server.address();
};

var driver = new webdriver.Builder().
        withCapabilities(caps).
        usingServer(getServer()).
        build();

driver.get('and-activity://io.selendroid.testapp.HomeScreenActivity');
driver.getCurrentUrl().then(function (currentUrl) {
    assert.equal(currentUrl, 'and-activity://HomeScreenActivity')
});
driver.findElement(webdriver.By.id('my_text_field')).then(function (myTextField) {
    myTextField.sendKeys('Hello Selendroid');
    return myTextField.getText();
}).then(function (text) {
	assert.equal(text, 'Hello Selendroid');
});

driver.quit();