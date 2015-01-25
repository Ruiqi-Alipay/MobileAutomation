/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.alipay.automation.utils;

import java.util.ArrayList;

/**
 * 
 * @author qiyi.wxc
 * @version $Id: CommandResult.java, v 0.1 2014年9月16日 下午8:47:15 qiyi.wxc Exp $
 */
public class CommandResult {
    public int     ret     = 0;
    public Strings console = new Strings(new ArrayList<String>());

    public CommandResult() {
    }
}
