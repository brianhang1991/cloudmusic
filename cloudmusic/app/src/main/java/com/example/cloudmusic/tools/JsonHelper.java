package com.example.cloudmusic.tools;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.cloudmusic.pojo.Music;

import java.util.List;

public class JsonHelper {

    public static JSONObject getJsonObjectFromString(String s){
        return JSONObject.from(s);
    }

    public static JSONObject getEmptyJsonObject() {
        return new JSONObject();
    }

    public static <T> List<T> getArrayFromString(String s, Class<T> t) {
        return JSONArray.parseArray(s, t);
    }
}
