-- 创建数据转换用的序列，开始于10000
CREATE sequence DC_SEQUENCE
    minvalue 1
    start with 10000
    increment by 1
    cache 20;

-- 数据转换记录
CREATE TABLE DC_RECORD(
  TYPE_         VARCHAR2(255) NOT NULL,
  FROM_ID       NUMBER(19) NOT NULL,
  TO_ID         NUMBER(19) NOT NULL,
  FROM_TABLE    VARCHAR2(255),
  TO_TABLE      VARCHAR2(255),
  CREATE_DATE   TIMESTAMP NOT NULL,
  REMARK        VARCHAR2(4000),
  MODIFIED_DATE TIMESTAMP
);
COMMENT ON TABLE DC_RECORD IS '数据转换记录';
COMMENT ON COLUMN DC_RECORD.TYPE_ IS '类型';
COMMENT ON COLUMN DC_RECORD.FROM_ID IS '旧表数据ID';
COMMENT ON COLUMN DC_RECORD.TO_ID IS '新表数据的ID';
COMMENT ON COLUMN DC_RECORD.FROM_TABLE IS '旧数据表名';
COMMENT ON COLUMN DC_RECORD.TO_TABLE IS '新数据表名';
COMMENT ON COLUMN DC_RECORD.CREATE_DATE IS '创建时间';
COMMENT ON COLUMN DC_RECORD.REMARK IS '备注说明';
COMMENT ON COLUMN DC_RECORD.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE DC_RECORD ADD CONSTRAINT UK_RECORD UNIQUE (TYPE_, FROM_ID);

-- bc营运管理子系统的建表脚本,所有表名须附带前缀"BS_"
-- 运行此脚本之前需先运行平台的建表脚本framework.db.mysql.CREATE.sql

