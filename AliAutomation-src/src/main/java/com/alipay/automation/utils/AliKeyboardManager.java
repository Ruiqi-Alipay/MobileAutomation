package com.alipay.automation.utils;

import io.selendroid.client.SelendroidDriver;

import java.util.HashMap;
import java.util.Map;

public class AliKeyboardManager {

	public static final String KEYBOARD_STATE_QWE = "QUE";
	public static final String KEYBOARD_STATE_NUM = "NUM";

	private static AliKeyboardManager sInstance;

	public static AliKeyboardManager getInstance(SelendroidDriver driver) {
		if (sInstance == null) {
			sInstance = new AliKeyboardManager(driver);
		}

		return sInstance;
	}

	private String mCurrentKeyboardState = KEYBOARD_STATE_QWE;
	private Map<String, int[]> mLetterCoordinateMap = new HashMap<String, int[]>();
	private Map<String, int[]> mNumCoordinateMap = new HashMap<String, int[]>();
	private Map<String, int[]> mPasswordCoordinateMap = new HashMap<String, int[]>();
	private int[] mNumQweToggle;
	private int[] mOkToggle;
	private int mDialogButtonY;
	private int mLeftDialogButtonX;
	private int mRightDialogButtonX;

	private int SCREEN_RATIO_PADDING = 2;
	private int SCREEN_RATIO_WIDTH = 9;
	private int SCREEN_RATIO_HEIGHT = 12;

