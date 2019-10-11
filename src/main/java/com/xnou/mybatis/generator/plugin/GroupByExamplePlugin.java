package com.xnou.mybatis.generator.plugin;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
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
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

/**
 * 支持使用Group By语法对查询结果进行分组，包含COUNT(*)的计算结果。
 * 
 * @author OU Xingning
 * @date 2019/01/14
 */
public class GroupByExamplePlugin extends PluginAdapter {

    private static final Log LOG = LogFactory.getLog(GroupByExamplePlugin.class);

    private static final String GROUP_BY_CAMEL = "GroupBy";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 创建 groupBy 的模型类。
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        String targetPackage = getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        String targetProject = getContext().getJavaModelGeneratorConfiguration().getTargetProject();

        // 模型基类
        FullyQualifiedJavaType baseType = null;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            baseType = new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType());
        } else {
            baseType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        }

        // 类型
        FullyQualifiedJavaType recordType = null;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            recordType = new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType() + GROUP_BY_CAMEL);
        } else {
            recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType() + GROUP_BY_CAMEL);
        }

        // 序列化接口
        FullyQualifiedJavaType serializable = new FullyQualifiedJavaType("java.io.Serializable");

        // 顶级类
        TopLevelClass topLevelClass = new TopLevelClass(recordType);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.setSuperClass(baseType);

        topLevelClass.addImportedType(baseType);
        topLevelClass.addImportedType(serializable);
        topLevelClass.addSuperInterface(serializable);

        // 序列号字段
        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("1L");
        field.setName("serialVersionUID");
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long"));
        field.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(field);

        // countGroupBy字段
        Field count = new Field();
        count.setName("countGroupBy");
        count.setType(new FullyQualifiedJavaType("long"));
        count.setVisibility(JavaVisibility.PRIVATE);
        count.addJavaDocLine("/** 分组统计的值，即count(*)统计的值 */");
        topLevelClass.addField(count);

        // getter和setter方法
        Method getCountGroupBy = new Method();
        getCountGroupBy.setVisibility(JavaVisibility.PUBLIC);
        getCountGroupBy.setReturnType(new FullyQualifiedJavaType("long"));
        getCountGroupBy.setName("getCountGroupBy");
        getCountGroupBy.addBodyLine("return countGroupBy;");
        topLevelClass.addMethod(getCountGroupBy);

        Method setCountGroupBy = new Method();
        setCountGroupBy.setVisibility(JavaVisibility.PUBLIC);
        setCountGroupBy.setName("setCountGroupBy");
        setCountGroupBy.addParameter(new Parameter(new FullyQualifiedJavaType("long"), "countGroupBy"));
        setCountGroupBy.addBodyLine("this.countGroupBy = countGroupBy;");
        topLevelClass.addMethod(setCountGroupBy);

        context.getCommentGenerator().addModelClassComment(topLevelClass, introspectedTable);

        StringBuilder sb = new StringBuilder();
        sb.append(targetProject).append(File.separator);
        sb.append(targetPackage.replaceAll("\\.", File.separator));
        sb.append(File.separator).append(recordType.getShortName()).append(".java");

        File file = new File(sb.toString());
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        // Java文件内容
        StringBuilder buf = new StringBuilder();
        buf.append(topLevelClass.getFormattedContent());

        try {
            byte[] content = buf.toString().getBytes(StandardCharsets.UTF_8);
            Files.write(Paths.get(sb.toString()), content);
        } catch (IOException e) {
            LOG.error("An error occurs when writing " + sb.toString(), e);
        }

        return null;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {

        this.addGroupByExampleMethod(interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        this.addResultMapXml(document, introspectedTable);
        this.addGroupByExampleXml(document, introspectedTable);
        return true;
    }

    /**
     * 为Example类添加groupByClause属性，并创建getter和setter方法。
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        FullyQualifiedJavaType stringJavaType = FullyQualifiedJavaType.getStringInstance();

        Field groupByClause = new Field();
        groupByClause.setName("groupByClause");
        groupByClause.setVisibility(JavaVisibility.PRIVATE);
        groupByClause.setType(stringJavaType);
        topLevelClass.addField(groupByClause);

        Method getGroupByClause = new Method();
        getGroupByClause.setVisibility(JavaVisibility.PUBLIC);
        getGroupByClause.setReturnType(stringJavaType);
        getGroupByClause.setName("getGroupByClause");
        getGroupByClause.addBodyLine("return groupByClause;");
        topLevelClass.addMethod(getGroupByClause);

        Method setGroupByClause = new Method();
        setGroupByClause.setVisibility(JavaVisibility.PUBLIC);
        setGroupByClause.setName("setGroupByClause");
        setGroupByClause.addParameter(new Parameter(stringJavaType, "groupByClause"));
        setGroupByClause.addBodyLine("this.groupByClause = groupByClause;");
        topLevelClass.addMethod(setGroupByClause);

        return true;
    }

    /**
     * 增加 groupByExample 方法。
     */
    private void addGroupByExampleMethod(Interface interfaze, IntrospectedTable introspectedTable) {

        FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());

        // 需要导入的类
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        importedTypes.add(exampleType);

        Method method = new Method();
        // 1. 方法可见性
        method.setVisibility(JavaVisibility.PUBLIC);
        // 2. 返回值类型
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType listType = null;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            listType = new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType() + GROUP_BY_CAMEL);
        } else {
            listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType() + GROUP_BY_CAMEL);
        }

        importedTypes.add(listType);
        returnType.addTypeArgument(listType);
        method.setReturnType(returnType);
        // 3. 设置方法名
        method.setName("groupByExample");
        // 4. 设置参数列表
        method.addParameter(new Parameter(exampleType, "example"));

        method.addJavaDocLine("/**");
        method.addJavaDocLine("* 按条件进行分组查询，额外带上 count(*) as countGroupBy 字段的查询结果。");
        method.addJavaDocLine("*/");

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    /**
     * 增加 resultMap 节点。
     */
    private void addResultMapXml(Document document, IntrospectedTable introspectedTable) {

        String recordType = null;
        String baseResultMap = null;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            recordType = introspectedTable.getRecordWithBLOBsType() + GROUP_BY_CAMEL;
            baseResultMap = introspectedTable.getResultMapWithBLOBsId();
        } else {
            recordType = introspectedTable.getBaseRecordType() + GROUP_BY_CAMEL;
            baseResultMap = introspectedTable.getBaseResultMapId();
        }

        XmlElement resultMap = new XmlElement("resultMap");
        resultMap.addAttribute(new Attribute("extends", baseResultMap));
        resultMap.addAttribute(new Attribute("id", "ResultMapGroupBy"));
        resultMap.addAttribute(new Attribute("type", recordType));

        // 增加countGroupBy字段
        XmlElement count = new XmlElement("result");
        count.addAttribute(new Attribute("column", "count_group_by"));
        count.addAttribute(new Attribute("jdbcType", "BIGINT"));
        count.addAttribute(new Attribute("property", "countGroupBy"));

        resultMap.addElement(count);

        document.getRootElement().addElement(resultMap);
    }

    /**
     * 增加 groupByExample 节点。
     */
    private void addGroupByExampleXml(Document document, IntrospectedTable introspectedTable) {

        String exampleType = introspectedTable.getExampleType();

        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute("id", "groupByExample"));
        answer.addAttribute(new Attribute("resultMap", "ResultMapGroupBy"));
        answer.addAttribute(new Attribute("parameterType", exampleType));

        context.getCommentGenerator().addComment(answer);

        answer.addElement(new TextElement("select"));
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "distinct"));
        ifElement.addElement(new TextElement("distinct"));
        answer.addElement(ifElement);

        StringBuilder sb = new StringBuilder();
        if (stringHasValue(introspectedTable.getSelectByExampleQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByExampleQueryId());
            sb.append("' as QUERYID,");
            answer.addElement(new TextElement(sb.toString()));
        }

        answer.addElement(getBaseColumnListElement(introspectedTable));

        if (introspectedTable.hasBLOBColumns()) {
            answer.addElement(new TextElement(","));
            answer.addElement(getBlobColumnListElement(introspectedTable));
        }

        answer.addElement(new TextElement(", count(*) as count_group_by"));

        sb.setLength(0);
        sb.append("from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getExampleIncludeElement(introspectedTable));

        ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "groupByClause != null"));
        ifElement.addElement(new TextElement("group by ${groupByClause}"));
        answer.addElement(ifElement);

        ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "orderByClause != null"));
        ifElement.addElement(new TextElement("order by ${orderByClause}"));
        answer.addElement(ifElement);

        document.getRootElement().addElement(answer);
    }

    protected XmlElement getBaseColumnListElement(IntrospectedTable introspectedTable) {
        XmlElement answer = new XmlElement("include");
        answer.addAttribute(new Attribute("refid", introspectedTable.getBaseColumnListId()));
        return answer;
    }

    protected XmlElement getBlobColumnListElement(IntrospectedTable introspectedTable) {
        XmlElement answer = new XmlElement("include");
        answer.addAttribute(new Attribute("refid", introspectedTable.getBlobColumnListId()));
        return answer;
    }

    protected XmlElement getExampleIncludeElement(IntrospectedTable introspectedTable) {
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "_parameter != null"));

        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", introspectedTable.getExampleWhereClauseId()));
        ifElement.addElement(includeElement);

        return ifElement;
    }

}
