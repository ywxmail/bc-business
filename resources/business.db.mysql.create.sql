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
    PAYMENT_DATE datetime NOT NULL COMMENT '缴费日期',
    COMPANY varchar(255) NOT NULL COMMENT '公司',
    COLOUR varchar(255)  COMMENT '颜色',
    ADDRESS varchar(255)  COMMENT '地址',
    PRINCIPAL varchar(255) NOT NULL COMMENT '负责人',
    PHONE varchar(255)  COMMENT '电话',
    FAX varchar(255)  COMMENT '传真',
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
    ID int NOT NULL auto_increment,
    
    UID_ varchar(36)  COMMENT '关联附件的标识号',
   
    FILE_DATE datetime NOT NULL COMMENT '创建时间',

    AUTHOR_ID int NOT NULL COMMENT '创建人ID',
    AUTHOR_NAME varchar(100) NOT NULL COMMENT '创建人姓名',
    DEPART_ID int COMMENT '创建人所在部门ID，如果用户直接隶属于单位，则为null',
    DEPART_NAME varchar(255) COMMENT '创建人所在部门名称，如果用户直接隶属于单位，则为null',
    UNIT_ID int NOT NULL COMMENT '创建人所在单位ID',
    UNIT_NAME varchar(255) NOT NULL COMMENT '创建人所在单位名称',

    MOTORCADE_ID int NOT NULL COMMENT '车队ID',
    MOTORCADE_NAME varchar(100) NOT NULL COMMENT '车队名',

    YEAR int(4) NOT NULL COMMENT '年份',
    MONTH int(2) NOT NULL COMMENT '月份',
    CARQUANTITY int(4) NOT NULL COMMENT '车辆数',
    
    MODIFIER_ID int COMMENT '最后修改人ID',
    MODIFIER_NAME varchar(255) COMMENT '最后修改人名称',
    MODIFIED_DATE datetime COMMENT '最后修改时间',
    STATUS_ int(1)  COMMENT '状态：0-已禁用,1-启用中,2-已删除',
    
    primary key (ID)
)COMMENT='查看历史车辆数';
ALTER TABLE BS_HISTORY_CAR_QUANTITY ADD CONSTRAINT BS_HISTORY_CAR_QUANTITY_MOTORCADE FOREIGN KEY (MOTORCADE_ID) 
	REFERENCES BS_MOTORCADE (ID);
ALTER TABLE BS_HISTORY_CAR_QUANTITY ADD CONSTRAINT BS_HISTORY_CAR_QUANTITY_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);


-- 车队负责人信息
create table BS_CHARGER (
    ID int NOT NULL auto_increment,
    UID_ varchar(36) NOT NULL  COMMENT '关联附件的标识号',
   
    FILE_DATE datetime NOT NULL COMMENT '创建时间',

    AUTHOR_ID int NOT NULL COMMENT '创建人ID',
    AUTHOR_NAME varchar(100) NOT NULL COMMENT '创建人姓名',
    DEPART_ID int COMMENT '创建人所在部门ID，如果用户直接隶属于单位，则为null',
    DEPART_NAME varchar(255) COMMENT '创建人所在部门名称，如果用户直接隶属于单位，则为null',
    UNIT_ID int NOT NULL COMMENT '创建人所在单位ID',
    UNIT_NAME varchar(255) NOT NULL COMMENT '创建人所在单位名称',

   
    CARD varchar(255)  COMMENT '身份证',
    NAME varchar(255) NOT NULL COMMENT '姓名',
    SEX varchar(100)  COMMENT '性别',
    IDADDRESS varchar(255)  COMMENT '身份证地址',
    TWIC varchar(255)  COMMENT '资格证',
    BRITHDATE datetime COMMENT '出生日期',
    PHONE varchar(255)  COMMENT '电话',
    ORDERID varchar(255)  COMMENT '排序号',
    UNIT varchar(255)  COMMENT '分支机构',
    AREA varchar(255)  COMMENT '区域',
    TEMPORARYADDRESS varchar(255)  COMMENT '暂住地址',
    NATIVEPLACE varchar(255)  COMMENT '籍贯',
    DESC_ text COMMENT '备注',
   
    MODIFIER_ID int COMMENT '最后修改人ID',
    MODIFIER_NAME varchar(255) COMMENT '最后修改人名称',
    MODIFIED_DATE datetime COMMENT '最后修改时间',
    STATUS_ int(1)  COMMENT '状态：0-已禁用,1-启用中,2-已删除',
    
    primary key (ID)
) COMMENT='车队负责人信息';
ALTER TABLE BS_CHARGER ADD CONSTRAINT BS_CHARGER_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);
	
	
	