	private AliKeyboardManager(SelendroidDriver driver) {
		String[] dimensions = driver.manage().window().getSize().toString()
				.split("\\D");
		int screenWidth = Integer.parseInt(dimensions[1]);
		int screenHeight = Integer.parseInt(dimensions[3]);

		int cellW = screenWidth / 10;
		int cellH = cellW * SCREEN_RATIO_HEIGHT / SCREEN_RATIO_WIDTH;
		int padding = cellW * SCREEN_RATIO_PADDING / SCREEN_RATIO_WIDTH;

		int numBtnW = screenWidth / 3;
		int numBtnH = numBtnW * 2 / 5;

		int[] pl1c1 = new int[] { (int) (0.5 * numBtnW),
				screenHeight - (int) (3.5 * numBtnH) };
		int[] pl1c2 = new int[] { (int) (1.5 * numBtnW),
				screenHeight - (int) (3.5 * numBtnH) };
		int[] pl1c3 = new int[] { (int) (2.5 * numBtnW),
				screenHeight - (int) (3.5 * numBtnH) };
		int[] pl2c1 = new int[] { (int) (0.5 * numBtnW),
				screenHeight - (int) (2.5 * numBtnH) };
		int[] pl2c2 = new int[] { (int) (1.5 * numBtnW),
				screenHeight - (int) (2.5 * numBtnH) };
		int[] pl2c3 = new int[] { (int) (2.5 * numBtnW),
				screenHeight - (int) (2.5 * numBtnH) };
		int[] pl3c1 = new int[] { (int) (0.5 * numBtnW),
				screenHeight - (int) (1.5 * numBtnH) };
		int[] pl3c2 = new int[] { (int) (1.5 * numBtnW),
				screenHeight - (int) (1.5 * numBtnH) };
		int[] pl3c3 = new int[] { (int) (2.5 * numBtnW),
				screenHeight - (int) (1.5 * numBtnH) };
		int[] pl4c1 = new int[] { (int) (0.5 * numBtnW),
				screenHeight - (int) (0.5 * numBtnH) };
		int[] pl4c2 = new int[] { (int) (1.5 * numBtnW),
				screenHeight - (int) (0.5 * numBtnH) };
		int[] pl4c3 = new int[] { (int) (2.5 * numBtnW),
				screenHeight - (int) (0.5 * numBtnH) };
		mPasswordCoordinateMap.put("1", pl1c1);
		mPasswordCoordinateMap.put("2", pl1c2);
		mPasswordCoordinateMap.put("3", pl1c3);
		mPasswordCoordinateMap.put("4", pl2c1);
		mPasswordCoordinateMap.put("5", pl2c2);
		mPasswordCoordinateMap.put("6", pl2c3);
		mPasswordCoordinateMap.put("7", pl3c1);
		mPasswordCoordinateMap.put("8", pl3c2);
		mPasswordCoordinateMap.put("9", pl3c3);
		mPasswordCoordinateMap.put("del", pl4c1);
		mPasswordCoordinateMap.put("0", pl4c2);
		mPasswordCoordinateMap.put("ok", pl4c3);

		// use for device under API 17
		mDialogButtonY = (int) (screenHeight / 1.627);
		mLeftDialogButtonX = screenWidth / 2 - 40;
		mRightDialogButtonX = screenWidth / 2 + 40;

		mNumQweToggle = new int[] { cellW, screenHeight - cellH / 2 };
		mOkToggle = new int[] { screenWidth - cellW, screenHeight - cellH / 2 };

		int[] l1c1 = new int[] { (int) (0.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };
		int[] l1c2 = new int[] { (int) (1.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };
		int[] l1c3 = new int[] { (int) (2.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };
		int[] l1c4 = new int[] { (int) (3.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };
		int[] l1c5 = new int[] { (int) (4.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };
		int[] l1c6 = new int[] { (int) (5.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };
		int[] l1c7 = new int[] { (int) (6.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };
		int[] l1c8 = new int[] { (int) (7.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };
		int[] l1c9 = new int[] { (int) (8.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };
		int[] l1c10 = new int[] { (int) (9.5 * cellW),
				screenHeight - 3 * (cellH + padding) - cellH / 2 - padding };

		int[] l2c1 = new int[] { 1 * cellW,
				screenHeight - 2 * (cellH + padding) - cellH / 2 - padding };
		int[] l2c2 = new int[] { 2 * cellW,
				screenHeight - 2 * (cellH + padding) - cellH / 2 - padding };
		int[] l2c3 = new int[] { 3 * cellW,
				screenHeight - 2 * (cellH + padding) - cellH / 2 - padding };
		int[] l2c4 = new int[] { 4 * cellW,
				screenHeight - 2 * (cellH + padding) - cellH / 2 - padding };
		int[] l2c5 = new int[] { 5 * cellW,
				screenHeight - 2 * (cellH + padding) - cellH / 2 - padding };
		int[] l2c6 = new int[] { 6 * cellW,
				screenHeight - 2 * (cellH + padding) - cellH / 2 - padding };
		int[] l2c7 = new int[] { 7 * cellW,
				screenHeight - 2 * (cellH + padding) - cellH / 2 - padding };
		int[] l2c8 = new int[] { 8 * cellW,
				screenHeight - 2 * (cellH + padding) - cellH / 2 - padding };
		int[] l2c9 = new int[] { 9 * cellW,
				screenHeight - 2 * (cellH + padding) - cellH / 2 - padding };

		int[] l3c1 = new int[] { 2 * cellW,
				screenHeight - 1 * (cellH + padding) - cellH / 2 - padding };
		int[] l3c2 = new int[] { 3 * cellW,
				screenHeight - 1 * (cellH + padding) - cellH / 2 - padding };
		int[] l3c3 = new int[] { 4 * cellW,
				screenHeight - 1 * (cellH + padding) - cellH / 2 - padding };
		int[] l3c4 = new int[] { 5 * cellW,
				screenHeight - 1 * (cellH + padding) - cellH / 2 - padding };
		int[] l3c5 = new int[] { 6 * cellW,
				screenHeight - 1 * (cellH + padding) - cellH / 2 - padding };
		int[] l3c6 = new int[] { 7 * cellW,
				screenHeight - 1 * (cellH + padding) - cellH / 2 - padding };
		int[] l3c7 = new int[] { 8 * cellW,
				screenHeight - 1 * (cellH + padding) - cellH / 2 - padding };

		mLetterCoordinateMap.put("q", l1c1);
		mLetterCoordinateMap.put("w", l1c2);
		mLetterCoordinateMap.put("e", l1c3);
		mLetterCoordinateMap.put("r", l1c4);
		mLetterCoordinateMap.put("t", l1c5);
		mLetterCoordinateMap.put("y", l1c6);
		mLetterCoordinateMap.put("u", l1c7);
		mLetterCoordinateMap.put("i", l1c8);
		mLetterCoordinateMap.put("o", l1c9);
		mLetterCoordinateMap.put("p", l1c10);
		mNumCoordinateMap.put("1", l1c1);
		mNumCoordinateMap.put("2", l1c2);
		mNumCoordinateMap.put("3", l1c3);
		mNumCoordinateMap.put("4", l1c4);
		mNumCoordinateMap.put("5", l1c5);
		mNumCoordinateMap.put("6", l1c6);
		mNumCoordinateMap.put("7", l1c7);
		mNumCoordinateMap.put("8", l1c8);
		mNumCoordinateMap.put("9", l1c9);
		mNumCoordinateMap.put("0", l1c10);

		mLetterCoordinateMap.put("a", l2c1);
		mLetterCoordinateMap.put("s", l2c2);
		mLetterCoordinateMap.put("d", l2c3);
		mLetterCoordinateMap.put("f", l2c4);
		mLetterCoordinateMap.put("g", l2c5);
		mLetterCoordinateMap.put("h", l2c6);
		mLetterCoordinateMap.put("j", l2c7);
		mLetterCoordinateMap.put("k", l2c8);
		mLetterCoordinateMap.put("l", l2c9);
		mNumCoordinateMap.put("~", l2c1);
		mNumCoordinateMap.put("!", l2c2);
		mNumCoordinateMap.put("@", l2c3);
		mNumCoordinateMap.put("#", l2c4);
		mNumCoordinateMap.put("%", l2c5);
		mNumCoordinateMap.put("'", l2c6);
		mNumCoordinateMap.put("&", l2c7);
		mNumCoordinateMap.put("*", l2c8);
		mNumCoordinateMap.put("?", l2c9);

		mLetterCoordinateMap.put("z", l3c1);
		mLetterCoordinateMap.put("x", l3c2);
		mLetterCoordinateMap.put("c", l3c3);
		mLetterCoordinateMap.put("v", l3c4);
		mLetterCoordinateMap.put("b", l3c5);
		mLetterCoordinateMap.put("n", l3c6);
		mLetterCoordinateMap.put("m", l3c7);
		mNumCoordinateMap.put("(", l3c1);
		mNumCoordinateMap.put(")", l3c2);
		mNumCoordinateMap.put("-", l3c3);
		mNumCoordinateMap.put("_", l3c4);
		mNumCoordinateMap.put(":", l3c5);
		mNumCoordinateMap.put(";", l3c6);
		mNumCoordinateMap.put("/", l3c7);
	}

	public void mockDialogOk(SelendroidDriver driver) {
		tap(driver, mOkToggle[0], mOkToggle[1]);
		sleepInMillionsecond(50);
		tap(driver, mRightDialogButtonX, mDialogButtonY);
	}

	public void mockDialogCancel(SelendroidDriver driver) {
		tap(driver, mOkToggle[0], mOkToggle[1]);
		sleepInMillionsecond(50);
		tap(driver, mLeftDialogButtonX, mDialogButtonY);
	}

	public void passwordIn(String keyValue, SelendroidDriver driver) {
		if (mPasswordCoordinateMap.containsKey(keyValue)) {
			int[] letterXY = mPasswordCoordinateMap.get(keyValue);
			tap(driver, letterXY[0], letterXY[1]);
		}
	}

	public void keyIn(String keyValue, SelendroidDriver driver) {
		if (mLetterCoordinateMap.containsKey(keyValue)) {
			if (!KEYBOARD_STATE_QWE.equals(mCurrentKeyboardState)) {
				mCurrentKeyboardState = KEYBOARD_STATE_QWE;
				tap(driver, mNumQweToggle[0], mNumQweToggle[1]);
			}
			int[] letterXY = mLetterCoordinateMap.get(keyValue);
			tap(driver, letterXY[0], letterXY[1]);
		} else {
			if (!KEYBOARD_STATE_NUM.equals(mCurrentKeyboardState)) {
				mCurrentKeyboardState = KEYBOARD_STATE_NUM;
				tap(driver, mNumQweToggle[0], mNumQweToggle[1]);
			}
			int[] numXY = mNumCoordinateMap.get(keyValue);
			tap(driver, numXY[0], numXY[1]);
		}
	}

	public void resetKeyboard(SelendroidDriver driver) {
		mCurrentKeyboardState = KEYBOARD_STATE_QWE;
		tap(driver, mNumQweToggle[0], mNumQweToggle[1]);
	}

	private void tap(SelendroidDriver driver, int x, int y) {
		driver.getTouch().down(x, y);
		sleepInMillionsecond(50);
		driver.getTouch().up(x, y);
	}

	private static void sleepInMillionsecond(int second) {
		try {
			Thread.sleep(second);
		} catch (InterruptedException e) {
		}
	}

}
