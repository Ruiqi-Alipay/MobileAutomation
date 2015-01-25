package com.alipay.automation.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DataBankAgent {

	private static String host = "databank.alibaba-inc.com";
	private static String dbType = "fc";

	public static String commonMethod(Map<String, String> userParamsMap,
			String agentId) {

		StringBuffer userParams = new StringBuffer();
		userParams.append("{");
		for (Map.Entry<String, String> entry : userParamsMap.entrySet()) {
			userParams.append("\"" + entry.getKey() + "\":");
			userParams.append("\"" + entry.getValue() + "\",");
		}
		String parms = userParams.substring(0, userParams.length() - 1);
		parms = parms + "}";

		String runInfoIds = callDataBankAgent(agentId, parms);

		return getDataBankResult(runInfoIds);
	}

	public static String creatNewIpayTradeNo(String buyerCardNo,
			String cashAmount, String couponAmount) {
		String tradeNo = null;
		String tradeOrderList = "[{\\\"buyerCardNo\\\": \\\"2188205006685382\\\",\\\"buyerShowName\\\": \\\"databank buyer\\\",\\\"createDt\\\": \\\"2014-07-08 00:00:30.000|US/Pacific\\\",\\\"expireDt\\\": \\\"1406530830000\\\",\\\"tradeMainType\\\": \\\"AE_COMMON\\\",\\\"site\\\": \\\"ru\\\",\\\"safeStockTime\\\": \\\"1406803438000\\\",\\\"activityIds\\\": \\\"98,99\\\",     \\\"goodsItemList\\\": [{\\\"rootCategory\\\": \\\"12\\\",\\\"subCategories\\\": \\\"34/56\\\",\\\"count\\\": \\\"1\\\",\\\"itemNo\\\": \\\"62900102424272\\\",\\\"price\\\": \\\"221.49\\\",\\\"priceCur\\\": \\\"USD\\\",\\\"title\\\": \\\"2014 New girls sandals kids boots children rivets pu shoes 4colors casual sandals for 2-10 years girls free shipping\\\",\\\"unit\\\": \\\"piece\\\"}],\\\"intentPayAmount\\\": \\\"7855.35\\\",\\\"intentPayCur\\\": \\\"RUB\\\",\\\"logisticInfo\\\": {\\\"email\\\":\\\"helloipay@ipay.com\\\",\\\"address1\\\": \\\"Molodegnaya 1/15\\\",\\\"address2\\\": \\\"\\\",\\\"contactPerson\\\": \\\"apitest buyer\\\",\\\"faxNo\\\": \\\"--\\\",\\\"mobileNo\\\": \\\"89022024650\\\",\\\"phoneNo\\\": \\\"46-3812-89022024650\\\",\\\"postCode\\\": \\\"644505\\\",\\\"shippingCity\\\": \\\"Omsk\\\",\\\"shippingCountry\\\": \\\"RU\\\",\\\"shippingFee\\\": \\\"0.0\\\",\\\"shippingFeeCur\\\": \\\"USD\\\",\\\"shippingState\\\": \\\"Omsk Oblast\\\",\\\"shippingMethod\\\": \\\"CPAM\\\",\\\"shippingExt\\\": {\\\"CPF\\\": \\\"02367591911\\\"}},\\\"originAmount\\\": \\\"221.49\\\",\\\"originAmountCur\\\": \\\"USD\\\",\\\"partnerBuyerId\\\": \\\"1007825241\\\", \\\"partnerOrderNo\\\": \\\"63311102424272\\\",\\\"partnerSellerId\\\": \\\"1008757982\\\",\\\"partnerSubType\\\": \\\"2088000001\\\",\\\"preDefPayBillList\\\": [{\\\"direction\\\": \\\"OUT\\\",\\\"participant\\\": \\\"BUYER\\\",\\\"tradeAmount\\\": \\\"191.49\\\",\\\"tradeAmountCur\\\": \\\"USD\\\"},{\\\"direction\\\": \\\"OUT\\\",\\\"participant\\\": \\\"AE_COUPON\\\",\\\"tradeAmount\\\": \\\"30\\\",\\\"tradeAmountCur\\\": \\\"USD\\\"}],\\\"riskData\\\": \\\"{\\\\\\\"billToCity\\\\\\\":\\\\\\\"Omsk\\\\\\\",\\\\\\\"billToCountry\\\\\\\":\\\\\\\"RU\\\\\\\",\\\\\\\"billToEmail\\\\\\\":\\\\\\\"helloipay@ipay.com\\\\\\\",\\\\\\\"billToPhoneNumber\\\\\\\":\\\\\\\"46381289022024650\\\\\\\",\\\\\\\"billToPostalCode\\\\\\\":\\\\\\\"644505\\\\\\\",\\\\\\\"billToState\\\\\\\":\\\\\\\"Omsk Oblast\\\\\\\",\\\\\\\"billToStreet1\\\\\\\":\\\\\\\"Molodegnaya 1/15\\\\\\\",\\\\\\\"billToStreet2\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"buyerAdminSeq\\\\\\\":\\\\\\\"1007825241\\\\\\\",\\\\\\\"buyerLoginId\\\\\\\":\\\\\\\"db1007825240\\\\\\\",\\\\\\\"buyerSeq\\\\\\\":\\\\\\\"1007825241\\\\\\\",\\\\\\\"category2\\\\\\\":\\\\\\\"200000947\\\\\\\",\\\\\\\"category3\\\\\\\":\\\\\\\"200001003\\\\\\\",\\\\\\\"categoryLeaf\\\\\\\":\\\\\\\"200001003\\\\\\\",\\\\\\\"categoryRoot\\\\\\\":\\\\\\\"322\\\\\\\",\\\\\\\"item0ProductName\\\\\\\":\\\\\\\"2014 New girls sandals kids boots children rivets pu shoes 4colors casual sandals for 2-10 years girls free shipping\\\\\\\",\\\\\\\"item0Quantiry\\\\\\\":\\\\\\\"1\\\\\\\",\\\\\\\"item0UnitPrice\\\\\\\":\\\\\\\"22149\\\\\\\",\\\\\\\"logisticsAmount\\\\\\\":\\\\\\\"0\\\\\\\",\\\\\\\"mobileNo\\\\\\\":\\\\\\\"89022024650\\\\\\\",\\\\\\\"outOrderId\\\\\\\":\\\\\\\"62900102424272\\\\\\\",\\\\\\\"outRef\\\\\\\":\\\\\\\"62900102424272\\\\\\\",\\\\\\\"productId\\\\\\\":\\\\\\\"1912777748\\\\\\\",\\\\\\\"sellerAdminSeq\\\\\\\":\\\\\\\"1008757982\\\\\\\",\\\\\\\"sellerLoginId\\\\\\\":\\\\\\\"db1008757981\\\\\\\",\\\\\\\"sellerSeq\\\\\\\":\\\\\\\"1008757982\\\\\\\",\\\\\\\"shipToCity\\\\\\\":\\\\\\\"Omsk\\\\\\\",\\\\\\\"shipToContactName\\\\\\\":\\\\\\\"apitest buyer\\\\\\\",\\\\\\\"shipToCountry\\\\\\\":\\\\\\\"RU\\\\\\\",\\\\\\\"shipToPhoneNumber\\\\\\\":\\\\\\\"46381289022024650\\\\\\\",\\\\\\\"shipToPostalCode\\\\\\\":\\\\\\\"644505\\\\\\\",\\\\\\\"shipToShippingMethod\\\\\\\":\\\\\\\"CPAM\\\\\\\",\\\\\\\"shipToState\\\\\\\":\\\\\\\"Omsk Oblast\\\\\\\",\\\\\\\"shipToStreet1\\\\\\\":\\\\\\\"Molodegnaya 1/15\\\\\\\",\\\\\\\"shipToStreet2\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"transactionTime\\\\\\\":\\\\\\\"2014-07-08 00:00:30\\\\\\\",\\\\\\\"buyerServiceType\\\\\\\":\\\\\\\"IFM\\\\\\\",\\\\\\\"sellerServiceType\\\\\\\":\\\\\\\"CGS\\\\\\\",\\\\\\\"osType\\\\\\\":\\\\\\\"WINDOWS\\\\\\\"}\\\",     \\\"sellerCardNo\\\": \\\"2188205002099413\\\",\\\"sellerShowName\\\": \\\"databank seller\\\",\\\"tradeAmount\\\": \\\"221.49\\\",\\\"tradeAmountCur\\\": \\\"USD\\\",  \\\"tradeChannel\\\": \\\"pc\\\",\\\"tradeRule\\\": {\\\"payChannelRule\\\": {\\\"supportPaymentType\\\": \\\"all\\\"}}}]";
		dbChange();
		Map<String, String> param = new HashMap<String, String>();
		param.put("expireTime", "2592000000");
		param.put("trade_channel", "PC");
		param.put("trade_order_list", tradeOrderList);
		param.put("databankServerUrl", "http://databank.intl.alipay.net:8080");
		param.put("prod_code", "DEFAULT_PRODUCT_CODE");
		param.put("env", dbType);
		param.put("trade_cash_amount", cashAmount);
		param.put("trade_coupon_amount", couponAmount);
		param.put("newOrder", "true");
		param.put("partnerSubType", "2088000001");
		param.put("version", "1.0.0");
		param.put("partner", "2188400000000016");
		param.put("sitbuyerCardNo", buyerCardNo);
		param.put("devbuyerCardNo", buyerCardNo);
		param.put("devItradeUrl", "http://itrade.stable.alipay.net:8080");

		String result = commonMethod(param, "2294");
		result = result.split(",")[1];
		tradeNo = result.split("=")[2];

		return tradeNo;
	}

	public static void dbChange() {
		if (dbType.equals("fc") || dbType.equals("dev")) {
			dbType = "dev";
		} else {
			dbType = "sit";
		}
	}

	public static String callDataBankAgent(String agentId, String userParams) {
		String path = "/execute/executeData.htm";
		String url = "http://" + host + path;
		String result = "";
		String runInfoIds = "";

		URL serverUrl = null;
		HttpURLConnection urlConnection = null;
		try {
			serverUrl = new URL(url);
			urlConnection = (HttpURLConnection) serverUrl.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");

			BufferedWriter httpRequestBodyWriter = new BufferedWriter(
					new OutputStreamWriter(urlConnection.getOutputStream()));
			StringBuilder bodyBuilder = new StringBuilder();
			bodyBuilder.append("loginName").append("=").append("tianzhong.ctz")
					.append("&");
			bodyBuilder.append("dataId").append("=").append(agentId)
					.append("&");
			bodyBuilder.append("userParam").append("=").append(userParams)
					.append("&");
			bodyBuilder.append("isShare").append("=").append("0").append("&");
			bodyBuilder.append("number").append("=").append("1").append("&");
			bodyBuilder.append("version").append("=").append("1.0.0");
			httpRequestBodyWriter.write(bodyBuilder.toString());
			httpRequestBodyWriter.close();

			Scanner httpResponseScanner = new Scanner(
					urlConnection.getInputStream());
			StringBuilder resultBuilder = new StringBuilder();
			while (httpResponseScanner.hasNextLine()) {
				resultBuilder.append(httpResponseScanner.nextLine());
			}
			httpResponseScanner.close();

			result = resultBuilder.toString();

			if (result != null && !result.isEmpty()) {
				runInfoIds = result.split(",")[0].split(":")[1].substring(1);
				runInfoIds = runInfoIds.substring(0, runInfoIds.length() - 1);
			}
			return runInfoIds.trim();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		return null;
	}

	public static String getDataBankResult(String runInfos) {
		String path = "/execute/executeDataStatus.htm";
		String url = "http://" + host + path;
		String result = "";

		URL serverUrl = null;
		HttpURLConnection urlConnection = null;
		for (int i = 0; i < 60; i++) {
			try {
				serverUrl = new URL(url);
				urlConnection = (HttpURLConnection) serverUrl.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("POST");

				BufferedWriter httpRequestBodyWriter = new BufferedWriter(
						new OutputStreamWriter(urlConnection.getOutputStream()));
				StringBuilder bodyBuilder = new StringBuilder();
				bodyBuilder.append("runInfoId").append("=").append(runInfos);
				httpRequestBodyWriter.write(bodyBuilder.toString());
				httpRequestBodyWriter.close();

				Scanner httpResponseScanner = new Scanner(
						urlConnection.getInputStream());
				StringBuilder resultBuilder = new StringBuilder();
				while (httpResponseScanner.hasNextLine()) {
					resultBuilder.append(httpResponseScanner.nextLine());
				}
				httpResponseScanner.close();

				result = resultBuilder.toString();
				if (result.indexOf("COMPLETED") > 0) {
					break;
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {

					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
			}
		}

		return result;
	}

}
