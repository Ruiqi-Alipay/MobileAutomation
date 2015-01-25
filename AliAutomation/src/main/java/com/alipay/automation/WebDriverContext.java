package com.alipay.automation;

import io.selendroid.client.SelendroidDriver;
import io.selendroid.common.SelendroidCapabilities;
import io.selendroid.standalone.SelendroidConfiguration;
import io.selendroid.standalone.SelendroidLauncher;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.AuthenticationException;

import net.coobird.thumbnailator.Thumbnails;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.alipay.automation.modles.TestAction;
import com.alipay.automation.modles.TestActionTypes;
import com.alipay.automation.modles.TestTarget;
import com.alipay.automation.utils.AliKeyboardManager;
import com.alipay.automation.utils.FileManager;
import com.alipay.automation.utils.HTTPUtils;
import com.alipay.automation.utils.LogUtils;
import com.alipay.automation.utils.TerminalExecutor;
import com.alipay.automation.utils.TextUtils;

public class WebDriverContext {
	public static final int DEFAULT_WAIT_ELEMENT_SECOND = 20;
	public static final int DEFAULT_ROLLBACK_WAIT_SECOND = 3;

	private static final int WEB_DRIVER_RETRY_COUNT = 3;

	private static WebDriverContext sInstance;

	private SelendroidDriver mDriver;
	private SelendroidLauncher mSelenServer;

	public static WebDriverContext getInstance() {
		if (sInstance == null) {
			sInstance = new WebDriverContext();
		}

		return sInstance;
	}

	private WebDriverContext() {

	}

