package com.xnou.mybatis.generator.plugin;

import static freemarker.template.Configuration.VERSION_2_3_28;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import com.google.common.base.CaseFormat;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * 这是根据数据库表生成实体Controller类的MyBatis Generator插件.
 * 
 * @author OU Xingning
 * @date 2018/10/30
 */
public class EntityControllerPlugin extends PluginAdapter {

    private static final Log LOG = LogFactory.getLog(EntityControllerPlugin.class);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        if (!needToGenerateFiles(introspectedTable)) {
            return null;
        }

        // 创建 AbstractControllerGenerated 抽象类，位于 com.xnou.web.controller.generated 包下
        this.generateControllerAbstractIfNotExists();

        // 创建 EntityControllerGenerated 类，位于 com.xnou.web.controller.generated 包下
        this.generateControllerGenerated(introspectedTable);

        return null;
    }

    /**
     * 当代码已存在的时候，是否覆盖原来的代码？
     */
    private boolean isOverwriteCode() {
        String overwriteCode = this.getContext().getProperty("overwriteControllerCode");
        if (null != overwriteCode) {
            return Boolean.parseBoolean(overwriteCode);
        }
        return false;
    }

    /**
     * 创建 AbstractControllerGenerated 抽象类，位于 com.xnou.web.controller.generated 包下。
     */
    private void generateControllerAbstractIfNotExists() {
        String domainPackage = getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        String clientPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String clientProject = getContext().getJavaClientGeneratorConfiguration().getTargetProject();

        // 目标包名
        String targetPackage = null;
        int lastDot = clientPackage.lastIndexOf('.');
        if (lastDot > 0) {
            targetPackage = clientPackage.substring(0, lastDot) + ".web.controller.generated";
        } else {
            targetPackage = clientPackage + ".web.controller.generated";
        }

        // 继承的基类
        String superClassTypeStr = targetPackage + ".AbstractControllerGenerated";
        FullyQualifiedJavaType superClassType = new FullyQualifiedJavaType(superClassTypeStr);

        StringBuilder sb = new StringBuilder();
        sb.append(clientProject).append(File.separator);
        sb.append(superClassTypeStr.replaceAll("\\.", File.separator)).append(".java");

        File file = new File(sb.toString());
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (file.exists() && file.length() > 0L) {
            if (!isOverwriteCode()) {
                LOG.warn("Existing file " + file.getAbsolutePath() + " was ignored.");
                return;
            }
        }

        // 模板名
        String templateFile = "EntityControllerAbstract.ftl";

        // 数据模型
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("packageName", superClassType.getPackageName());
        dataModel.put("classShortName", superClassType.getShortName());
        dataModel.put("author", "OU Xingning");
        dataModel.put("date", new Date());

        dataModel.put("domainPackage", domainPackage);
        dataModel.put("clientPackage", clientPackage);

        try {
            this.processTemplate(templateFile, dataModel, file);
            LOG.debug("Created file " + file.getAbsolutePath());
        } catch (Exception e) {
            LOG.error("Error occurs when generating file " + file.getAbsolutePath(), e);
        }

    }

    /**
     * 创建 EntityControllerGenerated 类， 位于 com.xnou.web.controller.generated 包下。
     */
    private void generateControllerGenerated(IntrospectedTable introspectedTable) {
        String domainPackage = getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        String clientPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String clientProject = getContext().getJavaClientGeneratorConfiguration().getTargetProject();

        // 基础类名
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String clazzShortName = baseRecordType.getShortName();

        // 目标包名
        String targetPackage = null;
        String servicePackage = null;
        int lastDot = clientPackage.lastIndexOf('.');
        if (lastDot > 0) {
            targetPackage = clientPackage.substring(0, lastDot) + ".web.controller.generated";
            servicePackage = clientPackage.substring(0, lastDot) + ".service";
        } else {
            targetPackage = clientPackage + ".web.controller.generated";
            servicePackage = clientPackage + ".service";
        }

        // 控制器的类名
        String classTypeStr = targetPackage + "." + clazzShortName + "ControllerGenerated";
        FullyQualifiedJavaType classType = new FullyQualifiedJavaType(classTypeStr);

        StringBuilder sb = new StringBuilder();
        sb.append(clientProject).append(File.separator);
        sb.append(classTypeStr.replaceAll("\\.", File.separator)).append(".java");

        File file = new File(sb.toString());
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (file.exists() && file.length() > 0L) {
            if (!isOverwriteCode()) {
                LOG.warn("Existing file " + file.getAbsolutePath() + " was ignored.");
                return;
            }
        }

        // 模板名
        String templateFile = "EntityControllerGenerated.ftl";

        // 数据模型
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("packageName", classType.getPackageName());
        dataModel.put("entityName", clazzShortName);
        dataModel.put("entityNameLowerHyphen", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, clazzShortName));
        dataModel.put("entityNameLowerCamel", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazzShortName));
        dataModel.put("author", "OU Xingning");
        dataModel.put("date", new Date());

        dataModel.put("domainPackage", domainPackage);
        dataModel.put("clientPackage", clientPackage);
        dataModel.put("servicePackage", servicePackage);
        dataModel.put("tableName", introspectedTable.getFullyQualifiedTable());
        dataModel.put("withBLOBs", introspectedTable.getRules().generateSelectByExampleWithBLOBs());

        // 分析主键的类型
        FullyQualifiedJavaType primaryKeyType = null;
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns.size() == 1) {
            primaryKeyType = primaryKeyColumns.get(0).getFullyQualifiedJavaType();
        } else if (primaryKeyColumns.size() > 1) {
            primaryKeyType = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
        } else {
            primaryKeyType = new FullyQualifiedJavaType("java.lang.Long"); // 给一个默认的主键类型
        }

        dataModel.put("primaryKeyPackageName", primaryKeyType.getPackageName());
        dataModel.put("primaryKeyShortName", primaryKeyType.getShortName());

        dataModel.put("primaryKeyIsLong", "Long".equals(primaryKeyType.getShortName()));
        dataModel.put("primaryKeyIsInteger", "Integer".equals(primaryKeyType.getShortName()));

        // Example类型
        FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        dataModel.put("exampleShortName", exampleType.getShortName());

        // 含有所有字段的类名，包括BLOB类型的字段
        String allFieldsShortName = introspectedTable.getRules().calculateAllFieldsClass().getShortName();
        dataModel.put("allFieldsShortName", allFieldsShortName);

        try {
            this.processTemplate(templateFile, dataModel, file);
            LOG.debug("Created file " + file.getAbsolutePath());
        } catch (Exception e) {
            LOG.error("Error occurs when generating file " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 根据代码模板生成服务代码。
     * 
     * @param templateFile
     *            代码模板
     * @param dataModel
     *            数据模型
     * @param outputFile
     *            输出的源码文件
     * @throws IOException
     * @throws ParseException
     * @throws MalformedTemplateNameException
     * @throws TemplateNotFoundException
     */
    private void processTemplate(String templateFile, Map<String, Object> dataModel, File outputFile)
        throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {

        Configuration cfg = new Configuration(VERSION_2_3_28);
        cfg.setClassForTemplateLoading(getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper(VERSION_2_3_28));
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());
        cfg.setOutputEncoding(StandardCharsets.UTF_8.name());

        Template template = cfg.getTemplate(templateFile);
        try (Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));) {
            template.process(dataModel, out);
            out.flush();
        } catch (TemplateException e) {
            LOG.error("Process templateFile error " + templateFile, e);
        }
    }

    /**
     * 判断是否需要创建文件。判断的条件是table标签中有 generateController 属性，并且值为 true
     * 
     * @return 如果需要创建文件则返回true，否则返回false
     */
    private boolean needToGenerateFiles(IntrospectedTable introspectedTable) {
        String generateManualSql = introspectedTable.getTableConfigurationProperty("generateControllerCode");
        if (null != generateManualSql && "true".equalsIgnoreCase(generateManualSql)) {
            // 主键的类型为 java.lang.Long 型才能生成Controller代码
            return this.primaryKeyIsLongOrInteger(introspectedTable);
        }
        return false;
    }

    /**
     * 判断表的主键是否为 java.lang.Long 或者 java.lang.Integer 型。
     * 
     * @return 如果是 java.lang.Long 或 java.lang.Integer 型则返回true，否则返回false
     */
    private boolean primaryKeyIsLongOrInteger(IntrospectedTable introspectedTable) {

        // 分析主键的类型
        FullyQualifiedJavaType primaryKeyType = null;
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns.size() == 1) {
            primaryKeyType = primaryKeyColumns.get(0).getFullyQualifiedJavaType();
        } else if (primaryKeyColumns.size() > 1) {
            primaryKeyType = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
        }

        if (null != primaryKeyType) {
            String longType = new FullyQualifiedJavaType("java.lang.Long").getFullyQualifiedName();
            String integerType = new FullyQualifiedJavaType("java.lang.Integer").getFullyQualifiedName();

            return (longType.equals(primaryKeyType.getFullyQualifiedName())
                || integerType.equals(primaryKeyType.getFullyQualifiedName()));
        }

        return false;
    }

    public static void main(String[] args) {
        String[] names = new String[] {"Configuration", "needToGenerateFiles", "FullyQualifiedJavaType", "deomDTO"};
        for (String name : names) {
            System.out.println(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name));
        }
    }

}
