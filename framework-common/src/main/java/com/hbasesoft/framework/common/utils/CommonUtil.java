/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtil {

    /**
     * 消息格式化
     * 
     * @param message message <br>
     * @param params params <br>
     * @return String <br>
     */
    public static String messageFormat(String message, Object... params) {
        return (params == null || params.length == 0) ? message : MessageFormat.format(message, params);
    }

    /**
     * 获取事务ID
     * 
     * @return 事务ID <br>
     */
    public static String getTransactionID() {
        return UUID.randomUUID().toString().replace("-", GlobalConstants.BLANK);
    }

    /**
     * Description: 根据UUID的hash code生成随机码<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String getRandomCode() {
        return Long.toUnsignedString(Long.parseUnsignedLong(UUID.randomUUID().toString().substring(24), 16), 36);
    }

    /**
     * Description: 获取指定位数的随机数<br>
     * 
     * @author 王伟 <br>
     * @param size <br>
     * @return <br>
     */
    public static String getRandomNumber(int size) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            sb.append((char) ('0' + random.nextInt(10)));
        }
        return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param size <br>
     * @return <br>
     */
    public static String getRandomChar(int size) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            switch (random.nextInt(10) % 3) {
                case 0:
                    sb.append((char) ('0' + random.nextInt(10)));
                    break;
                case 1:
                    sb.append((char) ('a' + random.nextInt(26)));
                    break;
                case 2:
                    sb.append((char) ('A' + random.nextInt(26)));
                    break;
                default:
                    ;
            }
        }
        return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj <br>
     * @return <br>
     */
    public static String getString(Object obj) {
        String result = null;
        if (obj != null) {
            result = obj instanceof String ? (String) obj : obj.toString();
        }
        return result;
    }

    public static String notNullStr(String str) {
        return StringUtils.isEmpty(str) ? GlobalConstants.BLANK : str;
    }

    /**
     * Description:getDate <br>
     * 
     * @author 王伟 <br>
     * @param time <br
     * @return <br>
     */
    public static Date getDate(Long time) {
        if (time != null) {
            return new Date(time);
        }
        return null;
    }

    /**
     * Description: 从拼接的字符串中获取id数组<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idStr
     * @param splitor
     * @return <br>
     */
    public static final Integer[] splitId(String idStr, String splitor) {
        Integer[] ids = null;
        if (StringUtils.isNotEmpty(idStr)) {
            String[] strs = StringUtils.split(idStr, splitor);
            ids = new Integer[strs.length];
            for (int i = 0; i < strs.length; i++) {
                ids[i] = Integer.valueOf(strs[i]);
            }
        }
        return ids;
    }

    public static final Long[] splitIdsByLong(String idStr, String splitor) {
        Long[] ids = null;
        if (StringUtils.isNotEmpty(idStr)) {
            String[] strs = StringUtils.split(idStr, splitor);
            ids = new Long[strs.length];
            for (int i = 0; i < strs.length; i++) {
                ids[i] = Long.valueOf(strs[i]);
            }
        }
        return ids;
    }

    /**
     * Description: 从拼接的字符串中获取id数组<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idStr
     * @return <br>
     */
    public static final Integer[] splitId(String idStr) {
        return splitId(idStr, GlobalConstants.SPLITOR);
    }

    /**
     * Description: 多个通配符匹配<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param rules rule 规则 带正则表达式的
     * @param matchValue
     * @return <br>
     */
    public static boolean wildcardMatch(Collection<String> rules, String matchValue) {
        for (String rule : rules) {
            if (wildcardMatch(rule, matchValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通配符匹配
     *
     * @param rule 规则 带正则表达式的
     * @param matchValue 要匹配的值
     * @return
     */
    public static boolean wildcardMatch(String rule, String matchValue) {
        rule = org.apache.commons.lang3.StringUtils
            .replaceEach(org.apache.commons.lang3.StringUtils.replaceEach(rule, new String[] {
                "\\", ".", "^", "$"
            }, new String[] {
                "\\\\", "\\.", "\\^", "\\$"
            }), new String[] {
                "*", "?"
            }, new String[] {
                ".*", ".?"
            });
        Pattern pp = Pattern.compile(rule);
        Matcher m = pp.matcher(matchValue);
        return m.matches();
    }

    /**
     * Description: 匹配配置项中是否含有matchValue<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     * @param matchValue <br>
     * @return <br>
     */
    public static boolean match(String rule, String matchValue) {
        boolean ismatch = false;
        if (StringUtils.isNotEmpty(rule) && StringUtils.isNotEmpty(matchValue)) {
            // match all
            if (GlobalConstants.ASTERISK.equals(rule)) {
                ismatch = true;
            }
            // not eq match
            else if (rule.startsWith("NOT:")) {
                rule = rule.substring(4);
                ismatch = rule.indexOf(GlobalConstants.SPLITOR) == -1 ? !rule.equals(matchValue)
                    : new StringBuilder().append(GlobalConstants.SPLITOR).append(rule).append(GlobalConstants.SPLITOR)
                        .indexOf(new StringBuilder().append(GlobalConstants.SPLITOR).append(matchValue)
                            .append(GlobalConstants.SPLITOR).toString()) == -1;
            }
            // eq match
            else {
                ismatch = rule.indexOf(GlobalConstants.SPLITOR) == -1 ? rule.equals(matchValue)
                    : new StringBuilder().append(GlobalConstants.SPLITOR).append(rule).append(GlobalConstants.SPLITOR)
                        .indexOf(new StringBuilder().append(GlobalConstants.SPLITOR).append(matchValue)
                            .append(GlobalConstants.SPLITOR).toString()) != -1;
            }
        }
        return ismatch;
    }

    /**
     * Description: 除去字符串中的所有符号<br>
     * 
     * @author 何佳文<br>
     * @taskId <br>
     * @param str
     * @return <br>
     */
    public static String removeAllSymbol(String str) {
        if (StringUtils.isNotEmpty(str)) {
            return str.replaceAll("[\\pP\\p{Punct}]", "");
        }
        return "";
    }

    /**
     * Description: 去除所有的空格和制表符、换行符<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @return <br>
     */
    public static String replaceAllBlank(String str) {
        String dest = GlobalConstants.BLANK;
        if (StringUtils.isNotEmpty(str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(GlobalConstants.BLANK);
        }
        return dest;
    }

    /**
     * Description: 替换多余的制表符、换行符，只保留一个<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @return <br>
     */
    public static String replaceRedundantBlank(String str) {
        String dest = GlobalConstants.BLANK;
        if (StringUtils.isNotEmpty(str)) {
            Pattern p = Pattern.compile("\\s{1,}|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(" ");
        }
        return StringUtils.trim(dest);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param paramTypes
     * @return <br>
     */
    public static <T> boolean isNotEmpty(T[] paramTypes) {
        return !ArrayUtils.isEmpty(paramTypes);
    }

    public static <T> boolean isEmpty(T[] paramTypes) {
        return ArrayUtils.isEmpty(paramTypes);
    }
}
