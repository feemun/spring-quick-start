package cloud.catfish.es.proxy;

import cloud.catfish.es.annotation.*;
import cloud.catfish.es.template.EsDslTemplate;
import cloud.catfish.es.template.EsSqlTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Elasticsearch Mapper动态代理工厂
 * 负责创建Mapper接口的代理实例，处理注解解析和查询执行
 */
@Slf4j
public class EsMapperProxyFactory {

    private final EsDslTemplate esDslTemplate;
    private final EsSqlTemplate esSqlTemplate;
    private final ObjectMapper objectMapper;
    
    // 参数占位符模式
    private static final Pattern PARAM_PATTERN = Pattern.compile("#\\{([^}]+)\\}");
    private static final Pattern SQL_PARAM_PATTERN = Pattern.compile("\\?");

    public EsMapperProxyFactory(EsDslTemplate esDslTemplate, 
                               EsSqlTemplate esSqlTemplate, 
                               ObjectMapper objectMapper) {
        this.esDslTemplate = esDslTemplate;
        this.esSqlTemplate = esSqlTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建Mapper接口的代理实例
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> mapperInterface) {
        return (T) Proxy.newProxyInstance(
            mapperInterface.getClassLoader(),
            new Class[]{mapperInterface},
            new MapperInvocationHandler(mapperInterface)
        );
    }

    /**
     * Mapper方法调用处理器
     */
    private class MapperInvocationHandler implements InvocationHandler {
        
        private final Class<?> mapperInterface;
        private final String defaultIndex;

        public MapperInvocationHandler(Class<?> mapperInterface) {
            this.mapperInterface = mapperInterface;
            EsMapper esMapper = mapperInterface.getAnnotation(EsMapper.class);
            this.defaultIndex = esMapper != null ? esMapper.defaultIndex() : "";
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 处理Object类的方法
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }
            
            // 处理默认方法
            if (method.isDefault()) {
                return invokeDefaultMethod(proxy, method, args);
            }

            // 获取方法参数信息
            Map<String, Object> paramMap = buildParamMap(method, args);
            
            // 处理EsSQL注解
            EsSQL esSQL = method.getAnnotation(EsSQL.class);
            if (esSQL != null) {
                return handleEsSQLMethod(method, esSQL, paramMap);
            }

            // 处理EsDSL注解
            EsDSL esDSL = method.getAnnotation(EsDSL.class);
            if (esDSL != null) {
                return handleEsDSLMethod(method, esDSL, paramMap);
            }

            throw new UnsupportedOperationException("Method " + method.getName() + " is not annotated with @EsSQL or @EsDSL");
        }
        
