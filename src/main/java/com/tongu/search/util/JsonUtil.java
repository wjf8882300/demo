package com.tongu.search.util;


import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Json 转化工具类
 *
 * @author ZhangZhiQiang
 * @ClassName: JsonUtil
 * @since 2020/5/19
 */
public class JsonUtil {

    /**
     * 转Json字符串
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        return object == null ? "" : JSON.toJSONString(object);
    }


    /**
     * 转json对象
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> tClass) {
        return JSON.parseObject(json, tClass);
    }


    /**
     * Json对象转map
     *
     * @param JsonStr
     * @return
     */
    public static Map jsonStrToMap(String JsonStr) {
        return (Map) com.alibaba.fastjson.JSON.parse(JsonStr);
    }


    /**
     * List对象转json字符串
     *
     * @param list
     * @return
     */
    public static String listToJsonStr(List list) {
        return com.alibaba.fastjson.JSON.toJSONString(list);
    }


    public static <T> List<T> asList(String json, Class<T> clazz) {
        if (json == null || "".equals(json)) {
            return new ArrayList<>();
        }
        try {
            return com.alibaba.fastjson.JSON.parseArray(json, clazz);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
