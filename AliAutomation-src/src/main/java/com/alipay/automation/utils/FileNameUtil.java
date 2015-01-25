/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.automation.utils;

/**
 * 文件名工具类
 * @author qiyi.wxc
 * @version $Id: FileNameUtil.java, v 0.1 2014年9月3日 上午11:28:47 qiyi.wxc Exp $
 */
public class FileNameUtil {

    /**
     * 去掉 无法文件名禁止的一些字符 \ / : * ? " < > | &
     * 
     * @param name
     * @return
     */
    public static String convertStringToFileName(String name) {
        if (name == null) {
            return name;
        }
        // 去掉 无法文件名禁止的一些字符 \ / : * ? " < > | &
        //        return name.replaceAll("\\\\|/|:|\\*|\\?|\"|<|>|\\||&", "");
        return StringUtil.MD5(name);
    }
}
