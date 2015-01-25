package com.alipay.automation.utils;

import java.io.File;

/**
 * Persistent file manager, manage include final report file and interim file.
 * 
 * @author ruiqi.li
 */
public class FileManager {

	private static FileManager sInstance;

	// Context root
	// public final static File ROOT = new File(System.getProperty("user.dir"));
	public final static File ROOT = new File("/Users/ruiqili/Documents/interautotest_installware");

	// Test case script file
	public final static File TEST_CASE_DIR = new File(ROOT, "/test_cases");

	// Test verify image file
	public final static File TEST_VERIFY_IMG_DIR = new File(ROOT,
			"/verify_image");

	// Report root directory
	public final static File ENVIRONMENT_ROOT = new File(ROOT, "environment");
	public final static File REPORT_ROOT = new File(ROOT, "environment/html");

	// External resources
	public final static File EXTERNAL_RES_DIR = new File(ROOT, "/lib");

	public static FileManager getInstance() {
		if (sInstance == null) {
			sInstance = new FileManager();
		}

		return sInstance;
	}

	private FileManager() {

	}

	public void createReportDirectoryIfNeeded() throws Exception {
		if (!REPORT_ROOT.exists()) {
			REPORT_ROOT.mkdirs();
		}

		FileUtils.copyTree(new File(EXTERNAL_RES_DIR, "lightbox"), REPORT_ROOT);
	}

	public File getVerityImageFile(String verifyFileName) {
		return new File(TEST_VERIFY_IMG_DIR, verifyFileName);
	}

	public void clearAllReports() {
		emptyDirectory(REPORT_ROOT);
	}

	public void deleteTree(File dir) {
		emptyDirectory(dir);
		dir.delete();
	}

	public void emptyDirectory(File dir) {
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		int len = files.length;
		for (int i = 0; i < len; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				deleteTree(file);
			} else {
				file.delete();
			}
		}
	}

}
