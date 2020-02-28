package cust.aowei.jwtstudy.util;

import cust.aowei.jwtstudy.exception.CustomException;
import cust.aowei.jwtstudy.model.ResultCode;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
/**
 * @author aowei
 */
public class JwtUtils {
    // 签名私钥
    public static final String AUTH_HEADER_KEY = "Authorization";
    // 签名的失效时间
    private static final long TTL = 10000 ;

    private static Logger log = LoggerFactory.getLogger(JwtUtils.class);
    /**
     * 设置认证token
     * id:登录用户id
     * subject:登录用户名
     */

    public static String createJwt(String id, String name, Map<String,Object> map) throws CustomException {
        // 设置失效时间
        long now = System.currentTimeMillis(); // 当前毫秒数
        long exp = now + TTL;

        // 创建jwtBuilder
        String token = null;
        try {
            JwtBuilder jwtBuilder = Jwts.builder()
                    .setId(id) // 添加id
                    .setSubject(name) // 添加用户名
                    .setIssuedAt(new Date()) // 添加当前时间
                    .signWith(SignatureAlgorithm.HS256,AUTH_HEADER_KEY); // 设置加密算法，密钥

            // 根据map设置claims
            for (Map.Entry<String,Object> entry : map.entrySet()){
                jwtBuilder.claim(entry.getKey(),entry.getValue());
            }

            // 指定失效时间
            jwtBuilder.setExpiration(new Date(exp));

            // 创建token
            token = jwtBuilder.compact();
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new CustomException(ResultCode.PERMISSION_SIGNATURE_ERROR);
        }
        return token;
    }

    /**
     * 解析token字符串，获取clamis
     */
    public static Claims parseJwt(String token) throws CustomException {

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(AUTH_HEADER_KEY)
                    .parseClaimsJws(token).getBody();
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("===== Token过期 =====", e);
            throw new CustomException(ResultCode.PERMISSION_TOKEN_EXPIRED);
        } catch (Exception e) {
            log.error("===== token解析异常 =====", e);
            throw new CustomException(ResultCode.PERMISSION_TOKEN_INVALID);
        }
    }
}
