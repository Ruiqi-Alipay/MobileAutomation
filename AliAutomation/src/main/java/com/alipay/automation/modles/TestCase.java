/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.automation.modles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.automation.utils.DataBankAgent;

/**
 * Single test case, represented by each yaml file in the testcase folder
 * 
 * @author ruiqi.li
 */
public class TestCase implements TestCaseInterface {

	// Test case title, extract from yaml script
	private String mCategory;

	// Test case description, extract from yaml script
	private String mName;

	// Test case parameter, extract from yaml script
	private Map<String, String> mParamsMap;

	private List<TestAction> mSuitActions;

	// Test case command queue, extract from yaml script, will be execute one by
	// one starting from the first item in the array
	private List<TestAction> mActions;

	// Success stander, all verify case in this array must success executed when
	// we mark this whole test case as success
	private List<TestVerify> mTestResultVerifys;

	private List<TestAction> mRollbackActions;

	private List<TestVerify> mStartingPageVerifies;

	private String mRecursiveParam;
	private String mRecursiveBuyerId;
	private int mRecursiveCombine;
	private String mAmount;
	private String mCouponAmount;

	public static TestCase parseJSON(JSONObject scriptJson,
			String recursivePram, String recursiveBuyerId,
			int recursiveCombine, String amount, String couponAmount)
			throws Exception {
		Map<String, String> paramsMap = new HashMap<String, String>();
		if (scriptJson.has(CASE_PARAMETER)) {
			JSONArray originalParamArray = scriptJson
					.getJSONArray(CASE_PARAMETER);
			int length = originalParamArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject item = originalParamArray.getJSONObject(i);
				paramsMap.put(item.getString("name"), item.getString("value"));
			}
		}

		String category = scriptJson.getString(CASE_CATEGORY);
		String name = scriptJson.getString(CASE_TITLE);
		if (paramsMap.containsKey(name)) {
			name = paramsMap.get(name);
		}

		List<TestAction> suitActionList = null;
		if (scriptJson.has(CASE_SUIT_ACTIONS)) {
			suitActionList = TestAction.convertToActions(scriptJson
					.getJSONArray(CASE_SUIT_ACTIONS));
		}

		List<TestAction> rollbackActions = null;
		if (scriptJson.has(CASE_ROLLBACK_ACTIONS)) {
			rollbackActions = TestAction.convertToActions(scriptJson
					.getJSONArray(CASE_ROLLBACK_ACTIONS));
		}

		List<TestVerify> startingPageVerifies = null;
		if (scriptJson.has(CASE_ROLLBACK_VERIFIES)) {
			startingPageVerifies = TestVerify.convertToVerifies(scriptJson
					.getJSONArray(CASE_ROLLBACK_VERIFIES));
		}

		List<TestAction> actionList = TestAction.convertToActions(scriptJson
				.getJSONArray(CASE_ACTIONS));
		List<TestVerify> resultVerifyList = TestVerify
				.convertToVerifies(scriptJson.getJSONArray(CASE_VERIFIES));

		return new TestCase(category, name, paramsMap, suitActionList,
				actionList, resultVerifyList, rollbackActions,
				startingPageVerifies, recursivePram, recursiveBuyerId,
				recursiveCombine, amount, couponAmount);
	}

	public TestCase(String title, String name, Map<String, String> paramsMap,
			List<TestAction> suitActions, List<TestAction> actions,
			List<TestVerify> resultVerifies, List<TestAction> rollbackActions,
			List<TestVerify> startingPageVerifies, String recursivePram,
			String recursiveBuyerId, int recursiveCombine, String amount,
			String couponAmount) {
		mCategory = title;
		mName = name;
		mParamsMap = paramsMap;
		mSuitActions = suitActions;
		mActions = actions;
		mTestResultVerifys = resultVerifies;
		mRollbackActions = rollbackActions;
		mStartingPageVerifies = startingPageVerifies;
		mRecursiveParam = recursivePram;
		mRecursiveBuyerId = recursiveBuyerId;
		mRecursiveCombine = recursiveCombine;
		mAmount = amount;
		mCouponAmount = couponAmount;
	}

	public String getRecursiveParam() {
		return mRecursiveParam;
	}

	public void prepareRecursiveData() {
		if (mRecursiveParam == null) {
			return;
		}

		Random random = new Random();
		StringBuilder recursiveParamBuilder = new StringBuilder();
		for (int i = 0; i < mRecursiveCombine; i++) {
			String amount = String.valueOf(random.nextInt(100) + 1);
			String couponAmount = "0";
			try {
				amount = String.valueOf(Double.valueOf(mAmount));
				couponAmount = String.valueOf(Double.valueOf(mCouponAmount));
			} catch (Exception e) {

			}

			recursiveParamBuilder.append(DataBankAgent.creatNewIpayTradeNo(
					mRecursiveBuyerId, amount, couponAmount));
			if (i != mRecursiveCombine - 1) {
				recursiveParamBuilder.append(",");
			}
		}

		mParamsMap.put(mRecursiveParam, recursiveParamBuilder.toString());
	}

	/**
	 * @return test case title, use as indicator
	 */
	public String getCategory() {
		return mCategory;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getAmount() {
		return mAmount;
	}

	public String getCouponAmount() {
		return mCouponAmount;
	}

	public List<TestAction> getRollbackActions() {
		return mRollbackActions;
	}

	public List<TestVerify> getStartPageVerifies() {
		return mStartingPageVerifies;
	}

	/**
	 * @return test case name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return test case parameter
	 */
	public String getParameter(String key) {
		return mParamsMap.get(key);
	}

	/**
	 * @return action list
	 */
	public List<TestAction> getActions() {
		return mActions;
	}

	/**
	 * @return action list
	 */
	public List<TestAction> getSuitActions() {
		return mSuitActions;
	}

	/**
	 * @return verify list
	 */
	public List<TestVerify> getVerifies() {
		return mTestResultVerifys;
	}

	@Override
	public String toString() {
		return mCategory;
	}

}
