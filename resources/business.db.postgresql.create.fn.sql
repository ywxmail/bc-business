-- ##营运子系统的 postgresql 自定义函数和存储过程##

-- 获取指定车辆实时的营运司机信息,只适用于对当前在案车辆的处理
-- 返回值的格式为：张三,正班,id1;李四,副班,id2;小明,顶班,id3;小军,主挂,id4
-- 返回值是先按营运班次正序排序再按司机的入职时间正序排序进行合并的
-- 参数：cid - 车辆的id
CREATE OR REPLACE FUNCTION getDriverInfoByCarId(cid IN integer) RETURNS varchar AS $$
DECLARE
	--定义变量
	driverInfo varchar(4000);
BEGIN
	select string_agg(concat(name,',',(case when classes=1 then '正班' when classes=2 then '副班' when classes=3 then '顶班' when classes=4 then '主挂' else '无' end),',',id),';')
		into driverInfo
		from (select m.id as id,m.name as name,cm.classes as classes 
			from BS_CAR_DRIVER cm
			inner join BS_CARMAN m on m.id=cm.driver_id
			where cm.status_=0 and cm.car_id=cid
			order by cm.classes asc,m.work_date asc) as t;
	return driverInfo;
END;
$$ LANGUAGE plpgsql;

-- 获取指定车辆实时的经济合同责任人信息,只适用于对当前在案车辆的处理
-- 返回值的格式为：张三,李四
-- 返回值是按责任人的入职时间正序排序的
-- 参数：cid - 车辆的id
CREATE OR REPLACE FUNCTION getChargerInfoByCarId(cid IN integer) RETURNS varchar AS $$
DECLARE
	--定义变量
	chargerInfo varchar(4000);
BEGIN
	select string_agg(concat(name,',',id),';') into chargerInfo
		from (SELECT m.name as name,m.id as id
			FROM bs_car_contract cc
			inner join bs_contract c on c.id=cc.contract_id
			inner join bs_contract_charger c1 on c1.id=c.id
			inner join bs_carman_contract cm on cm.contract_id=c.id
			inner join bs_carman m on m.id=cm.man_id
			where cc.car_id=cid
			order by m.work_date asc) as t;
	return chargerInfo;
END;
$$ LANGUAGE plpgsql;

-- 获取指定司机所营运车辆的经济合同责任人信息,只适用于对当前在案司机的处理
-- 返回值的格式为：张三,李四
-- 返回值是按责任人的创建时间正序排序的
-- 参数：did - 司机的id
CREATE OR REPLACE FUNCTION getChargerInfoByDriverId(did IN integer) RETURNS varchar AS $$
DECLARE
	--定义变量
	chargerInfo varchar(4000);
BEGIN
	select string_agg(concat(name,',',id),';') into chargerInfo
		from (SELECT distinct p.name as name,p.id as id,p.file_date
			FROM bs_car_driver cd
			inner join bs_car_contract cc on cc.car_id=cd.car_id
			inner join bs_contract c on c.id=cc.contract_id
			inner join bs_carman_contract pm on pm.contract_id=cc.contract_id
			inner join bs_carman p on p.id=pm.man_id
			-- 正常的营运班次信息+当前经济合同 条件
			where cd.status_=0 and c.status_=0 and c.main=0 and c.type_=2 and cd.driver_id=did
			order by p.file_date asc) as t;
	return chargerInfo;
END;
$$ LANGUAGE plpgsql;

-- 获取指定经济合同的车辆信息
-- 返回值的格式为：粤A.xxxx1,id1;粤A.xxxx2,id2
-- 返回值是按合同的创建时间正序排序的
-- 参数：cid - 经济合同的id
CREATE OR REPLACE FUNCTION getCarInfoByChargerContractId(cid IN integer) RETURNS varchar AS $$
DECLARE
	--定义变量
	carInfo varchar(4000);
BEGIN
	select string_agg(concat(plateType,'.',plateNo,',',id),';') into carInfo
		from (SELECT car.plate_type as plateType,car.plate_no as plateNo,car.id as id
			FROM bs_car_contract cc
			inner join bs_contract c on c.id=cc.contract_id
			inner join bs_contract_charger c1 on c1.id=c.id
			inner join bs_car car on car.id=cc.car_id
			where cc.contract_id=cid
			order by c.file_date desc) as t;

	return carInfo;
END;
$$ LANGUAGE plpgsql;


-- 获取指定经济合同的责任人信息
-- 返回值的格式为：姓名1,id1;姓名2,id2
-- 返回值是按合同的创建时间正序排序的
-- 参数：cid - 经济合同的id
CREATE OR REPLACE FUNCTION getChargerInfoByChargerContractId(cid IN integer) RETURNS varchar AS $$
DECLARE
	--定义变量
	chargerInfo varchar(4000);
