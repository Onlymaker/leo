package com.onlymaker.leo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonTransfer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<HashMap<String, Object>> TYPE_REFERENCE = new TypeReference<HashMap<String, Object>>() {};
    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }
    public static String toJsonString(Object obj) {
        try {
            String jsonStr = OBJECT_MAPPER.writeValueAsString(obj);
            StringBuffer sb = new StringBuffer ();
            for(int i = 0; i < jsonStr.length(); i++) {
                char c = jsonStr.charAt(i);
                switch (c) {
                    case '\b':
                        sb.append("\\b");
                        break;
                    case '\f':
                        sb.append("\\f");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    default:
                        sb.append(c);
                }
            }
            return sb.toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] toJsonBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static <T> T toBean(String json, Class<T> targetClass) {
        try {
            return OBJECT_MAPPER.readValue(json, targetClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static <T> T toBean(byte[] json, Class<T> targetClass) {
        try {
            return OBJECT_MAPPER.readValue(json, targetClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Map<String, Object> toMap(String json) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = OBJECT_MAPPER.readValue(json, TYPE_REFERENCE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}