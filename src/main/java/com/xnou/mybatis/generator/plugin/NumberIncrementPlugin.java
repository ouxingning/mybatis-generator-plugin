package com.xnou.mybatis.generator.plugin;

import java.sql.Types;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * 对于一些数值类型的字段，可以实现 update table set filed1 = field1 + incrBy1, field2 = field2 + incrBy2 where ...的处理。
 * 
 * @author OU Xingning
 * @date 2018/11/05
 */
public class NumberIncrementPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {

        if (this.needToGenerate(introspectedTable)) {
            this.addIncrByExampleSelectiveMethod(interfaze, introspectedTable);
        }

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        if (this.needToGenerate(introspectedTable)) {
            this.addIncrByExampleSelectiveXml(document, introspectedTable);
        }

        return true;
    }

    /**
     * 判断是否需要生成代码，对于一个表里的所有字段都是联合主键的情况，或者没有数值型字段的表，不需要创建。
     */
    protected boolean needToGenerate(IntrospectedTable introspectedTable) {
        boolean isNeed = false;
        List<IntrospectedColumn> normalColumns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        for (IntrospectedColumn introspectedColumn : normalColumns) {
            if (isNumberColumn(introspectedColumn.getJdbcType())) {
                isNeed = true;
                break;
            }
        }
        return isNeed;
    }

    /**
     * 增加 incrByExampleSelective 方法。
     */
    private void addIncrByExampleSelectiveMethod(Interface interfaze, IntrospectedTable introspectedTable) {

        Method method = new Method();
        // 1. 方法可见性
        method.setVisibility(JavaVisibility.PUBLIC);
        // 2. 返回值类型
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        // 3. 设置方法名
        method.setName("incrByExampleSelective");
        // 4. 设置参数列表
        FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        method.addParameter(new Parameter(parameterType, "record", "@Param(\"record\")"));

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        importedTypes.add(parameterType);

        FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        method.addParameter(new Parameter(exampleType, "example", "@Param(\"example\")"));
        importedTypes.add(exampleType);

        importedTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));

        method.addJavaDocLine("/**");
        method.addJavaDocLine("* 此方法用于对数值类型的字段进行修改，语法原型为：");
        method.addJavaDocLine("* <p>update table set num_field1 = num_field1 + value1, str_field2 = value2 where ...");
        method.addJavaDocLine("*/");

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    /**
     * 增加 incrByExampleSelective 节点。
     */
    private void addIncrByExampleSelectiveXml(Document document, IntrospectedTable introspectedTable) {

        XmlElement updateElement = new XmlElement("update");
        updateElement.addAttribute(new Attribute("id", "incrByExampleSelective"));
        updateElement.addAttribute(new Attribute("parameterType", "map"));

        context.getCommentGenerator().addComment(updateElement);

        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        updateElement.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set");
        updateElement.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns())) {
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty("record."));
            sb.append(" != null");
            XmlElement isNotNullElement = new XmlElement("if");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");

            if (isNumberColumn(introspectedColumn.getJdbcType())) {
                sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
                sb.append(" + ");
            }

            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "record."));
            sb.append(',');

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        updateElement.addElement(getUpdateByExampleIncludeElement(introspectedTable));

        document.getRootElement().addElement(updateElement);
    }

    protected XmlElement getUpdateByExampleIncludeElement(IntrospectedTable introspectedTable) {
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "_parameter != null"));

        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", introspectedTable.getMyBatis3UpdateByExampleWhereClauseId()));
        ifElement.addElement(includeElement);

        return ifElement;
    }

    private boolean isNumberColumn(int jdbcType) {
        boolean result = false;
        switch (jdbcType) {
            case Types.BIT:
                result = true;
                break;
            case Types.TINYINT:
                result = true;
                break;
            case Types.SMALLINT:
                result = true;
                break;
            case Types.INTEGER:
                result = true;
                break;
            case Types.BIGINT:
                result = true;
                break;
            case Types.FLOAT:
                result = true;
                break;
            case Types.REAL:
                result = true;
                break;
            case Types.DOUBLE:
                result = true;
                break;
            case Types.NUMERIC:
                result = true;
                break;
            case Types.DECIMAL:
                result = true;
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

}