        /**
         * 调用默认方法
         */
        private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);
            
            Class<?> declaringClass = method.getDeclaringClass();
            int allModes = MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE
                | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE;
            
            return constructor.newInstance(declaringClass, allModes)
                .unreflectSpecial(method, declaringClass)
                .bindTo(proxy)
                .invokeWithArguments(args);
        }

        /**
         * 构建参数映射
         */
        private Map<String, Object> buildParamMap(Method method, Object[] args) {
            Map<String, Object> paramMap = new HashMap<>();
            Parameter[] parameters = method.getParameters();
            
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Param paramAnnotation = parameter.getAnnotation(Param.class);
                
                String paramName;
                if (paramAnnotation != null) {
                    paramName = paramAnnotation.value();
                } else {
                    paramName = parameter.getName();
                }
                
                paramMap.put(paramName, args[i]);
            }
            
            return paramMap;
        }

        /**
         * 处理EsSQL注解的方法
         */
        private Object handleEsSQLMethod(Method method, EsSQL esSQL, Map<String, Object> paramMap) throws Exception {
            String sql = esSQL.value();
            String index = resolveIndex(esSQL.index(), paramMap);
            
            // 替换参数占位符
            sql = replaceNamedParameters(sql, paramMap);
            List<Object> sqlParams = extractSqlParameters(sql, paramMap);
            
            log.debug("Executing SQL: {} on index: {} with params: {}", sql, index, sqlParams);
            
            // 根据返回类型和查询类型执行相应的操作
            Class<?> returnType = method.getReturnType();
            
            if (esSQL.pageable()) {
                int from = (Integer) paramMap.getOrDefault("from", esSQL.defaultFrom());
                int size = (Integer) paramMap.getOrDefault("size", esSQL.defaultPageSize());
                return esSqlTemplate.queryForPage(index, sql, sqlParams, from, size);
            }
            
            if (returnType == long.class || returnType == Long.class) {
                return esSqlTemplate.queryForCount(index, sql, sqlParams);
            }
            
            if (returnType == boolean.class || returnType == Boolean.class) {
                long count = esSqlTemplate.queryForCount(index, sql, sqlParams);
                return count > 0;
            }
            
            if (List.class.isAssignableFrom(returnType)) {
                return esSqlTemplate.queryForList(index, sql, sqlParams, getGenericType(method));
            }
            
            return esSqlTemplate.queryForObject(index, sql, sqlParams, returnType);
        }

        /**
         * 处理EsDSL注解的方法
         */
        private Object handleEsDSLMethod(Method method, EsDSL esDSL, Map<String, Object> paramMap) throws Exception {
            String dslJson = getDslJson(esDSL, paramMap);
            String index = resolveIndex(esDSL.index(), paramMap);
            
            log.debug("Executing DSL: {} on index: {}", dslJson, index);
            
            // 根据查询类型执行相应的操作
            Class<?> returnType = method.getReturnType();
            
            switch (esDSL.type()) {
                case COUNT:
                    return esDslTemplate.count(index, dslJson);
                    
                case AGGREGATION:
                    Map<String, Object> aggResult = esDslTemplate.aggregation(index, dslJson);
                    if (returnType == Map.class) {
                        return aggResult;
                    }
                    return objectMapper.convertValue(aggResult, returnType);
                    
                case BULK:
                    // 批量操作需要特殊处理
                    throw new UnsupportedOperationException("Bulk operations not yet implemented");
                    
                case SEARCH:
                default:
                    if (esDSL.pageable()) {
                        int from = (Integer) paramMap.getOrDefault("from", esDSL.defaultFrom());
                        int size = (Integer) paramMap.getOrDefault("size", esDSL.defaultPageSize());
                        return esDslTemplate.searchWithPagination(index, dslJson, from, size, getGenericType(method));
                    }
                    
                    if (List.class.isAssignableFrom(returnType)) {
                        return esDslTemplate.search(index, dslJson, getGenericType(method));
                    }
                    
                    List<?> results = esDslTemplate.search(index, dslJson, returnType);
                    return results.isEmpty() ? null : results.get(0);
            }
        }

        /**
         * 获取DSL JSON字符串
         */
        private String getDslJson(EsDSL esDSL, Map<String, Object> paramMap) throws IOException {
            String dslJson;
            
            if (StringUtils.hasText(esDSL.value())) {
                dslJson = esDSL.value();
            } else if (StringUtils.hasText(esDSL.file())) {
                // 从文件读取DSL
                ClassPathResource resource = new ClassPathResource(esDSL.file());
                dslJson = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } else {
                throw new IllegalArgumentException("Either value or file must be specified in @EsDSL");
            }
            
            // 替换参数占位符
            return replaceNamedParameters(dslJson, paramMap);
        }

        /**
         * 解析索引名称
         * 优先级：方法注解 > 类注解 > 默认值
         */
        private String resolveIndexName(Method method, Class<?> mapperInterface) {
            // 1. 检查方法上的@EsSQL或@EsDSL注解
            EsSQL esSQL = method.getAnnotation(EsSQL.class);
            if (esSQL != null && !esSQL.index().isEmpty()) {
                return esSQL.index();
            }
            
            EsDSL esDSL = method.getAnnotation(EsDSL.class);
            if (esDSL != null && !esDSL.index().isEmpty()) {
                return esDSL.index();
            }
            
            // 2. 检查类上的@EsMapper注解
            EsMapper esMapper = mapperInterface.getAnnotation(EsMapper.class);
            if (esMapper != null && !esMapper.defaultIndex().isEmpty()) {
                return esMapper.defaultIndex();
            }
            
            // 3. 默认使用类名（去掉Mapper后缀）
            String className = mapperInterface.getSimpleName();
            if (className.endsWith("Mapper")) {
                className = className.substring(0, className.length() - 6);
            }
            return className.toLowerCase();
        }

        /**
         * 解析索引名称
         */
        private String resolveIndex(String indexExpression, Map<String, Object> paramMap) {
            if (!StringUtils.hasText(indexExpression)) {
                return defaultIndex;
            }
            return replaceNamedParameters(indexExpression, paramMap);
        }

        /**
         * 替换命名参数占位符
         */
        private String replaceNamedParameters(String template, Map<String, Object> paramMap) {
            Matcher matcher = PARAM_PATTERN.matcher(template);
            StringBuffer sb = new StringBuffer();
            
            while (matcher.find()) {
                String paramName = matcher.group(1);
                Object paramValue = paramMap.get(paramName);
                
                if (paramValue == null) {
                    throw new IllegalArgumentException("Parameter '" + paramName + "' not found");
                }
                
                String replacement;
                if (paramValue instanceof String) {
                    replacement = (String) paramValue;
                } else {
                    try {
                        replacement = objectMapper.writeValueAsString(paramValue);
                        // 如果是简单值，去掉引号
                        if (replacement.startsWith("\"") && replacement.endsWith("\"")) {
                            replacement = replacement.substring(1, replacement.length() - 1);
                        }
                    } catch (Exception e) {
                        replacement = paramValue.toString();
                    }
                }
                
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);
            
            return sb.toString();
        }

        /**
         * 提取SQL参数（用于?占位符）
         */
        private List<Object> extractSqlParameters(String sql, Map<String, Object> paramMap) {
            List<Object> params = new ArrayList<>();
            Matcher matcher = SQL_PARAM_PATTERN.matcher(sql);
            
            // 按顺序提取参数值
            for (Object value : paramMap.values()) {
                if (matcher.find()) {
                    params.add(value);
                }
            }
            
            return params;
        }

        /**
         * 获取泛型类型
         */
        private Class<?> getGenericType(Method method) {
            Type returnType = method.getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) returnType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    Type actualType = actualTypeArguments[0];
                    if (actualType instanceof Class) {
                        return (Class<?>) actualType;
                    }
                }
            }
            return Object.class;
        }
    }
}