-- 数据转换记录
CREATE TABLE DC_RECORD(
  TYPE_         VARCHAR(255) NOT NULL COMMENT '类型',
  FROM_ID       BIGINT NOT NULL COMMENT '旧表数据ID',
  TO_ID         BIGINT NOT NULL COMMENT '新表数据的ID',
  FROM_TABLE    VARCHAR(255) COMMENT '旧数据表名',
  TO_TABLE      VARCHAR(255) COMMENT '新数据表名',
  CREATE_DATE   DATETIME NOT NULL COMMENT '创建时间',
  REMARK        VARCHAR(4000) COMMENT '备注说明',
  MODIFIED_DATE DATETIME COMMENT '最后修改时间'
) COMMENT '数据转换记录';
ALTER TABLE DC_RECORD ADD CONSTRAINT BSUK_RECORD UNIQUE (TYPE_, FROM_ID);


-- bc营运管理子系统的建表脚本,所有表名须附带前缀"BS_"
-- 运行此脚本之前需先运行平台的建表脚本framework.db.mysql.create.sql

-- 车队信息
CREATE TABLE BS_MOTORCADE(
   ID                   BIGINT NOT NULL auto_increment,
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
   AUTHOR_ID            BIGINT NOT NULL COMMENT '创建人ID',
   MODIFIER_ID          BIGINT COMMENT '最后修改人ID',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   PRIMARY KEY (ID)
) COMMENT '车队';
ALTER TABLE BS_MOTORCADE ADD CONSTRAINT BSFK_MOTORCADE_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_MOTORCADE ADD CONSTRAINT BSFK_MOTORCADE_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 车队历史车辆数
CREATE TABLE BS_MOTORCADE_CARQUANTITY(
   ID                   BIGINT NOT NULL auto_increment,
   MOTORCADE_ID         BIGINT NOT NULL COMMENT '所属车队ID',
   YEAR_                NUMERIC(4,0) NOT NULL COMMENT '年份',
   MONTH_               NUMERIC(2,0) NOT NULL COMMENT '月份',
   QUANTITY             BIGINT NOT NULL COMMENT '车辆数',
   FILE_DATE            DATETIME NOT NULL COMMENT '创建时间',
   AUTHOR_ID            BIGINT NOT NULL COMMENT '创建人ID',
   MODIFIER_ID          BIGINT COMMENT '最后修改人ID',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   PRIMARY KEY (ID)
) COMMENT '车队历史车辆数';
ALTER TABLE BS_MOTORCADE_CARQUANTITY ADD CONSTRAINT BSFK_CARQUANTITY_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_MOTORCADE_CARQUANTITY ADD CONSTRAINT BSFK_CARQUANTITY_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_MOTORCADE_CARQUANTITY ADD CONSTRAINT BSFK_CARQUANTITY_MOTORCADE FOREIGN KEY (MOTORCADE_ID)
      REFERENCES BS_MOTORCADE (ID);


-- 车队负责人信息
create table BS_CHARGER (
    ID BIGINT NOT NULL auto_increment,
    UID_ varchar(36) NOT NULL  COMMENT '关联附件的标识号',
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
   
    FILE_DATE datetime NOT NULL COMMENT '创建时间',
    AUTHOR_ID BIGINT NOT NULL COMMENT '创建人ID',
    MODIFIER_ID BIGINT COMMENT '最后修改人ID',
    MODIFIED_DATE datetime COMMENT '最后修改时间',
    STATUS_ int(1)  COMMENT '状态：0-已禁用,1-启用中,2-已删除',
    
    primary key (ID)
) COMMENT='车队负责人信息';
ALTER TABLE BS_CHARGER ADD CONSTRAINT BS_CHARGER_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
	

-- 证件
create table BS_CERT (
    ID BIGINT NOT NULL auto_increment,
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
    AUTHOR_ID BIGINT NOT NULL COMMENT '创建人ID',
    MODIFIER_ID BIGINT COMMENT '最后修改人ID',
    MODIFIED_DATE datetime COMMENT '最后修改时间',
    primary key (ID)
) COMMENT='证件';
ALTER TABLE BS_CERT ADD CONSTRAINT BS_CERT_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CERT ADD CONSTRAINT BS_CERT_MODIFIER FOREIGN KEY (MODIFIER_ID) 
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CERT ADD INDEX BSIDX_CERT_CODE (CERT_CODE);
ALTER TABLE BS_CERT ADD INDEX BSIDX_CERT_NAME (CERT_NAME);

