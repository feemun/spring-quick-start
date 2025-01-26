package cloud.catfish.mbg.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class LocalDateTimeDeserializablePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (isDateType(introspectedColumn)) {
            // 添加@JsonFormatter注解
            String json = "@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")";
            field.addAnnotation(json);
            // 确保导入了@JsonFormatter注解对应的包
            topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.format.annotation.DateTimeFormat"));
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    private boolean isDateType(IntrospectedColumn column) {
        String typeName = column.getJdbcTypeName();
        return "DATE".equalsIgnoreCase(typeName) || "TIME".equalsIgnoreCase(typeName) || "TIMESTAMP".equalsIgnoreCase(typeName);
    }
}
