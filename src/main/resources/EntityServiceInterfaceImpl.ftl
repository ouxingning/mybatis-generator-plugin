package ${packageName};

import org.springframework.stereotype.Service;

import ${superPackageName}.${entityName}Service;
import ${superPackageName}.generated.${entityName}ServiceGeneratedImpl;

/**
 * {@link ${entityName}Service} 接口的实现。
 * 
 * @author ${author!"OU Xingning"}
 * @date ${date?string("yyyy/MM/dd")}
 */
@Service
public class ${entityName}ServiceImpl extends ${entityName}ServiceGeneratedImpl implements ${entityName}Service {

}