-- 证件:居民身份证
create table BS_CERT_IDENTITY (
    ID BIGINT NOT NULL,
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
    ID BIGINT NOT NULL,
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
    ID BIGINT NOT NULL,
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
    ID BIGINT NOT NULL,
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
    ID BIGINT NOT NULL,
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
   ID                   BIGINT NOT NULL,
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
   DIM_LEN              BIGINT COMMENT '外廓尺寸：长，单位MM',
   DIM_WIDTH            BIGINT COMMENT '外廓尺寸：宽，单位MM',
   DIM_HEIGHT           BIGINT COMMENT '外廓尺寸：高，单位MM',
   TOTAL_WEIGHT         BIGINT COMMENT '总质量，单位KG',
   CURB_WEIGHT          BIGINT COMMENT '整备质量，单位KG',
   ACCESS_WEIGHT        BIGINT COMMENT '核定载质量，单位KG',
   ACCESS_COUNT         BIGINT COMMENT '核定载人数',
   SCRAP_DATE           DATETIME COMMENT '强制报废日期',
   DESC_                VARCHAR(500) COMMENT '备注',
   RECORD_              VARCHAR(500) COMMENT '检验记录',
   PRIMARY KEY (ID)
) COMMENT '证件：机动车行驶证';
ALTER TABLE BS_CERT_VEHICELICENSE ADD CONSTRAINT BSFK_CERT4VEHICELICENSE_CERT FOREIGN KEY (ID)
      REFERENCES BS_CERT (ID);
	  
-- 证件：道路运输证
CREATE TABLE BS_CERT_ROADTRANSPORT(
   ID                   BIGINT NOT NULL,
   FACTORY              VARCHAR(255) COMMENT '品牌型号，如“桑塔纳SVW7182QQD”',
   PLATE                VARCHAR(255) COMMENT '车牌及号码，如“粤AC4X74”',
   OWNER                VARCHAR(255) COMMENT '业户名称',
   ADDRESS              VARCHAR(255) COMMENT '地址',
   BS_CERT_NO           VARCHAR(255) COMMENT '经营许可证号',
   SEAT                 VARCHAR(255) COMMENT '吨（座）位',
   DIM_LEN              BIGINT COMMENT '外廓尺寸：长，单位MM',
   DIM_WIDTH            BIGINT COMMENT '外廓尺寸：宽，单位MM',
   DIM_HEIGHT           BIGINT COMMENT '外廓尺寸：高，单位MM',
   SCOPE_               VARCHAR(255) COMMENT '经营范围',
   LEVEL_               VARCHAR(255) COMMENT '技术等级',
   PRIMARY KEY (ID)
) COMMENT '证件：道路运输证';
ALTER TABLE BS_CERT_ROADTRANSPORT ADD CONSTRAINT BSFK_CERT4ROADTRANSPORT_CERT FOREIGN KEY (ID)
      REFERENCES BS_CERT (ID);


