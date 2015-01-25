package com.alipay.automation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.json.JSONObject;

import com.alipay.automation.utils.FileManager;
import com.alipay.automation.utils.GZipUtils;
import com.alipay.automation.utils.LogUtils;
import com.alipay.automation.utils.ProcessUtil;

public class Monitor {
	private static Monitor sInstance;
	private String mPackageName;
	private int mAppPid;
	private int mAppUid;
	private DecimalFormat mFormater;
	private double[] mNetworkData;
	private StringBuilder mLogBuilder;
	private boolean mNewStart;
	private String mRecordFilePath;
	private Thread mThread;

	public static Monitor getInstance() {
		if (sInstance == null) {
			sInstance = new Monitor();
		}

		return sInstance;
	}

	private Monitor() {

	}

	public void startRecording() {
		mFormater = new DecimalFormat("#.00");

		mPackageName = TestContext.getInstance().getPackageName();
		List<Integer> pids = ProcessUtil.getPidsByPackageName(mPackageName);
		if (pids != null && pids.size() != 0) {
			mAppPid = pids.get(0);
		}
		mAppUid = ProcessUtil.getUidByPid(mAppPid);

		mNetworkData = retriveNetworkData();
		mLogBuilder = new StringBuilder();

		mNewStart = true;

		if (!FileManager.REPORT_ROOT.exists()) {
			FileManager.REPORT_ROOT.mkdirs();
		}
		mRecordFilePath = FileManager.REPORT_ROOT + "/performance.report";
	}

	public void finishRecording() {
		recordContent(mRecordFilePath, "]");
	}

	public void recordActionStart(int index, String action) {
		try {
			Runtime.getRuntime().exec("adb logcat -c");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		mLogBuilder.setLength(0);
		mNetworkData = retriveNetworkData();

		mThread = new Thread(new Runnable() {

			@Override
			public void run() {
				Process cpuProcess = null;
				BufferedReader mReader = null;
				try {
					cpuProcess = Runtime.getRuntime().exec(
							"adb logcat | grep " + mAppPid + "\n");
					mReader = new BufferedReader(new InputStreamReader(
							cpuProcess.getInputStream(), "UTF-8"));
					String line = null;
					while ((line = mReader.readLine()) != null) {
						if (Thread.currentThread().isInterrupted()) {
							return;
						}

						if (line.equals("")) {
							continue;
						}

						mLogBuilder.append(line).append("\n");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (mReader != null) {
						try {
							mReader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (cpuProcess != null) {
						cpuProcess.destroy();
						cpuProcess = null;
					}
				}
			}
		});
		mThread.start();
	}

	public void recordActionEnd(int index, String action) {
		if (mThread != null) {
			mThread.interrupt();
			mThread = null;
		}

		WebDriverContext drierContext = WebDriverContext.getInstance();

		String memoInfoTotal = drierContext.executeAdb("dumpsys meminfo "
				+ mPackageName);

		String[] memoInfo = null;// drierContext.executeAdb("dumpsys meminfo "
		// + mPackageName + " | grep \"Dalvik Heap\"");
		double heapSize = Double.valueOf(mFormater.format(Double
				.valueOf(memoInfo[6]) / 1024));

		double[] networkData = retriveNetworkData();
		double sent = networkData[0] - mNetworkData[0];
		double rev = networkData[1] - mNetworkData[1];

		sent = round(sent, 1);
		rev = round(rev, 1);

		int cupProcent = retriveCPUUsage();

		JSONObject record = new JSONObject();
		record.put("index", index);
		record.put("action", action);
		record.put("heap", heapSize);
		record.put("sent", sent);
		record.put("reve", rev);
		record.put("cpu", cupProcent);

		JSONObject data = new JSONObject();
		data.put("log", mLogBuilder.toString());
		data.put("mem", memoInfoTotal);
		record.put("data", data);

		if (mNewStart) {
			recordContent(mRecordFilePath, "[");
		} else {
			recordContent(mRecordFilePath, ",");
		}
		recordContent(mRecordFilePath, record.toString());
		mNewStart = false;
	}

	public void generateReport() {
		new Thread() {

			@Override
			public void run() {
				LogUtils.log("Generating report...");
				int waitSecond = 60;
				File htmlReport = new File(FileManager.REPORT_ROOT,
						"reportng.js");
				while (waitSecond > 0) {
					waitSecond--;

					if (htmlReport.exists()) {
						break;
					} else {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
						}
					}
				}

				if (htmlReport.exists()) {
					Calendar calendar = Calendar.getInstance();
					StringBuilder builder = new StringBuilder();
					builder.append(calendar.get(Calendar.YEAR)).append("_");
					builder.append(calendar.get(Calendar.MONTH) + 1)
							.append("_");
					builder.append(calendar.get(Calendar.DAY_OF_MONTH)).append(
							"_");
					builder.append(calendar.get(Calendar.HOUR)).append("_");
					builder.append(calendar.get(Calendar.MINUTE)).append("_");
					builder.append(calendar.get(Calendar.SECOND));

					try {
						String name = "report_" + builder.toString();
						GZipUtils.zipDirectory(FileManager.REPORT_ROOT,
								FileManager.ROOT, name);
						FileManager.getInstance().deleteTree(
								FileManager.REPORT_ROOT);
						LogUtils.log("Generat report success: " + name);
					} catch (Exception e) {
						e.printStackTrace();
						LogUtils.log("Generat report failed: " + e);
					}
				} else {
					LogUtils.log("Generat report failed: html folder not found!");
				}
			}

		}.start();
	}

	private double[] retriveNetworkData() {
		WebDriverContext drierContext = WebDriverContext.getInstance();
		String contentRcv = "" + 0;
		String contentSnd = "" + 0;

		String uids = drierContext
				.executeAdb("adb shell ls /proc/uid_stat | grep " + mAppUid);
		if (uids.contains("" + mAppUid)) {
			contentRcv = drierContext
					.executeAdb("adb shell cat /proc/uid_stat/" + mAppUid
							+ "/tcp_rcv");
			contentSnd = drierContext
					.executeAdb("adb shell cat /proc/uid_stat/" + mAppUid
							+ "/tcp_snd");
		}

		double sentValue = Double.valueOf(mFormater.format(Double
				.valueOf(contentSnd) / 1024));
		double revValue = Double.valueOf(mFormater.format(Double
				.valueOf(contentRcv) / 1024));

		return new double[] { sentValue, revValue };
	}

	private int retriveCPUUsage() {
		BufferedReader in = null;
		Process cpuProcess = null;
		try {
			cpuProcess = Runtime.getRuntime().exec(
					"adb shell top -n 999999 -d 1" + "\n");
			in = new BufferedReader(new InputStreamReader(
					cpuProcess.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.equals("") || !line.contains(mPackageName)) {
					continue;
				}

				String[] rows = line.trim().split("\\s{1,}");
				return Integer.valueOf(rows[2].substring(0,
						rows[2].indexOf("%")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (cpuProcess != null) {
				cpuProcess.destroy();
				cpuProcess = null;
			}
		}

		return 0;
	}

	private void recordContent(String filePath, String content) {
		FileWriterWithEncoding writer = null;
		try {
			writer = new FileWriterWithEncoding(filePath, "UTF-8", true);
			writer.write(content);
		} catch (IOException e) {

		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static double round(double value, int places) {
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
