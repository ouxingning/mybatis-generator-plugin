package com.xnou.mybatis.generator.plugin;

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

/**
 * 生成批量插入记录的接口。
 * 
 * <p>
 * 此接口有个限制：每条待入库的记录的字段名称要一致，而且字段值不能为null， <br/>
 * 构建SQL语句时参考的是列表中第一个对象的值不为null的字段属性。<br/>
 * 否则会抛java.sql.SQLException: Column count doesn't match value count的异常。
 * 
 * @author OU Xingning
 * @date 2018/10/30
 */
public class BatchInsertSelectivePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {

        this.addBatchInsertSelectiveMethod(interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        this.addBatchInsertSelectiveXml(document, introspectedTable);
        return true;
    }

    /**
     * 增加 batchInsertSelective 方法。
     */
    private void addBatchInsertSelectiveMethod(Interface interfaze, IntrospectedTable introspectedTable) {

        FullyQualifiedJavaType genericType = introspectedTable.getRules().calculateAllFieldsClass();

        // 需要导入的类
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        importedTypes.add(genericType);

        Method method = new Method();
        // 1. 方法可见性
        method.setVisibility(JavaVisibility.PUBLIC);
        // 2. 返回值类型
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getIntInstance();
        method.setReturnType(returnType);
        // 3. 设置方法名
        method.setName("batchInsertSelective");
        // 4. 设置参数列表
        FullyQualifiedJavaType paramType = FullyQualifiedJavaType.getNewListInstance();

        paramType.addTypeArgument(genericType);
        method.addParameter(new Parameter(paramType, "records"));

        method.addJavaDocLine("/**");
        method.addJavaDocLine("* 批量插入数据库记录的接口。");
        method.addJavaDocLine("* <p>此接口有个限制：每条待入库的记录的字段名称要一致，而且字段值不能为null， <br/>");
        method.addJavaDocLine("* 构建SQL语句时参考的是列表中第一个对象的值不为null的字段属性。<br/>");
        method.addJavaDocLine("* 否则会抛java.sql.SQLException: Column count doesn't match value count的异常。");
        method.addJavaDocLine("*/");

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    /**
     * 增加 batchInsertSelective 节点。
     */
    private void addBatchInsertSelectiveXml(Document document, IntrospectedTable introspectedTable) {

        XmlElement batchInsertElement = new XmlElement("insert");
        batchInsertElement.addAttribute(new Attribute("id", "batchInsertSelective"));
        batchInsertElement.addAttribute(new Attribute("parameterType", "java.util.List"));

        context.getCommentGenerator().addComment(batchInsertElement);

        // field names
        XmlElement trimFieldsElement = new XmlElement("trim");
        trimFieldsElement.addAttribute(new Attribute("prefix", "("));
        trimFieldsElement.addAttribute(new Attribute("suffix", ")"));
        trimFieldsElement.addAttribute(new Attribute("suffixOverrides", ","));

        // field values
        XmlElement javaPropertyAndDatabaseType = new XmlElement("trim");
        javaPropertyAndDatabaseType.addAttribute(new Attribute("prefix", "("));
        javaPropertyAndDatabaseType.addAttribute(new Attribute("suffix", ")"));
        javaPropertyAndDatabaseType.addAttribute(new Attribute("suffixOverrides", ","));

        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : columns) {
            String javaProp = introspectedColumn.getJavaProperty();
            String columnName = introspectedColumn.getActualColumnName();
            if (!introspectedColumn.isAutoIncrement()) { // 不是自增字段才会加入到批量插入SQL中
                XmlElement fieldIfTest = new XmlElement("if");
                fieldIfTest.addAttribute(new Attribute("test", "list[0]." + javaProp + " != null"));
                fieldIfTest.addElement(new TextElement(columnName + ","));
                trimFieldsElement.addElement(fieldIfTest);

                XmlElement valueIfTest = new XmlElement("if");
                valueIfTest.addAttribute(new Attribute("test", "list[0]." + javaProp + " != null"));
                valueIfTest.addElement(new TextElement("#{item." + javaProp + ",jdbcType=" + introspectedColumn.getJdbcTypeName() + "},"));
                javaPropertyAndDatabaseType.addElement(valueIfTest);
            }
        }

        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("index", "index"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("separator", ","));

        batchInsertElement.addElement(new TextElement("insert into " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));
        batchInsertElement.addElement(trimFieldsElement);
        batchInsertElement.addElement(new TextElement(" values "));
        foreachElement.addElement(javaPropertyAndDatabaseType);
        batchInsertElement.addElement(foreachElement);

        document.getRootElement().addElement(batchInsertElement);
    }

}