-- 司机责任人
create table BS_CARMAN (
    ID BIGINT NOT NULL auto_increment,
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
    MODEL_               varchar(255) comment '准驾车型',
	LEVEL_               VARCHAR(255) COMMENT '司机等级',
    CERT_IDENTITY        varchar(255) comment '身份证号',
    CERT_DRIVING         varchar(255) comment '驾驶证号',
    CERT_DRIVING_ARCHIVE varchar(255) comment '驾驶证档案号',
    CERT_DRIVING_FIRST_DATE datetime comment '初次领证日期',
    CERT_DRIVING_START_DATE datetime comment '驾驶证起效日期',
    CERT_DRIVING_END_DATE datetime comment '驾驶证过期日期',
    CERT_FWZG            varchar(255) comment '服务资格证号',
    CERT_CYZG            varchar(255) comment '从业资格证号',
    DRIVING_STATUS       varchar(255) comment '驾驶状态',
    EXT_FZJG             varchar(255) comment '分支机构：用于历史数据的保存',
    EXT_ZRR              varchar(255) comment '责任人：用于历史数据的保存',
    GZ                   int(1) comment '驾驶证是否广州:0-否,1-是',
    ACCESS_CERTS         varchar(255) comment '已考取证件：历史数据保存',
    DESC_                varchar(4000) comment '备注',
    FILE_DATE datetime NOT NULL COMMENT '创建时间',
    AUTHOR_ID BIGINT NOT NULL COMMENT '创建人ID',
    MODIFIER_ID BIGINT COMMENT '最后修改人ID',
    MODIFIED_DATE datetime COMMENT '最后修改时间',
    primary key (ID)
) COMMENT='司机责任人';
ALTER TABLE BS_CARMAN ADD CONSTRAINT BSFK_CARMAN_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CARMAN ADD CONSTRAINT BSFK_CARMAN_MODIFIER FOREIGN KEY (MODIFIER_ID) 
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 司机责任人与证件的关联
CREATE TABLE BS_CARMAN_CERT (
    MAN_ID BIGINT NOT NULL COMMENT '司机责任人ID',
    CERT_ID BIGINT NOT NULL COMMENT '证件ID',
    PRIMARY KEY (MAN_ID,CERT_ID)
) COMMENT='司机责任人与证件的关联';
ALTER TABLE BS_CARMAN_CERT ADD CONSTRAINT BSFK_CARMANCERT_MAN FOREIGN KEY (MAN_ID) 
	REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_CARMAN_CERT ADD CONSTRAINT BSFK_CARMANCERT_CERT FOREIGN KEY (CERT_ID) 
	REFERENCES BS_CERT (ID);
    
