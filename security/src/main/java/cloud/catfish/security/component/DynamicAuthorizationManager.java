package cloud.catfish.security.component;

import cloud.catfish.security.config.IgnoreUrlsConfig;
import cn.hutool.core.collection.CollUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.PathContainer;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 动态鉴权管理器，用于判断是否有资源的访问权限
 */
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final DynamicSecurityMetadataSource securityDataSource;
    private final IgnoreUrlsConfig ignoreUrlsConfig;
    private final List<PathPattern> ignorePatterns;

    public DynamicAuthorizationManager(DynamicSecurityMetadataSource securityDataSource, IgnoreUrlsConfig ignoreUrlsConfig) {
        this.securityDataSource = securityDataSource;
        this.ignoreUrlsConfig = ignoreUrlsConfig;
        this.ignorePatterns = ignoreUrlsConfig.urls().stream()
                .map(PathPatternParser.defaultInstance::parse)
                .toList();
    }


    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authentication, RequestAuthorizationContext object) {
        HttpServletRequest request = object.getRequest();
        String path = request.getRequestURI();
        PathContainer pathContainer = PathContainer.parsePath(path);
        for (PathPattern ignorePattern : ignorePatterns) {
            if (ignorePattern.matches(pathContainer)) {
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
