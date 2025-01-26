package cloud.catfish.mbg.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.kotlin.KotlinType;
import org.mybatis.generator.api.IntrospectedTable.TargetRuntime;

import java.util.List;
import java.util.Properties;

public class CustomSerializablePlugin extends PluginAdapter {

    private final FullyQualifiedJavaType serializable;
    private final FullyQualifiedJavaType gwtSerializable;
    private boolean addGWTInterface;
    private boolean suppressJavaInterface;

    public CustomSerializablePlugin() {
        super();
        serializable = new FullyQualifiedJavaType("java.io.Serializable"); //$NON-NLS-1$
        gwtSerializable = new FullyQualifiedJavaType("com.google.gwt.user.client.rpc.IsSerializable"); //$NON-NLS-1$
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        addGWTInterface = Boolean.parseBoolean(properties.getProperty("addGWTInterface")); //$NON-NLS-1$
        suppressJavaInterface = Boolean.parseBoolean(properties.getProperty("suppressJavaInterface")); //$NON-NLS-1$
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    protected void makeSerializable(TopLevelClass topLevelClass,
                                    IntrospectedTable introspectedTable) {
        if (addGWTInterface) {
            topLevelClass.addImportedType(gwtSerializable);
            topLevelClass.addSuperInterface(gwtSerializable);
        }

        if (!suppressJavaInterface) {
            topLevelClass.addImportedType(serializable);
            topLevelClass.addSuperInterface(serializable);

            // 添加java.io.Serial注解的导入
            FullyQualifiedJavaType serialAnnotation = new FullyQualifiedJavaType("java.io.Serial"); //$NON-NLS-1$
            topLevelClass.addImportedType(serialAnnotation);

            Field field = new Field("serialVersionUID", //$NON-NLS-1$
                    new FullyQualifiedJavaType("long")); //$NON-NLS-1$
            field.setFinal(true);
            field.setInitializationString("1L"); //$NON-NLS-1$
            field.setStatic(true);
            field.setVisibility(JavaVisibility.PRIVATE);

            // 给serialVersionUID字段添加@Serial注解
            field.addAnnotation("@Serial"); //$NON-NLS-1$

            if (introspectedTable.getTargetRuntime() == TargetRuntime.MYBATIS3_DSQL) {
                context.getCommentGenerator().addFieldAnnotation(field, introspectedTable,
                        topLevelClass.getImportedTypes());
            } else {
                context.getCommentGenerator().addFieldComment(field, introspectedTable);
            }

            topLevelClass.addField(field);
        }
    }

    @Override
    public boolean kotlinDataClassGenerated(KotlinFile kotlinFile, KotlinType dataClass,
                                            IntrospectedTable introspectedTable) {
        kotlinFile.addImport("java.io.Serializable"); //$NON-NLS-1$
        dataClass.addSuperType("Serializable"); //$NON-NLS-1$
        return true;
    }
}
