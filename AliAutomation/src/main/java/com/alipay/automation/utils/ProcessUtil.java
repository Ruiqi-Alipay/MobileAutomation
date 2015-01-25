/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.automation.utils;

import java.util.ArrayList;

import com.alipay.automation.WebDriverContext;

/**
 * 
 * @author qiyi.wxc
 * @version $Id: ProcessUtil.java, v 0.1 2014年9月16日 下午8:24:14 qiyi.wxc Exp $
 */
public class ProcessUtil {

    public static int getUidByPid(int pid) {
        int uid = -1;
        try {
            ArrayList<String> uidString = new ShellExecute().execute(String.format(
                "cat /proc/%s/status", pid)).console.grep("Uid").strings;
            uid = Integer.valueOf(uidString.get(0).split("\t")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uid;
    }

    private static int getPidRowNumber() {
        String psHead = new ShellExecute().execute("ps").console.strings.get(0);
        String[] psHeadRow = psHead.split(" ");
        int rowNumber = 0;
        for (int i = 0; i < psHeadRow.length; i++) {
            if ("".equals(psHeadRow[i])) {
                continue;
            }
            rowNumber++;
            if ("PID".equals(psHeadRow[i])) {
                return rowNumber;
            }
        }
        return 0;
    }

    public static ArrayList<Integer> getPidsByPackageName(String packageName) {
        int pidRowNumber = getPidRowNumber();
        ArrayList<Integer> pids = new ArrayList<Integer>();
        ArrayList<String> pidStrings = new ShellExecute().execute("ps").console.grep(
            packageName).getRow("\\s{1,}", pidRowNumber).strings;
        for (String pid : pidStrings) {
            try {
                pids.add(Integer.valueOf(pid));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return pids;
    }
}
