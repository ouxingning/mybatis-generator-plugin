
# 插件列表

  - 自定义注释插件（XnouCommentGenerator）
  - 限制查询返回记录数的插件，使用MySQL的LIMIT语法，在DomainExample类增加offset和limit字段（LimitClausePlugin）
  - 批量插入记录的插件，插入的字段以List中的第一条记录的NOT NULL字段为准（BatchInsertSelectivePlugin）
  - 删除已存在的XML文件，重复生成代码会使得XML文件内容合并，因此需要先删除旧的XML文件，再生成新的（DeleteExistingSqlMapsPlugin）
  - 更新或插入记录的接口，在插入记录时，如果数据库表中已存在唯一索引的记录，则更新记录的数据（UpsertSelectivePlugin）
  - 对一些数值类型的字段，实现 UPDATE table SET field1 = field1 + incrBy1 的操作（NumberIncrementPlugin）
  - 增加分组统计查询的功能，根据条件查询并返回带上COUNT(*)的查询记录（GroupByExamplePlugin）
  - 创建手工编写SQL的脚手架代码，包括manual子包中XML文件和Java代码 (ManualSqlScaffoldPlugin)
  - 生成基础Service和Controller代码的插件（ServiceControllerPlugin）
  
  
# 如何使用

  - MyBatis内置插件
  
  ```xml
  <plugin type="org.mybatis.generator.plugins.ToStringPlugin"></plugin>
  <plugin type="org.mybatis.generator.plugins.SerializablePlugin"></plugin>
  <plugin type="org.mybatis.generator.plugins.EqualsHashCodePlugin"></plugin>
  <plugin type="org.mybatis.generator.plugins.RowBoundsPlugin"></plugin>
  ```
  
  - 自定义插件
  
  ```xml
  <plugin type="com.xnou.mybatis.generator.plugin.LimitClausePlugin"></plugin>
  <plugin type="com.xnou.mybatis.generator.plugin.GroupByExamplePlugin"></plugin>
  <plugin type="com.xnou.mybatis.generator.plugin.NumberIncrementPlugin"></plugin>
  <plugin type="com.xnou.mybatis.generator.plugin.UpsertSelectivePlugin"></plugin>
  <plugin type="com.xnou.mybatis.generator.plugin.BatchInsertSelectivePlugin"></plugin>
  <plugin type="com.xnou.mybatis.generator.plugin.DeleteExistingSqlMapsPlugin"></plugin>
  
  <plugin type="com.xnou.mybatis.generator.plugin.ManualSqlScaffoldPlugin"></plugin>
  <plugin type="com.xnou.mybatis.generator.plugin.EntityServicePlugin"></plugin>
  <plugin type="com.xnou.mybatis.generator.plugin.EntityControllerPlugin"></plugin>
  ```
  
  - 自定义插件的参数配置
  
  ```xml
  <table tableName="user" domainObjectName="User">
      <!-- 是否创建手工SQL的脚手架代码 -->
      <property name="generateManualSql" value="true" /> 
      
      <!-- 是否创建数据表的Service代码 -->
      <property name="generateServiceCode" value="true" /> 
      
      <!-- 是否创建数据表的Controller代码 -->
      <property name="generateControllerCode" value="true" />
  </table>
  ```
  
  - 自定义注释插件
  
  ```xml
  <commentGenerator type="com.xnou.mybatis.generator.plugin.XnouCommentGenerator">
      <property name="author" value="OU Xingning" />
      <property name="dateFormat" value="yyyy/MM/dd" />
      <property name="addRemarkComments" value="true" />
      <property name="supressAllComments" value="false" />
  </commentGenerator>
  ```
  
  - Context的Properties配置
  
  ```xml
  <context id="mysqlTables" targetRuntime="MyBatis3">
      <!-- 覆盖自动生成的Service代码 -->
      <property name="overwriteServiceCode" value="true" />
      
      <!-- 覆盖自动生成的Controller代码 -->
      <property name="overwriteControllerCode" value="true" />
  </context>
  ```
  
---
> 欧兴宁, 2019.01.14

