package cust.aowei.jwtstudy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;
import java.util.Map;
@Getter
@Setter
@ConfigurationProperties("jwt.config")
public class JwtUtils {
    // 签名私钥
    private String key;
    // 签名的失效时间
    private Long ttl;
    /**
     * 设置认证token
     * id:登录用户id
     * subject:登录用户名
     */

    public String createJwt(String id, String name, Map<String,Object> map){
        // 设置失效时间
        long now = System.currentTimeMillis(); // 当前毫秒数
        long exp = now + ttl;

        // 创建jwtBuilder
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(id) // 添加id
                .setSubject(name) // 添加用户名
                .setIssuedAt(new Date()) // 添加当前时间
                .signWith(SignatureAlgorithm.HS256,"aowei"); // 设置加密算法，密钥

        // 根据map设置claims
        for (Map.Entry<String,Object> entry : map.entrySet()){
            jwtBuilder.claim(entry.getKey(),entry.getValue());
        }

        // 指定失效时间
        jwtBuilder.setExpiration(new Date(exp));

        // 创建token
        String token = jwtBuilder.compact();
        return token;
    }

    /**
     * 解析token字符串，获取clamis
     */
    public Claims parseJwt(String token){
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token).getBody();
            return claims;
    }
}
