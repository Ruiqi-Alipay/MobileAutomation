/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.alipay.automation.utils;

import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;


/**
 * 
 * @author jianmin.jiang
 * 
 * @version $Id: StringUtil.java, v 0.1 2012-2-7 下午3:14:58 jianmin.jiang Exp $
 */

public class StringUtil {

    /**
     * 16进制转义
     * 
     * @param src
     * @return
     */
    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * 将字符串 source 中的 oldStr 替换为 newStr, 并以大小写敏感方式进行查找
     * 
     * @param source
     *            需要替换的源字符串
     * @param oldStr
     *            需要被替换的老字符串
     * @param newStr
     *            替换为的新字符串
     */
    public static String replace(String source, String oldStr, String newStr) {
        return replace(source, oldStr, newStr, true);
    }

    /**
     * 将字符串 source 中的 oldStr 替换为 newStr, matchCase 为是否设置大小写敏感查找
     * 
     * @param source
     *            需要替换的源字符串
     * @param oldStr
     *            需要被替换的老字符串
     * @param newStr
     *            替换为的新字符串
     * @param matchCase
     *            是否需要按照大小写敏感方式查找
     */
    public static String replace(String source, String oldStr, String newStr, boolean matchCase) {
        if (source == null) {
            return null;
        }
        // 首先检查旧字符串是否存在, 不存在就不进行替换
        if (source.toLowerCase(Locale.getDefault())
            .indexOf(oldStr.toLowerCase(Locale.getDefault())) == -1) {
            return source;
        }
        int findStartPos = 0;
        int a = 0;
        StringBuffer bbuf = new StringBuffer(source);
        while (a > -1) {
            int b = 0;
            String strA, strB;
            if (matchCase) {
                strA = source;
                strB = oldStr;
            } else {
                strA = source.toLowerCase(Locale.getDefault());
                strB = oldStr.toLowerCase(Locale.getDefault());
            }
            a = strA.indexOf(strB, findStartPos);
            if (a > -1) {
                b = oldStr.length();
                findStartPos = a + b;
                bbuf = bbuf.replace(a, a + b, newStr);
                // 新的查找开始点位于替换后的字符串的结尾
                findStartPos = findStartPos + newStr.length() - b;
            }
        }
        return bbuf.toString();
    }

    /**
     * 清除字符串结尾的空格.
     * 
     * @param input
     *            String 输入的字符串
     * @return 转换结果
     */
    public static String trimTailSpaces(String input) {
        if (isEmpty(input)) {
            return "";
        }

        String trimedString = input.trim();
        if (trimedString.length() == input.length()) {
            return input;
        }
        return input.substring(0, input.indexOf(trimedString) + trimedString.length());
    }

    /**
     * Change the null string value to "", if not null, then return it self, use
     * this to avoid display a null string to "null".
     * 
     * @param input
     *            the string to clear
     * @return the result
     */
    public static String clearNull(String input) {
        return isEmpty(input) ? "" : input;
    }

    /**
     * 截取固定长度字符串
     * 
     * @param input
     *            String
     * @param maxLength
     *            int
     * @return String processed result
     */
    public static String limitStringLength(String input, int maxLength) {
        if (isEmpty(input))
            return "";

        if (input.length() <= maxLength) {
            return input;
        } else {
            return input.substring(0, maxLength - 3) + "...";
        }

    }

