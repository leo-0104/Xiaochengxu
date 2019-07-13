package com.demo.huyaxiaochengxu.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nowcoder on 2016/7/3.
 */
public class returnJsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(returnJsonUtil.class);

    public static String returnJson(int code,Object data){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("data",data);
        return jsonObject.toJSONString();
    }

}
