package com.alipay.automation.modles;

public interface TestActionTypes {
	/**
	 * Element click
	 */
	public final static String ACTION_TYPE_CLICK = "点击";
	public final static String ACTION_TYPE_CLICK_LOCATION = "点击位置";
	public final static String ACTION_TYPE_CLICK_IMAGE = "点击图片";

	/**
	 * Text input, has BUG
	 */
	public final static String ACTION_TYPE_INPUT = "输入";
	public final static String ACTION_TYPE_ALIKEYBORAD = "阿里键盘输入";
	public final static String ACTION_TYPE_ALIKEYBORAD_NUM = "阿里数字键盘输入";
	public final static String ACTION_TYPE_KEYEVENT = "按键";
	
	public final static String ACTION_TYPE_CHECK = "选择";
	public final static String ACTION_TYPE_QUICK_CHECK = "快速选择";
	public final static String ACTION_TYPE_RADIO_LOCATION = "单选位置";
	public final static String ACTION_TYPE_CHECKBOX_LOCATION = "多选位置";
	
	/**
	 * Clear text
	 */
	public final static String ACTION_TYPE_CLEAR_ALL = "清屏";
	public final static String ACTION_TYPE_CLEAR = "清除";

	/**
	 * Screen scroll down
	 */
	public final static String ACTION_TYPE_SCROLLDOWN = "向下滚动";
}
