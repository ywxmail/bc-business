-- bc营运管理子系统的 postgresql 删表脚本
-- 运行此脚本之后再运行平台的删表脚本framework.db.postgresql.drop.sql

-- 用于生成数据转换 id的序列
drop sequence if exists CORE_SEQUENCE;
drop sequence if exists DC_SEQUENCE;

-- 数据转换记录
drop table if exists DC_RECORD;

-- 司机招聘
DROP TABLE IF EXISTS BS_TEMP_DRIVER_WORKFLOW;
DROP TABLE IF EXISTS BS_TEMP_DRIVER;

-- 安全学习和回场检
drop table if exists BS_ARRANGE_DRIVER;
drop table if exists BS_ARRANGE_CAR;
drop table if exists BS_SIGN_RECORD;
drop table if exists BS_ARRANGE_MEMBER;
drop table if exists BS_ARRANGE;
drop table if exists BS_MEMBER;

-- 信息管理
drop table if exists BS_INFO;

-- 承包费
drop table if exists BS_FEE_DETAIL;
drop table if exists BS_FEE;

-- 费用模板表
drop table if exists BS_FEE_TEMPLATE;
-- 合同的收费明细
drop table if exists bs_contract_fee_detail;

-- 发票管理
drop table if exists BS_INVOICE_SELL_DETAIL;
drop table if exists BS_INVOICE_SELL;
drop table if exists BS_INVOICE_BUY;

-- 证照遗失管理
drop table if exists BS_CERT_LOST_ITEM;
drop table if exists BS_CERT_LOST;

-- LPG配置模块
drop table if exists BS_CAR_LPGMODEL;

-- 同步记录
drop table if exists BS_SYNC_JIAOWEI_JTWF;
drop table if exists BS_SYNC_JINDUN_JTWF;
drop table if exists BS_SYNC_JIAOWEI_YYWZ; 
drop table if exists BS_SYNC_JIAOWEI_ADVICE;

-- 黑名单
--黑名单关系表
drop table if exists BS_CARMAN_BLACKLIST;
--黑名单表
drop table if exists BS_BLACKLIST;

-- 合同
drop table if exists BS_INDUSTRIAL_INJURY;
drop table if exists BS_CARMAN_CONTRACT;
drop table if exists BS_CAR_CONTRACT;
drop table if exists BS_CONTRACT_LABOUR;
drop table if exists BS_CONTRACT_CHARGER;
drop table if exists BS_CONTRACT;

-- 营运事件
DROP TABLE IF EXISTS BS_CASE_LOST;
DROP TABLE IF EXISTS BS_CASE_ADVICE;
DROP TABLE IF EXISTS BS_CASE_PRAISE;
DROP TABLE IF EXISTS BS_CASE_ACCIDENT;
DROP TABLE IF EXISTS BS_CASE_INFRACT_TRAFFIC;
DROP TABLE IF EXISTS BS_CASE_INFRACT_BUSINESS;
DROP TABLE IF EXISTS BS_CASE_BASE;

-- 司机迁移历史
drop table if exists BS_CARMAN_HISTORY;

-- 车辆保单险种
drop table if exists BS_INSURANCE_TYPE;
-- 购买车保险种
drop table if exists BS_BUY_PLANT;
-- 车辆保单
drop table if exists BS_CAR_POLICY;

-- 司机营运车辆
drop table if exists BS_CAR_DRIVER;

-- 迁移记录
drop table if exists BS_CAR_DRIVER_HISTORY;

-- 车辆经营权
drop table if exists BS_CAR_OWNERSHIP;

-- 司机责任人与证件的关联
drop table if exists BS_CARMAN_CERT;

-- 车辆与证件的关联
drop table if exists BS_CAR_CERT;

-- 证件
drop table if exists BS_CERT_IDENTITY;
drop table if exists BS_CERT_DRIVING;
drop table if exists BS_CERT_CYZG;
drop table if exists BS_CERT_FWZG;
drop table if exists BS_CERT_JSPX;
drop table if exists BS_CERT_ROADTRANSPORT;
drop table if exists BS_CERT_VEHICELICENSE;
drop table if exists BS_CERT;

-- 司机责任人
drop table if exists BS_CARMAN;

-- 车辆
drop table if exists BS_CAR_MODEL;
drop table if exists BS_CAR;

-- 车队历史车辆数
drop table if exists BS_HISTORY_CAR_QUANTITY;
drop table if exists BS_MOTORCADE_CARQUANTITY;

-- 车队负责人
drop table if exists BS_CHARGER;
-- 车队信息
drop table if exists BS_MOTORCADE;