	public void start() {
		LogUtils.log("Starting test evnernment, this may take a few seconds..");
		String appPath = TestContext.getInstance().getAppPath();
		String appPackage = TestContext.getInstance().getPackageName();
		try {
			String previousServer = TerminalExecutor.runCommand(
					"lsof -i :4444", null);
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(previousServer);
			if (m.find()) {
				String pid = m.group(1);
				TerminalExecutor.runCommand("kill " + pid, null);
			}
			
			// TerminalExecutor.runCommand("export ANDROID_HOME=/Users/ruiqili/android-sdks", null);
			String ff = TerminalExecutor.runCommand("echo $ANDROID_HOME$", null);

			for (int i = 0; i < 20; i++) {
				SelendroidConfiguration configuration = new SelendroidConfiguration();
				configuration.setForceReinstall(true);
				configuration.setShouldKeepAdbAlive(true);
				configuration.setServerStartRetries(5);
				configuration.addSupportedApp(appPath);
				mSelenServer = new SelendroidLauncher(configuration);
				mSelenServer.launchSelendroid();
				
				JSONArray test = mSelenServer.getServer().getDriver().getSupportedApps();
				
				String adb = "/Users/ruiqili/android-sdks/platform-tools//adb";

				int insertIndex = appPath.lastIndexOf("/");
				String resignAppPath = appPath.substring(0, insertIndex + 1) + "resigned-"
						+ appPath.substring(insertIndex + 1);
				findDeveices(adb, 10);
				TerminalExecutor.runCommand(adb + " uninstall " + appPackage,
						"waiting for device");
				findDeveices(adb, 10);
				TerminalExecutor.runCommand(adb + " install " + resignAppPath,
						"waiting for device");

				TerminalExecutor.runCommand(" rm " + resignAppPath, null);
				mSelenServer.stopSelendroid();
			}

			JSONObject status = HTTPUtils.simpleGet(
					"http://localhost:4444/wd/hub/status").getJSONObject(
					"value");
			LogUtils.log("Server started:");
			LogUtils.log("Running on os: "
					+ status.getJSONObject("os").getString("name") + " / "
					+ status.getJSONObject("os").getString("version"));
			LogUtils.log("Server build: "
					+ status.getJSONObject("build").getString("browserName")
					+ " / "
					+ status.getJSONObject("build").getString("version"));

			JSONArray testApps = status.getJSONArray("supportedApps");
			JSONObject targetApp = null;
			for (int i = 0; i < testApps.length(); i++) {
				JSONObject app = testApps.getJSONObject(i);
				if (appPackage.equals(app.getString("basePackage"))) {
					targetApp = app;
					break;
				}
			}

			SelendroidCapabilities cap = new SelendroidCapabilities(
					targetApp.getString("appId"));
			mDriver = new SelendroidDriver(cap);
		} catch (Exception e) {
			LogUtils.log("Start selenium server failed: " + e.toString());
		} finally {
			try {
				TerminalExecutor.runCommand(" rm " + appPath, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		LogUtils.log("Sutting down test environment..");
		if (mDriver != null) {
			mDriver.quit();
			mDriver = null;
		}
		if (mSelenServer != null) {
			mSelenServer.stopSelendroid();
			mSelenServer = null;
		}
	}

	public boolean isRunning() {
		return mSelenServer != null && mDriver != null;
	}

	public File takeCapture(File dir, String fileName) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String dateTime = formatter.format(new Date());
		File file = new File(dir, dateTime + "_" + fileName);
		File screenshot = mDriver.getScreenshotAs(OutputType.FILE);
		try {
			Thumbnails.of(screenshot).size(320, 480).outputQuality(0.5)
					.toFile(file);
		} catch (IOException e) {
			screenshot.renameTo(file);
		}

		return file;
	}

	public String executeAdb(String command) {
		return mDriver.getAdbConnection().executeShellCommand(command);
	}

	public File takeTempCapture() {
		return mDriver.getScreenshotAs(OutputType.FILE);
	}

	public void performAction(TestAction action, int waitSecond)
			throws NoSuchElementException {
		LogUtils.log("Perform Action: " + action.getOriginalCommand());
		TestTarget target = action.getTestTarget();
		String actionType = action.getType();
		String actionParams = action.getParams();

		AliKeyboardManager.getInstance(mDriver);

		if (TestActionTypes.ACTION_TYPE_CLICK.equals(actionType)
				|| TestActionTypes.ACTION_TYPE_CLICK_LOCATION
						.equals(actionType)
				|| TestActionTypes.ACTION_TYPE_CLICK_IMAGE.equals(actionType)) {
			WebElement element = findElement(target, waitSecond);
			try {
				element.click();
			} catch (StaleElementReferenceException e) {
				AliKeyboardManager.getInstance(mDriver).mockDialogOk(mDriver);
			}
		} else if (TestActionTypes.ACTION_TYPE_CHECK.equals(actionType)
				|| TestActionTypes.ACTION_TYPE_CHECKBOX_LOCATION
						.equals(actionType)
				|| TestActionTypes.ACTION_TYPE_RADIO_LOCATION
						.equals(actionType)) {
			WebElement element = findElement(target, waitSecond);
			element.click();
		} else if (TestActionTypes.ACTION_TYPE_INPUT.equals(actionType)) {
			WebElement element = findElement(target, waitSecond);
			input(element, actionParams);
		} else if (TestActionTypes.ACTION_TYPE_ALIKEYBORAD.equals(actionType)
				|| TestActionTypes.ACTION_TYPE_ALIKEYBORAD_NUM
						.equals(actionType)) {
			if (TestActionTypes.ACTION_TYPE_ALIKEYBORAD.equals(actionType)) {
				findElement(target, waitSecond);
			}

			AliKeyboardManager keyboardManager = AliKeyboardManager
					.getInstance(mDriver);
			String[] passwordArray = actionParams.split("");
			for (int i = 0; i < passwordArray.length; i++) {
				String oneByOne = passwordArray[i];
				if (!TextUtils.isEmpty(oneByOne)) {
					if (TestActionTypes.ACTION_TYPE_ALIKEYBORAD
							.equals(actionType)) {
						keyboardManager.keyIn(oneByOne, mDriver);
					} else {
						keyboardManager.passwordIn(oneByOne, mDriver);
						sleepInMillionsecond(1000);
					}
				}
			}
			if (TestActionTypes.ACTION_TYPE_ALIKEYBORAD.equals(actionType)) {
				keyboardManager.resetKeyboard(mDriver);
			} else {
				keyboardManager.passwordIn("ok", mDriver);
			}
		} else if (TestActionTypes.ACTION_TYPE_CLEAR.equals(actionType)) {
			WebElement element = findElement(target, waitSecond);
			element.clear();
		} else if (TestActionTypes.ACTION_TYPE_CLEAR_ALL.equals(actionType)) {
			try {
				findElement(target, waitSecond);
				List<WebElement> elements = mDriver
						.findElementsByClassName("android.widget.EditText");

				for (int i = 0; i < WEB_DRIVER_RETRY_COUNT; i++) {
					if (elements != null && !elements.isEmpty()) {
						boolean clearSuccess = true;
						for (WebElement element : elements) {
							element.clear();

							if (element.getText() != null
									&& element.getText().length() > 10) {
								clearSuccess = false;
							}
						}

						if (clearSuccess) {
							break;
						}
					} else {
						break;
					}
				}
			} catch (Exception e) {
				LogUtils.log(e.getMessage());
			}
		} else if (TestActionTypes.ACTION_TYPE_KEYEVENT.equals(actionType)) {
			// mDriver.getKeyboard().sendKeyEvent(StringUtil.strToInteger(actionParams,
			// 0));
		} else if (TestActionTypes.ACTION_TYPE_SCROLLDOWN.equals(actionType)) {
			mDriver.roll(0, 100);
		} else if (TestActionTypes.ACTION_TYPE_QUICK_CHECK.equals(actionType)) {
			String startCharacter = actionParams.substring(0, 1);
			WebElement quickIndex = findElement(new TestTarget(
					TestTarget.TARGET_TYPE_NAME, startCharacter),
					DEFAULT_ROLLBACK_WAIT_SECOND);
			quickIndex.click();
			int findRetry = 10;
			while (findRetry > 0) {
				findRetry--;
				try {
					WebElement element = findElement(target, waitSecond);
					element.click();
					break;
				} catch (Exception e) {
					mDriver.roll(0, 100);
					sleepInMillionsecond(100);
				}
			}
		} else {
			// not support
		}
	}

	public boolean hasElement(TestTarget widget, int waitSecond) {
		return findElement(widget, waitSecond) != null;
	}

	private WebElement findElement(TestTarget widget, int waitSecond) {
		String locatorType = widget.getType();
		String locatorName = widget.getElement();

		for (int i = 0; i < waitSecond; i += 2) {
			try {
				if (TestTarget.TARGET_TYPE_NAME.equals(locatorType)) {
					int diliverPos = locatorName.indexOf("||");
					if (diliverPos > 0 && diliverPos < locatorName.length()) {
						String[] items = locatorName.split("\\|\\|");
						for (int index = 0; index < items.length; index++) {
							try {
								WebElement element = mDriver
										.findElementByName(items[index].trim());
								if (element != null) {
									return element;
								}
							} catch (NoSuchElementException e) {

							}
						}
						LogUtils.log("Finding element: " + locatorName);
					} else {
						WebElement element = mDriver
								.findElementByName(locatorName);
						if (element != null) {
							return element;
						}
					}
				} else if (TestTarget.TARGET_TYPE_LOCATION.equals(locatorType)) {
					int dividerIndex = locatorName.indexOf("[");
					String className = locatorName.substring(0, dividerIndex);
					int index = Integer.valueOf(locatorName.substring(
							dividerIndex + 1, locatorName.length() - 1));
					List<WebElement> elements = mDriver
							.findElementsByClassName(className);
					if (elements != null && elements.size() >= index) {
						return elements.get(index - 1);
					}
				}
			} catch (NoSuchElementException e) {
				LogUtils.log("Finding element: " + locatorName);
			}

			sleepInMillionsecond(2000);
		}

		throw new NoSuchElementException("Type: " + locatorType + "; Name: "
				+ locatorName);
	}

	public void input(WebElement element, String text) {
		element.click();
		sleepInMillionsecond(200);

		for (int i = 0; i < WEB_DRIVER_RETRY_COUNT; i++) {
			String elementText = element.getText();
			if (TextUtils.isEmpty(elementText)
					|| !text.replaceAll(" ", "").equals(
							elementText.replaceAll(" ", ""))) {
				mDriver.getAdbConnection().executeShellCommand(
						"input text " + text);
				sleepInMillionsecond(200);
			} else {
				return;
			}
		}
	}

	private void findDeveices(String adb, int retry)
			throws AuthenticationException {
		for (int i = 0; i < retry; i++) {
			try {
				String devices = TerminalExecutor.runCommand(adb + " devices",
						"waiting for device");
				if (devices.indexOf("device",
						devices.indexOf("List of devices attached")) > 0) {
					return;
				}
				TerminalExecutor.runCommand(adb + " kill-server", "watting");
				sleepInMillionsecond(500);
				TerminalExecutor.runCommand(adb + " start-server", "watting");
				sleepInMillionsecond(500);
				
				LogUtils.log("restarting adb " + i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		throw new AuthenticationException("adb devices not found!");
	}

	private void sleepInMillionsecond(int second) {
		try {
			Thread.sleep(second);
		} catch (InterruptedException e) {
		}
	}
}
