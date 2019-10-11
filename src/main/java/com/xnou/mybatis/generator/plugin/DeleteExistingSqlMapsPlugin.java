package com.xnou.mybatis.generator.plugin;

import java.io.File;
import java.util.List;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

/**
 * 删除已存在的XML文件，数据库表新增了字段，重新生成代码之前需要手工删除XML文件，因为默认是合并XML内容的。
 * 
 * @author OU Xingning
 * @date 2018/10/31
 */
public class DeleteExistingSqlMapsPlugin extends PluginAdapter {

    private static final Log LOG = LogFactory.getLog(DeleteExistingSqlMapsPlugin.class);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append(sqlMap.getTargetProject()).append(File.separator);
        sb.append(sqlMap.getTargetPackage().replaceAll("\\.", File.separator));
        sb.append(File.separator).append(sqlMap.getFileName());

        File sqlMapFile = new File(sb.toString());
        if (sqlMapFile.delete()) {
            LOG.warn("XML file " + sb.toString() + " was deleted by DeleteExistingSqlMapsPlugin.");
        }

        return true;
    }

}
