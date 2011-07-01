-- bc营运管理子系统的建表脚本,所有表名须附带前缀"BS_"
-- 运行此脚本之前需先运行平台的建表脚本framework.db.mysql.create.sql

-- 车辆 
create table BS_CAR (
    ID int NOT NULL auto_increment,
    UNIT_ID int COMMENT '所属单位ID',
    NAME varchar(500) NOT NULL COMMENT '名称',
    DESC_ text COMMENT '备注',
    primary key (ID)
) COMMENT='车辆';
