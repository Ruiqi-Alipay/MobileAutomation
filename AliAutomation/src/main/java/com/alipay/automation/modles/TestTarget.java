/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.automation.modles;

import java.util.regex.Pattern;

/**
 * Test target, a mobile UI widget for example.
 * 
 * @author ruiqi.li
 */
public class TestTarget implements TestTargetType {

	/**
	 * Element locate by element display name
	 */
	public final static String TARGET_TYPE_NAME = "name";

	/**
	 * Element locate by type and location in xx.xx.xx[d] format
	 */
	public final static String TARGET_TYPE_LOCATION = "location";

	// Widget search type, name, class or xpath.
	private String mType;

	// Widget search element, one of widget display text, widget class name or
	// widget xPath according to the mType respectively
	private String mElement;

	public TestTarget(String element) throws Exception {
		mElement = element;
		Pattern locationPattern = Pattern.compile(".*\\[\\d\\]");

		// Now create matcher object.
		if (locationPattern.matcher(element).find()) {
			int dividerIndex = element.indexOf("[");
			mType = TARGET_TYPE_LOCATION;
			mElement = typeToPackageName(element.substring(0, dividerIndex))
					+ element.substring(dividerIndex);
		} else {
			mType = TARGET_TYPE_NAME;
		}
	}

	public TestTarget(String type, String element) {
		mType = type;
		mElement = element;
	}

	public String getType() {
		return mType;
	}

	public String getElement() {
		return mElement;
	}

	private String typeToPackageName(String type) throws Exception {
		boolean isAndroid = true;
		if (EDIT_TEXT.equals(type)) {
			if (isAndroid) {
				return "android.widget.EditText";
			} else {
				return "";
			}
		} else if (CHECKBOX.equals(type)) {
			if (isAndroid) {
				return "android.widget.CheckBox";
			} else {
				return "";
			}
		} else if (RADIO.equals(type)) {
			if (isAndroid) {
				return "android.widget.RadioButton";
			} else {
				return "";
			}
		} else if (EDIT_TEXT.equals(type)) {
			if (isAndroid) {
				return "android.widget.Button";
			} else {
				return "";
			}
		} else if (IMAGE.equals(type)) {
			if (isAndroid) {
				return "android.widget.ImageView";
			} else {
				return "";
			}
		} else {
			throw new Exception("Target type not supported: " + type);
		}
	}
}
