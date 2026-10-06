package com.warehouse.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成Token
     */
    public String generateToken(Integer userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 从Token中获取Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) return null;
        Object userId = claims.get("userId");
        if (userId == null) return null;
        if (userId instanceof Integer) return (Integer) userId;
        if (userId instanceof Long) return ((Long) userId).intValue();
        if (userId instanceof Number) return ((Number) userId).intValue();
        try {
            return Integer.parseInt(userId.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 从Token中获取角色
     */
    public Integer getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) return null;
        Object role = claims.get("role");
        if (role == null) return null;
        if (role instanceof Integer) return (Integer) role;
        if (role instanceof Long) return ((Long) role).intValue();
        if (role instanceof Number) return ((Number) role).intValue();
        try {
            return Integer.parseInt(role.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 验证Token是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证Token
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}

