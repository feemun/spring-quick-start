package cloud.catfish.mbg;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;
import java.time.LocalDateTime;

public class CustomJavaTypeResolver extends JavaTypeResolverDefaultImpl {

    public CustomJavaTypeResolver() {
        super();
        // 添加或修改类型映射
//        typeMap.put(Types.TIME, new JdbcTypeInformation("TIME", //$NON-NLS-1$
//                new FullyQualifiedJavaType(LocalDateTime.class.getName())));
//        typeMap.put(Types.TIMESTAMP, new JdbcTypeInformation("TIMESTAMP", //$NON-NLS-1$
//                new FullyQualifiedJavaType(LocalDateTime.class.getName())));
//        typeMap.put(Types.DATE, new JdbcTypeInformation("TIMESTAMP", //$NON-NLS-1$
//                new FullyQualifiedJavaType(LocalDateTime.class.getName())));
//        // 你可以根据需要继续添加或修改其他类型映射
    }

    @Override
    public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn) {
        // 如果需要更多的自定义逻辑，可以在这里实现
        return super.calculateJavaType(introspectedColumn);
    }
}