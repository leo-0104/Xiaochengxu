package com.demo.huyaxiaochengxu.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.demo.huyaxiaochengxu.entity.AppInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    public static final String ALG = "HS256";
    public static final String TYP = "JWT";
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
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

//            Algorithm algorithm = Algorithm.HMAC256(AppInfo.getAPPSECRET());
//            String sToken = JWT.create()
//                    .withHeader(header)
//                    .withIssuedAt(iat)
//                    .withExpiresAt(exp)
//                    .withClaim("appId", AppInfo.getAPPID())
//                    .sign(algorithm);
            String sToken =  Jwts.builder()
                    .setHeader(header)
                    .setIssuedAt(iat)
                    .setExpiration(exp)
                    .claim("appId", AppInfo.getAPPID())
                    .signWith(SignatureAlgorithm.HS256,AppInfo.getAPPSECRET())
                    .compact();

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
     //解密 JWT token
    public static Claims decryptByToken(String token){
        logger.info("decryptByToken  token---" + token);
        Claims claims = null;
        try{
            //得到DefaultJwtParser
            claims = Jwts.parser()
                    //设置签名的密钥
                    .setSigningKey(AppInfo.getAPPSECRET())
                    //设置需要解析的jwt
                    .parseClaimsJws(token).getBody();
        }catch (Exception e){
            logger.error("decryptByToken error,message => " + e.getMessage());
            return null;
        }
        return claims;
    }
}
