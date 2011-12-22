-- ##营运子系统的 mysql 自定义函数和存储过程##

DELIMITER $$ 
DROP FUNCTION IF EXISTS getDriverInfoByCarId $$ 
-- 获取指定车辆实时的营运司机信息,只适用于对当前在案车辆的处理
-- 返回值的格式为：张三(正班),李四(副班),小明(顶班)
-- 返回值是先按营运班次正序排序再按司机的入职时间正序排序进行合并的
-- 参数：cid - 车辆的id
CREATE FUNCTION getDriverInfoByCarId(cid BIGINT) RETURNS varchar(4000) 
BEGIN
	DECLARE driverInfo varchar(4000);
	select group_concat(concat(name,'(',(case when classes=1 then '正班' when classes=2 then '副班' when classes=3 then '顶班' else '无' end),')'))
		into driverInfo
		from (select m.name as name,cm.classes as classes 
			from BS_CAR_DRIVER cm
			inner join BS_CARMAN m on m.id=cm.driver_id
			where cm.status_=0 and cm.car_id=cid
			order by cm.classes asc,m.work_date asc) as t;
	return driverInfo;
END $$ 
DELIMITER ; 
