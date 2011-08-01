-- bc营运管理子系统的建表脚本,所有表名须附带前缀"BS_"
-- 运行此脚本之前需先运行平台的建表脚本framework.db.mysql.create.sql

-- 车队信息
CREATE TABLE BS_MOTORCADE(
   ID                   INT NOT NULL auto_increment,
   UID_                 VARCHAR(36),
   CODE                 VARCHAR(255) COMMENT '编码',
   NAME                 VARCHAR(255) NOT NULL COMMENT '简称',
   FULLNAME             VARCHAR(255) COMMENT '全称',
   PAYMENT_DATE         DATETIME COMMENT '缴费日期',
   COMPANY              VARCHAR(255) COMMENT '公司',
   COLOR                VARCHAR(255) COMMENT '颜色',
   ADDRESS              VARCHAR(255) COMMENT '地址',
   PRINCIPAL            VARCHAR(500) COMMENT '负责人',
   PHONE                VARCHAR(255) COMMENT '电话',
   FAX                  VARCHAR(255) COMMENT '传真',
   DESC_                VARCHAR(4000) COMMENT '备注',
   STATUS_              INT(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
   FILE_DATE            DATETIME NOT NULL COMMENT '创建时间',
   AUTHOR_ID            INT NOT NULL COMMENT '创建人ID',
   AUTHOR_NAME          VARCHAR(100) NOT NULL COMMENT '创建人姓名',
   AUTHOR_DEPART_ID     INT COMMENT '创建人所在部门ID',
   AUTHOR_DEPART_NAME   VARCHAR(255) COMMENT '创建人所在部门名称',
   AUTHOR_UNIT_ID       INT NOT NULL COMMENT '创建人所在单位ID',
   AUTHOR_UNIT_NAME     VARCHAR(255) NOT NULL COMMENT '创建人所在单位名称',
   MODIFIER_ID          INT COMMENT '最后修改人ID',
   MODIFIER_NAME        VARCHAR(255) COMMENT '最后修改人名称',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   PRIMARY KEY (ID)
) COMMENT '车队';
ALTER TABLE BS_MOTORCADE ADD CONSTRAINT BSFK_MOTORCADE_AUTHORID FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR (ID);

-- 车队历史车辆数
CREATE TABLE BS_MOTORCADE_CARQUANTITY(
   ID                   INT NOT NULL auto_increment,
   MOTORCADE_ID         INT NOT NULL COMMENT '所属车队ID',
   YEAR_                NUMERIC(4,0) NOT NULL COMMENT '年份',
   MONTH_               NUMERIC(2,0) NOT NULL COMMENT '月份',
   QUANTITY             INT NOT NULL COMMENT '车辆数',
   FILE_DATE            DATETIME NOT NULL COMMENT '创建时间',
   AUTHOR_ID            INT NOT NULL COMMENT '创建人ID',
   AUTHOR_NAME          VARCHAR(100) NOT NULL COMMENT '创建人姓名',
   AUTHOR_DEPART_ID     INT COMMENT '创建人所在部门ID',
   AUTHOR_DEPART_NAME   VARCHAR(255) COMMENT '创建人所在部门名称',
   AUTHOR_UNIT_ID       INT NOT NULL COMMENT '创建人所在单位ID',
   AUTHOR_UNIT_NAME     VARCHAR(255) NOT NULL COMMENT '创建人所在单位名称',
   MODIFIER_ID          INT COMMENT '最后修改人ID',
   MODIFIER_NAME        VARCHAR(255) COMMENT '最后修改人名称',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   PRIMARY KEY (ID)
) COMMENT '车队历史车辆数';
ALTER TABLE BS_MOTORCADE_CARQUANTITY ADD CONSTRAINT BSFK_CARQUANTITY_AUTHORID FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR (ID);
ALTER TABLE BS_MOTORCADE_CARQUANTITY ADD CONSTRAINT BSFK_CARQUANTITY_MOTORCADE FOREIGN KEY (MOTORCADE_ID)
      REFERENCES BS_MOTORCADE (ID);


-- 车队负责人信息
create table BS_CHARGER (
    ID int NOT NULL auto_increment,
    UID_ varchar(36) NOT NULL  COMMENT '关联附件的标识号',
   
    FILE_DATE datetime NOT NULL COMMENT '创建时间',

    AUTHOR_ID int NOT NULL COMMENT '创建人ID',
    AUTHOR_NAME varchar(100) NOT NULL COMMENT '创建人姓名',
    AUTHOR_DEPART_ID int COMMENT '创建人所在部门ID，如果用户直接隶属于单位，则为null',
    AUTHOR_DEPART_NAME varchar(255) COMMENT '创建人所在部门名称，如果用户直接隶属于单位，则为null',
    AUTHOR_UNIT_ID int NOT NULL COMMENT '创建人所在单位ID',
    AUTHOR_UNIT_NAME varchar(255) NOT NULL COMMENT '创建人所在单位名称',

   
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
	

-- 证件
create table BS_CERT (
    ID int NOT NULL auto_increment,
    STATUS_ int(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
    UID_ varchar(36) NOT NULL,
    CERT_CODE varchar(255) NOT NULL COMMENT '证件号',
    CERT_NAME varchar(255) NOT NULL COMMENT '证件简称',
    CERT_FULL_NAME varchar(255) COMMENT '证件全称',
    LICENCER varchar(255) COMMENT '发证机关',
    ISSUE_DATE datetime COMMENT '发证日期',
    START_DATE datetime COMMENT '生效日期',
    END_DATE datetime COMMENT '到期日期',
    EXT_STR1 varchar(255),
    EXT_STR2 varchar(255),
    EXT_STR3 varchar(255),
    EXT_NUM1 int(19),
    EXT_NUM2 int(19),
    EXT_NUM3 int(19),
    
    FILE_DATE datetime NOT NULL COMMENT '创建时间',
    AUTHOR_ID int NOT NULL COMMENT '创建人ID',
    AUTHOR_NAME varchar(100) NOT NULL COMMENT '创建人姓名',
    AUTHOR_DEPART_ID int COMMENT '创建人所在部门ID，如果用户直接隶属于单位，则为null',
    AUTHOR_DEPART_NAME varchar(255) COMMENT '创建人所在部门名称，如果用户直接隶属于单位，则为null',
    AUTHOR_UNIT_ID int NOT NULL COMMENT '创建人所在单位ID',
    AUTHOR_UNIT_NAME varchar(255) NOT NULL COMMENT '创建人所在单位名称',
    MODIFIER_ID int COMMENT '最后修改人ID',
    MODIFIER_NAME varchar(255) COMMENT '最后修改人名称',
    MODIFIED_DATE datetime COMMENT '最后修改时间',
    primary key (ID)
) COMMENT='证件';
ALTER TABLE BS_CERT ADD INDEX BSIDX_CERT_CODE (CERT_CODE);
ALTER TABLE BS_CERT ADD INDEX BSIDX_CERT_NAME (CERT_NAME);

-- 证件:居民身份证
create table BS_CERT_IDENTITY (
    ID int NOT NULL,
    NAME varchar(255) COMMENT '姓名',
    SEX int(1) COMMENT '性别(0-未设置,1-男,2-女)',
    BIRTHDATE datetime COMMENT '出生日期',
    NATION varchar(255) COMMENT '民族',
    ADDRESS varchar(500) COMMENT '住址',
    primary key (ID)
) COMMENT='证件:居民身份证';
ALTER TABLE BS_CERT_IDENTITY ADD CONSTRAINT BSFK_CERT4IDENTITY_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:机动车驾驶证
create table BS_CERT_DRIVING (
    ID int NOT NULL,
    NAME varchar(255) COMMENT '姓名',
    SEX int(1) COMMENT '性别(0-未设置,1-男,2-女)',
    BIRTHDATE datetime COMMENT '出生日期',
    NATION varchar(255) COMMENT '国籍',
    ADDRESS varchar(500) COMMENT '地址',
    MODEL varchar(255) COMMENT '准驾车型',
    RECEIVEDATE datetime COMMENT '初次领证日期',
    VALIDFOR varchar(255) COMMENT '有效期限',
    ARCHIVENO varchar(255) COMMENT '档案编号',
    RECORD varchar(255) COMMENT '记录',
    primary key (ID)
) COMMENT='证件:机动车驾驶证';
ALTER TABLE BS_CERT_DRIVING ADD CONSTRAINT BSFK_CERT4DRIVING_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:从业资格证
create table BS_CERT_CYZG (
    ID int NOT NULL,
   NAME                 varchar(255) comment '姓名',
   SEX                  int(1) comment '性别(0-未设置,1-男,2-女)',
   BIRTHDATE            datetime comment '出生日期',
   NATION               varchar(255) comment '民族',
   ADDRESS              varchar(500) comment '住址',
   SCOPE_               varchar(255) comment '从业资格',
   IDENTITY_NO          varchar(255) comment '身份证件号',
   SERVICE_UNIT         varchar(500) comment '服务单位',
   primary key (ID)
) COMMENT '证件:从业资格证';
ALTER TABLE BS_CERT_CYZG ADD CONSTRAINT BSFK_CERT4CYZG_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:服务资格证
create table BS_CERT_FWZG (
    ID int NOT NULL,
   NAME                 varchar(255) comment '姓名',
   SEX                  int(1) comment '性别(0-未设置,1-男,2-女)',
   BIRTHDATE            datetime comment '出生日期',
   NATION               varchar(255) comment '民族',
   ADDRESS              varchar(500) comment '住址',
   LEVEL_               varchar(255) comment '等级',
   SERVICE_UNIT         varchar(500) comment '服务单位',
   primary key (ID)
)COMMENT '证件:服务资格证';
ALTER TABLE BS_CERT_FWZG ADD CONSTRAINT BSFK_CERT4FWZG_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:驾驶培训证
create table BS_CERT_JSPX (
    ID int NOT NULL,
   NAME                 varchar(255) comment '姓名',
   SEX                  int(1) comment '性别(0-未设置,1-男,2-女)',
   BIRTHDATE            datetime comment '出生日期',
   NATION               varchar(255) comment '民族',
   ADDRESS              varchar(500) comment '住址',
   DOMAIN               varchar(255) comment '培训专业',
   TRAIN_DATE           datetime comment '培训时间',
   TRAIN_HOUR           int(3) comment '培训学时',
   GRADE1               int(3) comment '理论知识考核成绩',
   GRADE2               varchar(10) comment '操作技能考核成绩',
   GRADE3               varchar(10) comment '评定成绩',
   IDENTITY_NO          varchar(255) comment '身份证件号',
   primary key (ID)
)COMMENT '证件:驾驶培训证';
ALTER TABLE BS_CERT_JSPX ADD CONSTRAINT BSFK_CERT4JSPX_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);
    
-- 证件：机动车行驶证
CREATE TABLE BS_CERT_VEHICELICENSE(
   ID                   int NOT NULL,
   FACTORY              VARCHAR(255) COMMENT '品牌型号，如“桑塔纳SVW7182QQD”',
   PLATE                VARCHAR(255) COMMENT '车牌及号码，如“粤AC4X74”',
   OWNER                VARCHAR(255) COMMENT '业户名称',
   ADDRESS              VARCHAR(255) COMMENT '地址',
   USE_CHARACTER        VARCHAR(255) COMMENT '使用性质',
   VEHICE_TYPE          VARCHAR(255) COMMENT '车辆类型',
   VIN                  VARCHAR(255) COMMENT '车辆识别代号',
   ENGINE_NO            VARCHAR(255) COMMENT '发动机号码',
   REGISTER_DATE        DATETIME COMMENT '注册日期',
   ARCHIVE_NO           VARCHAR(255) COMMENT '档案编号',
   DIM_LEN              int COMMENT '外廓尺寸：长，单位MM',
   DIM_WIDTH            int COMMENT '外廓尺寸：宽，单位MM',
   DIM_HEIGHT           int COMMENT '外廓尺寸：高，单位MM',
   TOTAL_WEIGHT         int COMMENT '总质量，单位KG',
   CURB_WEIGHT          int COMMENT '整备质量，单位KG',
   ACCESS_WEIGHT        int COMMENT '核定载质量，单位KG',
   ACCESS_COUNT         int COMMENT '核定载人数',
   SCRAP_DATE           DATETIME COMMENT '强制报废日期',
   DESC_                VARCHAR(500) COMMENT '备注',
   RECORD_              VARCHAR(500) COMMENT '检验记录',
   PRIMARY KEY (ID)
) COMMENT '证件：机动车行驶证';
ALTER TABLE BS_CERT_VEHICELICENSE ADD CONSTRAINT BSFK_CERT4VEHICELICENSE_CERT FOREIGN KEY (ID)
      REFERENCES BS_CERT (ID);
	  
-- 证件：道路运输证
CREATE TABLE BS_CERT_ROADTRANSPORT(
   ID                   int NOT NULL,
   FACTORY              VARCHAR(255) COMMENT '品牌型号，如“桑塔纳SVW7182QQD”',
   PLATE                VARCHAR(255) COMMENT '车牌及号码，如“粤AC4X74”',
   OWNER                VARCHAR(255) COMMENT '业户名称',
   ADDRESS              VARCHAR(255) COMMENT '地址',
   BS_CERT_NO           VARCHAR(255) COMMENT '经营许可证号',
   SEAT                 VARCHAR(255) COMMENT '吨（座）位',
   DIM_LEN              int COMMENT '外廓尺寸：长，单位MM',
   DIM_WIDTH            int COMMENT '外廓尺寸：宽，单位MM',
   DIM_HEIGHT           int COMMENT '外廓尺寸：高，单位MM',
   SCOPE_               VARCHAR(255) COMMENT '经营范围',
   LEVEL_               VARCHAR(255) COMMENT '技术等级',
   PRIMARY KEY (ID)
) COMMENT '证件：道路运输证';
ALTER TABLE BS_CERT_ROADTRANSPORT ADD CONSTRAINT BSFK_CERT4ROADTRANSPORT_CERT FOREIGN KEY (ID)
      REFERENCES BS_CERT (ID);


-- 司机责任人
create table BS_CARMAN (
    ID int NOT NULL auto_increment,
    UID_ varchar(36) NOT NULL COMMENT 'UID',
    STATUS_ int(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
    TYPE_ int(1) default 0 NOT NULL COMMENT '类别:0-司机,1-责任人,2-司机和责任人',
    NAME varchar(255) NOT NULL COMMENT '姓名',
    ORDER_ varchar(100) COMMENT '排序号',
    SEX int(1) default 0 NOT NULL COMMENT 'user-性别：0-未设置,1-男,2-女',
    WORK_DATE datetime COMMENT '入职日期',
    ORIGIN               varchar(255) comment '籍贯',
    REGION_              varchar(255) comment '区域',
    HOUSE_TYPE           varchar(255) comment '户口类型',
    BIRTHDATE datetime comment '出生日期',
    FORMER_UNIT          varchar(255) comment '入职原单位',
    ADDRESS              varchar(500) comment '身份证住址',
    ADDRESS1             varchar(500) comment '暂住地址',
    PHONE                varchar(500) comment '电话',
    PHONE1               varchar(500) comment '电话1',
    FILE_DATE datetime NOT NULL COMMENT '创建时间',
    AUTHOR_ID int NOT NULL COMMENT '创建人ID',
    AUTHOR_NAME varchar(100) NOT NULL COMMENT '创建人姓名',
    AUTHOR_DEPART_ID int COMMENT '创建人所在部门ID，如果用户直接隶属于单位，则为null',
    AUTHOR_DEPART_NAME varchar(255) COMMENT '创建人所在部门名称，如果用户直接隶属于单位，则为null',
    AUTHOR_UNIT_ID int NOT NULL COMMENT '创建人所在单位ID',
    AUTHOR_UNIT_NAME varchar(255) NOT NULL COMMENT '创建人所在单位名称',
    MODIFIER_ID int COMMENT '最后修改人ID',
    MODIFIER_NAME varchar(255) COMMENT '最后修改人名称',
    MODIFIED_DATE datetime COMMENT '最后修改时间',
    primary key (ID)
) COMMENT='司机责任人';
ALTER TABLE BS_CARMAN ADD CONSTRAINT BSFK_CARMAN_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);

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
    
-- 车辆
CREATE TABLE BS_CAR(
   ID                   INT NOT NULL auto_increment,
   UID_                 VARCHAR(36) NOT NULL,
   STATUS_              INT(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
   BELONG_UNITID        INT NOT NULL COMMENT '所属单位ID',
   MANAGE_UNITID        INT COMMENT '分管单位ID',
   MOTORCADE_ID         INT NOT NULL COMMENT '所属车队ID',
   BS_TYPE              VARCHAR(255) COMMENT '营运性质',
   CODE                 VARCHAR(255) COMMENT '自编号',
   ORIGIN_NO            VARCHAR(255) COMMENT '原车号',
   PLATE_TYPE           VARCHAR(255) COMMENT '车牌归属，如“粤A”',
   PLATE_NO             VARCHAR(255) COMMENT '车牌号码，如“C4X74”',
   VIN                  VARCHAR(255) COMMENT '车辆识别代号',
   FACTORY_TYPE         VARCHAR(255) COMMENT '厂牌类型，如“桑塔纳”',
   FACTORY_MODEL        VARCHAR(255) COMMENT '厂牌型号，如“SVW7182QQD”',
   REDISTER_DATE        VARCHAR(255) COMMENT '登记日期',
   OPERATE_DATE         VARCHAR(255) COMMENT '投产日期',
   SCRAP_DATE           VARCHAR(255) COMMENT '报废日期',
   FACTORY_DATE         VARCHAR(255) COMMENT '出厂日期',
   REDISTER_NO          VARCHAR(255) COMMENT '机动车登记编号',
   LEVEL_               VARCHAR(255) COMMENT '车辆定级',
   COLOR                VARCHAR(255) COMMENT '颜色',
   RNGINE_NO            VARCHAR(255) COMMENT '发动机号码',
   RNGINE_TYPE          VARCHAR(255) COMMENT '发动机类型',
   FUEL_TYPE            VARCHAR(255) COMMENT '燃料类型，如“汽油”',
   DISPLACEMENT         INT COMMENT '排量，单位ML',
   POWER                NUMERIC(19,2) COMMENT '功率，单位KW',
   TURN_TYPE            VARCHAR(255) COMMENT '转向方式，如“方向盘”',
   TIRE_COUNT           INT COMMENT '轮胎数',
   TIRE_STANDARD        VARCHAR(255) COMMENT '轮胎规格',
   AXIS_DISTANCE        INT COMMENT '轴距',
   AXIS_COUNT           INT COMMENT '轴数',
   PIECE_COUNT          INT COMMENT '后轴钢板弹簧片数',
   DIM_LEN              INT COMMENT '外廓尺寸：长，单位MM',
   DIM_WIDTH            INT COMMENT '外廓尺寸：宽，单位MM',
   DIM_HEIGHT           INT COMMENT '外廓尺寸：高，单位MM',
   TOTAL_WEIGHT         INT COMMENT '总质量，单位KG',
   ACCESS_WEIGHT        INT COMMENT '核定载质量，单位KG',
   ACCESS_COUNT         INT COMMENT '核定载人数',
   ORIGINAL_VALUE       NUMERIC(19,2) COMMENT '固定资产原值，单位元',
   INVOICE_NO1          VARCHAR(255) COMMENT '购车发票号',
   INVOICE_NO2          VARCHAR(255) COMMENT '购置税发票号',
   PAYMENT_TYPE         VARCHAR(255) COMMENT '缴费日',
   CERT_NO1             VARCHAR(255) COMMENT '购置税证号',
   CERT_NO2             VARCHAR(255) COMMENT '经营权使用证号',
   CERT_NO3             VARCHAR(255) COMMENT '强检证号',
   TAXIMETER_FACTORY    VARCHAR(255) COMMENT '计价器制造厂',
   TAXIMETER_TYPE       VARCHAR(255) COMMENT '计价器型号',
   TAXIMETER_NO         VARCHAR(255) COMMENT '计价器出厂编号',
   DESC1                VARCHAR(4000) COMMENT '备注1',
   DESC2                VARCHAR(4000) COMMENT '备注2',
   DESC3                VARCHAR(4000) COMMENT '备注3',
   FILE_DATE            DATETIME NOT NULL COMMENT '创建时间',
   AUTHOR_ID            INT COMMENT '创建人ID',
   AUTHOR_NAME          VARCHAR(100) NOT NULL COMMENT '创建人姓名',
   AUTHOR_DEPART_ID     INT COMMENT '创建人所在部门ID',
   AUTHOR_DEPART_NAME   VARCHAR(255) COMMENT '创建人所在部门名称',
   AUTHOR_UNIT_ID       INT NOT NULL COMMENT '创建人所在单位ID',
   AUTHOR_UNIT_NAME     VARCHAR(255) NOT NULL COMMENT '创建人所在单位名称',
   MODIFIER_ID          INT COMMENT '最后修改人ID',
   MODIFIER_NAME        VARCHAR(255) COMMENT '最后修改人名称',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   PRIMARY KEY (ID)
) COMMENT '车辆';
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_AUTHORID FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_MANAGEUNITID FOREIGN KEY (MANAGE_UNITID)
      REFERENCES BC_IDENTITY_ACTOR (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_MOTORCADEID FOREIGN KEY (MOTORCADE_ID)
      REFERENCES BS_MOTORCADE (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_UNITID FOREIGN KEY (BELONG_UNITID)
      REFERENCES BC_IDENTITY_ACTOR (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- 车辆与证件的关联
CREATE TABLE BS_CAR_CERT(
   CAR_ID               INT NOT NULL COMMENT '车辆ID',
   CERT_ID              INT NOT NULL COMMENT '证件ID',
   PRIMARY KEY (CAR_ID, CERT_ID)
) COMMENT '车辆与证件的关联';
ALTER TABLE BS_CAR_CERT ADD CONSTRAINT BSFK_CAR_CERT_CARID FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE BS_CAR_CERT ADD CONSTRAINT BSFK_CAR_CERT_CERTID FOREIGN KEY (CERT_ID)
      REFERENCES BS_CERT (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
