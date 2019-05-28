/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class CommonUtilTest {

    @Test
    public void messageFormat() {
        String str = "你好，我叫{0}，我今年{1}岁了";
        str = CommonUtil.messageFormat(str, "小红", 8);

        Assert.equals(str, "你好，我叫小红，我今年8岁了", ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("message format success.");
    }

    @Test
    public void getRandomCode() throws IOException {
        Set<String> randomSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            String code = CommonUtil.getRandomCode();
            Assert.isFalse(randomSet.contains(code), ErrorCodeDef.SYSTEM_ERROR_10001);
            randomSet.add(code);
        }

        System.out.println("getRandom36Code success.");
    }

    @Test
    public void getTransactionID() {
        String str1 = CommonUtil.getTransactionID();
        String str2 = CommonUtil.getTransactionID();
        Assert.notEquals(str1, str2, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("生成了两个不一样的串码");
    }

    @Test
    public void getRandomNumber() {
        String str1 = CommonUtil.getRandomNumber(5);
        Assert.isTrue(str1.length() == 5, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("生成了一个长度为5的随机数字");
    }

    @Test
    public void getRandomChar() {
        String str1 = CommonUtil.getRandomChar(100);
        String str2 = CommonUtil.getRandomChar(100);
        Assert.isTrue(str1.length() == 100, ErrorCodeDef.SYSTEM_ERROR_10001);
        Assert.notEquals(str1, str2, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("生成了两个不一样的随机字符串");
    }

    @Test
    public void getString() {
        Object obj = new Object();
        String str1 = CommonUtil.getString(obj);
        Assert.equals(str1, obj.toString(), ErrorCodeDef.SYSTEM_ERROR_10001);

        System.out.println("CommonUtil.getString调用了Object的toString方法");

        obj = null;
        str1 = CommonUtil.getString(obj);
        Assert.isNull(str1, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("null对象的toString还是null");
    }

    @Test
    public void notNullStr() {
        String obj = null;
        String str1 = CommonUtil.notNullStr(obj);
        Assert.equals(str1, "", ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("null字符串的notNullStr得到的是空字符串");
    }

    @Test
    public void getDate() {
        long time = 10000;
        Date d = CommonUtil.getDate(time);
        Assert.equals(time, d.getTime(), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("将long类型转化为日期类型");

    }

    @Test
    public void splitId() {
        String idStr = "1,2,3,4";
        Integer[] ids = CommonUtil.splitId(idStr);
        Assert.equals(ids[2], 3, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("将逗号分割的数字转化为int[]");

        idStr = "1|2|3|4";
        ids = CommonUtil.splitId(idStr, GlobalConstants.VERTICAL_LINE);
        Assert.equals(ids[2], 3, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("将竖线分割的数字转化为int[]");
    }

    @Test
    public void splitIdsByLong() {
        String idStr = "1,2,3,4";
        Long[] ids = CommonUtil.splitIdsByLong(idStr, GlobalConstants.SPLITOR);
        Assert.equals(ids[2], 3l, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("将逗号分割的数字转化为long[]");
    }

    @Test
    public void match() {
        String a = "10,100, 110";
        String b = "10";

        Assert.isTrue(CommonUtil.match(a, b), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("字符串b在字符串a内可以找到");

        b = "1";
        Assert.isFalse(CommonUtil.match(a, b), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("字符串b在字符串a内不能找到");

        a = "NOT:10,100,110";
        Assert.isTrue(CommonUtil.match(a, b), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("字符串b不在字符串a内");
    }

    @Test
    public void removeAllSymbol() {
        String str1 = "你好!什么\"#$额%&'()*+,-./:;<=天呐>?@[\\]^_`{好吧|}~";
        String str2 = CommonUtil.removeAllSymbol(str1);
        Assert.equals(str2, "你好什么额天呐好吧", ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("字符串str1中\"!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\"这些符号都被去掉了");
    }

    @Test
    public void replaceAllBlank() {
        String str1 = "       你好呀\n       你在干什么\t\n";
        String str2 = CommonUtil.replaceAllBlank(str1);
        Assert.equals(str2, "你好呀你在干什么", ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("去掉字符串str1中的空格、换行、制表符号");
    }

    @Test
    public void replaceRedundantBlank() {
        String str1 = "       你好 呀\n       你在干什么\t\n";
        String str2 = CommonUtil.replaceRedundantBlank(str1);
        Assert.equals(str2, "你好 呀 你在干什么", ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("去掉字符串str1中的首尾空格，以及多余的空格、换行、制表符号");
    }

    @Test
    public void isNotEmpty() {
        String str = "hello world";
        Assert.isTrue(StringUtils.isNotEmpty(str), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("str 不是空字符串");

        List<String> strList = new ArrayList<>();
        strList.add(str);
        Assert.isTrue(CollectionUtils.isNotEmpty(strList), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("strList 不是空集合");

        String[] strs = new String[] {
            str
        };
        Assert.isTrue(CommonUtil.isNotEmpty(strs), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("strs 不是空数组");

        Map<String, String> strMap = new HashMap<>();
        strMap.put("a", str);
        Assert.isTrue(MapUtils.isNotEmpty(strMap), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("strMap 不是空Map");
    }

    @Test
    public void isEmpty() {

        String str = null;
        Assert.isTrue(StringUtils.isEmpty(str), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("str 是null字符串");

        str = "";
        Assert.isTrue(StringUtils.isEmpty(str), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("str 是空字符串");

        List<String> strList = null;
        Assert.isTrue(CollectionUtils.isEmpty(strList), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("strList 是null集合");

        strList = new ArrayList<>();
        Assert.isTrue(CollectionUtils.isEmpty(strList), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("strList 是空集合");

        String[] strs = null;
        Assert.isTrue(CommonUtil.isEmpty(strs), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("strs 是null数组");

        strs = new String[0];
        Assert.isTrue(CommonUtil.isEmpty(strs), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("strs 是空数组");

        Map<String, String> strMap = null;
        Assert.isTrue(MapUtils.isEmpty(strMap), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("strMap 是null Map");

        strMap = new HashMap<>();
        Assert.isTrue(MapUtils.isEmpty(strMap), ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("strMap 是空 Map");

    }
}
