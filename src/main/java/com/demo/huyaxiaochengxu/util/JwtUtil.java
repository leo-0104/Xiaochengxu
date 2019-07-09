package com.demo.huyaxiaochengxu.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.demo.huyaxiaochengxu.entity.AppInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    public static final String ALG = "HS256";
    public static final String TYP = "JWT";
    public static Map<String, Object> getJwtParamsMap(Map<String,Object> paramsMap){
        //获取时间戳（毫秒）
        long currentTimeMillis = System.currentTimeMillis();
        long expireTimeMillis = System.currentTimeMillis() + 10 * 60 * 1000;
        Date iat = new Date(currentTimeMillis);
        Date exp = new Date(expireTimeMillis);

        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            Map<String, Object> header = new HashMap<String, Object>();
            header.put("alg", ALG);
            header.put("typ", TYP);

            Algorithm algorithm = Algorithm.HMAC256(AppInfo.getAPPSECRET());
            String sToken = JWT.create()
                    .withHeader(header)
                    .withIssuedAt(iat)
                    .withExpiresAt(exp)
                    .withClaim("appId", AppInfo.getAPPID())
                    .sign(algorithm);


            Map<String, Object> authMap = new HashMap<String, Object>();
            authMap.put("iat", currentTimeMillis / 1000);
            authMap.put("exp", expireTimeMillis / 1000);
            authMap.put("sToken", sToken);
            authMap.put("appId",AppInfo.getAPPID());

            resultMap.putAll(authMap);
            resultMap.putAll(paramsMap);

            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
