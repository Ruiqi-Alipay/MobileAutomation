package com.alipay.automation.utils;

import java.io.File;
import java.io.FileFilter;

public class FileExtensionFilter implements FileFilter {

	private String mExtension;

	public FileExtensionFilter(String extension) {
		mExtension = extension;
	}

	public boolean accept(File pathname) {
		if (pathname.getName().endsWith(mExtension)) {
			return true;
		}
		return false;
	}

}