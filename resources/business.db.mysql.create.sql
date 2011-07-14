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
-- 车队信息
create table BS_MOTORCADE (
    ID int NOT NULL auto_increment,
    UID_ varchar(36)  COMMENT '关联附件的标识号',
   
    FILE_DATE datetime NOT NULL COMMENT '创建时间',

    AUTHOR_ID int NOT NULL COMMENT '创建人ID',
    AUTHOR_NAME varchar(100) NOT NULL COMMENT '创建人姓名',
    DEPART_ID int COMMENT '创建人所在部门ID，如果用户直接隶属于单位，则为null',
    DEPART_NAME varchar(255) COMMENT '创建人所在部门名称，如果用户直接隶属于单位，则为null',
    UNIT_ID int NOT NULL COMMENT '创建人所在单位ID',
    UNIT_NAME varchar(255) NOT NULL COMMENT '创建人所在单位名称',

   
   CODE varchar(100) NOT NULL COMMENT '编码',
   NAME varchar(255) NOT NULL COMMENT '简称',
   FULLNAME varchar(255) NOT NULL COMMENT '全称',
   STATUS_ int(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
   PAYMENT_DATE datetime COMMENT '缴费日期',
   COMPANY varchar(255) NOT NULL COMMENT '公司',
   COLOUR varchar(255) NOT NULL COMMENT '颜色',
   ADDRESS varchar(255) NOT NULL COMMENT '地址',
   PRINCIPAL varchar(255) NOT NULL COMMENT '负责人',
   PHONE varchar(255) NOT NULL COMMENT '电话',
   FAX varchar(255) NOT NULL COMMENT '传真',
   DESC_ text COMMENT '备注',
   
    MODIFIER_ID int COMMENT '最后修改人ID',
    MODIFIER_NAME varchar(255) COMMENT '最后修改人名称',
    MODIFIED_DATE datetime COMMENT '最后修改时间',
    
    primary key (ID)
) COMMENT='车队信息';
ALTER TABLE BS_MOTORCADE ADD CONSTRAINT BS_MOTORCADE_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);
	
	
-- 查看历史车辆数
create table BS_HISTORY_CAR_QUANTITY(
ID int NOT UNLL auto_increment,
YEAR varchar(100) NOT NULL COMMENT '年份',
MONTH varchar(255) NOT NULL COMMENT '月份',
CARQUANTITY varchar(255) NOT NULL COMMENT '车辆数'
primary key (ID)
)COMMENT='查看历史车辆数';
