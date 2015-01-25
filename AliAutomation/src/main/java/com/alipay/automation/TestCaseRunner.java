package com.alipay.automation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.alipay.automation.modles.TestAction;
import com.alipay.automation.modles.TestCase;
import com.alipay.automation.modles.TestVerify;
import com.alipay.automation.utils.FileManager;
import com.alipay.automation.utils.FileNameUtil;
import com.alipay.automation.utils.LogUtils;

/**
 * Order test script runner
 * 
 * @author ruiqi.li
 */
public class TestCaseRunner {

	private static final int ACTION_RETRY = 3;

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite(ITestContext context) throws Exception {

	}

	@Test
	@Parameters({ "testcaseIndex", "testcaseTotal", "testsuitCategory" })
	public void test(String testcaseIndex, String testcaseTotal,
			String suiteName) throws InterruptedException {
		if (!WebDriverContext.getInstance().isRunning()) {
			throw new InterruptedException("Selenium not running, finish test!");
		}

		int caseIndex = Integer.valueOf(testcaseIndex);
		LogUtils.log("====================================================================");
		LogUtils.log(">>>>>>>>>>>>>>>>>>>>>>>>  " + suiteName + "  "
				+ (caseIndex + 1) + "/" + testcaseTotal
				+ "  <<<<<<<<<<<<<<<<<<<<<<<<");

		TestCase testCase = TestContext.getInstance().getTestCase(suiteName,
				caseIndex);
		if (testCase.getRecursiveParam() != null) {
			LogUtils.log("Creating trade ID..");
			// testCase.prepareRecursiveData();
			LogUtils.log("Creat trade ID finished");
		}

		boolean result = true;

		TestReporter.recordLogLine("类型:" + testCase.getCategory());
		TestReporter.recordLogLine("描述:" + testCase.getName());

		WebDriverContext webDriverContext = WebDriverContext.getInstance();

		String message = "启动:" + TestContext.getInstance().getAppPath();
		File captureImage = webDriverContext.takeCapture(
				FileManager.REPORT_ROOT, "启动界面.png");
		TestReporter.recordAction(message, captureImage, true);

		if (caseIndex == 0) {
			runActions(testCase.getSuitActions(), testCase, caseIndex,
					FileManager.REPORT_ROOT, webDriverContext);
		}

		runActions(testCase.getActions(), testCase, caseIndex,
				FileManager.REPORT_ROOT, webDriverContext);

		List<TestVerify> results = testCase.getVerifies();
		for (TestVerify verify : results) {
			result = verify(verify, FileManager.REPORT_ROOT, webDriverContext,
					true, WebDriverContext.DEFAULT_WAIT_ELEMENT_SECOND);
			if (!result) {
				break;
			}
		}

		if (!result) {
			Assert.fail("判断结果时出现错误");
		}
	}

	private void runActions(List<TestAction> actions, TestCase testCase,
			int caseIndex, File captureDir, WebDriverContext driverContext) {
		if (actions == null) {
			return;
		}

		TestAction lastAction = null;
		Monitor monitor = Monitor.getInstance();

		int i = 0;
		for (TestAction action : actions) {
			String originalAction = action.getOriginalCommand();
			try {
				// monitor.recordActionStart(caseIndex, originalAction);
				dot();

				for (int retry = 1; retry <= ACTION_RETRY; retry++) {
					try {
						configActionParameters(action, testCase);
						driverContext.performAction(action,
								WebDriverContext.DEFAULT_WAIT_ELEMENT_SECOND);
						File captureImage = driverContext.takeCapture(
								captureDir,
								(++i)
										+ FileNameUtil
												.convertStringToFileName(action
														.getOriginalCommand())
										+ ".png");
						TestReporter.recordAction(
								getActionRecordText(
										action.getOriginalCommand(), true),
								captureImage, true);
						lastAction = action;
						break;
					} catch (NoSuchElementException noneElement) {
						LogUtils.log("Element not found: "
								+ noneElement.getMessage());
						if (lastAction != null) {
							try {
								LogUtils.log("Retry last action: "
										+ lastAction.getOriginalCommand());
								driverContext.performAction(lastAction, 1);
							} catch (Exception e) {
								LogUtils.log("Error: Retry last action failed!");
								throw noneElement;
							}
						} else {
							throw noneElement;
						}
					} catch (WebDriverException webException) {
						LogUtils.log("WebDriver Error: "
								+ webException.getMessage());
						if (retry == ACTION_RETRY) {
							throw webException;
						} else {
							LogUtils.log("WebDriver Error: retry " + retry);
							dot();
							continue;
						}
					}
				}
			} catch (Exception e) {
				LogUtils.log(e.toString());
				File captureImage = driverContext.takeCapture(
						captureDir,
						FileNameUtil.convertStringToFileName(action
								.getOriginalCommand()) + ".png");
				String failMessage = getActionRecordText(
						action.getOriginalCommand(), false);
				TestReporter.recordAction(failMessage, captureImage, false);
				Assert.fail(failMessage, e);
			} finally {
				// monitor.recordActionEnd(caseIndex, originalAction);
			}
		}
	}

