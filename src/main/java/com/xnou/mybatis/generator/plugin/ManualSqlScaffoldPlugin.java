package com.xnou.mybatis.generator.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

/**
 * 此插件用于创建手工编写SQL的脚手架代码，包括manual子包中XML文件和Java代码。
 * 
 * <p>
 * 在table标签里增加generateManualSql属性：
 * 
 * <pre>
 *    <property name="generateManualSql" value="true" />
 * </pre>
 * 
 * @author OU Xingning
 * @date 2018/11/02
 */
public class ManualSqlScaffoldPlugin extends PluginAdapter {

    private static final Log LOG = LogFactory.getLog(ManualSqlScaffoldPlugin.class);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        if (!needToGenerateFiles(introspectedTable)) {
            return null;
        }

        String targetPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String targetProject = getContext().getJavaClientGeneratorConfiguration().getTargetProject();

        // 基础类名
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String clazzShortName = baseRecordType.getShortName();

        // 目标类
        String manualTypeStr = targetPackage + ".manual." + clazzShortName + "ManualMapper";
        FullyQualifiedJavaType manualType = new FullyQualifiedJavaType(manualTypeStr);

        Interface interfaze = new Interface(manualType);
        interfaze.setVisibility(JavaVisibility.PUBLIC);

        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine("* 手工编写SQL的接口。");
        interfaze.addJavaDocLine("*");
        interfaze.addJavaDocLine("* @author OU Xingning");
        interfaze.addJavaDocLine("* @date " + new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        interfaze.addJavaDocLine("*/");

        StringBuilder sb = new StringBuilder();
        sb.append(targetProject).append(File.separator);
        sb.append(manualTypeStr.replaceAll("\\.", File.separator)).append(".java");

        File file = new File(sb.toString());
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        
        if (file.exists() && file.length() > 0L) {
            LOG.warn("Existing file " + file.getAbsolutePath() + " was ignored.");
            return null;
        }

        // Java文件内容
        StringBuilder buf = new StringBuilder();
        buf.append(interfaze.getFormattedContent());

        try {
            byte[] content = buf.toString().getBytes(StandardCharsets.UTF_8);
            Files.write(Paths.get(sb.toString()), content);
        } catch (IOException e) {
            LOG.error("An error occurs when writing " + sb.toString(), e);
        }

        return null;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {

        if (!needToGenerateFiles(introspectedTable)) {
            return null;
        }

        String targetPackage = getContext().getSqlMapGeneratorConfiguration().getTargetPackage();
        String targetProject = getContext().getSqlMapGeneratorConfiguration().getTargetProject();
        String daoTargetPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();

        // 基础类名
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String clazzShortName = baseRecordType.getShortName();

        // 加个 manual 子包
        String newDaoTargetPackage = daoTargetPackage + ".manual";

        // 使用 manual 替换 generated来作为包名
        String newTargetPackage = targetPackage.replace("generated", "manual");

        // 类名加上 ManualMapper
        String newClazzShortName = clazzShortName + "ManualMapper";

        StringBuilder sb = new StringBuilder();
        sb.append(targetProject).append(File.separator);
        sb.append(newTargetPackage.replaceAll("\\.", File.separator));
        sb.append(File.separator).append(newClazzShortName).append(".xml");

        File file = new File(sb.toString());
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        
        if (file.exists() && file.length() > 0L) {
            LOG.warn("Existing file " + file.getAbsolutePath() + " was ignored.");
            return null;
        }

        String lineSeparator = System.getProperty("line.separator", "\n");

        String mapperPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String baseMapper = mapperPackage + "." + clazzShortName + "Mapper";

        // 示例XML
        StringBuilder xml = new StringBuilder();
        xml.append(lineSeparator);
        xml.append("\t<!-- 示例XML -->").append(lineSeparator);
        xml.append("\t<!--").append(lineSeparator);
        xml.append("\t<resultMap id=\"CustomResultMap\" type=\"com.model.Xxx\" extends=\"");
        xml.append(baseMapper).append(".BaseResultMap\">").append(lineSeparator);
        xml.append("\t</resultMap>").append(lineSeparator);
        xml.append(lineSeparator);
        xml.append("\t<select id=\"selectXxx\" parameterType=\"com.param.XxxParam\" resultMap=\"CustomResultMap\"");
        xml.append(lineSeparator);
        xml.append("\t  select <include refid=\"");
        xml.append(baseMapper).append(".Base_Column_List\" />").append(lineSeparator);
        xml.append("\t  from table").append(lineSeparator);
        xml.append("\t  where ...").append(lineSeparator);
        xml.append("\t</select>").append(lineSeparator);
        xml.append("\t-->").append(lineSeparator);
        xml.append(lineSeparator);

        // XML 内容
        XmlElement mapper = new XmlElement("mapper");
        String namespace = newDaoTargetPackage + "." + newClazzShortName;
        mapper.addAttribute(new Attribute("namespace", namespace));

        mapper.addElement(new TextElement(xml.toString()));
        context.getCommentGenerator().addRootComment(mapper);

        Document doc = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        doc.setRootElement(mapper);

        try {
            byte[] content = doc.getFormattedContent().getBytes(StandardCharsets.UTF_8);
            Files.write(Paths.get(sb.toString()), content);
        } catch (IOException e) {
            LOG.error("An error occurs when writing " + sb.toString(), e);
        }

        return null;
    }

    /**
     * 判断是否需要创建文件。判断的条件是table标签中有 generateManualSql 属性，并且值为 true
     * 
     * @return 如果需要创建文件则返回true，否则返回false
     */
    private boolean needToGenerateFiles(IntrospectedTable introspectedTable) {
        String generateManualSql = introspectedTable.getTableConfigurationProperty("generateManualSql");
        if (null != generateManualSql && "true".equalsIgnoreCase(generateManualSql)) {
            return true;
        }
        return false;
    }

}
