package ${packageName};

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ${domainPackage}.${entityName};
<#if entityName != allFieldsShortName >
import ${domainPackage}.${allFieldsShortName};
</#if>
import ${domainPackage}.${exampleShortName};
import ${servicePackage}.${entityName}Service;

import com.xnou.util.data.Page;
import com.xnou.util.data.PageRequest;
import com.xnou.util.data.Pageable;
import com.xnou.util.data.WrappedResponse;

/**
 * 数据表 ${tableName} 对应的控制器，实现了基本的增删改查接口。
 * 
 * @author ${author!"OU Xingning"}
 * @date ${date?string("yyyy/MM/dd")}
 */
@RestController
@RequestMapping("/generated/${entityNameLowerHyphen}")
public class ${entityName}ControllerGenerated extends AbstractControllerGenerated {

    private static final Logger logger = LoggerFactory.getLogger(${entityName}ControllerGenerated.class);

    @Autowired
    private ${entityName}Service ${entityNameLowerCamel}Service;

    @PostMapping
    public ResponseEntity<WrappedResponse> create(@RequestBody ${allFieldsShortName} ${entityNameLowerCamel}) {

        try {
            int rows = ${entityNameLowerCamel}Service.insertSelective(${entityNameLowerCamel});
            logger.info("Create ${entityName}, ${entityNameLowerCamel}={}, and rows={}", ${entityNameLowerCamel}, rows);
            return this.success(${entityNameLowerCamel});
        } catch (Exception e) {
            logger.error("Create ${entityName} error, ${entityNameLowerCamel}={}", ${entityNameLowerCamel}, e);
            return this.fail(e.getMessage() != null ? e.getMessage() : "internal error.");
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<WrappedResponse> update(@PathVariable("id") ${primaryKeyShortName} id, @RequestBody ${allFieldsShortName} ${entityNameLowerCamel}) {

        ${primaryKeyShortName} objectId = ${entityNameLowerCamel}.getId();
        if (null == objectId) {
            ${entityNameLowerCamel}.setId(id);
        }

		<#if primaryKeyIsLong >
		if (id.longValue() != ${entityNameLowerCamel}.getId().longValue()) {
		<#elseif primaryKeyIsInteger >
		if (id.intValue() != ${entityNameLowerCamel}.getId().intValue()) {
		<#else>
		if (id != ${entityNameLowerCamel}.getId()) {
		</#if>
            logger.warn("Update ${entityName} warn (id inconsistent), expected id={} but actual={}", id, ${entityNameLowerCamel}.getId());
            return this.error("ID inconsistent, expected=" + id + " but actual=" + ${entityNameLowerCamel}.getId());
        }

        try {
            int rows = ${entityNameLowerCamel}Service.updateByPrimaryKeySelective(${entityNameLowerCamel});
            logger.info("Update ${entityName}, ${entityNameLowerCamel}={}, and rows={}", ${entityNameLowerCamel}, rows);
            return this.success(rows > 0);
        } catch (Exception e) {
            logger.error("Update ${entityName} error, ${entityNameLowerCamel}={}", ${entityNameLowerCamel}, e);
            return this.fail(e.getMessage() != null ? e.getMessage() : "internal error.");
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WrappedResponse> delete(@PathVariable("id") ${primaryKeyShortName} id) {

        try {
            int rows = ${entityNameLowerCamel}Service.deleteByPrimaryKey(id);
            logger.info("Delete ${entityName}, id={}, and rows={}", id, rows);
            return this.success(rows > 0);
        } catch (Exception e) {
            logger.error("Delete ${entityName} error, id={}", id, e);
            return this.fail(e.getMessage() != null ? e.getMessage() : "internal error.");
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<WrappedResponse> findById(@PathVariable("id") ${primaryKeyShortName} id) {

        ${allFieldsShortName} ${entityNameLowerCamel} = ${entityNameLowerCamel}Service.selectByPrimaryKey(id);
        if (null != ${entityNameLowerCamel}) {
            return this.success(${entityNameLowerCamel});
        } else {
            return this.error(404, "resource not found, id=" + id);
        }

    }

    @GetMapping("/list")
    public ResponseEntity<WrappedResponse> findAll(@RequestParam(value = "limit", required = false) Integer limit) {

        int limitInt = (null == limit || limit.intValue() <= 0) ? DEFAULT_LIST_LIMIT : limit.intValue();

        ${exampleShortName} example = new ${exampleShortName}();
        example.setOrderByClause("id DESC LIMIT " + limitInt);
        List<${entityName}> ${entityNameLowerCamel}List = ${entityNameLowerCamel}Service.selectByExample(example);
        return this.success(${entityNameLowerCamel}List);

    }

    @GetMapping("/all")
    public ResponseEntity<WrappedResponse> findAllPageable(
        @RequestParam(value = "size", required = false) Integer pageSize,
        @RequestParam(value = "page", required = false) Integer pageNumber) {

        // 查询条件
        ${exampleShortName} example = new ${exampleShortName}();
        example.setOrderByClause(" id DESC ");

        // 分页信息
        int pageNumberInt = (null == pageNumber || pageNumber.intValue() <= 0) ? 1 : pageNumber.intValue();
        int pageSizeInt = (null == pageSize || pageSize.intValue() <= 0) ? DEFAULT_PAGE_SIZE : pageSize.intValue();
        Pageable pageable = new PageRequest(pageNumberInt, pageSizeInt);

        Page<${entityName}> page = ${entityNameLowerCamel}Service.pageableSelect(example, pageable);
        return this.success(page);

    }

}
