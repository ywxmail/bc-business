-- ##bc营运管理子系统的 oracle 删表脚本##
-- ##运行此脚本之后再运行平台的删表脚本framework.db.oracle.drop.sql##

-- 车辆
CALL DROP_USER_TABLE('BS_CAR');

-- 查看历史车辆数
CALL DROP_USER_TABLE('BS_HISTORY_CAR_QUANTITY');

-- 车队信息
CALL DROP_USER_TABLE('BS_MOTORCADE');

-- 司机责任人与证件的关联
CALL DROP_USER_TABLE('BS_CARMAN_CERT');

-- 证件
CALL DROP_USER_TABLE('BS_CERT_IDENTITY');
CALL DROP_USER_TABLE('BS_CERT_DRIVING');
CALL DROP_USER_TABLE('BS_CERT');

-- 司机责任人
CALL DROP_USER_TABLE('BS_CARMAN');
