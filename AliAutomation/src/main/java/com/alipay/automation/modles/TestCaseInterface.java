package com.alipay.automation.modles;

/**
 * QA interface, according to the test yaml script file
 * 
 * @author ruiqi.li
 */
public interface TestCaseInterface {
	
	public final static String CASE_CATEGORY = "category";

	/**
	 * Test case title
	 */
	public final static String CASE_TITLE = "title";

	/**
	 * Run test case parameters
	 */
	public final static String CASE_PARAMETER = "parameters";

	/**
	 * Test initiate start page, use to reset test case starting point
	 */
	public final static String CASE_ROLLBACK_VERIFIES = "rollbackVerifies";

	/**
	 * Rollback actions, use to bring the display page to the test starting
	 * page.
	 */
	public final static String CASE_ROLLBACK_ACTIONS = "rollback";

	/**
	 * Action list
	 */
	public final static String CASE_ACTIONS = "actions";
	
	public final static String CASE_SUIT_ACTIONS = "suitActions";

	/**
	 * Success stander
	 */
	public final static String CASE_VERIFIES = "verifies";

	public final static String CASE_RECUR_NAME = "recurParam";
}