-- 车队信息
CREATE TABLE BS_MOTORCADE (
   ID                   NUMBER(19)           NOT NULL,
   UID_                 VARCHAR2(36),
   CODE                 VARCHAR2(255),
   NAME                 VARCHAR2(255)        NOT NULL,
   FULLNAME             VARCHAR2(255),
   PAYMENT_DATE         DATE,
   COMPANY              VARCHAR2(255),
   COLOR                VARCHAR2(255),
   ADDRESS              VARCHAR2(255),
   PRINCIPAL            VARCHAR2(500),
   PHONE                VARCHAR2(255),
   FAX                  VARCHAR2(255),
   DESC_                VARCHAR2(4000),
   STATUS_              NUMBER(1)            NOT NULL,
   FILE_DATE            DATE                 NOT NULL,
   AUTHOR_ID            NUMBER(19)           NOT NULL,
   MODIFIER_ID          NUMBER(19),
   MODIFIED_DATE        DATE,
   PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_MOTORCADE IS '车队';
COMMENT ON COLUMN BS_MOTORCADE.CODE IS '编码';
COMMENT ON COLUMN BS_MOTORCADE.NAME IS '简称';
COMMENT ON COLUMN BS_MOTORCADE.FULLNAME IS '全称';
COMMENT ON COLUMN BS_MOTORCADE.PAYMENT_DATE IS '缴费日期';
COMMENT ON COLUMN BS_MOTORCADE.COMPANY IS '公司';
COMMENT ON COLUMN BS_MOTORCADE.COLOR IS '颜色';
COMMENT ON COLUMN BS_MOTORCADE.ADDRESS IS '地址';
COMMENT ON COLUMN BS_MOTORCADE.PRINCIPAL IS '负责人';
COMMENT ON COLUMN BS_MOTORCADE.PHONE IS '电话';
COMMENT ON COLUMN BS_MOTORCADE.FAX IS '传真';
COMMENT ON COLUMN BS_MOTORCADE.DESC_ IS '备注';
COMMENT ON COLUMN BS_MOTORCADE.STATUS_ IS '状态：0-已禁用,1-启用中,2-已删除';
COMMENT ON COLUMN BS_MOTORCADE.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_MOTORCADE.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_MOTORCADE.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BS_MOTORCADE.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BS_MOTORCADE ADD CONSTRAINT BSFK_MOTORCADE_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_MOTORCADE ADD CONSTRAINT BSFK_MOTORCADE_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 车队历史车辆数
CREATE TABLE BS_MOTORCADE_CARQUANTITY (
   ID                   NUMBER(19)           NOT NULL,
   MOTORCADE_ID         NUMBER(19)           NOT NULL,
   YEAR_                NUMBER(4)            NOT NULL,
   MONTH_               NUMBER(2)            NOT NULL,
   QUANTITY             NUMBER(19)           NOT NULL,
   FILE_DATE            DATE                 NOT NULL,
   AUTHOR_ID            NUMBER(19)           NOT NULL,
   MODIFIER_ID          NUMBER(19),
   MODIFIED_DATE        DATE,
   PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_MOTORCADE_CARQUANTITY IS '车队历史车辆数';
COMMENT ON COLUMN BS_MOTORCADE_CARQUANTITY.MOTORCADE_ID IS '所属车队ID';
COMMENT ON COLUMN BS_MOTORCADE_CARQUANTITY.YEAR_ IS '年份';
COMMENT ON COLUMN BS_MOTORCADE_CARQUANTITY.MONTH_ IS '月份';
COMMENT ON COLUMN BS_MOTORCADE_CARQUANTITY.QUANTITY IS '车辆数';
COMMENT ON COLUMN BS_MOTORCADE_CARQUANTITY.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_MOTORCADE_CARQUANTITY.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_MOTORCADE_CARQUANTITY.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BS_MOTORCADE_CARQUANTITY.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BS_MOTORCADE_CARQUANTITY ADD CONSTRAINT BSFK_CARQUANTITY_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_MOTORCADE_CARQUANTITY ADD CONSTRAINT BSFK_CARQUANTITY_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_MOTORCADE_CARQUANTITY ADD CONSTRAINT BSFK_CARQUANTITY_MOTORCADE FOREIGN KEY (MOTORCADE_ID)
      REFERENCES BS_MOTORCADE (ID);


-- 证件
create table BS_CERT (
    ID number(19) NOT NULL,
    STATUS_ number(1) NOT NULL,
    UID_ varchar2(36) NOT NULL,
    CERT_CODE varchar2(255) NOT NULL,
    CERT_NAME varchar2(255) NOT NULL,
    CERT_FULL_NAME varchar2(255),
    LICENCER varchar2(255),
    ISSUE_DATE date,
    START_DATE date,
    END_DATE date,
    EXT_STR1 varchar2(255),
    EXT_STR2 varchar2(255),
    EXT_STR3 varchar2(255),
    EXT_NUM1 number(19),
    EXT_NUM2 number(19),
    EXT_NUM3 number(19),
    FILE_DATE date NOT NULL,
    AUTHOR_ID number(19) NOT NULL,
    MODIFIER_ID number(19) ,
    MODIFIED_DATE date,
    primary key (ID)
);
COMMENT ON TABLE BS_CERT IS '证件';
COMMENT ON COLUMN BS_CERT.STATUS_ IS '状态：0-已禁用,1-启用中,2-已删除';
COMMENT ON COLUMN BS_CERT.CERT_CODE IS '证件号';
COMMENT ON COLUMN BS_CERT.CERT_NAME IS '证件简称';
COMMENT ON COLUMN BS_CERT.CERT_FULL_NAME IS '证件全称';
COMMENT ON COLUMN BS_CERT.LICENCER IS '发证机关';
COMMENT ON COLUMN BS_CERT.START_DATE IS '生效日期';
COMMENT ON COLUMN BS_CERT.ISSUE_DATE IS '发证日期';
COMMENT ON COLUMN BS_CERT.END_DATE IS '到期日期';
COMMENT ON COLUMN BS_CERT.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_CERT.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_CERT.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BS_CERT.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BS_CERT ADD CONSTRAINT BSFK_CERT_AUTHORID FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CERT ADD CONSTRAINT BSFK_CERT_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
CREATE INDEX BSIDX_CERT_CODE ON BS_CERT (CERT_CODE ASC);
CREATE INDEX BSIDX_CERT_NAME ON BS_CERT (CERT_NAME ASC);

-- 证件:居民身份证
create table BS_CERT_IDENTITY (
    ID number(19) NOT NULL,
    NAME varchar2(255),
    SEX number(1),
    BIRTHDATE date,
    NATION varchar2(255),
    ADDRESS varchar2(500),
    primary key (ID)
);
COMMENT ON TABLE BS_CERT_IDENTITY IS '证件:居民身份证';
COMMENT ON COLUMN BS_CERT_IDENTITY.NAME IS '姓名';
COMMENT ON COLUMN BS_CERT_IDENTITY.SEX IS '性别(0-未设置,1-男,2-女)';
COMMENT ON COLUMN BS_CERT_IDENTITY.BIRTHDATE IS '出生日期';
COMMENT ON COLUMN BS_CERT_IDENTITY.NATION IS '民族';
COMMENT ON COLUMN BS_CERT_IDENTITY.ADDRESS IS '住址';
ALTER TABLE BS_CERT_IDENTITY ADD CONSTRAINT BSFK_CERT4IDENTITY_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:机动车驾驶证
create table BS_CERT_DRIVING (
    ID number(19) NOT NULL,
    NAME varchar2(255),
    SEX number(1),
    BIRTHDATE date,
    NATION varchar2(255),
    ADDRESS varchar2(500),
    MODEL varchar2(255),
    RECEIVEDATE date,
    VALIDFOR varchar2(255),
    ARCHIVENO varchar2(255),
    RECORD varchar2(255),
    primary key (ID)
);
COMMENT ON TABLE BS_CERT_DRIVING IS '证件:机动车驾驶证';
COMMENT ON COLUMN BS_CERT_DRIVING.NAME IS '姓名';
COMMENT ON COLUMN BS_CERT_DRIVING.SEX IS '性别(0-未设置,1-男,2-女)';
COMMENT ON COLUMN BS_CERT_DRIVING.BIRTHDATE IS '出生日期';
COMMENT ON COLUMN BS_CERT_DRIVING.NATION IS '国籍';
COMMENT ON COLUMN BS_CERT_DRIVING.ADDRESS IS '地址';
COMMENT ON COLUMN BS_CERT_DRIVING.MODEL IS '准驾车型';
COMMENT ON COLUMN BS_CERT_DRIVING.RECEIVEDATE IS '初次领证日期';
COMMENT ON COLUMN BS_CERT_DRIVING.VALIDFOR IS '有效期限';
COMMENT ON COLUMN BS_CERT_DRIVING.ARCHIVENO IS '档案编号';
COMMENT ON COLUMN BS_CERT_DRIVING.RECORD IS '记录';
ALTER TABLE BS_CERT_DRIVING ADD CONSTRAINT BSFK_CERT4DRIVING_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:从业资格证
create table BS_CERT_CYZG (
    ID number(19) NOT NULL,
    NAME varchar2(255),
    SEX number(1),
    BIRTHDATE date,
    NATION varchar2(255),
    ADDRESS varchar2(500),
    SCOPE_ varchar2(255),
    IDENTITY_NO varchar2(255),
    SERVICE_UNIT varchar2(500),
    primary key (ID)
);
COMMENT ON TABLE BS_CERT_CYZG IS '证件:从业资格证';
COMMENT ON COLUMN BS_CERT_CYZG.NAME IS '姓名';
COMMENT ON COLUMN BS_CERT_CYZG.SEX IS '性别(0-未设置,1-男,2-女)';
COMMENT ON COLUMN BS_CERT_CYZG.BIRTHDATE IS '出生日期';
COMMENT ON COLUMN BS_CERT_CYZG.NATION IS '国籍';
COMMENT ON COLUMN BS_CERT_CYZG.ADDRESS IS '地址';
COMMENT ON COLUMN BS_CERT_CYZG.SCOPE_ IS '从业资格';
COMMENT ON COLUMN BS_CERT_CYZG.IDENTITY_NO IS '身份证件号';
COMMENT ON COLUMN BS_CERT_CYZG.SERVICE_UNIT IS '服务单位';
ALTER TABLE BS_CERT_CYZG ADD CONSTRAINT BSFK_CERT4CYZG_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:服务资格证
create table BS_CERT_FWZG (
    ID number(19) NOT NULL,
    NAME varchar2(255),
    SEX number(1),
    BIRTHDATE date,
    NATION varchar2(255),
    ADDRESS varchar2(500),
    LEVEL_ varchar2(255),
    SERVICE_UNIT varchar2(500),
    primary key (ID)
);
COMMENT ON TABLE BS_CERT_FWZG IS '证件:服务资格证';
COMMENT ON COLUMN BS_CERT_FWZG.NAME IS '姓名';
COMMENT ON COLUMN BS_CERT_FWZG.SEX IS '性别(0-未设置,1-男,2-女)';
COMMENT ON COLUMN BS_CERT_FWZG.BIRTHDATE IS '出生日期';
COMMENT ON COLUMN BS_CERT_FWZG.NATION IS '国籍';
COMMENT ON COLUMN BS_CERT_FWZG.ADDRESS IS '地址';
COMMENT ON COLUMN BS_CERT_FWZG.LEVEL_ IS '等级';
COMMENT ON COLUMN BS_CERT_FWZG.SERVICE_UNIT IS '服务单位';
ALTER TABLE BS_CERT_FWZG ADD CONSTRAINT BSFK_CERT4FWZG_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:驾驶培训证
create table BS_CERT_JSPX (
    ID number(19) NOT NULL,
    NAME varchar2(255),
    SEX number(1),
    BIRTHDATE date,
    NATION varchar2(255),
    ADDRESS varchar2(500),
    DOMAIN varchar2(255),
    TRAIN_DATE date,
    TRAIN_HOUR number(3),
    GRADE1 number(3),
    GRADE2 varchar2(10),
    GRADE3 varchar2(10),
    IDENTITY_NO varchar2(255),
    primary key (ID)
);
COMMENT ON TABLE BS_CERT_JSPX IS '证件:驾驶培训证';
COMMENT ON COLUMN BS_CERT_JSPX.NAME IS '姓名';
COMMENT ON COLUMN BS_CERT_JSPX.SEX IS '性别(0-未设置,1-男,2-女)';
COMMENT ON COLUMN BS_CERT_JSPX.BIRTHDATE IS '出生日期';
COMMENT ON COLUMN BS_CERT_JSPX.NATION IS '国籍';
COMMENT ON COLUMN BS_CERT_JSPX.ADDRESS IS '地址';
COMMENT ON COLUMN BS_CERT_JSPX.DOMAIN IS '培训专业';
COMMENT ON COLUMN BS_CERT_JSPX.TRAIN_DATE IS '培训时间';
COMMENT ON COLUMN BS_CERT_JSPX.TRAIN_HOUR IS '培训学时';
COMMENT ON COLUMN BS_CERT_JSPX.GRADE1 IS '理论知识考核成绩';
COMMENT ON COLUMN BS_CERT_JSPX.GRADE2 IS '操作技能考核成绩';
COMMENT ON COLUMN BS_CERT_JSPX.GRADE3 IS '评定成绩';
COMMENT ON COLUMN BS_CERT_JSPX.IDENTITY_NO IS '身份证件号';
ALTER TABLE BS_CERT_JSPX ADD CONSTRAINT BSFK_CERT4JSPX_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);

-- 证件:道路运输证
CREATE TABLE BS_CERT_ROADTRANSPORT(
	ID                   NUMBER(19) NOT NULL,
	FACTORY              VARCHAR2(255),
	PLATE                VARCHAR2(255),
	OWNER                VARCHAR2(255),
	ADDRESS              VARCHAR2(255),
	BS_CERT_NO           VARCHAR2(255),
	SEAT                 VARCHAR2(255),
	DIM_LEN              NUMBER(19),
	DIM_WIDTH            NUMBER(19),
	DIM_HEIGHT           NUMBER(19),
	SCOPE_               VARCHAR2(255),
	LEVEL_               VARCHAR2(255),
  PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_CERT_ROADTRANSPORT IS '证件：道路运输证';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.FACTORY IS '品牌型号，如“桑塔纳SVW7182QQD”';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.PLATE IS '车牌及号码，如“粤AC4X74”';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.OWNER IS '业户名称';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.ADDRESS IS '地址';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.BS_CERT_NO IS '经营许可证号';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.SEAT IS '吨（座）位';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.DIM_LEN IS '外廓尺寸：长，单位MM';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.DIM_WIDTH IS '外廓尺寸：宽，单位MM';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.DIM_HEIGHT IS '外廓尺寸：高，单位MM';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.SCOPE_ IS '经营范围';
COMMENT ON COLUMN BS_CERT_ROADTRANSPORT.LEVEL_ IS '技术等级';
ALTER TABLE BS_CERT_ROADTRANSPORT ADD CONSTRAINT BSFK_CERT4ROADTRANSPORT_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);
  
-- 证件:机动车行驶证
CREATE TABLE BS_CERT_VEHICELICENSE(
	ID                   NUMBER(19) NOT NULL,
	FACTORY              VARCHAR2(255),
	PLATE                VARCHAR2(255),
	OWNER                VARCHAR2(255),
	ADDRESS              VARCHAR2(255),
	USE_CHARACTER        VARCHAR2(255),
	VEHICE_TYPE          VARCHAR2(255),
	VIN                  VARCHAR2(255),
	ENGINE_NO            VARCHAR2(255),
	REGISTER_DATE        DATE,
	ARCHIVE_NO           VARCHAR2(255),
	DIM_LEN              number(19),
	DIM_WIDTH            number(19),
	DIM_HEIGHT           number(19),
	TOTAL_WEIGHT         number(19),
	CURB_WEIGHT          number(19),
	ACCESS_WEIGHT        number(19),
	ACCESS_COUNT         number(19),
	SCRAP_DATE           DATE,
	DESC_                VARCHAR2(500),
	RECORD_              VARCHAR2(500),
	PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_CERT_VEHICELICENSE IS '证件：机动车行驶证';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.FACTORY IS '品牌型号，如“桑塔纳SVW7182QQD”';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.PLATE IS '车牌及号码，如“粤AC4X74”';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.OWNER IS '业户名称';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.ADDRESS IS '地址';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.USE_CHARACTER IS '使用性质';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.VEHICE_TYPE IS '车辆类型';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.VIN IS '车辆识别代号';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.ENGINE_NO IS '发动机号码';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.REGISTER_DATE IS '注册日期';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.ARCHIVE_NO IS '档案编号';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.DIM_LEN IS '外廓尺寸：长，单位MM';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.DIM_WIDTH IS '外廓尺寸：宽，单位MM';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.DIM_HEIGHT IS '外廓尺寸：高，单位MM';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.TOTAL_WEIGHT IS '总质量，单位KG';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.CURB_WEIGHT IS '整备质量，单位KG';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.ACCESS_WEIGHT IS '核定载质量，单位KG';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.ACCESS_COUNT IS '核定载人数';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.SCRAP_DATE IS '强制报废日期';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.DESC_ IS '备注';
COMMENT ON COLUMN BS_CERT_VEHICELICENSE.RECORD_ IS '检验记录';
ALTER TABLE BS_CERT_VEHICELICENSE ADD CONSTRAINT BSFK_CERT4VEHICELICENSE_CERT FOREIGN KEY (ID) 
	REFERENCES BS_CERT (ID);


-- 司机责任人
create table BS_CARMAN (
    ID number(19) NOT NULL,
    UID_ varchar2(36) NOT NULL,
    STATUS_ number(1) NOT NULL,
    TYPE_ number(1) default 0 NOT NULL,
    NAME varchar2(255) NOT NULL,
    ORDER_ varchar2(100),
    SEX number(1) default 0 NOT NULL,
    WORK_DATE date,
    ORIGIN varchar2(255),
    REGION_ varchar2(255),
    HOUSE_TYPE varchar2(255),
    BIRTHDATE date,
    FORMER_UNIT varchar2(500),
    ADDRESS varchar2(500),
    ADDRESS1 varchar2(500),
    PHONE varchar2(500),
    PHONE1 varchar2(500),
    MODEL_                VARCHAR2(255),
	LEVEL_ varchar2(255),
    CERT_IDENTITY        VARCHAR2(255),
    CERT_DRIVING         VARCHAR2(255),
    CERT_DRIVING_ARCHIVE VARCHAR2(255),
    CERT_DRIVING_FIRST_DATE DATE,
    CERT_DRIVING_START_DATE DATE,
    CERT_DRIVING_END_DATE DATE,
    CERT_FWZG            VARCHAR2(255),
    CERT_CYZG            VARCHAR2(255),
    DRIVING_STATUS       VARCHAR2(255),
    EXT_FZJG             VARCHAR2(255),
    EXT_ZRR              VARCHAR2(255),
    GZ                   number(1),
    ACCESS_CERTS         VARCHAR2(255),
    DESC_                VARCHAR2(4000),
    FILE_DATE date NOT NULL,
    AUTHOR_ID number(19) NOT NULL,
    MODIFIER_ID number(19),
    MODIFIED_DATE date,
    primary key (ID)
);
COMMENT ON TABLE BS_CARMAN IS '司机责任人';
COMMENT ON COLUMN BS_CARMAN.UID_ IS 'UID';
COMMENT ON COLUMN BS_CARMAN.STATUS_ IS '状态：0-已禁用,1-启用中,2-已删除';
COMMENT ON COLUMN BS_CARMAN.TYPE_ IS '类别:0-司机,1-责任人,2-司机和责任人';
COMMENT ON COLUMN BS_CARMAN.NAME IS '姓名';
COMMENT ON COLUMN BS_CARMAN.ORDER_ IS '排序号';
COMMENT ON COLUMN BS_CARMAN.SEX IS '性别：0-未设置,1-男,2-女';
COMMENT ON COLUMN BS_CARMAN.WORK_DATE IS '入职日期';
COMMENT ON COLUMN BS_CARMAN.ORIGIN IS '籍贯';
COMMENT ON COLUMN BS_CARMAN.REGION_ IS '区域';
COMMENT ON COLUMN BS_CARMAN.HOUSE_TYPE IS '户口类型';
COMMENT ON COLUMN BS_CARMAN.BIRTHDATE IS '出生日期';
COMMENT ON COLUMN BS_CARMAN.FORMER_UNIT IS '入职原单位';
COMMENT ON COLUMN BS_CARMAN.ADDRESS IS '身份证住址';
COMMENT ON COLUMN BS_CARMAN.ADDRESS1 IS '暂住地址';
COMMENT ON COLUMN BS_CARMAN.PHONE IS '电话';
COMMENT ON COLUMN BS_CARMAN.PHONE1 IS '电话1';
COMMENT ON COLUMN BS_CARMAN.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_CARMAN.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_CARMAN.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BS_CARMAN.MODIFIED_DATE IS '最后修改时间';
COMMENT ON COLUMN BS_CARMAN.MODEL_ IS '准驾车型';
COMMENT ON COLUMN BS_CARMAN.LEVEL_ IS '司机等级';
COMMENT ON COLUMN BS_CARMAN.CERT_IDENTITY IS '身份证号';
COMMENT ON COLUMN BS_CARMAN.CERT_DRIVING IS '驾驶证号';
COMMENT ON COLUMN BS_CARMAN.CERT_DRIVING_ARCHIVE IS '驾驶证档案号';
COMMENT ON COLUMN BS_CARMAN.CERT_DRIVING_FIRST_DATE IS '初次领证日期';
COMMENT ON COLUMN BS_CARMAN.CERT_DRIVING_START_DATE IS '驾驶证起效日期';
COMMENT ON COLUMN BS_CARMAN.CERT_DRIVING_END_DATE IS '驾驶证过期日期';
COMMENT ON COLUMN BS_CARMAN.CERT_FWZG IS '服务资格证号';
COMMENT ON COLUMN BS_CARMAN.CERT_CYZG IS '从业资格证号';
COMMENT ON COLUMN BS_CARMAN.DRIVING_STATUS IS '驾驶状态';
COMMENT ON COLUMN BS_CARMAN.EXT_FZJG IS '分支机构：用于历史数据的保存';
COMMENT ON COLUMN BS_CARMAN.EXT_ZRR IS '责任人：用于历史数据的保存';
COMMENT ON COLUMN BS_CARMAN.GZ IS '驾驶证是否广州:0-否,1-是';
COMMENT ON COLUMN BS_CARMAN.ACCESS_CERTS IS '已考取证件：历史数据保存';
COMMENT ON COLUMN BS_CARMAN.DESC_ IS '备注';
ALTER TABLE BS_CARMAN ADD CONSTRAINT BSFK_CARMAN_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CARMAN ADD CONSTRAINT BSFK_CARMAN_MODIFIER FOREIGN KEY (MODIFIER_ID) 
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 司机责任人与证件的关联
CREATE TABLE BS_CARMAN_CERT (
    MAN_ID number(19) NOT NULL,
    CERT_ID number(19) NOT NULL,
    PRIMARY KEY (MAN_ID,CERT_ID)
);
COMMENT ON TABLE BS_CARMAN_CERT IS '司机责任人与证件的关联';
COMMENT ON COLUMN BS_CARMAN_CERT.MAN_ID IS '司机责任人ID';
COMMENT ON COLUMN BS_CARMAN_CERT.CERT_ID IS '证件ID';
ALTER TABLE BS_CARMAN_CERT ADD CONSTRAINT BSFK_CARMANCERT_MAN FOREIGN KEY (MAN_ID) 
	REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_CARMAN_CERT ADD CONSTRAINT BSFK_CARMANCERT_CERT FOREIGN KEY (CERT_ID) 
	REFERENCES BS_CERT (ID);


-- 车辆 
CREATE TABLE BS_CAR (
   ID                   NUMBER(19)           NOT NULL,
   UID_                 VARCHAR2(36)         NOT NULL,
   STATUS_              NUMBER(1)            NOT NULL,
   UNIT_ID              NUMBER(19)           NOT NULL,
   MOTORCADE_ID         NUMBER(19)           NOT NULL,
   BS_TYPE              VARCHAR2(255),
   CODE                 VARCHAR2(255),
   ORIGIN_NO            VARCHAR2(255),
   PLATE_TYPE           VARCHAR2(255),
   PLATE_NO             VARCHAR2(255),
   VIN                  VARCHAR2(255),
   FACTORY_TYPE         VARCHAR2(255),
   FACTORY_MODEL        VARCHAR2(255),
   REGISTER_DATE        DATE,
   OPERATE_DATE         DATE,
   SCRAP_DATE           DATE,
   FACTORY_DATE         DATE,
   REGISTER_NO          VARCHAR2(255),
   LEVEL_               VARCHAR2(255),
   COLOR                VARCHAR2(255),
   RNGINE_NO            VARCHAR2(255),
   RNGINE_TYPE          VARCHAR2(255),
   FUEL_TYPE            VARCHAR2(255),
   DISPLACEMENT         NUMBER(19),
   POWER                NUMBER(19,2),
   TURN_TYPE            VARCHAR2(255),
   TIRE_COUNT           NUMBER(19),
   TIRE_STANDARD        VARCHAR2(255),
   AXIS_DISTANCE        NUMBER(19),
   AXIS_COUNT           NUMBER(19),
   PIECE_COUNT          NUMBER(19),
   DIM_LEN              NUMBER(19),
   DIM_WIDTH            NUMBER(19),
   DIM_HEIGHT           NUMBER(19),
   TOTAL_WEIGHT         NUMBER(19),
   ACCESS_WEIGHT        NUMBER(19),
   ACCESS_COUNT         NUMBER(19),
   ORIGINAL_VALUE       NUMBER(19,2),
   INVOICE_NO1          VARCHAR2(255),
   INVOICE_NO2          VARCHAR2(255),
   PAYMENT_TYPE         VARCHAR2(255),
   CERT_NO1             VARCHAR2(255),
   CERT_NO2             VARCHAR2(255),
   CERT_NO3             VARCHAR2(255),
   TAXIMETER_FACTORY    VARCHAR2(255),
   TAXIMETER_TYPE       VARCHAR2(255),
   TAXIMETER_NO         VARCHAR2(255),
   DESC1                VARCHAR2(4000),
   DESC2                VARCHAR2(4000),
   DESC3                VARCHAR2(4000),
   FILE_DATE            DATE NOT NULL,
   AUTHOR_ID            NUMBER(19) NOT NULL,
   MODIFIER_ID          NUMBER(19),
   MODIFIED_DATE        DATE,
   PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_CAR IS '车辆';
COMMENT ON COLUMN BS_CAR.STATUS_ IS '状态：0-已禁用,1-启用中,2-已删除';
COMMENT ON COLUMN BS_CAR.UNIT_ID IS '所属单位ID';
COMMENT ON COLUMN BS_CAR.MOTORCADE_ID IS '所属车队ID';
COMMENT ON COLUMN BS_CAR.BS_TYPE IS '营运性质';
COMMENT ON COLUMN BS_CAR.CODE IS '自编号';
COMMENT ON COLUMN BS_CAR.ORIGIN_NO IS '原车号';
COMMENT ON COLUMN BS_CAR.PLATE_TYPE IS '车牌归属，如“粤A”';
COMMENT ON COLUMN BS_CAR.PLATE_NO IS '车牌号码，如“C4X74”';
COMMENT ON COLUMN BS_CAR.VIN IS '车辆识别代号';
COMMENT ON COLUMN BS_CAR.FACTORY_TYPE IS '厂牌类型，如“桑塔纳”';
COMMENT ON COLUMN BS_CAR.FACTORY_MODEL IS '厂牌型号，如“SVW7182QQD”';
COMMENT ON COLUMN BS_CAR.REGISTER_DATE IS '登记日期';
COMMENT ON COLUMN BS_CAR.OPERATE_DATE IS '投产日期';
COMMENT ON COLUMN BS_CAR.SCRAP_DATE IS '报废日期';
COMMENT ON COLUMN BS_CAR.FACTORY_DATE IS '出厂日期';
COMMENT ON COLUMN BS_CAR.REGISTER_NO IS '机动车登记编号';
COMMENT ON COLUMN BS_CAR.LEVEL_ IS '车辆定级';
COMMENT ON COLUMN BS_CAR.COLOR IS '颜色';
COMMENT ON COLUMN BS_CAR.RNGINE_NO IS '发动机号码';
COMMENT ON COLUMN BS_CAR.RNGINE_TYPE IS '发动机类型';
COMMENT ON COLUMN BS_CAR.FUEL_TYPE IS '燃料类型，如“汽油”';
COMMENT ON COLUMN BS_CAR.DISPLACEMENT IS '排量，单位ML';
COMMENT ON COLUMN BS_CAR.POWER IS '功率，单位KW';
COMMENT ON COLUMN BS_CAR.TURN_TYPE IS '转向方式，如“方向盘”';
COMMENT ON COLUMN BS_CAR.TIRE_COUNT IS '轮胎数';
COMMENT ON COLUMN BS_CAR.TIRE_STANDARD IS '轮胎规格';
COMMENT ON COLUMN BS_CAR.AXIS_DISTANCE IS '轴距';
COMMENT ON COLUMN BS_CAR.AXIS_COUNT IS '轴数';
COMMENT ON COLUMN BS_CAR.PIECE_COUNT IS '后轴钢板弹簧片数';
COMMENT ON COLUMN BS_CAR.DIM_LEN IS '外廓尺寸：长，单位MM';
COMMENT ON COLUMN BS_CAR.DIM_WIDTH IS '外廓尺寸：宽，单位MM';
COMMENT ON COLUMN BS_CAR.DIM_HEIGHT IS '外廓尺寸：高，单位MM';
COMMENT ON COLUMN BS_CAR.TOTAL_WEIGHT IS '总质量，单位KG';
COMMENT ON COLUMN BS_CAR.ACCESS_WEIGHT IS '核定载质量，单位KG';
COMMENT ON COLUMN BS_CAR.ACCESS_COUNT IS '核定载人数';
COMMENT ON COLUMN BS_CAR.ORIGINAL_VALUE IS '固定资产原值，单位元';
COMMENT ON COLUMN BS_CAR.INVOICE_NO1 IS '购车发票号';
COMMENT ON COLUMN BS_CAR.INVOICE_NO2 IS '购置税发票号';
COMMENT ON COLUMN BS_CAR.PAYMENT_TYPE IS '缴费日';
COMMENT ON COLUMN BS_CAR.CERT_NO1 IS '购置税证号';
COMMENT ON COLUMN BS_CAR.CERT_NO2 IS '经营权使用证号';
COMMENT ON COLUMN BS_CAR.CERT_NO3 IS '强检证号';
COMMENT ON COLUMN BS_CAR.TAXIMETER_FACTORY IS '计价器制造厂';
COMMENT ON COLUMN BS_CAR.TAXIMETER_TYPE IS '计价器型号';
COMMENT ON COLUMN BS_CAR.TAXIMETER_NO IS '计价器出厂编号';
COMMENT ON COLUMN BS_CAR.DESC1 IS '备注1';
COMMENT ON COLUMN BS_CAR.DESC2 IS '备注2';
COMMENT ON COLUMN BS_CAR.DESC3 IS '备注3';
COMMENT ON COLUMN BS_CAR.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_CAR.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_CAR.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BS_CAR.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_MOTORCADEID FOREIGN KEY (MOTORCADE_ID)
      REFERENCES BS_MOTORCADE (ID);
ALTER TABLE BS_CAR ADD CONSTRAINT BSFK_CAR_UNITID FOREIGN KEY (UNIT_ID)
      REFERENCES BC_IDENTITY_ACTOR (ID);

-- 车辆与证件的关联
CREATE TABLE BS_CAR_CERT (
    CAR_ID number(19) NOT NULL,
    CERT_ID number(19) NOT NULL,
    PRIMARY KEY (CAR_ID,CERT_ID)
);
COMMENT ON TABLE BS_CAR_CERT IS '车辆与证件的关联';
COMMENT ON COLUMN BS_CAR_CERT.CAR_ID IS '车辆ID';
COMMENT ON COLUMN BS_CAR_CERT.CERT_ID IS '证件ID';
ALTER TABLE BS_CAR_CERT ADD CONSTRAINT BSFK_CARCERT_CAR FOREIGN KEY (CAR_ID) 
	REFERENCES BS_CAR (ID);
ALTER TABLE BS_CAR_CERT ADD CONSTRAINT BSFK_CARCERT_CERT FOREIGN KEY (CERT_ID) 
	REFERENCES BS_CERT (ID);


-- 合同
CREATE TABLE BS_CONTRACT (
   ID                   NUMBER(19)           NOT NULL,
   UID_                 VARCHAR2(36)         NOT NULL,
   STATUS_              NUMBER(1)            NOT NULL,
   WORD_NO              VARCHAR2(255),
   CODE                 VARCHAR2(255)        NOT NULL,
   TYPE_                VARCHAR2(255)        NOT NULL,
   TRANSACTOR_ID        NUMBER(19)           NOT NULL,
   SIGN_DATE            DATE                 NOT NULL,
   START_DATE           DATE                 NOT NULL,
   END_DATE             DATE                 NOT NULL,
   CONTENT              VARCHAR2(4000),
   EXT_STR1             VARCHAR2(255),
   EXT_STR2             VARCHAR2(255),
   EXT_STR3             VARCHAR2(255),
   EXT_NUM1             NUMBER(19),
   EXT_NUM2             NUMBER(19),
   EXT_NUM3             NUMBER(19),
   FILE_DATE            DATE                 NOT NULL,
   AUTHOR_ID            NUMBER(19)           NOT NULL,
   MODIFIER_ID          NUMBER(19),
   MODIFIED_DATE        DATE,
   PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_CONTRACT IS '合同';
COMMENT ON COLUMN BS_CONTRACT.STATUS_ IS '状态：0-已禁用,1-启用中,2-已删除';
COMMENT ON COLUMN BS_CONTRACT.WORD_NO IS '文书号';
COMMENT ON COLUMN BS_CONTRACT.CODE IS '合同号';
COMMENT ON COLUMN BS_CONTRACT.TYPE_ IS '合同类型：如劳动合同、承包合同等';
COMMENT ON COLUMN BS_CONTRACT.TRANSACTOR_ID IS '经办人ID';
COMMENT ON COLUMN BS_CONTRACT.SIGN_DATE IS '签订日期';
COMMENT ON COLUMN BS_CONTRACT.START_DATE IS '生效日期';
COMMENT ON COLUMN BS_CONTRACT.END_DATE IS '到期日期';
COMMENT ON COLUMN BS_CONTRACT.CONTENT IS '合同内容';
COMMENT ON COLUMN BS_CONTRACT.EXT_STR1 IS '字符扩展域1';
COMMENT ON COLUMN BS_CONTRACT.EXT_STR2 IS '字符扩展域2';
COMMENT ON COLUMN BS_CONTRACT.EXT_STR3 IS '字符扩展域3';
COMMENT ON COLUMN BS_CONTRACT.EXT_NUM1 IS '数字扩展域1';
COMMENT ON COLUMN BS_CONTRACT.EXT_NUM2 IS '数字扩展域2';
COMMENT ON COLUMN BS_CONTRACT.EXT_NUM3 IS '数字扩展域3';
COMMENT ON COLUMN BS_CONTRACT.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_CONTRACT.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_CONTRACT.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BS_CONTRACT.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BS_CONTRACT ADD CONSTRAINT BSFK_CONTRACT_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_CONTRACT ADD CONSTRAINT BSFK_CONTRACT_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 司机劳动合同
CREATE TABLE BS_CONTRACT_LABOUR (
   ID                   NUMBER(19)           NOT NULL,
   DRIVER_ID            NUMBER(19)           NOT NULL,
   CAR_ID               NUMBER(19)           NOT NULL,
   ADDITION_PROTOCOL    NUMBER(1)            NOT NULL,
   PRE_INDUSTRY_NAME    VARCHAR2(255),
   PRE_INDUSTRY_TYPE    NUMBER(1)            NOT NULL,
   HIRING_PROCEDURE     NUMBER(1)            NOT NULL,
   DOLE                 NUMBER(1)            NOT NULL,
   FILING               NUMBER(1)            NOT NULL,
   CERT_NO              VARCHAR2(255)        NOT NULL,
   CONSTRAINT PK_BS_CONTRACT_LABOUR PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_CONTRACT_LABOUR IS '司机劳动合同';
COMMENT ON COLUMN BS_CONTRACT_LABOUR.DRIVER_ID IS '司机ID';
COMMENT ON COLUMN BS_CONTRACT_LABOUR.CAR_ID IS '车辆ID';
COMMENT ON COLUMN BS_CONTRACT_LABOUR.ADDITION_PROTOCOL IS '补充协议:0-无,1-有';
COMMENT ON COLUMN BS_CONTRACT_LABOUR.PRE_INDUSTRY_NAME IS '前身行业名称';
COMMENT ON COLUMN BS_CONTRACT_LABOUR.PRE_INDUSTRY_TYPE IS '前身工种行业:0-非特殊,1-特殊';
COMMENT ON COLUMN BS_CONTRACT_LABOUR.HIRING_PROCEDURE IS '招用工手续:0-未办,1-已办';
COMMENT ON COLUMN BS_CONTRACT_LABOUR.DOLE IS '下岗失业补贴:0-已发,1-未发';
COMMENT ON COLUMN BS_CONTRACT_LABOUR.FILING IS '是否已备案';
COMMENT ON COLUMN BS_CONTRACT_LABOUR.CERT_NO IS '资格证号';
ALTER TABLE BS_CONTRACT_LABOUR ADD CONSTRAINT BSFK_CONTRACT4LABOUR_CAR FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID);
ALTER TABLE BS_CONTRACT_LABOUR ADD CONSTRAINT BSFK_CONTRACT4LABOUR_CARMAN FOREIGN KEY (DRIVER_ID)
      REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_CONTRACT_LABOUR ADD CONSTRAINT BSFK_CONTRACT4LABOUR_CONTRACT FOREIGN KEY (ID)
      REFERENCES BS_CONTRACT (ID);

-- 责任人合同
CREATE TABLE BS_CONTRACT_CHARGER (
   ID                   NUMBER(19)           NOT NULL,
   CAR_ID               NUMBER(19)           NOT NULL,
   SIGN_TYPE            VARCHAR2(255)        NOT NULL,
   LOGOUT               NUMBER(1)            NOT NULL,
   TAKEBACK_ORIGIN      NUMBER(1)            NOT NULL,
   INCLUDE_COST         NUMBER(1)            NOT NULL,
   OLD_CONTENT          VARCHAR2(4000),
   CONSTRAINT PK_BS_CONTRACT_CHARGER PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_CONTRACT_CHARGER IS '责任人合同:如承包合同';
COMMENT ON COLUMN BS_CONTRACT_CHARGER.CAR_ID IS '车辆ID';
COMMENT ON COLUMN BS_CONTRACT_CHARGER.SIGN_TYPE IS '签约类型:如新户';
COMMENT ON COLUMN BS_CONTRACT_CHARGER.LOGOUT IS '注销:0-未,1-已';
COMMENT ON COLUMN BS_CONTRACT_CHARGER.TAKEBACK_ORIGIN IS '已经收回原件:0-未1-已';
COMMENT ON COLUMN BS_CONTRACT_CHARGER.INCLUDE_COST IS '包含检审费用:0-不包含,1-包含';
COMMENT ON COLUMN BS_CONTRACT_CHARGER.OLD_CONTENT IS '旧合同内容';
ALTER TABLE BS_CONTRACT_CHARGER ADD CONSTRAINT BSFK_CONTRACT4CHARGER_CAR FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID);
ALTER TABLE BS_CONTRACT_CHARGER ADD CONSTRAINT BSFK_CONTRACT4CHARGER_CONTRACT FOREIGN KEY (ID)
      REFERENCES BS_CONTRACT (ID);

-- 责任人与合同的关联
CREATE TABLE BS_CARMAN_CONTRACT (
   CONTRACT_ID          NUMBER(19)           NOT NULL,
   CARMAN_ID            NUMBER(19)           NOT NULL,
   CONSTRAINT PK_BS_CARMAN_CONTRACT PRIMARY KEY (CARMAN_ID, CONTRACT_ID)
);
COMMENT ON TABLE BS_CARMAN_CONTRACT IS '责任人与合同的关联';
COMMENT ON COLUMN BS_CARMAN_CONTRACT.CONTRACT_ID IS '合同ID';
COMMENT ON COLUMN BS_CARMAN_CONTRACT.CARMAN_ID IS '责任人ID';
ALTER TABLE BS_CARMAN_CONTRACT ADD CONSTRAINT BSFK_CARMANCONTRACT_CARMAN FOREIGN KEY (CARMAN_ID)
      REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_CARMAN_CONTRACT ADD CONSTRAINT BSFK_CARMANCONTRACT_CONTRACT FOREIGN KEY (CONTRACT_ID)
      REFERENCES BS_CONTRACT_CHARGER (ID);

-- 司机营运车辆
CREATE TABLE BS_CAR_DRIVER (
   ID                   NUMBER(19)           NOT NULL,
   STATUS_              NUMBER(1)            NOT NULL,
   DRIVER_ID            NUMBER(19)           NOT NULL,
   CAR_ID               NUMBER(19)           NOT NULL,
   CLASSES              VARCHAR2(255)        NOT NULL,
   START_DATE           DATE,
   END_DATE             DATE,
   FILE_DATE            DATE                 NOT NULL,
   AUTHOR_ID            NUMBER(19)           NOT NULL,
   MODIFIED_DATE        DATE,
   MODIFIER_ID          NUMBER(19),
   DESC_                VARCHAR2(4000),
  CONSTRAINT PK_BS_CAR_DRIVER PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_CAR_DRIVER IS '司机营运车辆';
COMMENT ON COLUMN BS_CAR_DRIVER.STATUS_ IS '状态：0-已禁用,1-启用中,2-已删除';
COMMENT ON COLUMN BS_CAR_DRIVER.DRIVER_ID IS '司机ID';
COMMENT ON COLUMN BS_CAR_DRIVER.CAR_ID IS '车辆ID';
COMMENT ON COLUMN BS_CAR_DRIVER.CLASSES IS '营运班次:如正班、副班、顶班';
COMMENT ON COLUMN BS_CAR_DRIVER.START_DATE IS '起始时段';
COMMENT ON COLUMN BS_CAR_DRIVER.END_DATE IS '结束时段';
COMMENT ON COLUMN BS_CAR_DRIVER.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_CAR_DRIVER.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_CAR_DRIVER.MODIFIED_DATE IS '最后修改时间';
COMMENT ON COLUMN BS_CAR_DRIVER.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BS_CAR_DRIVER.DESC_ IS '备注';
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
   ID                   NUMBER(19)           NOT NULL,
   TYPE_                VARCHAR2(255)        NOT NULL,
   DRIVER_ID            NUMBER(19)           NOT NULL,
   SUBJECT              VARCHAR2(1000),
   FROM_CARID           NUMBER(19),
   TO_CARID             NUMBER(19)           NOT NULL,
   FROM_CLASSES         VARCHAR2(255),
   TO_CLASSES           VARCHAR2(255)        NOT NULL,
   SHIFT_DATE           DATE                 NOT NULL,
   FROM_MOTORCADEID     NUMBER(19),
   TO_MOTORCADEID       NUMBER(19)           NOT NULL,
   FILE_DATE            DATE                 NOT NULL,
   AUTHOR_ID            NUMBER(19)           NOT NULL,
   MODIFIED_DATE        DATE,
   MODIFIER_ID          NUMBER(19),
   CONSTRAINT PK_BS_CARMAN_HISTORY PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_CARMAN_HISTORY IS '司机迁移历史';
COMMENT ON COLUMN BS_CARMAN_HISTORY.TYPE_ IS '迁移属性，如新入职';
COMMENT ON COLUMN BS_CARMAN_HISTORY.DRIVER_ID IS '司机ID';
COMMENT ON COLUMN BS_CARMAN_HISTORY.SUBJECT IS '其他';
COMMENT ON COLUMN BS_CARMAN_HISTORY.FROM_CARID IS '迁自车辆ID';
COMMENT ON COLUMN BS_CARMAN_HISTORY.TO_CARID IS '迁往车辆ID';
COMMENT ON COLUMN BS_CARMAN_HISTORY.FROM_CLASSES IS '原营运班次:如正班、副班、顶班';
COMMENT ON COLUMN BS_CARMAN_HISTORY.TO_CLASSES IS '新营运班次:如正班、副班、顶班';
COMMENT ON COLUMN BS_CARMAN_HISTORY.SHIFT_DATE IS '迁移日期';
COMMENT ON COLUMN BS_CARMAN_HISTORY.FROM_MOTORCADEID IS '原车队ID';
COMMENT ON COLUMN BS_CARMAN_HISTORY.TO_MOTORCADEID IS '迁往车队ID';
COMMENT ON COLUMN BS_CARMAN_HISTORY.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_CARMAN_HISTORY.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_CARMAN_HISTORY.MODIFIED_DATE IS '最后修改时间';
COMMENT ON COLUMN BS_CARMAN_HISTORY.MODIFIER_ID IS '最后修改人ID';
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

-- 黑名单ALTER TABLE BS_BLACKLIST
CREATE TABLE BS_BLACKLIST (
   ID                   NUMBER(19)           NOT NULL,
   TYPE_                VARCHAR2(255)        NOT NULL,
   UNIT_ID              number(19),
   DRIVER_ID            NUMBER(19)           NOT NULL,
   CAR_ID               NUMBER(19)           NOT NULL,
   MOTORCADE_ID         NUMBER(19),
   SUBJECT              VARCHAR2(1000)       NOT NULL,
   LOCK_DATE            DATE                 NOT NULL,
   UNLOCK_DATE          DATE,
   LOCKER_ID            NUMBER(19)           NOT NULL,
   UNLOCKER_ID          NUMBER(19),
   LOCK_REASON          VARCHAR2(4000),
   UNLOCK_REASON        VARCHAR2(4000),
   LEVEL_               VARCHAR2(255),
   CODE                 VARCHAR2(255),
   FILE_DATE            DATE                 NOT NULL,
   AUTHOR_ID            NUMBER(19)           NOT NULL,
   MODIFIED_DATE        DATE,
   MODIFIER_ID          NUMBER(19),
   CONSTRAINT PK_BS_BLACKLIST PRIMARY KEY (ID)
);
COMMENT ON TABLE BS_BLACKLIST IS '黑名单';
COMMENT ON COLUMN BS_BLACKLIST.TYPE_ IS '限制项目';
COMMENT ON COLUMN BS_BLACKLIST.UNIT_ID IS '车属单位ID';
COMMENT ON COLUMN BS_BLACKLIST.DRIVER_ID IS '司机ID';
COMMENT ON COLUMN BS_BLACKLIST.CAR_ID IS '车辆ID';
COMMENT ON COLUMN BS_BLACKLIST.MOTORCADE_ID IS '车队ID';
COMMENT ON COLUMN BS_BLACKLIST.SUBJECT IS '主题';
COMMENT ON COLUMN BS_BLACKLIST.LOCK_DATE IS '锁定时间';
COMMENT ON COLUMN BS_BLACKLIST.UNLOCK_DATE IS '解锁时间';
COMMENT ON COLUMN BS_BLACKLIST.LOCKER_ID IS '锁定人ID';
COMMENT ON COLUMN BS_BLACKLIST.UNLOCKER_ID IS '解锁人ID';
COMMENT ON COLUMN BS_BLACKLIST.LOCK_REASON IS '锁定原因';
COMMENT ON COLUMN BS_BLACKLIST.UNLOCK_REASON IS '解锁原因';
COMMENT ON COLUMN BS_BLACKLIST.LEVEL_ IS '等级';
COMMENT ON COLUMN BS_BLACKLIST.CODE IS '编号';
COMMENT ON COLUMN BS_BLACKLIST.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BS_BLACKLIST.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BS_BLACKLIST.MODIFIED_DATE IS '最后修改时间';
COMMENT ON COLUMN BS_BLACKLIST.MODIFIER_ID IS '最后修改人ID';
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_AUTHOR FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_UNIT FOREIGN KEY (UNIT_ID)
      REFERENCES BC_IDENTITY_ACTOR (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_MOTORCADE FOREIGN KEY (MOTORCADE_ID)
      REFERENCES BS_MOTORCADE (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_CAR FOREIGN KEY (CAR_ID)
      REFERENCES BS_CAR (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_DIRVER FOREIGN KEY (DRIVER_ID)
      REFERENCES BS_CARMAN (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_LOCKER FOREIGN KEY (LOCKER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BS_BLACKLIST ADD CONSTRAINT BS_BLACKLIST_UNLOCKER FOREIGN KEY (UNLOCKER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
