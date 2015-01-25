package com.alipay.automation;

import java.io.File;

import org.testng.Reporter;

import com.alipay.automation.utils.StringUtil;

public class TestReporter {

	/**
	 * Record test single action
	 * 
	 * @param message
	 *            readable action message
	 * @param capture
	 *            action screen shot, can be null
	 * @param success
	 *            success action or not
	 */
	public static void recordAction(String message, File capture,
			boolean success) {
		Reporter.setEscapeHtml(false);
		Reporter.log(message);
		if (capture != null) {
			String relativePath = capture.getAbsolutePath().substring(
					capture.getAbsolutePath().indexOf("html\\") + 5);
			Reporter.log("<a data-lightbox='"
					+ StringUtil.MD5(capture.getAbsolutePath()) + "' href =\"./"
					+ relativePath + "\">页面截图</a><br/>");
		}
	}

	/**
	 * 在testng中记录链接信息
	 * 
	 * @param linkName
	 *            链接名字
	 * @param linkPath
	 *            链接地址
	 */
	public static void recordLink(String linkName, String linkPath) {
		Reporter.setEscapeHtml(false);
		Reporter.log("<a href =\"" + linkPath + "\">" + linkName + "</a><br/>");
	}

	/**
	 * 记录日志
	 * 
	 * @param message
	 *            日志内容
	 */
	public static void recordLogLine(String message) {
		Reporter.log(message + "<br/>");
	}
}
