package com.xnou.mybatis.generator.plugin;

import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

/**
 * 这是根据数据库表生成实体Repository类的MyBatis Generator插件.
 * 
 * @author OU Xingning
 * @date 2018/10/30
 */
public class EntityRepositoryPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        return null;
    }

}
