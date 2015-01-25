/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.automation.utils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;

/**
 * 分析APK文件，取得APK文件中的 包名、版本号及图标
 * 
 * @author qiyi.wxc
 * @version $Id: ApkUtil.java, v 0.1 2014年8月29日 下午6:03:31 qiyi.wxc Exp $
 */
public class ApkUtil {
	/**
	 * @param apkUrl
	 *            apk文件位置
	 * @throws IOException
	 */
	public static String getPackageName(String apkUrl) {
		String packageName = "";
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(new File(apkUrl));
			Enumeration<?> enumeration = zipFile.entries();
			ZipEntry zipEntry = null;
			while (enumeration.hasMoreElements()) {
				zipEntry = (ZipEntry) enumeration.nextElement();
				if (zipEntry.isDirectory()) {

				} else {
					if ("AndroidManifest.xml".equals(zipEntry.getName())) {
						try {
							AXmlResourceParser parser = new AXmlResourceParser();
							parser.open(zipFile.getInputStream(zipEntry));
							while (true) {
								int type = parser.next();
								if (type == XmlPullParser.END_DOCUMENT) {
									break;
								}
								switch (type) {
								case XmlPullParser.START_TAG: {
									for (int i = 0; i != parser
											.getAttributeCount(); ++i) {
										if ("package".equals(parser
												.getAttributeName(i))) {
											packageName = getAttributeValue(
													parser, i);
										}
									}
								}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
					zipFile = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return packageName;
	}

	private static String getAttributeValue(AXmlResourceParser parser, int index) {
		int type = parser.getAttributeValueType(index);
		int data = parser.getAttributeValueData(index);
		if (type == TypedValue.TYPE_STRING) {
			return parser.getAttributeValue(index);
		}
		if (type == TypedValue.TYPE_ATTRIBUTE) {
			return String.format("?%s%08X", getPackage(data), data);
		}
		if (type == TypedValue.TYPE_REFERENCE) {
			return String.format("@%s%08X", getPackage(data), data);
		}
		if (type == TypedValue.TYPE_FLOAT) {
			return String.valueOf(Float.intBitsToFloat(data));
		}
		if (type == TypedValue.TYPE_INT_HEX) {
			return String.format("0x%08X", data);
		}
		if (type == TypedValue.TYPE_INT_BOOLEAN) {
			return data != 0 ? "true" : "false";
		}
		if (type == TypedValue.TYPE_DIMENSION) {
			return Float.toString(complexToFloat(data))
					+ DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type == TypedValue.TYPE_FRACTION) {
			return Float.toString(complexToFloat(data))
					+ FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type >= TypedValue.TYPE_FIRST_COLOR_INT
				&& type <= TypedValue.TYPE_LAST_COLOR_INT) {
			return String.format("#%08X", data);
		}
		if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return String.valueOf(data);
		}
		return String.format("<0x%X, type 0x%02X>", data, type);
	}

	private static String getPackage(int id) {
		if (id >>> 24 == 1) {
			return "android:";
		}
		return "";
	}

	// ///////////////////////////////// ILLEGAL STUFF, DONT LOOK :)
	public static float complexToFloat(int complex) {
		return (float) (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
	}

	private static final float RADIX_MULTS[] = { 0.00390625F, 3.051758E-005F,
			1.192093E-007F, 4.656613E-010F };
	private static final String DIMENSION_UNITS[] = { "px", "dip", "sp", "pt",
			"in", "mm", "", "" };
	private static final String FRACTION_UNITS[] = { "%", "%p", "", "", "", "",
			"", "" };
}
