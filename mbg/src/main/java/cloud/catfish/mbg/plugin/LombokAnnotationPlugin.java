package cloud.catfish.mbg.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class LombokAnnotationPlugin extends PluginAdapter {

    private static final String LOMBOK_MODEL_DATA_FULL_CLASS_NAME = "lombok.Data";

    @Override
    public boolean validate(List<String> warnings) {
        // 插件验证逻辑，通常返回true表示插件启用
        return true;
    }

    /**
     * 当基础记录类被生成时调用此方法。
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addLombokAnnotations(topLevelClass);
        return true;
    }

    /**
     * 在生成的顶级类上添加Lombok注解。
     */
    private void addLombokAnnotations(TopLevelClass topLevelClass) {
        // 添加 @ToString 注解
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.ToString"));
        topLevelClass.addAnnotation("@ToString");

        // 根据需要，你可以在这里添加更多的Lombok注解，比如：
        // 添加 @EqualsAndHashCode 注解
         topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.EqualsAndHashCode"));
         topLevelClass.addAnnotation("@EqualsAndHashCode");

        // 添加 @Getter 注解
         topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.Getter"));
         topLevelClass.addAnnotation("@Getter");

        // 添加 @Setter 注解
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.Setter"));
        topLevelClass.addAnnotation("@Setter");

        // 添加 @AllArgsConstructor 注解
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.AllArgsConstructor"));
        topLevelClass.addAnnotation("@AllArgsConstructor");

        // 添加 @NoArgsConstructor 注解
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.NoArgsConstructor"));
        topLevelClass.addAnnotation("@NoArgsConstructor");

        // 添加 @Builder 注解
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.Builder"));
        topLevelClass.addAnnotation("@Builder");
    }
}