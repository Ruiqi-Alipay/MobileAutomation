package com.alipay.automation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.alipay.automation.modles.TestAction;
import com.alipay.automation.modles.TestCase;
import com.alipay.automation.modles.TestCaseInterface;
import com.alipay.automation.utils.ApkUtil;
import com.alipay.automation.utils.AutomationException;
import com.alipay.automation.utils.FileExtensionFilter;
import com.alipay.automation.utils.FileManager;
import com.alipay.automation.utils.FileUtils;
import com.alipay.automation.utils.LogUtils;

public class TestContext {
	private static TestContext sInstance;

	private Map<String, List<TestCase>> mTestCases;
	private List<TestAction> mDefaultRollbackActions;
	private String mTestAppPath;
	private String mTestAppPackage;

	public static TestContext getInstance() {
		if (sInstance == null) {
			sInstance = new TestContext();
		}

		return sInstance;
	}

	private TestContext() {

	}

	public List<XmlSuite> setup(String[] args) throws AutomationException {
		LogUtils.log("Setting up test case..");

		mDefaultRollbackActions = new ArrayList<TestAction>();
		mTestCases = new HashMap<String, List<TestCase>>();

		FileManager fileManager = FileManager.getInstance();
		fileManager.clearAllReports();
		if (!FileManager.REPORT_ROOT.exists()) {
			FileManager.REPORT_ROOT.mkdirs();
		}

		File rollbackFile = new File(FileManager.ROOT, "rollback_actions.json");
		if (rollbackFile.exists()) {
			String jsonText;
			try {
				jsonText = new String(FileUtils.readBytes(rollbackFile),
						"UTF-8");
				JSONObject scriptJson = new JSONObject(jsonText);
				mDefaultRollbackActions.addAll(TestAction
						.convertToActions(scriptJson
								.getJSONArray(TestCase.CASE_ROLLBACK_ACTIONS)));
			} catch (Exception e) {
				throw new AutomationException(
						"Setting up test case failed: unformated rollbackFile: "
								+ e.toString());
			}
		}

		int recursiveCount = 10;
		int recursiveCombine = 1;
		String buyerId = null;
		mDefaultRollbackActions = new ArrayList<TestAction>();
		String amount = "RANDOM";
		String couponAmount = "RANDOM";

		try {
			recursiveCount = Integer.valueOf(args[0]);
			recursiveCombine = Integer.valueOf(args[1]);
			buyerId = args[2];
			amount = args[3];
			couponAmount = args[4];
		} catch (Exception e) {
			throw new AutomationException(
					"Setting up test case failed: unformated parameters: "
							+ e.toString());
		}

		List<File> appFileList = new ArrayList<File>();
		FileUtils.collectFiles(FileManager.ROOT,
				new FileExtensionFilter(".apk"), appFileList, false);

		if (appFileList.size() != 1) {
			throw new AutomationException(
					"Setting up test case failed: please make sure test directory ("
							+ FileManager.ROOT
							+ ") has only one android and ios app file each");
		}

		mTestAppPath = appFileList.get(0).getAbsolutePath();
		mTestAppPackage = ApkUtil.getPackageName(mTestAppPath);

		List<XmlSuite> testSuites = new ArrayList<XmlSuite>();

		// 4. setup test case from test case script file
		List<File> testcaseList = new ArrayList<File>();
		FileUtils.collectFiles(FileManager.TEST_CASE_DIR,
				new FileExtensionFilter(".json"), testcaseList, true);

		try {
			for (File testcaseFile : testcaseList) {
				JSONObject scriptJson = new JSONObject(new String(
						FileUtils.readBytes(testcaseFile), "UTF-8"));

				String suiteName = testcaseFile.getName().substring(0,
						testcaseFile.getName().indexOf(".json"));

				XmlSuite suite = new XmlSuite();
				suite.setName(suiteName);
				testSuites.add(suite);
				mTestCases.put(suiteName, new ArrayList<TestCase>());

				String recursiveParam = null;
				if (scriptJson.has(TestCaseInterface.CASE_RECUR_NAME)) {
					recursiveParam = scriptJson.getString(TestCaseInterface.CASE_RECUR_NAME);
				}

				for (int i = 0; i < recursiveCount; i++) {
					TestCase testCase = TestCase.parseJSON(scriptJson,
							recursiveParam, buyerId, recursiveCombine, amount,
							couponAmount);

					XmlTest test = new XmlTest(suite);
					test.setName(suiteName + "﹣" + i);
					List<XmlClass> classes = new ArrayList<XmlClass>();
					classes.add(new XmlClass(TestCaseRunner.class));
					test.setXmlClasses(classes);
					test.addParameter("testcaseIndex", String.valueOf(i));
					test.addParameter("testcaseTotal",
							String.valueOf(recursiveCount));
					test.addParameter("testsuitCategory", suiteName);

					mTestCases.get(suiteName).add(testCase);
				}
			}
		} catch (Exception e) {
			throw new AutomationException(
					"Setting up test case failed: unformated testcase: "
							+ e.toString());
		}

		if (testSuites.isEmpty()) {
			throw new AutomationException(
					"None test case has been found! test finished!");
		}

		return testSuites;
	}

	public List<TestAction> getDefaultRollbackActions() {
		return mDefaultRollbackActions;
	}

	public TestCase getTestCase(String suitName, int index) {
		return mTestCases.get(suitName).get(index);
	}

	public String getAppPath() {
		return mTestAppPath;
	}

	public String getPackageName() {
		return mTestAppPackage;
	}

}
