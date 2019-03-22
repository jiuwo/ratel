package org.jiuwo.ratel.util;

import java.lang.reflect.Type;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Steven Han
 */
@Slf4j
public class JsonUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private JsonUtil() {
    }

    public static <T> String serialize(T object) {
        JSON.DEFFAULT_DATE_FORMAT = DEFAULT_DATE_FORMAT;
        return JSON.toJSONString(object, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }

    public static <T> T deSerialize(String string, Class<T> tClass) {
        return JSON.parseObject(string, tClass);
    }

    public static <T> T deSerialize(String string, TypeReference<T> typeReference) {
        return JSON.parseObject(string, typeReference);

    }

    public static <T> List<T> deSerializeList(String string, Class<T> tClass) {
        return JSON.parseArray(string, tClass);
    }

    public static <T> List<T> deSerializeList(String string, Type[] types) {
        return (List<T>) JSON.parseArray(string, types);
    }

}
