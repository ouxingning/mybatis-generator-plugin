package com.xnou.mybatis.generator.types;

import java.sql.Types;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

/**
 * 自定义配置数据库字段类型与Java类型之间的映射关系。
 * 
 * @author OU Xingning
 * @date 2019/04/19
 */
public class CustomizedJavaTypeResolver extends JavaTypeResolverDefaultImpl {

    public CustomizedJavaTypeResolver() {
        super();

        // 把数据库的 TINYINT 类型映射成 Integer 类型
        typeMap.put(Types.TINYINT, new JdbcTypeInformation("TINYINT", new FullyQualifiedJavaType(Integer.class.getName())));
    }

}