-- 车辆
CREATE TABLE BS_CAR(
   ID                   BIGINT NOT NULL auto_increment,
   UID_                 VARCHAR(36) NOT NULL,
   STATUS_              INT(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
   UNIT_ID        		BIGINT NOT NULL COMMENT '所属单位ID',
   MOTORCADE_ID         BIGINT NOT NULL COMMENT '所属车队ID',
   BS_TYPE              VARCHAR(255) COMMENT '营运性质',
   CODE                 VARCHAR(255) COMMENT '自编号',
   ORIGIN_NO            VARCHAR(255) COMMENT '原车号',
   PLATE_TYPE           VARCHAR(255) COMMENT '车牌归属，如“粤A”',
   PLATE_NO             VARCHAR(255) COMMENT '车牌号码，如“C4X74”',
   VIN                  VARCHAR(255) COMMENT '车辆识别代号',
   FACTORY_TYPE         VARCHAR(255) COMMENT '厂牌类型，如“桑塔纳”',
   FACTORY_MODEL        VARCHAR(255) COMMENT '厂牌型号，如“SVW7182QQD”',
   REGISTER_DATE        DATETIME COMMENT '登记日期',
   OPERATE_DATE         DATETIME COMMENT '投产日期',
   SCRAP_DATE           DATETIME COMMENT '报废日期',
   FACTORY_DATE         DATETIME COMMENT '出厂日期',
   REGISTER_NO          VARCHAR(255) COMMENT '机动车登记编号',
   LEVEL_               VARCHAR(255) COMMENT '车辆定级',
   COLOR                VARCHAR(255) COMMENT '颜色',
   RNGINE_NO            VARCHAR(255) COMMENT '发动机号码',
   RNGINE_TYPE          VARCHAR(255) COMMENT '发动机类型',
   FUEL_TYPE            VARCHAR(255) COMMENT '燃料类型，如“汽油”',
   DISPLACEMENT         BIGINT COMMENT '排量，单位ML',
   POWER                NUMERIC(19,2) COMMENT '功率，单位KW',
   TURN_TYPE            VARCHAR(255) COMMENT '转向方式，如“方向盘”',
   TIRE_COUNT           BIGINT COMMENT '轮胎数',
   TIRE_STANDARD        VARCHAR(255) COMMENT '轮胎规格',
   AXIS_DISTANCE        BIGINT COMMENT '轴距',
   AXIS_COUNT           BIGINT COMMENT '轴数',
   PIECE_COUNT          BIGINT COMMENT '后轴钢板弹簧片数',
   DIM_LEN              BIGINT COMMENT '外廓尺寸：长，单位MM',
   DIM_WIDTH            BIGINT COMMENT '外廓尺寸：宽，单位MM',
   DIM_HEIGHT           BIGINT COMMENT '外廓尺寸：高，单位MM',
   TOTAL_WEIGHT         BIGINT COMMENT '总质量，单位KG',
   ACCESS_WEIGHT        BIGINT COMMENT '核定载质量，单位KG',
   ACCESS_COUNT         BIGINT COMMENT '核定载人数',
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
   AUTHOR_ID            BIGINT COMMENT '创建人ID',
   MODIFIER_ID          BIGINT COMMENT '最后修改人ID',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   PRIMARY KEY (ID)
) COMMENT '车辆';
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_MOTORCADEID FOREIGN KEY (MOTORCADE_ID)
      REFERENCES BS_MOTORCADE (ID);
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_UNITID FOREIGN KEY (UNIT_ID)
      REFERENCES BC_IDENTITY_ACTOR (ID);

-- 车辆与证件的关联
CREATE TABLE BS_CAR_CERT(
   CAR_ID               BIGINT NOT NULL COMMENT '车辆ID',
   CERT_ID              BIGINT NOT NULL COMMENT '证件ID',
   PRIMARY KEY (CAR_ID, CERT_ID)
) COMMENT '车辆与证件的关联';
ALTER TABLE BS_CAR_CERT ADD CONSTRAINT BSFK_CAR_CERT_CARID FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE BS_CAR_CERT ADD CONSTRAINT BSFK_CAR_CERT_CERTID FOREIGN KEY (CERT_ID)
      REFERENCES BS_CERT (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- 合同
CREATE TABLE BS_CONTRACT(
   ID                   BIGINT NOT NULL auto_increment,
   UID_                 VARCHAR(36) NOT NULL,
   STATUS_              INT(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
   WORD_NO              VARCHAR(255) COMMENT '文书号',
   CODE                 VARCHAR(255) NOT NULL COMMENT '合同号',
   TYPE_                INT(1) NOT NULL COMMENT '合同类型：如劳动合同、承包合同等',
   TRANSACTOR_ID        BIGINT NOT NULL COMMENT '经办人ID',
   SIGN_DATE            DATETIME NOT NULL COMMENT '签订日期',
   START_DATE           DATETIME NOT NULL COMMENT '生效日期',
   END_DATE             DATETIME NOT NULL COMMENT '到期日期',
   CONTENT              VARCHAR(4000) COMMENT '合同内容',
   EXT_STR1             VARCHAR(255) COMMENT '字符扩展域1',
   EXT_STR2             VARCHAR(255) COMMENT '字符扩展域2',
   EXT_STR3             VARCHAR(255) COMMENT '字符扩展域3',
   EXT_NUM1             BIGINT COMMENT '数字扩展域1',
   EXT_NUM2             BIGINT COMMENT '数字扩展域2',
   EXT_NUM3             BIGINT COMMENT '数字扩展域3',
   FILE_DATE            DATETIME NOT NULL COMMENT '创建时间',
   AUTHOR_ID            BIGINT NOT NULL COMMENT '创建人ID',
   MODIFIER_ID          BIGINT COMMENT '最后修改人ID',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   PRIMARY KEY (ID)
) COMMENT '合同';
ALTER TABLE BS_CONTRACT ADD CONSTRAINT BSFK_CONTRACT_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CONTRACT ADD CONSTRAINT BSFK_CONTRACT_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 司机劳动合同
CREATE TABLE BS_CONTRACT_LABOUR(
   ID                   BIGINT NOT NULL,
   DRIVER_ID            BIGINT NOT NULL COMMENT '司机ID',
   CAR_ID               BIGINT NOT NULL COMMENT '车辆ID',
   ADDITION_PROTOCOL    INT(1) NOT NULL COMMENT '补充协议:0-无,1-有',
   PRE_INDUSTRY_NAME    VARCHAR(255) COMMENT '前身行业名称',
   PRE_INDUSTRY_TYPE    INT(1) NOT NULL COMMENT '前身工种行业:0-非特殊,1-特殊',
   HIRING_PROCEDURE     INT(1) NOT NULL COMMENT '招用工手续:0-未办,1-已办',
   DOLE                 INT(1) NOT NULL COMMENT '下岗失业补贴:0-已发,1-未发',
   CERT_NO              VARCHAR(255) NOT NULL COMMENT '资格证号',
   FILING               INT(1) NOT NULL COMMENT '是否已备案',
   PRIMARY KEY (ID)
) COMMENT '司机劳动合同';
ALTER TABLE BS_CONTRACT_LABOUR ADD CONSTRAINT BSFK_CONTRACT4LABOUR_CAR FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE BS_CONTRACT_LABOUR ADD CONSTRAINT BSFK_CONTRACT4LABOUR_CARMAN FOREIGN KEY (DRIVER_ID)
      REFERENCES BS_CARMAN (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE BS_CONTRACT_LABOUR ADD CONSTRAINT BSFK_CONTRACT4LABOUR_CONTRACT FOREIGN KEY (ID)
      REFERENCES BS_CONTRACT (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- 责任人合同
CREATE TABLE BS_CONTRACT_CHARGER(
   ID                   BIGINT NOT NULL,
   CAR_ID               BIGINT NOT NULL COMMENT '车辆ID',
   SIGN_TYPE            VARCHAR(255) NOT NULL COMMENT '签约类型:如新户',
   LOGOUT               INT(1) NOT NULL COMMENT '注销:0-未,1-已',
   TAKEBACK_ORIGIN      INT(1) NOT NULL COMMENT '已经收回原件:0-未1-已',
   INCLUDE_COST         INT(1) NOT NULL COMMENT '包含检审费用:0-不包含,1-包含',
   OLD_CONTENT          VARCHAR(4000) COMMENT '旧合同内容',
   PRIMARY KEY (ID)
) COMMENT '责任人合同:如承包合同';
ALTER TABLE BS_CONTRACT_CHARGER ADD CONSTRAINT BSFK_CONTRACT4CHARGER_CAR FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE BS_CONTRACT_CHARGER ADD CONSTRAINT BSFK_CONTRACT4CHARGER_CONTRACT FOREIGN KEY (ID)
      REFERENCES BS_CONTRACT (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- 责任人与合同的关联
CREATE TABLE BS_CARMAN_CONTRACT(
   CONTRACT_ID          BIGINT NOT NULL COMMENT '合同ID',
   CARMAN_ID            BIGINT NOT NULL COMMENT '责任人ID',
   PRIMARY KEY (CARMAN_ID, CONTRACT_ID)
) COMMENT '责任人与合同的关联';
ALTER TABLE BS_CARMAN_CONTRACT ADD CONSTRAINT BSFK_CARMANCONTRACT_CARMAN FOREIGN KEY (CARMAN_ID)
      REFERENCES BS_CARMAN (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE BS_CARMAN_CONTRACT ADD CONSTRAINT BSFK_CARMANCONTRACT_CONTRACT FOREIGN KEY (CONTRACT_ID)
      REFERENCES BS_CONTRACT_CHARGER (ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- 司机营运车辆
CREATE TABLE BS_CAR_DRIVER(
   ID                   BIGINT NOT NULL auto_increment,
   STATUS_ int(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
   DRIVER_ID            BIGINT NOT NULL COMMENT '司机ID',
   CAR_ID               BIGINT NOT NULL COMMENT '车辆ID',
   CLASSES                VARCHAR(255) NOT NULL COMMENT '营运班次:如正班、副班、顶班',
   START_DATE           DATETIME COMMENT '起始时段',
   END_DATE             DATETIME COMMENT '结束时段',
   FILE_DATE            DATETIME NOT NULL COMMENT '创建时间',
   AUTHOR_ID            BIGINT NOT NULL COMMENT '创建人ID',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   MODIFIER_ID          BIGINT COMMENT '最后修改人ID',
   DESC_                VARCHAR(4000) COMMENT '备注',
   PRIMARY KEY (ID)
) COMMENT '司机营运车辆';
ALTER TABLE BS_CAR_DRIVER ADD CONSTRAINT BSFK_CARDRIVER_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CAR_DRIVER ADD CONSTRAINT BSFK_CARDRIVER_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CAR_DRIVER ADD CONSTRAINT BSFK_CARDRIVER_CAR FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID);
ALTER TABLE BS_CAR_DRIVER ADD CONSTRAINT BSFK_CARDRIVER_CARMAN FOREIGN KEY (DRIVER_ID)
      REFERENCES BS_CARMAN (ID);
      
-- 司机迁移历史
CREATE TABLE BS_CARMAN_HISTORY (
   ID                   BIGINT NOT NULL auto_increment,
   TYPE_                VARCHAR(255) NOT NULL COMMENT '迁移属性，如新入职',
   DRIVER_ID            BIGINT NOT NULL COMMENT '司机ID',
   SUBJECT              VARCHAR(1000) COMMENT '其他',
   FROM_CARID           BIGINT COMMENT '迁自车辆ID',
   TO_CARID             BIGINT NOT NULL COMMENT '迁往车辆ID',
   FROM_CLASSES         VARCHAR(255) COMMENT '原营运班次:如正班、副班、顶班',
   TO_CLASSES           VARCHAR(255) NOT NULL COMMENT '新营运班次:如正班、副班、顶班',
   SHIFT_DATE           DATETIME NOT NULL COMMENT '迁移日期',
   FROM_MOTORCADEID     BIGINT COMMENT '原车队ID',
   TO_MOTORCADEID       BIGINT NOT NULL COMMENT '迁往车队ID',
   FILE_DATE            DATETIME NOT NULL COMMENT '创建时间',
   AUTHOR_ID            BIGINT NOT NULL COMMENT '创建人ID',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   MODIFIER_ID          BIGINT COMMENT '最后修改人ID',
   PRIMARY KEY (ID)
) COMMENT '司机迁移历史';
ALTER TABLE BS_CARMAN_HISTORY ADD CONSTRAINT BS_CARMANHISTORY_DRIVER FOREIGN KEY (DRIVER_ID)
      REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_CARMAN_HISTORY ADD CONSTRAINT BS_CARMANHISTORY_FROMCAR FOREIGN KEY (FROM_CARID)
      REFERENCES BS_CAR (ID);
ALTER TABLE BS_CARMAN_HISTORY ADD CONSTRAINT BS_CARMANHISTORY_TOCAR FOREIGN KEY (TO_CARID)
      REFERENCES BS_CAR (ID);
ALTER TABLE BS_CARMAN_HISTORY ADD CONSTRAINT BS_CARMANHISTORY_FROMMOTORCADE FOREIGN KEY (FROM_MOTORCADEID)
      REFERENCES BS_MOTORCADE (ID);
ALTER TABLE BS_CARMAN_HISTORY ADD CONSTRAINT BS_CARMANHISTORY_TOMOTORCADE FOREIGN KEY (TO_MOTORCADEID)
      REFERENCES BS_MOTORCADE (ID);
ALTER TABLE BS_CARMAN_HISTORY ADD CONSTRAINT BSFK_CARMANHISTORY_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CARMAN_HISTORY ADD CONSTRAINT BSFK_CARMANHISTORY_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 黑名单
CREATE TABLE BS_BLACKLIST(
   ID                   BIGINT NOT NULL AUTO_INCREMENT,
   TYPE_                VARCHAR(255) NOT NULL COMMENT '限制项目',
   UNIT_ID              BIGINT COMMENT '车属单位ID',
   DRIVER_ID            BIGINT NOT NULL COMMENT '司机ID',
   CAR_ID               BIGINT NOT NULL COMMENT '车辆ID',
   MOTORCADE_ID         BIGINT COMMENT '车队ID',
   SUBJECT              VARCHAR(1000) NOT NULL COMMENT '主题',
   LOCK_DATE            DATETIME NOT NULL COMMENT '锁定时间',
   UNLOCK_DATE          DATETIME COMMENT '解锁时间',
   LOCKER_ID            BIGINT NOT NULL COMMENT '锁定人ID',
   UNLOCKER_ID          BIGINT COMMENT '解锁人ID',
   LOCK_REASON          VARCHAR(4000) COMMENT '锁定原因',
   UNLOCK_REASON        VARCHAR(4000) COMMENT '解锁原因',
   LEVEL_               VARCHAR(255) COMMENT '等级',
   CODE                 VARCHAR(255) COMMENT '编号',
   FILE_DATE            DATETIME NOT NULL COMMENT '创建时间',
   AUTHOR_ID            BIGINT NOT NULL COMMENT '创建人ID',
   MODIFIED_DATE        DATETIME COMMENT '最后修改时间',
   MODIFIER_ID          BIGINT COMMENT '最后修改人ID',
   PRIMARY KEY (ID)
) COMMENT '黑名单';
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_UNIT FOREIGN KEY (UNIT_ID)
      REFERENCES BC_IDENTITY_ACTOR (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_CAR FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_DIRVER FOREIGN KEY (DRIVER_ID)
      REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_LOCKER FOREIGN KEY (LOCKER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_MOTORCADE FOREIGN KEY (MOTORCADE_ID)
      REFERENCES BS_MOTORCADE (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_UNLOCKER FOREIGN KEY (UNLOCKER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