    /**
     * 判断字符串是否全是数字字符.
     * 
     * @param input
     *            输入的字符串
     * @return 判断结果, true 为全数字, false 为还有非数字字符
     */
    public static boolean isNumeric(String input) {
        if (isEmpty(input)) {
            return false;
        }

        for (int i = 0; i < input.length(); i++) {
            char charAt = input.charAt(i);

            if (!Character.isDigit(charAt)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 转换由表单读取的数据的内码(从 ISO8859 转换到 gb2312).
     * 
     * @param input
     *            输入的字符串
     * @return 转换结果, 如果有错误发生, 则返回原来的值
     */
    public static String ISO2GBK(String input) {
        try {
            byte[] bytes = input.getBytes("ISO8859-1");
            return new String(bytes, "GBK");
        } catch (Exception ex) {
        }
        return input;
    }

    /**
     * 转换由表单读取的数据的内码到 ISO(从 GBK 转换到ISO8859-1).
     * 
     * @param input
     *            输入的字符串
     * @return 转换结果, 如果有错误发生, 则返回原来的值
     */
    public static String GBK2ISO(String input) {
        return changeEncoding(input, "GBK", "ISO8859-1");
    }

    /**
     * 转换字符串的内码.
     * 
     * @param input
     *            输入的字符串
     * @param sourceEncoding
     *            源字符集名称
     * @param targetEncoding
     *            目标字符集名称
     * @return 转换结果, 如果有错误发生, 则返回原来的值
     */
    public static String changeEncoding(String input, String sourceEncoding, String targetEncoding) {
        if (input == null || input.equals("")) {
            return input;
        }

        try {
            byte[] bytes = input.getBytes(sourceEncoding);
            return new String(bytes, targetEncoding);
        } catch (Exception ex) {
        }
        return input;
    }

    /**
     * 将单个的 ' 换成 ''; SQL 规则:如果单引号中的字符串包含一个嵌入的引号,可以使用两个单引号表示嵌入的单引号.
     */

    public static String replaceSql(String input) {
        return replace(input, "'", "''");
    }

    /**
     * 判断字符串是否未空, 如果为 null 或者长度为0, 均返回 true.
     */
    public static boolean isEmpty(String input) {
        if (input != null) {
            input = input.trim();
        }
        return (input == null || "".equals(input) || input.length() == 0);
    }

    /**
     * 获得输入字符串的字节长度(即二进制字节数), 用于发送短信时判断是否超出长度.
     * 
     * @param input
     *            输入字符串
     * @return 字符串的字节长度(不是 Unicode 长度)
     */
    public static int getBytesLength(String input) {
        if (input == null) {
            return 0;
        }

        int bytesLength = input.getBytes().length;
        return bytesLength;
    }

    /**
     * 检验字符串是否未空, 如果是, 则返回给定的出错信息.
     * 
     * @param input
     *            输入的字符串
     * @param errorMsg
     *            出错信息
     * @return 空串返回出错信息
     */
    public static String isEmpty(String input, String errorMsg) {
        if (isEmpty(input)) {
            return errorMsg;
        }
        return "";
    }

    /**
     * 判断是否为质数
     * 
     * @param x
     * @return
     */
    public static boolean isPrime(int x) {
        if (x <= 7) {
            if (x == 2 || x == 3 || x == 5 || x == 7)
                return true;
        }
        int c = 7;
        if (x % 2 == 0)
            return false;
        if (x % 3 == 0)
            return false;
        if (x % 5 == 0)
            return false;
        int end = (int) Math.sqrt(x);
        while (c <= end) {
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 6;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 6;
        }
        return true;
    }

    /**
     * 返回指定字节长度的字符串
     * 
     * @param str
     *            String 字符串
     * @param length
     *            int 指定长度
     * @return String 返回的字符串
     */
    public static String toLength(String str, int length) {
        if (str == null) {
            return null;
        }
        if (length <= 0) {
            return "";
        }
        try {
            if (str.getBytes("GBK").length <= length) {
                return str;
            }
        } catch (Exception ex) {
        }
        StringBuffer buff = new StringBuffer();

        int index = 0;
        char c;
        length -= 3;
        while (length > 0) {
            c = str.charAt(index);
            if (c < 128) {
                length--;
            } else {
                length--;
                length--;
            }
            buff.append(c);
            index++;
        }
        buff.append("...");
        return buff.toString();
    }

    /**
     * 判断是否为整数
     * 
     * @param str
     *            传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否为浮点数，包括double和float
     * 
     * @param str
     *            传入的字符串
     * @return 是浮点数返回true,否则返回false
     */
    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断输入的字符串是否符合Email样式.
     * 
     * @param str
     *            传入的字符串
     * @return 是Email样式返回true,否则返回false
     */
    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断输入的字符串是否为纯汉字
     * 
     * @param str
     *            传入的字符窜
     * @return 如果是纯汉字返回true,否则返回false
     */
    public static boolean isChinese(String str) {
        Pattern pattern = Pattern.compile("[\u0391-\uFFE5]+$");
        return pattern.matcher(str).matches();
    }

    /**
     * 转译特殊符号标签:
     * 
     * @param value
     * @return
     */
    public static String filter(String value) {
        if (value == null || value.length() == 0)
            return value;
        StringBuffer result = null;
        String filtered = null;
        for (int i = 0; i < value.length(); i++) {
            filtered = null;
            switch (value.charAt(i)) {
                case 60: // '&lt;'
                    filtered = "&lt;";
                    break;

                case 62: // '&gt;'
                    filtered = "&gt;";
                    break;

                case 38: // '&amp;'
                    filtered = "&amp;";
                    break;

                case 34: // '"'
                    filtered = "\"";
                    break;

                case 39: // '\''
                    filtered = "'";
                    break;
            }
            if (result == null) {
                if (filtered != null) {
                    result = new StringBuffer(value.length() + 50);
                    if (i > 0)
                        result.append(value.substring(0, i));
                    result.append(filtered);
                }
            } else if (filtered == null)
                result.append(value.charAt(i));
            else
                result.append(filtered);
        }

        return result != null ? result.toString() : value;
    }

    public static int strToInteger(String str, int defaultValue) {
        int re = defaultValue;
        try {
            re = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return re;
    }

    public static float strToFloat(String str, float defaultValue) {
        if (str == null) {
            return defaultValue;
        }

        float re = defaultValue;
        try {
            re = Float.parseFloat(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 检查字符串是否是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * 
     * <pre>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     * </pre>
     * 
     * @param str
     *            要检查的字符串
     * 
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查字符串是否是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * 
     * <pre>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     * </pre>
     * 
     * @param str
     *            要检查的字符串
     * 
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isBlankCollection(List<String> str) {

        if (str == null) {
            return true;
        }

        if (str.size() <= 0) {
            return true;
        }

        for (String inner : str) {
            if (!isBlank(inner)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 比较两个字符串（大小写不敏感）。
     * 
     * <pre>
     * StringUtil.equalsIgnoreCase(null, null)   = true
     * StringUtil.equalsIgnoreCase(null, "abc")  = false
     * StringUtil.equalsIgnoreCase("abc", null)  = false
     * StringUtil.equalsIgnoreCase("abc", "abc") = true
     * StringUtil.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     * 
     * @param str1
     *            要比较的字符串1
     * @param str2
     *            要比较的字符串2
     * 
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equalsIgnoreCase(str2);
    }

    /**
     * 转换String为int
     * 
     * <pre>
     * string2int("1234")   = 1234
     * string2int("abc")   = 0
     * </pre>
     * 
     * @param String
     * 
     * @return int
     */
    public static int string2int(String numbers) {
        try {
            if (isBlank(numbers)) {
                return 0;
            } else {
                return Integer.parseInt(numbers);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 转换String为long
     * 
     * <pre>
     * double2string(1.0123456789) = &quot;1.01234&quot;
     * </pre>
     * 
     * @param double
     * 
     * @return string
     */
    public static long string2long(String numbers) {
        try {
            if (isBlank(numbers)) {
                return 0L;
            } else {
                return Long.parseLong(numbers);
            }
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * MD5摘要
     * 
     * @param str
     * @return
     */
    public static String MD5(String str) {
        try {
            if (isBlank(str)) {
                return null;
            }
            MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(str.getBytes("UTF-8"));
            byte tmp[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(String.format("%02x", tmp[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(3);
            long result = 0;
            switch (number) {
                case 0:
                    result = Math.round(Math.random() * 25 + 65);
                    sb.append(String.valueOf((char) result));
                    break;
                case 1:
                    result = Math.round(Math.random() * 25 + 97);
                    sb.append(String.valueOf((char) result));
                    break;
                case 2:
                    sb.append(String.valueOf(new Random().nextInt(10)));
                    break;
            }
        }
        return sb.toString();
    }
}
