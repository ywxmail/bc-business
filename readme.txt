营运管理子系统相关模块

源码： 
    git@[serverIp]:bc-business.git
    git@[serverIp]:bc-business-webapp.git
    git@[serverIp]:bc-business-test.git

一) 编译发布需要的工具
ant1.8+、maven3+、java1.5+

二) 生成数据库的构建脚本：
    >ant build
    运行后会在build目录下生成如下名称的sql文件
    1) db.[dbtype].drop.sql 删除数据库表的脚本
    2) db.[dbtype].create.sql 数据库的建表脚本
    3) db.[dbtype].data.sql 数据库的初始化数据
    * [dbtype]可能的值为mysql、oracle、mssql，目前仅mysql可用。
