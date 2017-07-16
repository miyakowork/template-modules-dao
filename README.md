# template-modules-dao
template-projects中的关系型数据库dao部分封装 
## 更新日志 ## 
#### 3.0.0.RELEASE
- template框架所有项目更新为同一版本，以便于使用和管理维护
- 未做任何改动，仅变动版本号
#### 1.7.4.RELEASE
- 修复findListBeanByMap泛型方法错误
#### 1.7.3.RELEASE
- 修复泛型BUG
#### 1.7.2.RELEASE
- 更新相关泛型
- 更新pom依赖为最新
#### 1.7.1.RELEASE
- 更新pom中的依赖为最新
#### 1.7.0.RELEASE
- 修改dao中的一些方法
- 增加dao方：法insertxxxGenKey类的方法
- 修正dao中xxxxMap某些方法的bug
#### 1.6.5.RELEASE
- 修改template-utils-pojo为2.0.0.RELEASE版本
#### 1.6.4.RELEASE
- 修改批量插入Bean的方法中的Collection参数为问号类型参数形式
#### 1.6.3.RELEASE
- AncestorDao增加findBeanByBean的方法
#### 1.6.2.RELEASE
- release版本发布 
#### 1.6.2 更新日志
- 使用ThreadLocal隔离多个线程会同时操作setDynamicDao的问题.
- 注释英文说明下：因为以前用的jdk8环境下的maven打包，中文注释会报错所以以前是全英文注释，后面换成jdk7环境下就不会有问题。
- 以后注释会慢慢改回中文
#### 1.5.4 更新日志
- 不推荐使用SimpleJdbcInsert来自增插入，会出现非预期性的结果
- 增加了额外2个新的自增插入方法
#### 1.5.3 更新日志
- 存储AncestorDao的Map更改为线程安全的HashTable类
#### 1.5.2 更新日志
- template-utils-pojo 依赖版本改为1.2.0
#### 1.5.1 更新日志
- 排除template-modules-pojo依赖改为template-utils-pojo，且方式改为provided，需开发者自己依赖
#### 1.5.0 更新日志
- 增加 postgresql 模块支持
- 包名变更 me.wuwenbin 改为 org.templateproject
#### 1.3.1 更新日志
- 把sql部分单独提出作为utils子项目，命名为template-utils-sql
#### 1.3.0 更新日志
- 更改多数据源注释方式，增加type参数
- 增加多种数据库支持，目前支持mysql、h2、oracle、sqlite
- 由于各种数据与预发不同，故在原先posterityDao之下增加集成的子类，本身改为抽象类。
- 简化@SQLColumn的属性
#### 1.2.0 更新日志
- 更改为模块目录，由template-dao更名为template-modules-dao
- 优化注释说明
- bug fixed
#### 1.0.0 发布日志
- 初始版本发布

## 开始使用 ##
> maven 坐标  最新版本号请至中央仓库查询 当前 ```1.6.1```（2017年5月4日）
> maven中央仓库会延迟1~2天
```xml
<dependency>
  <groupId>me.wuwenbin</groupId>
  <artifactId>template-modules-dao</artifactId>
  <version>${template-version}</version>
</dependency>
```
## 要求 
- jdk 1.7 以上
- springframework 4.1.7.RELEASE 以上

## 文档
- [入门指南](https://github.com/miyakowork/template-modules-dao/wiki/入门)
- [主要方法](https://github.com/miyakowork/template-modules-dao/wiki/主要方法API)
