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

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * 这是根据数据库表生成实体Service类的MyBatis Generator插件.
 * 
 * @author OU Xingning
 * @date 2018/10/30
 */
public class EntityServicePlugin extends PluginAdapter {

    private static final Log LOG = LogFactory.getLog(EntityServicePlugin.class);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        if (!needToGenerateFiles(introspectedTable)) {
            return null;
        }

        // 创建 EntityService 接口，位于 com.xnou.servcie 包下。
        this.generateServiceInterface(introspectedTable);

        // 创建 EntityServiceImpl 实现类，位于 com.xnou.servcie.impl 包下。
        this.generateServiceImplementation(introspectedTable);

        // 创建 EntityServiceGenerated 接口，位于 com.xnou.service.generated 包下。
        this.generateServcieGeneratedInterface(introspectedTable);

        // 创建 EntityServiceGeneratedImpl 类， 位于 com.xnou.servcie.generated 包下。
        this.generateServcieGeneratedImplementation(introspectedTable);

        return null;
    }

    /**
     * 当代码已存在的时候，是否覆盖原来的代码？
     */
    private boolean isOverwriteCode() {
        String overwriteCode = this.getContext().getProperty("overwriteServiceCode");
        if (null != overwriteCode) {
            return Boolean.parseBoolean(overwriteCode);
        }
        return false;
    }

    /**
     * 创建 EntityService 接口，位于 com.xnou.servcie 包下。
     */
    private void generateServiceInterface(IntrospectedTable introspectedTable) {

        String clientPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String clientProject = getContext().getJavaClientGeneratorConfiguration().getTargetProject();

        // 基础类名
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String clazzShortName = baseRecordType.getShortName();

        // 目标包名
        String targetPackage = null;
        int lastDot = clientPackage.lastIndexOf('.');
        if (lastDot > 0) {
            targetPackage = clientPackage.substring(0, lastDot) + ".service";
        } else {
            targetPackage = clientPackage + ".service";
        }

        // 接口名
        String interfaceTypeStr = targetPackage + "." + clazzShortName + "Service";
        FullyQualifiedJavaType interfaceType = new FullyQualifiedJavaType(interfaceTypeStr);

        StringBuilder sb = new StringBuilder();
        sb.append(clientProject).append(File.separator);
        sb.append(interfaceTypeStr.replaceAll("\\.", File.separator)).append(".java");

        File file = new File(sb.toString());
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (file.exists() && file.length() > 0L) {
            LOG.warn("Existing file " + file.getAbsolutePath() + " was ignored.");
            return;
        }

        // 模板名
        String templateFile = "EntityServiceInterface.ftl";

        // 数据模型
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("packageName", interfaceType.getPackageName());
        dataModel.put("entityName", clazzShortName);
        dataModel.put("tableName", introspectedTable.getFullyQualifiedTable());
        dataModel.put("author", "OU Xingning");
        dataModel.put("date", new Date());

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
     * 创建 EntityServiceImpl 实现类，位于 com.xnou.servcie.impl 包下。
     */
    private void generateServiceImplementation(IntrospectedTable introspectedTable) {
        String clientPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String clientProject = getContext().getJavaClientGeneratorConfiguration().getTargetProject();

        // 基础类名
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String clazzShortName = baseRecordType.getShortName();

        // 目标包名
        String targetPackage = null;
        int lastDot = clientPackage.lastIndexOf('.');
        if (lastDot > 0) {
            targetPackage = clientPackage.substring(0, lastDot) + ".service";
        } else {
            targetPackage = clientPackage + ".service";
        }

        // 实现类
        String implTypeStr = targetPackage + ".impl." + clazzShortName + "ServiceImpl";
        FullyQualifiedJavaType implType = new FullyQualifiedJavaType(implTypeStr);

        StringBuilder sb = new StringBuilder();
        sb.append(clientProject).append(File.separator);
        sb.append(implTypeStr.replaceAll("\\.", File.separator)).append(".java");

        File file = new File(sb.toString());
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (file.exists() && file.length() > 0L) {
            LOG.warn("Existing file " + file.getAbsolutePath() + " was ignored.");
            return;
        }

        // 模板名
        String templateFile = "EntityServiceInterfaceImpl.ftl";

        // 数据模型
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("packageName", implType.getPackageName());
        dataModel.put("superPackageName", targetPackage);
        dataModel.put("entityName", clazzShortName);
        dataModel.put("author", "OU Xingning");
        dataModel.put("date", new Date());

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
     * 创建 EntityServiceGenerated 接口，位于 com.xnou.service.generated 包下。
     */
    private void generateServcieGeneratedInterface(IntrospectedTable introspectedTable) {
        String domainPackage = getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        String clientPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String clientProject = getContext().getJavaClientGeneratorConfiguration().getTargetProject();

        // 基础类名
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String clazzShortName = baseRecordType.getShortName();

        // 目标包名
        String targetPackage = null;
        int lastDot = clientPackage.lastIndexOf('.');
        if (lastDot > 0) {
            targetPackage = clientPackage.substring(0, lastDot) + ".service";
        } else {
            targetPackage = clientPackage + ".service";
        }

        // 继承的接口名
        String superInterfaceTypeStr = targetPackage + ".generated." + clazzShortName + "ServiceGenerated";
        FullyQualifiedJavaType superInterfaceType = new FullyQualifiedJavaType(superInterfaceTypeStr);

        StringBuilder sb = new StringBuilder();
        sb.append(clientProject).append(File.separator);
        sb.append(superInterfaceTypeStr.replaceAll("\\.", File.separator)).append(".java");

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
        String templateFile = "EntityServiceGenerated.ftl";

        // 数据模型
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("packageName", superInterfaceType.getPackageName());
        dataModel.put("entityName", clazzShortName);
        dataModel.put("author", "OU Xingning");
        dataModel.put("date", new Date());

        dataModel.put("domainPackage", domainPackage);
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

        // 是否需要创建 incrByExampleSelective 方法
        NumberIncrementPlugin incrPlugin = new NumberIncrementPlugin();
        boolean needToGenerateIncrByMethod = incrPlugin.needToGenerate(introspectedTable);
        dataModel.put("needToGenerateIncrByMethod", needToGenerateIncrByMethod);
        LOG.warn("Table " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + ", needToGenerateIncrByMethod: "
            + needToGenerateIncrByMethod);

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
     * 创建 EntityServiceGeneratedImpl 类， 位于 com.xnou.servcie.generated 包下。
     */
    private void generateServcieGeneratedImplementation(IntrospectedTable introspectedTable) {
        String domainPackage = getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        String clientPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String clientProject = getContext().getJavaClientGeneratorConfiguration().getTargetProject();
        
        // 基础类名
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String clazzShortName = baseRecordType.getShortName();

        // 目标包名
        String targetPackage = null;
        int lastDot = clientPackage.lastIndexOf('.');
        if (lastDot > 0) {
            targetPackage = clientPackage.substring(0, lastDot) + ".service";
        } else {
            targetPackage = clientPackage + ".service";
        }

        // 继承的基类
        String superClassTypeStr = targetPackage + ".generated." + clazzShortName + "ServiceGeneratedImpl";
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
        String templateFile = "EntityServiceGeneratedImpl.ftl";

        // 数据模型
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("packageName", superClassType.getPackageName());
        dataModel.put("entityName", clazzShortName);
        dataModel.put("entityMapper", clazzShortName.substring(0, 1).toLowerCase() + clazzShortName.substring(1) + "Mapper");
        dataModel.put("author", "OU Xingning");
        dataModel.put("date", new Date());

        dataModel.put("domainPackage", domainPackage);
        dataModel.put("clientPackage", clientPackage);
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

        // 是否需要创建 incrByExampleSelective 方法
        NumberIncrementPlugin incrPlugin = new NumberIncrementPlugin();
        boolean needToGenerateIncrByMethod = incrPlugin.needToGenerate(introspectedTable);
        dataModel.put("needToGenerateIncrByMethod", needToGenerateIncrByMethod);

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
     * 判断是否需要创建文件。判断的条件是table标签中有 generateService 属性，并且值为 true
     * 
     * @return 如果需要创建文件则返回true，否则返回false
     */
    private boolean needToGenerateFiles(IntrospectedTable introspectedTable) {
        String generateManualSql = introspectedTable.getTableConfigurationProperty("generateServiceCode");
        if (null != generateManualSql && "true".equalsIgnoreCase(generateManualSql)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(StandardCharsets.UTF_8.name());
    }

}