BEGIN
	select string_agg(concat(name,',',id),';') into chargerInfo
		from (SELECT p.name as name,p.id as id
			FROM bs_carman_contract cc
			inner join bs_contract c on c.id=cc.contract_id
			inner join bs_contract_charger c1 on c1.id=c.id
			inner join bs_carman p on p.id=cc.man_id
			where cc.contract_id=cid
			order by p.work_date asc) as t;

	return chargerInfo;
END;
$$ LANGUAGE plpgsql;

-- 获取指定劳动合同的车辆信息
-- 返回值的格式为：粤A.xxxx1,id1;粤A.xxxx2,id2
-- 返回值是按合同的创建时间正序排序的
-- 参数：cid - 劳动合同的id
CREATE OR REPLACE FUNCTION getCarInfoByLabourContractId(cid IN integer) RETURNS varchar AS $$
DECLARE
	--定义变量
	carInfo varchar(4000);
BEGIN
	select string_agg(concat(plateType,'.',plateNo,',',id),';') into carInfo
		from (SELECT car.plate_type as plateType,car.plate_no as plateNo,car.id as id
			FROM bs_car_contract cc
			inner join bs_contract c on c.id=cc.contract_id
			inner join bs_contract_labour c1 on c1.id=c.id
			inner join bs_car car on car.id=cc.car_id
			where cc.contract_id=cid
			order by c.file_date desc) as t;

	return carInfo;
END;
$$ LANGUAGE plpgsql;


--##查找司机营运车辆的自定义函数和存储过程##
CREATE OR REPLACE FUNCTION getCarInfoByDriverId(did IN integer) RETURNS varchar AS $$
DECLARE
	--定义变量
	caridInfo varchar(4000);
BEGIN
	select string_agg(concat(name,',',(case when classes=1 then '正班' when classes=2 then '副班' when classes=3 then '主挂' when classes=4 then '顶班' else '无' end),',',id),';')
		into caridInfo
		from (select c.id as id,concat(c.plate_type,'.',c.plate_no) as name,cm.classes as classes 
			from BS_CAR_DRIVER cm
			inner join bs_car c on c.id=cm.car_id
			where cm.status_=0 and cm.driver_id=did
			order by cm.classes asc,c.file_date asc) as t;
	return caridInfo;
END;
$$ LANGUAGE plpgsql;


--##将现有的迁移记录的最新的迁移记录标识为当前的迁移记录##
CREATE OR REPLACE FUNCTION updateCarByDriverHistory4Min(did IN integer) RETURNS integer AS $$
DECLARE
	--定义变量
	main integer;
BEGIN
	
	update BS_CAR_DRIVER_HISTORY set main=0 where id in (
		select  id from BS_CAR_DRIVER_HISTORY where driver_id=did order by move_date desc limit 1 );

	return main;
END;
$$ LANGUAGE plpgsql;


--##获取司机最新的迁移类型##
CREATE OR REPLACE FUNCTION getDriverMoveTypeByDriverId(did IN integer) RETURNS integer AS $$
DECLARE
	--定义变量
	moveType integer;
BEGIN
	select  h.move_type
		into moveType
		from BS_CAR_DRIVER_HISTORY h where h.driver_id=did order by h.move_date desc limit 1;
	return moveType;
END;
$$ LANGUAGE plpgsql;

--##获取司机最新的迁移日期##
CREATE OR REPLACE FUNCTION getDriverMoveDateByDriverId(did IN integer) RETURNS timestamp AS $$
DECLARE
	--定义变量
	moveDate timestamp;
BEGIN
	select  h.move_date
		into moveDate
		from BS_CAR_DRIVER_HISTORY h where h.driver_id=did order by h.move_date desc limit 1;
	return moveDate;
END;
$$ LANGUAGE plpgsql;

--##获取司机最新的驾驶状态--
CREATE OR REPLACE FUNCTION getDriverClassesByDriverId(did IN integer) RETURNS integer AS $$
DECLARE
	--定义变量
	classes integer;
BEGIN
	select  c.classes
		into classes
		from BS_CAR_DRIVER c where driver_id=did order by c.file_date desc limit 1;
	return classes;
END;
$$ LANGUAGE plpgsql;


--##获取司机最新的营运的主车辆ID--
CREATE OR REPLACE FUNCTION getDriverMainCarIdByDriverId(did IN integer) RETURNS integer AS $$
DECLARE
	--定义变量
	mainCarId integer;
BEGIN
	select  c.car_id
		into mainCarId
		from BS_CAR_DRIVER c where c.status_=0 and (c.classes=1 or c.classes=2 or c.classes=4) and driver_id=did;
	return mainCarId;
END;
$$ LANGUAGE plpgsql;

