package com.xnou.mybatis.generator.plugin;

import java.util.List;

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
import org.mybatis.generator.config.GeneratedKey;

/**
 * 更新或插入记录的接口，在插入记录时，如果数据库表中已存在唯一索引的记录，则更新记录的数据。
 * 
 * <p>
 * 使用MySQL的 insert into ... on duplicate key update 语法。
 * 
 * @author OU Xingning
 * @date 2018/11/05
 */
public class UpsertSelectivePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {

        if (this.needToGenerate(introspectedTable)) {
            this.addUpsertSelectiveMethod(interfaze, introspectedTable);
        }

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        if (this.needToGenerate(introspectedTable)) {
            this.addUpsertSelectiveXml(document, introspectedTable);
        }

        return true;
    }

    /**
     * 判断是否需要生成代码，对于一个表里的所有字段都是联合主键的情况，不需要创建。
     */
    private boolean needToGenerate(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> normalColumns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        return (normalColumns.size() > 0);
    }

    /**
     * 增加 upsertSelective 方法。
     */
    private void addUpsertSelectiveMethod(Interface interfaze, IntrospectedTable introspectedTable) {

        Method method = new Method();
        // 1. 方法可见性
        method.setVisibility(JavaVisibility.PUBLIC);
        // 2. 返回值类型
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getIntInstance();
        method.setReturnType(returnType);
        // 3. 设置方法名
        method.setName("upsertSelective");
        // 4. 设置参数列表
        FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        method.addParameter(new Parameter(parameterType, "record"));

        method.addJavaDocLine("/**");
        method.addJavaDocLine("* 更新或插入记录的接口，在插入记录时，如果数据库表中已存在唯一索引的记录，则更新记录的数据。");
        method.addJavaDocLine("* <p>MySQL语法原型：insert into table ... on duplicate key update ...");
        method.addJavaDocLine("*/");

        interfaze.addMethod(method);
    }

    /**
     * 增加 upsertSelective 节点。
     */
    private void addUpsertSelectiveXml(Document document, IntrospectedTable introspectedTable) {

        XmlElement insertElement = new XmlElement("insert");

        insertElement.addAttribute(new Attribute("id", "upsertSelective"));

        FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        insertElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));

        context.getCommentGenerator().addComment(insertElement);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn introspectedColumn = introspectedTable.getColumn(gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null) {
                if (gk.isJdbcStandard()) {
                    insertElement.addAttribute(new Attribute("useGeneratedKeys", "true"));
                    insertElement.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty()));
                    insertElement.addAttribute(new Attribute("keyColumn", introspectedColumn.getActualColumnName()));
                } else {
                    insertElement.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("insert into ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        insertElement.addElement(new TextElement(sb.toString()));

        XmlElement insertTrimElement = new XmlElement("trim");
        insertTrimElement.addAttribute(new Attribute("prefix", "("));
        insertTrimElement.addAttribute(new Attribute("suffix", ")"));
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        insertElement.addElement(insertTrimElement);

        XmlElement valuesTrimElement = new XmlElement("trim");
        valuesTrimElement.addAttribute(new Attribute("prefix", "values ("));
        valuesTrimElement.addAttribute(new Attribute("suffix", ")"));
        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ","));
        insertElement.addElement(valuesTrimElement);

        // for (IntrospectedColumn introspectedColumn :
        // ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns())) {
        for (IntrospectedColumn introspectedColumn : ListUtilities
            .removeGeneratedAlwaysColumns(introspectedTable.getAllColumns())) {

            if (introspectedColumn.isSequenceColumn() || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                // if it is a sequence column, it is not optional
                // This is required for MyBatis3 because MyBatis3 parses
                // and calculates the SQL before executing the selectKey

                // if it is primitive, we cannot do a null check
                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertTrimElement.addElement(new TextElement(sb.toString()));

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                sb.append(',');
                valuesTrimElement.addElement(new TextElement(sb.toString()));

                continue;
            }

            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null");
            XmlElement insertNotNullElement = new XmlElement("if");
            insertNotNullElement.addAttribute(new Attribute("test", sb.toString()));

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(',');
            insertNotNullElement.addElement(new TextElement(sb.toString()));
            insertTrimElement.addElement(insertNotNullElement);

            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null");
            XmlElement valuesNotNullElement = new XmlElement("if");
            valuesNotNullElement.addAttribute(new Attribute("test", sb.toString()));

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            sb.append(',');
            valuesNotNullElement.addElement(new TextElement(sb.toString()));
            valuesTrimElement.addElement(valuesNotNullElement);
        }

        if (introspectedTable.getNonPrimaryKeyColumns().size() > 0) {

            insertElement.addElement(new TextElement("on duplicate key update"));

            XmlElement dynamicElement = new XmlElement("trim");
            dynamicElement.addAttribute(new Attribute("suffixOverrides", ","));

            insertElement.addElement(dynamicElement);

            for (IntrospectedColumn introspectedColumn : ListUtilities
                .removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {

                sb.setLength(0);
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null");
                XmlElement isNotNullElement = new XmlElement("if");
                isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                dynamicElement.addElement(isNotNullElement);

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(" = ");
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                sb.append(',');

                isNotNullElement.addElement(new TextElement(sb.toString()));
            }

        }

        document.getRootElement().addElement(insertElement);

    }

    /**
     * This method should return an XmlElement for the select key used to automatically generate keys.
     * 
     * @param introspectedColumn
     *            the column related to the select key statement
     * @param generatedKey
     *            the generated key for the current table
     * @return the selectKey element
     */
    protected XmlElement getSelectKey(IntrospectedColumn introspectedColumn, GeneratedKey generatedKey) {
        String identityColumnType = introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName();

        XmlElement answer = new XmlElement("selectKey");
        answer.addAttribute(new Attribute("resultType", identityColumnType));
        answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty()));
        answer.addAttribute(new Attribute("order", generatedKey.getMyBatis3Order()));

        answer.addElement(new TextElement(generatedKey.getRuntimeSqlStatement()));

        return answer;
    }

}
