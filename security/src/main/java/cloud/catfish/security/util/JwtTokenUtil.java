package cloud.catfish.security.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * JwtToken生成的工具类
 * JWT token的格式：header.payload.signature
 * header的格式（算法、token的类型）：
 * {"alg": "HS512","typ": "JWT"}
 * payload的格式（用户名、创建时间、生成时间）：
 * {"sub":"wang","created":1489079981393,"exp":1489684781}
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 */
@Slf4j
public class JwtTokenUtil {
    private static final String CLAIM_KEY_SCOPE = "scope";
    private static final String CLAIM_KEY_SCP = "scp";
    private static final String CLAIM_KEY_CREATED = "created";
    private final Long expiration;
    private final String tokenHead;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtTokenUtil(cloud.catfish.security.config.SecurityProperties.JwtProperties properties, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.expiration = properties.expiration();
        this.tokenHead = properties.tokenHead();
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * 根据负责生成JWT的token
     */
    private String generateToken(String username, Long createdEpochMillis, List<String> scopes) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claimsSetBuilder = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .claim(CLAIM_KEY_CREATED, createdEpochMillis);
        if (scopes != null && !scopes.isEmpty()) {
            claimsSetBuilder.claim(CLAIM_KEY_SCOPE, String.join(" ", scopes));
        }
        JwtClaimsSet claimsSet = claimsSetBuilder.build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS512).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }

    /**
     * 从token中获取JWT中的负载
     */
    private Jwt getJwtFromToken(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            log.info("JWT格式验证失败:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 从token中获取登录用户名
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Jwt jwt = getJwtFromToken(token);
            username = jwt == null ? null : jwt.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 验证token是否还有效
     *
     * @param token       客户端传入的token
     * @param userDetails 从数据库中查询出来的用户信息
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        return StrUtil.isNotEmpty(username) && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断token是否已经失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        if (expiredDate == null) {
            return true;
        }
        return expiredDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Jwt jwt = getJwtFromToken(token);
        if (jwt == null || jwt.getExpiresAt() == null) {
            return null;
        }
        return Date.from(jwt.getExpiresAt());
    }

    /**
     * 根据用户信息生成token
     */
    public String generateToken(UserDetails userDetails) {
        List<String> scopes = userDetails.getAuthorities().stream()
                .map(item -> item == null ? null : item.getAuthority())
                .filter(StrUtil::isNotEmpty)
                .toList();
        return generateToken(userDetails.getUsername(), Instant.now().toEpochMilli(), scopes);
    }

    /**
     * 当原来的token没过期时是可以刷新的
     *
     * @param oldToken 带tokenHead的token
     */
    public String refreshHeadToken(String oldToken) {
        if (StrUtil.isEmpty(oldToken)) {
            return null;
        }
        if (!oldToken.startsWith(tokenHead)) {
            return null;
        }
        String token = oldToken.substring(tokenHead.length());
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        //token校验不通过
        Jwt jwt = getJwtFromToken(token);
        if (jwt == null) {
            return null;
        }
        //如果token已经过期，不支持刷新
        if (isTokenExpired(token)) {
            return null;
        }
        //如果token在30分钟之内刚刷新过，返回原token
        if (tokenRefreshJustBefore(token, 30 * 60)) {
            return token;
        } else {
            return generateToken(jwt.getSubject(), Instant.now().toEpochMilli(), extractScopes(jwt));
        }
    }

    /**
     * 判断token在指定时间内是否刚刚刷新过
     *
     * @param token 原token
     * @param time  指定时间（秒）
     */
    private boolean tokenRefreshJustBefore(String token, int time) {
        Jwt jwt = getJwtFromToken(token);
        if (jwt == null) {
            return false;
        }
        Date created = getCreatedDate(jwt);
        if (created == null) {
            return false;
        }
        Date refreshDate = new Date();
        //刷新时间在创建时间的指定时间内
        if (refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time))) {
            return true;
        }
        return false;
    }

    private Date getCreatedDate(Jwt jwt) {
        Object created = jwt.getClaims().get(CLAIM_KEY_CREATED);
        if (created instanceof Number createdNumber) {
            return new Date(createdNumber.longValue());
        }
        if (created instanceof String createdString) {
            try {
                return new Date(Long.parseLong(createdString));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private List<String> extractScopes(Jwt jwt) {
        Object raw = jwt.getClaims().get(CLAIM_KEY_SCOPE);
        if (raw == null) {
            raw = jwt.getClaims().get(CLAIM_KEY_SCP);
        }
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof Collection<?> collection) {
            List<String> result = new ArrayList<>(collection.size());
            for (Object item : collection) {
                if (item == null) {
                    continue;
                }
                String authority = item.toString();
                if (StrUtil.isNotEmpty(authority)) {
                    result.add(authority);
                }
            }
            return List.copyOf(result);
        }
        if (raw instanceof String rawString) {
            if (StrUtil.isEmpty(rawString)) {
                return List.of();
            }
            String[] parts = rawString.split("\\s+");
            List<String> result = new ArrayList<>(parts.length);
            for (String part : parts) {
                if (StrUtil.isNotEmpty(part)) {
                    result.add(part);
                }
            }
            return List.copyOf(result);
        }
        return List.of();
    }
}
