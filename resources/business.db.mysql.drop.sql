-- bc营运管理子系统的删表脚本
-- 运行此脚本之后再运行平台的删表脚本framework.db.mysql.drop.sql

-- 数据转换记录
drop table if exists DC_RECORD;

-- 同步记录
drop table if exists BS_SYNC_INFRACT_TRAFFIC;

-- 黑名单
drop table if exists BS_BLACKLIST;

-- 合同
drop table if exists BS_INDUSTRIAL_INJURY;
drop table if exists BS_CARMAN_CONTRACT;
drop table if exists BS_CAR_CONTRACT;
drop table if exists BS_CONTRACT_LABOUR;
drop table if exists BS_CONTRACT_CHARGER;
drop table if exists BS_CONTRACT;

-- 营运事件
DROP TABLE IF EXISTS BS_CASE_ADVICE;
DROP TABLE IF EXISTS BS_CASE_PRAISE;
DROP TABLE IF EXISTS BS_CASE_ACCIDENT;
DROP TABLE IF EXISTS BS_CASE_INFRACT_TRAFFIC;
DROP TABLE IF EXISTS BS_CASE_INFRACT_BUSINESS;
DROP TABLE IF EXISTS BS_CASE_BASE;

-- 司机迁移历史
drop table if exists BS_CARMAN_HISTORY;

-- 司机营运车辆
drop table if exists BS_CAR_DRIVER;

-- 车辆与证件的关联
drop table if exists BS_CAR_CERT;

-- 车辆
drop table if exists BS_CAR;

-- 车队历史车辆数
drop table if exists BS_HISTORY_CAR_QUANTITY;
drop table if exists BS_MOTORCADE_CARQUANTITY;

-- 车队负责人
drop table if exists BS_CHARGER;
-- 车队信息
drop table if exists BS_MOTORCADE;

-- 司机责任人与证件的关联
drop table if exists BS_CARMAN_CERT;

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
