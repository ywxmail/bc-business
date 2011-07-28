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

    YEAR varchar(100) NOT NULL COMMENT '年份',
    MONTH varchar(255) NOT NULL COMMENT '月份',
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


-- 证件
create table BS_CERT (
    ID int NOT NULL auto_increment,
    CODE varchar(255) NOT NULL COMMENT '证件号',
    NAME varchar(255) NOT NULL COMMENT '证件简称',
    FULL_NAME varchar(255) COMMENT '证件全称',
    LICENCER varchar(255) COMMENT '发证机关',
    START_DATE datetime COMMENT '生效日期',
    END_DATE datetime COMMENT '到期日期',
    primary key (ID)
) COMMENT='证件';
ALTER TABLE BS_CERT ADD INDEX BS_CERT_CODE (CODE);
ALTER TABLE BS_CERT ADD INDEX BS_CERT_NAME (NAME);

-- 证件:身份证
create table BS_CERT_IDENTITY (
    ID int NOT NULL,
    ADDRESS varchar(500) NOT NULL COMMENT '地址',
    primary key (ID)
) COMMENT='证件:身份证';
ALTER TABLE BS_CERT_IDENTITY ADD CONSTRAINT BS_CERT4IDENTITY_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:驾驶证
create table BS_CERT_DRIVING (
    ID int NOT NULL,
    MODEL varchar(255) NOT NULL COMMENT '准驾车型',
    primary key (ID)
) COMMENT='证件:驾驶证';
ALTER TABLE BS_CERT_DRIVING ADD CONSTRAINT BS_CERT4DRIVING_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 司机责任人
create table BS_CARMAN (
    ID int NOT NULL auto_increment,
    UID_ varchar(36) NOT NULL COMMENT 'UID',
    STATUS_ int(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
    TYPE_ int(1) default 0 NOT NULL COMMENT '类别:0-司机,1-责任人,2-司机和责任人',
    CODE varchar(255) NOT NULL COMMENT '编号',
    NAME varchar(255) NOT NULL COMMENT '姓名',
    ORDER_ varchar(100) COMMENT '排序号',
    SEX int(1) default 0 NOT NULL COMMENT 'user-性别：0-未设置,1-男,2-女',
    WORK_DATE datetime COMMENT '入职日期',
    primary key (ID)
) COMMENT='司机责任人';

-- 司机责任人与证件的关联
CREATE TABLE BS_CARMAN_CERT (
    MAN_ID int NOT NULL COMMENT '司机责任人ID',
    CERT_ID int NOT NULL COMMENT '证件ID',
    PRIMARY KEY (MAN_ID,CERT_ID)
) COMMENT='司机责任人与证件的关联';
ALTER TABLE BS_CARMAN_CERT ADD CONSTRAINT BSFK_CARMANCERT_MAN FOREIGN KEY (MAN_ID) 
	REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_CARMAN_CERT ADD CONSTRAINT BSFK_CARMANCERT_CERT FOREIGN KEY (CERT_ID) 
	REFERENCES BS_CERT (ID);
