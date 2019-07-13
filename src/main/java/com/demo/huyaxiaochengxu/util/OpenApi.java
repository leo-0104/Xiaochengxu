package com.demo.huyaxiaochengxu.util;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OpenApi {

    public static String getLiveGiftInfoList() {

        String url = "https://open-apiext.huya.com/proxy/index";

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("do","getLiveGiftInfoList");
        Map<String,Object> authMap = JwtUtil.getJwtParamsMap(paramsMap);
        url = url + ParamsUtil.MapToUrlString(authMap);
        String result = HttpUtil.doGet(url);
        JSONObject jsonObject= JSONObject.parseObject(result);
        return jsonObject.toString();
    }
}
