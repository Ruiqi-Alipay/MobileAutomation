/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.automation.modles;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.automation.utils.TextUtils;

/**
 * Simulated user interaction.
 * 
 * @author ruiqi.li
 */
public class TestAction implements TestActionInterface {

	// original encoded action command
	private String mOriginalCommand;

	// action type
	private String mType;

	// action parameters
	private String mParams;

	// action target
	private TestTarget mTarget;

	public static List<TestAction> convertToActions(JSONArray actionArray)
			throws Exception {
		List<TestAction> actionList = new ArrayList<TestAction>();
		if (actionArray != null) {
			int length = actionArray.length();
			for (int i = 0; i < length; i++) {
				actionList.add(convertToAction(actionArray.getJSONObject(i)));
			}
		}

		return actionList;
	}

	public static TestAction convertToAction(JSONObject actionObject)
			throws Exception {
		String actionType = actionObject.getString(TYPE);
		String actionParams = null;
		String textTarget = null;
		TestTarget target = null;

		if (TestActionTypes.ACTION_TYPE_KEYEVENT.equals(actionType)) {
			//actionParams = String.valueOf(textKeyToKeyCode(actionObject
			//		.getString(TARGET)));
		} else {
			if (actionObject.has(PARAM)) {
				actionParams = actionObject.getString(PARAM);
			}
			if (actionObject.has(TARGET)) {
				textTarget = actionObject.getString(TARGET);
				if (!TextUtils.isEmpty(textTarget)) {
					target = new TestTarget(textTarget);
				}
			}
		}

		return new TestAction(actionType
				+ (actionParams == null ? "" : " | " + actionParams)
				+ (textTarget == null ? "" : " | " + textTarget), actionType,
				actionParams, target);
	}

	public TestAction(String originalCommand, String type, String param,
			TestTarget target) {
		mOriginalCommand = originalCommand;
		mType = type;
		mParams = param;
		mTarget = target;
	}

	/**
	 * @return the action original command in yaml script
	 */
	public String getOriginalCommand() {
		return mOriginalCommand;
	}

	/**
	 * @return action target widget
	 */
	public TestTarget getTestTarget() {
		return mTarget;
	}

	/**
	 * @return action type
	 */
	public String getType() {
		return mType;
	}

	public void setParams(String params) {
		mParams = params;
	}

	/**
	 * @return action parameters
	 */
	public String getParams() {
		return mParams;
	}

}
