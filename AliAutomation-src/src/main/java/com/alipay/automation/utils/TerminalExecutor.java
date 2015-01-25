package com.alipay.automation.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerminalExecutor {
	private static final String[] WIN_RUNTIME = { "cmd.exe", "/C" };
	private static final String[] OS_LINUX_RUNTIME = { "/bin/bash", "-l", "-c" };
	private static boolean windows;
	static {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("win") >= 0) {
			windows = true;
		} else {
			windows = false;
		}
	}

	private TerminalExecutor() {

	}

	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static String runCommand(String command, String breakText)
			throws IOException {
		String[] allCommand = null;
		BufferedReader in = null;
		try {
			if (windows) {
				allCommand = concat(WIN_RUNTIME, new String[] { command });
			} else {
				allCommand = concat(OS_LINUX_RUNTIME, new String[] { command });
			}
			ProcessBuilder pb = new ProcessBuilder(allCommand);
			pb.redirectErrorStream(true);
			Process p = pb.start();
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String _temp = null;
			StringBuilder result = new StringBuilder();
			while ((_temp = in.readLine()) != null) {
				if (breakText != null && _temp.indexOf(breakText) >= 0) {
					break;
				}
				result.append(_temp);
			}
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
		}
	}
}
