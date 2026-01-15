package cloud.catfish.security.component;

import jakarta.annotation.PostConstruct;
import org.springframework.http.server.PathContainer;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.*;

/**
 * 动态权限数据源，用于获取动态权限规则
 */
public class DynamicSecurityMetadataSource {

    private final DynamicSecurityService dynamicSecurityService;
    private final PathPatternParser pathPatternParser = PathPatternParser.defaultInstance;
    private volatile List<Rule> rules;

    public DynamicSecurityMetadataSource(DynamicSecurityService dynamicSecurityService) {
        this.dynamicSecurityService = dynamicSecurityService;
    }

    @PostConstruct
    public void loadDataSource() {
        Map<String, String> raw = dynamicSecurityService.loadDataSource();
        if (CollectionUtils.isEmpty(raw)) {
            this.rules = List.of();
            return;
        }
        List<Rule> compiled = new ArrayList<>(raw.size());
        for (Map.Entry<String, String> entry : raw.entrySet()) {
            compiled.add(new Rule(pathPatternParser.parse(entry.getKey()), entry.getValue()));
        }
        this.rules = List.copyOf(compiled);
    }

    public void clearDataSource() {
        this.rules = null;
    }


    //根据当前访问的路径获取对应权限
    public List<String> getConfigAttributesWithPath(String path) {
        if (this.rules == null) {
            this.loadDataSource();
        }
        List<String> configAttributes = new ArrayList<>();
        PathContainer pathContainer = PathContainer.parsePath(path);
        for (Rule rule : this.rules) {
            if (rule.pattern().matches(pathContainer)) {
                configAttributes.add(rule.attribute());
            }
        }
        // 未设置操作请求权限，返回空集合
        return configAttributes;
    }

    private record Rule(PathPattern pattern, String attribute) {
    }

}
