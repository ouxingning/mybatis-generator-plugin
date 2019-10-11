package ${packageName};

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import com.xnou.util.data.Page;
import com.xnou.util.data.Pageable;

import ${clientPackage}.${entityName}Mapper;
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
 * {@link ${entityName}ServiceGenerated} 接口的实现。
 * 
 * @author ${author!"OU Xingning"}
 * @date ${date?string("yyyy/MM/dd")}
 */
public class ${entityName}ServiceGeneratedImpl implements ${entityName}ServiceGenerated {

    @Autowired
    protected ${entityName}Mapper ${entityMapper};

    @Override
    public long countByExample(${exampleShortName} example) {
        return ${entityMapper}.countByExample(example);
    }

    @Override
    public int deleteByExample(${exampleShortName} example) {
        return ${entityMapper}.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(${primaryKeyShortName} pk) {
        return ${entityMapper}.deleteByPrimaryKey(pk);
    }

    @Override
    public int insert(${allFieldsShortName} entity) {
        return ${entityMapper}.insert(entity);
    }

    @Override
    public int insertSelective(${allFieldsShortName} entity) {
        return ${entityMapper}.insertSelective(entity);
    }

    @Override
    public List<${entityName}> selectByExampleWithRowbounds(${exampleShortName} example, RowBounds rowBounds) {
        return ${entityMapper}.selectByExampleWithRowbounds(example, rowBounds);
    }

    @Override
    public List<${entityName}> selectByExample(${exampleShortName} example) {
        return ${entityMapper}.selectByExample(example);
    }

    @Override
    public ${allFieldsShortName} selectByPrimaryKey(${primaryKeyShortName} pk) {
        return ${entityMapper}.selectByPrimaryKey(pk);
    }

    @Override
    public int updateByExampleSelective(${allFieldsShortName} entity, ${exampleShortName} example) {
        return ${entityMapper}.updateByExampleSelective(entity, example);
    }

    @Override
    public int updateByExample(${entityName} entity, ${exampleShortName} example) {
        return ${entityMapper}.updateByExample(entity, example);
    }

    @Override
    public int updateByPrimaryKeySelective(${allFieldsShortName} entity) {
        return ${entityMapper}.updateByPrimaryKeySelective(entity);
    }

    @Override
    public int updateByPrimaryKey(${entityName} entity) {
        return ${entityMapper}.updateByPrimaryKey(entity);
    }
    
    <#-- 判断是否有BLOB字段 -->
    <#if withBLOBs >
    
    @Override
    public List<${allFieldsShortName}> selectByExampleWithBLOBsWithRowbounds(${exampleShortName} example, RowBounds rowBounds) {
        return ${entityMapper}.selectByExampleWithBLOBsWithRowbounds(example, rowBounds);
    }

    @Override
    public List<${allFieldsShortName}> selectByExampleWithBLOBs(${exampleShortName} example) {
        return ${entityMapper}.selectByExampleWithBLOBs(example);
    }
    
     @Override
    public int updateByExampleWithBLOBs(${allFieldsShortName} entity, ${exampleShortName} example) {
        return ${entityMapper}.updateByExampleWithBLOBs(entity, example);
    }
    
    @Override
    public int updateByPrimaryKeyWithBLOBs(${allFieldsShortName} entity) {
        return ${entityMapper}.updateByPrimaryKeyWithBLOBs(entity);
    }
    
    </#if>
    

    @Override
    public List<${allFieldsShortName}GroupBy> groupByExample(${exampleShortName} example) {
        return ${entityMapper}.groupByExample(example);
    }
    
    <#-- 判断是否需要生成 incrByExampleSelective 方法 -->
    <#if needToGenerateIncrByMethod >

    @Override
    public int incrByExampleSelective(${allFieldsShortName} entity, ${exampleShortName} example) {
        return ${entityMapper}.incrByExampleSelective(entity, example);
    }
    
    </#if>

    @Override
    public int upsertSelective(${allFieldsShortName} entity) {
        return ${entityMapper}.upsertSelective(entity);
    }

    @Override
    public int batchInsertSelective(List<${allFieldsShortName}> entities) {
        return ${entityMapper}.batchInsertSelective(entities);
    }

    @Override
    public Page<${entityName}> pageableSelect(${exampleShortName} example, Pageable pageable) {

        // 统计符合条件的总记录数
        long totalItems = ${entityMapper}.countByExample(example);

        // 分页信息
        Page<${entityName}> page = new Page<${entityName}>(pageable, totalItems);

        // 有符合条件的记录才去查询
        if (totalItems > 0L) {
            // 分页条件
            example.setOffset(page.getOffset());
            example.setLimit(page.getLimit());

            // 查询当前页数据，不包含BLOB字段
            List<${entityName}> paginalContents = ${entityMapper}.selectByExample(example);
            page.setContents(paginalContents);
        }

        return page;
    }

}
