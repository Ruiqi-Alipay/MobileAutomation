/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.automation.utils;

import java.util.ArrayList;

/**
 * 
 * @author qiyi.wxc
 * @version $Id: Strings.java, v 0.1 2014年9月16日 下午8:46:40 qiyi.wxc Exp $
 */
public class Strings {
	public ArrayList<String> strings = null;

	public Strings(ArrayList<String> strings) {
		this.strings = strings;
	}

	public Strings grep(String str) {
		ArrayList<String> ret = new ArrayList<String>();
		for (String line : strings) {
			if (line.contains(str)) {
				ret.add(line);
			}
		}
		return new Strings(ret);
	}

	public Strings getRow(String regularExpression, int rowNumber)
			throws ArrayIndexOutOfBoundsException {
		ArrayList<String> ret = new ArrayList<String>();
		for (String line : strings) {
			if (line.equals("")) {
				continue;
			}
			String[] rows = line.trim().split(regularExpression);
			if (rows.length < rowNumber) {
				throw new ArrayIndexOutOfBoundsException(String.format(
						"rows.length(%s) < rowNumber(%s) line:%s", rows.length,
						rowNumber, line));
			}
			ret.add(rows[rowNumber - 1]);
		}
		return new Strings(ret);
	}

	public String[] getRows(String regularExpression)
			throws ArrayIndexOutOfBoundsException {
		for (String line : strings) {
			if (line.equals("")) {
				continue;
			}
			return line.trim().split(regularExpression);
		}

		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(1024);
		for (String line : strings) {
			sb.append(line).append('\n');
		}
		return sb.toString();
	}

	/**
	 * e.g. transfer "767E" to "\u767E"
	 * 
	 * @param unicodeString
	 * @return "" means failed
	 */
	public static String unicodeStringToUnicode(String unicodeString) {
		try {
			char[] unicode = new char[unicodeString.length() / 4];
			for (int i = 0, j = 0; i < unicodeString.length(); i += 4, j++) {
				unicode[j] = (char) Integer.parseInt(
						unicodeString.substring(i, i + 4), 16);
			}
			return new String(unicode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static int getRStringId(String packageName, String stringName) {
		Class<?> stringClass = getRClass(packageName, "string");
		if (null == stringClass) {
			return -1;
		}
		try {
			return (Integer) stringClass.getDeclaredField(stringName).get(
					stringClass.newInstance());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * NOTICE: This method can not work at apk which is chaos.
	 * 
	 * @param packageName
	 * @param className
	 * @return
	 */
	public static Class<?> getRClass(String packageName, String className) {
		try {
			Class<?>[] classes = Class.forName(packageName + ".R")
					.getDeclaredClasses();
			for (int i = 0; i < classes.length; i++) {
				if (classes[i].getName().indexOf("$" + className) != -1) {
					return classes[i];
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
