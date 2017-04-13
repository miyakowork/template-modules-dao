# template-modules-dao
template-projects中的关系型数据库dao部分封装 
## 更新日志 ## 
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
> maven 坐标  最新版本号请至中央仓库查询 当前 ```2.0.0```（2017年4月13日）
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