	@AfterMethod
	public void afterMethod(ITestResult result) throws Exception {
		Reporter.setCurrentTestResult(result);
	}

	@AfterTest
	public void afterTest(ITestContext context) throws Exception {
		String indexText = context.getCurrentXmlTest().getParameter(
				"testcaseIndex");
		String testSuite = context.getCurrentXmlTest().getParameter(
				"testsuitCategory");
		int caseIndex = Integer.parseInt(indexText);
		TestCase testCase = TestContext.getInstance().getTestCase(testSuite,
				caseIndex);
		backToHomePage(caseIndex, testCase);
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {

	}

	private void backToHomePage(int caseIndex, TestCase testCase) {
		List<TestAction> rollbackActions = new ArrayList<TestAction>();
		rollbackActions.addAll(testCase.getRollbackActions());
		rollbackActions.addAll(TestContext.getInstance()
				.getDefaultRollbackActions());
		List<TestVerify> startingPageVerifies = testCase.getStartPageVerifies();
		WebDriverContext driverContext = WebDriverContext.getInstance();

		for (int backToHomePages = 1; backToHomePages <= 2; backToHomePages++) {
			for (TestAction action : rollbackActions) {
				try {
					dot();
					driverContext.performAction(action,
							WebDriverContext.DEFAULT_ROLLBACK_WAIT_SECOND);
				} catch (Exception e) {
					continue;
				}

				boolean result = verify(startingPageVerifies, null,
						driverContext, false,
						WebDriverContext.DEFAULT_ROLLBACK_WAIT_SECOND);
				if (result) {
					return;
				}
			}
		}
	}

	private boolean verify(List<TestVerify> verifies, File captureDir,
			WebDriverContext driverContext, boolean takeRecord, int waitSecond) {
		for (TestVerify verify : verifies) {
			boolean result = verify(verify, captureDir, driverContext,
					takeRecord, waitSecond);
			if (!result) {
				return false;
			}
		}

		return true;
	}

	private boolean verify(TestVerify verify, File captureDir,
			WebDriverContext driverContext, boolean takeRecord, int waitSecond) {
		String verifyFile = FileNameUtil.convertStringToFileName(verify
				.getOriginalCommand()) + ".png";
		File captureImage = null;
		if (takeRecord) {
			captureImage = driverContext.takeCapture(captureDir, verifyFile);
		}

		if (!verify.execute(waitSecond)) {
			if (takeRecord) {
				String failMessage = getVerifyRecordText(
						verify.getOriginalCommand(), false);
				TestReporter.recordAction(failMessage, captureImage, false);
				Assert.fail(failMessage);
			}
			return false;
		} else {
			if (takeRecord) {
				TestReporter.recordAction(
						getVerifyRecordText(verify.getOriginalCommand(), true),
						captureImage, true);
			}
			return true;
		}
	}

	private void configActionParameters(TestAction action, TestCase testcase) {
		String paramPlaceHold = action.getParams();
		String paramValue = testcase.getParameter(paramPlaceHold);
		if (paramValue != null) {
			action.setParams(paramValue);
		}
	}

	private String getVerifyRecordText(String message, boolean success) {
		return "判断结果 '" + message + (success ? "' 正确\n" : "' 时出现错误\n");
	}

	private String getActionRecordText(String message, boolean success) {
		return "执行 '" + message + (success ? "' 成功" : "' 时出现错误\n");
	}

	private void dot() throws InterruptedException {
		Thread.sleep(300);
	}

}
