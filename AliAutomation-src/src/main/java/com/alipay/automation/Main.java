package com.alipay.automation;

import java.util.ArrayList;
import java.util.List;

import org.testng.IExecutionListener;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;
import org.uncommons.reportng.HTMLReporter;

import com.alipay.automation.utils.FileManager;
import com.alipay.automation.utils.LogUtils;

public class Main {

	public static void main(String[] args) {
		try {
			TestContext testContext = TestContext.getInstance();
			List<XmlSuite> testSuites = testContext.setup(args);

			List<Class> listeners = new ArrayList<Class>();
			listeners.add(HTMLReporter.class);

			TestNG testNG = new TestNG();
			testNG.addExecutionListener(new IExecutionListener() {

				@Override
				public void onExecutionStart() {
					WebDriverContext.getInstance().start();
				}

				@Override
				public void onExecutionFinish() {
					WebDriverContext.getInstance().stop();
				}
			});
			testNG.setListenerClasses(listeners);
			testNG.setXmlSuites(testSuites);
			testNG.setVerbose(8);
			testNG.setUseDefaultListeners(false);
			testNG.setOutputDirectory(FileManager.ENVIRONMENT_ROOT
					.getAbsolutePath());
			testNG.run();

			// driver.get("and-activity://com.alipay.android.app.demo.DemoActivity");
		} catch (Exception e) {
			LogUtils.log(e.getMessage());
			e.printStackTrace();
		}
	}
}
