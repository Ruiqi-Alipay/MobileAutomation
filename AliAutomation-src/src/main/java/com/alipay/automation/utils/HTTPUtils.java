package com.alipay.automation.utils;

import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openqa.selenium.remote.internal.HttpClientFactory;
import org.json.JSONObject;

public class HTTPUtils {

	public static JSONObject simpleGet(String urlString) {
		BufferedReader br = null;
		try {
			URL url = new URL(urlString);
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strTemp = "";
			StringBuilder builder = new StringBuilder();
			while (null != (strTemp = br.readLine())) {
				builder.append(strTemp);
			}

			return new JSONObject(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				br = null;
			}
		}

		return null;
	}
}
