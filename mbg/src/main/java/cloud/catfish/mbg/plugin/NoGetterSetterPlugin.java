package cloud.catfish.mbg.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class NoGetterSetterPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 关闭实体类的 Setter 生成
     *
     * @param method             the setter, or mutator, method generated for the specified
     *                           column
     * @param topLevelClass      the partially implemented model class. You can add additional
     *                           imported classes to the implementation class if necessary.
     * @param introspectedColumn The class containing information about the column related
     *                           to this field as introspected from the database
     * @param introspectedTable  The class containing information about the table as
     *                           introspected from the database
     * @param modelClassType     the type of class that the field is generated for
     * @return
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     * 关闭实体类的 Getter 生成
     *
     * @param method             the getter, or accessor, method generated for the specified
     *                           column
     * @param topLevelClass      the partially implemented model class. You can add additional
     *                           imported classes to the implementation class if necessary.
     * @param introspectedColumn The class containing information about the column related
     *                           to this field as introspected from the database
     * @param introspectedTable  The class containing information about the table as
     *                           introspected from the database
     * @param modelClassType     the type of class that the field is generated for
     * @return
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }


}