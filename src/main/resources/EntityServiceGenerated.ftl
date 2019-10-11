package ${packageName};

import java.util.List;

import org.apache.ibatis.session.RowBounds;

import com.xnou.util.data.Page;
import com.xnou.util.data.Pageable;

import ${domainPackage}.${entityName};
<#if entityName != allFieldsShortName >
import ${domainPackage}.${allFieldsShortName};
</#if>
import ${domainPackage}.${exampleShortName};
import ${domainPackage}.${allFieldsShortName}GroupBy;

<#if primaryKeyPackageName?starts_with("java.lang.") >
import ${primaryKeyPackageName}.${primaryKeyShortName};
</#if>

/**
 * 自动生成表 ${tableName} 的数据操作与查询接口。
 * 
 * @author ${author!"OU Xingning"}
 * @date ${date?string("yyyy/MM/dd")}
 */
public interface ${entityName}ServiceGenerated {

    /**
     * 统计满足条件的记录数量。
     * 
     * @param example
     *            查询条件
     * @return 满足条件的记录数量
     */
    long countByExample(${exampleShortName} example);

    /**
     * 根据条件删除记录。
     * 
     * @param example
     *            查询条件
     * @return 受影响的记录数
     */
    int deleteByExample(${exampleShortName} example);

    /**
     * 根据主键删除记录。
     * 
     * @param pk
     *            主键
     * @return 受影响的记录数
     */
    int deleteByPrimaryKey(${primaryKeyShortName} pk);

    /**
     * 插入新记录，所有字段都入库。
     * 
     * @param entity
     *            待插入数据库的实体对象
     * @return 受影响的记录数
     */
    int insert(${allFieldsShortName} entity);

    /**
     * 插入新记录，值不为<code>null</code>的字段才入库。
     * 
     * @param entity
     *            待插入数据库的实体对象
     * @return 受影响的记录数
     */
    int insertSelective(${allFieldsShortName} entity);

    /**
     * 查询符合条件的记录，不包括<code>BLOB</code>类型的字段，可实现分页。
     * 
     * @param example
     *            查询条件
     * @param rowBounds
     *            记录偏移
     * @return 符合条件的记录对象列表，不为<code>null</code>
     */
    List<${entityName}> selectByExampleWithRowbounds(${exampleShortName} example, RowBounds rowBounds);

    /**
     * 查询符合条件的记录，不包括<code>BLOB</code>类型的字段。
     * 
     * @param example
     *            查询条件
     * @return 符合条件的记录对象列表，不为<code>null</code>
     */
    List<${entityName}> selectByExample(${exampleShortName} example);

    /**
     * 根据主键查询记录。
     * 
     * @param pk
     *            主键
     * @return 主键对应的记录对象，如果找不到记录则返回<code>null</code>
     */
    ${allFieldsShortName} selectByPrimaryKey(${primaryKeyShortName} pk);

    /**
     * 根据条件更新记录，值为<code>null</code>的字段不更新。
     * 
     * @param entity
     *            数据库记录对象
     * @param example
     *            查询条件
     * @return 受影响的记录数
     */
    int updateByExampleSelective(${allFieldsShortName} entity, ${exampleShortName} example);

    /**
     * 根据条件更新记录，所有字段都更新，不包括<code>BLOB</code>类型的字段。
     * 
     * @param entity
     *            数据库记录对象
     * @param example
     *            查询条件
     * @return 受影响的记录数
     */
    int updateByExample(${entityName} entity, ${exampleShortName} example);


    /**
     * 根据主键更新记录，值为<code>null</code>的字段不更新。
     * 
     * @param entity
     *            数据库记录对象
     * @return 受影响的记录数
     */
    int updateByPrimaryKeySelective(${allFieldsShortName} entity);

    /**
     * 根据主键更新记录，所有字段都更新，不包括<code>BLOB</code>类型的字段。
     * 
     * @param entity
     *            数据库记录对象
     * @return 受影响的记录数
     */
    int updateByPrimaryKey(${entityName} entity);
    
    
    <#-- 判断是否有BLOB字段 -->
    <#if withBLOBs >
    
    /**
     * 查询符合条件的记录，包括<code>BLOB</code>类型的字段，可实现分页。
     * 
     * @param example
     *            查询条件
     * @param rowBounds
     *            记录偏移
     * @return 符合条件的记录对象列表，不为<code>null</code>
     */
    List<${allFieldsShortName}> selectByExampleWithBLOBsWithRowbounds(${exampleShortName} example, RowBounds rowBounds);

    /**
     * 查询符合条件的记录，包括<code>BLOB</code>类型的字段。
     * 
     * @param example
     *            查询条件
     * @return 符合条件的记录对象列表，不为<code>null</code>
     */
    List<${allFieldsShortName}> selectByExampleWithBLOBs(${exampleShortName} example);
     
    /**
     * 根据条件更新记录，所有字段都更新，包括<code>BLOB</code>类型的字段。
     * 
     * @param entity
     *            数据库记录对象
     * @param example
     *            查询条件
     * @return 受影响的记录数
     */
    int updateByExampleWithBLOBs(${allFieldsShortName} entity, ${exampleShortName} example);
      
    /**
     * 根据主键更新记录，所有字段都更新，包括<code>BLOB</code>类型的字段。
     * 
     * @param entity
     *            数据库记录对象
     * @return 受影响的记录数
     */
    int updateByPrimaryKeyWithBLOBs(${allFieldsShortName} entity);
    
    </#if>
    

    /**
     * 按条件进行分组查询，额外带上 count(*) as countGroupBy 字段的查询结果。
     * 
     * @param example
     *            查询条件
     * @return 分组查询后的结果
     */
    List<${allFieldsShortName}GroupBy> groupByExample(${exampleShortName} example);
    
    <#-- 判断是否需要生成 incrByExampleSelective 方法 -->
    <#if needToGenerateIncrByMethod >

    /**
     * 此方法用于对数值类型的字段进行修改，语法原型为：
     * <p>
     * update table set num_field1 = num_field1 + value1, str_field2 = value2 where ...
     * 
     * @param entity
     *            修改后部分字段的内容数据
     * @param example
     *            修改条件
     * @return 受影响的记录数
     */
    int incrByExampleSelective(${allFieldsShortName} entity, ${exampleShortName} example);
    
    </#if>

    /**
     * 更新或插入记录的接口，在插入记录时，如果数据库表中已存在唯一索引的记录，则更新记录的数据。
     * <p>
     * MySQL语法原型：insert into table ... on duplicate key update ...
     * 
     * @param entity
     *            新增或修改的记录对象
     * @return 受影响的记录数
     */
    int upsertSelective(${allFieldsShortName} entity);

    /**
     * 批量插入数据库记录的接口，此接口有个限制：每条待入库的记录的字段名称要一致，而且那些入库的字段值都不能为null，
     * 
     * 否则会抛java.sql.SQLException: Column count doesn't match value count的异常。
     * 
     * @param entities
     *            待批量入库的对象列表
     * @return 受影响的记录数
     */
    int batchInsertSelective(List<${allFieldsShortName}> entities);

    /**
     * 根据条件进行分页查询。
     * 
     * @param example
     *            查询条件，包括排序的字段，参见<code>orderByClause</code>字段
     * @param pageable
     *            分页信息
     * @return 符合查询条件的指定页数据
     */
    Page<${entityName}> pageableSelect(${exampleShortName} example, Pageable pageable);

}
