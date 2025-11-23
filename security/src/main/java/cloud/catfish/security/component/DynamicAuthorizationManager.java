package cloud.catfish.security.component;

import cloud.catfish.security.config.IgnoreUrlsConfig;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 动态鉴权管理器，用于判断是否有资源的访问权限
 * Created by macro on 2023/11/3.
 */
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Resource
    private DynamicSecurityMetadataSource securityDataSource;
    @Resource
    private IgnoreUrlsConfig ignoreUrlsConfig;


    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authentication, RequestAuthorizationContext object) {
        HttpServletRequest request = object.getRequest();
        String path = request.getRequestURI();
        PathMatcher pathMatcher = new AntPathMatcher();
        List<String> ignoreUrls = ignoreUrlsConfig.getUrls();
        for (String ignoreUrl : ignoreUrls) {
            if (pathMatcher.match(ignoreUrl, path)) {
                return new AuthorizationDecision(true);
            }
        }
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return new AuthorizationDecision(true);
        }
        if (path.startsWith("/ws")) {
            return new AuthorizationDecision(true);
        }
        List<String> needAuthorities = securityDataSource.getConfigAttributesWithPath(path);
        Authentication currentAuth = authentication.get();
        if (currentAuth != null && currentAuth.isAuthenticated()) {
            Collection<? extends GrantedAuthority> grantedAuthorities = currentAuth.getAuthorities();
            List<? extends GrantedAuthority> hasAuth = grantedAuthorities.stream()
                    .filter(item -> needAuthorities.contains(item.getAuthority()))
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(hasAuth)) {
                return new AuthorizationDecision(true);
            } else {
                return new AuthorizationDecision(false);
            }
        } else {
            return new AuthorizationDecision(false);
        }
    }
}
