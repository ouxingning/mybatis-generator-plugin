package com.xnou.mybatis.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * 使用MySQL的LIMIT关键词来限制返回的记录数，一般用于实现分页查询功能。
 * 
 * @author OU Xingning
 * @date 2018/11/01
 */
public class LimitClausePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 为每个Example类添加offset和limit属性，并创建getter和setter方法。
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        FullyQualifiedJavaType longWrapper = new FullyQualifiedJavaType("java.lang.Long");
        topLevelClass.addImportedType(longWrapper);

        Field offset = new Field();
        offset.setName("offset");
        offset.setVisibility(JavaVisibility.PRIVATE);
        offset.setType(longWrapper);
        topLevelClass.addField(offset);

        Method getOffset = new Method();
        getOffset.setVisibility(JavaVisibility.PUBLIC);
        getOffset.setReturnType(longWrapper);
        getOffset.setName("getOffset");
        getOffset.addBodyLine("return offset;");
        topLevelClass.addMethod(getOffset);

        Method setOffset = new Method();
        setOffset.setVisibility(JavaVisibility.PUBLIC);
        setOffset.setName("setOffset");
        setOffset.addParameter(new Parameter(longWrapper, "offset"));
        setOffset.addBodyLine("this.offset = offset;");
        topLevelClass.addMethod(setOffset);

        Field limit = new Field();
        limit.setName("limit");
        limit.setVisibility(JavaVisibility.PRIVATE);
        limit.setType(longWrapper);
        topLevelClass.addField(limit);

        Method getLimit = new Method();
        getLimit.setVisibility(JavaVisibility.PUBLIC);
        getLimit.setReturnType(longWrapper);
        getLimit.setName("getLimit");
        getLimit.addBodyLine("return limit;");
        topLevelClass.addMethod(getLimit);

        Method setLimit = new Method();
        setLimit.setVisibility(JavaVisibility.PUBLIC);
        setLimit.setName("setLimit");
        setLimit.addParameter(new Parameter(longWrapper, "limit"));
        setLimit.addBodyLine("this.limit = limit;");
        topLevelClass.addMethod(setLimit);

        return true;
    }

    /**
     * 为selectByExample增加LIMIT限制条件
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {

        XmlElement limitNotNullElement = new XmlElement("if");
        limitNotNullElement.addAttribute(new Attribute("test", "limit != null"));

        XmlElement offsetNotNullElement = new XmlElement("if");
        offsetNotNullElement.addAttribute(new Attribute("test", "offset != null"));
        offsetNotNullElement.addElement(new TextElement("limit ${offset}, ${limit}"));
        limitNotNullElement.addElement(offsetNotNullElement);

        XmlElement offsetNullElement = new XmlElement("if");
        offsetNullElement.addAttribute(new Attribute("test", "offset == null"));
        offsetNullElement.addElement(new TextElement("limit ${limit}"));
        limitNotNullElement.addElement(offsetNullElement);

        element.addElement(limitNotNullElement);

        return true;
    }

    /**
     * 为selectByExample增加LIMIT限制条件
     */
    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {

        XmlElement limitNotNullElement = new XmlElement("if");
        limitNotNullElement.addAttribute(new Attribute("test", "limit != null"));

        XmlElement offsetNotNullElement = new XmlElement("if");
        offsetNotNullElement.addAttribute(new Attribute("test", "offset != null"));
        offsetNotNullElement.addElement(new TextElement("limit ${offset}, ${limit}"));
        limitNotNullElement.addElement(offsetNotNullElement);

        XmlElement offsetNullElement = new XmlElement("if");
        offsetNullElement.addAttribute(new Attribute("test", "offset == null"));
        offsetNullElement.addElement(new TextElement("limit ${limit}"));
        limitNotNullElement.addElement(offsetNullElement);

        element.addElement(limitNotNullElement);

        return true;
    }

}
