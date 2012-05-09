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
	select string_agg(concat(name,',',(case when classes=1 then '正班' when classes=2 then '副班' when classes=3 then '主挂' when classes=4 then '顶班' else '无' end),',',id),';')
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
			where cc.car_id=cid and c.status_=0
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
		from BS_CAR_DRIVER c where c.status_=0 and (c.classes=1 or c.classes=2 or c.classes=3) and driver_id=did;
	return mainCarId;
END;
$$ LANGUAGE plpgsql;

-- 统计采购库存号码段函数
-- 输入参数：bid采购单id,buy_count采购数量,start_no采购单开始号,end_no采购单结束号
CREATE OR REPLACE FUNCTION getbalancenumberbyinvoicebuyid(bid INTEGER,buy_count INTEGER,start_no CHARACTER VARYING,end_no CHARACTER VARYING)
	RETURNS CHARACTER VARYING  AS
$BODY$
DECLARE
		-- 定义变量
		-- 临时开始号,每比较一条销售明细临时开始号都会根据情况变化
		startno_tmp CHARACTER VARYING;
		-- 临时结束号,每比较一条销售明细临时结束号都会根据情况变化
		endno_tmp CHARACTER VARYING;
		-- 数字类型临时变量
		number_temp1 INTEGER;
		-- 数字类型临时变量
		number_temp2 INTEGER;
		-- 销售数量
		sell_count INTEGER;
		-- 记录库存号码段
		remainingNumber CHARACTER VARYING;
		-- 变量一行结果的记录	
		rowinfo RECORD;
BEGIN
	-- 先根据采购单id,查销售数量
	SELECT SUM(count_) INTO sell_count
	FROM bs_invoice_sell_detail 
	WHERE buy_id=bid AND status_=0;
	-- 当sell_count为空,没有销售,所以库存号码段为采购单的开始号到结束号
	IF sell_count IS NULL THEN
		RETURN '['||start_no||'~'||end_no||']';
	-- 当销售数量大于或等采购数量时,此采购单已经销售完,库存号码返回空
	ELSEIF sell_count>=buy_count THEN
		RETURN '';
	-- 其他情况此采购单有对应的销售单,并且采购数量大于销售数量,有库存号码
	ELSE
			-- 初始化库存号码段变量
			remainingNumber := '';
			-- 将采购单的开始号赋值给临时开始号变量；
			startno_tmp := trim(start_no);
							-- 根据采购单ID查出对应的销售明细结果，并将结果排序
			FOR rowinfo IN SELECT d.start_no,d.end_no
											FROM  bs_invoice_sell_detail d
											WHERE d.buy_id=bid and d.status_=0
											ORDER BY d.start_no
			-- 循环开始
			LOOP
					-- 第一次循环时将明细开始号和临时开始号转为数字临时变量,后续作两号比较时使用
					number_temp1 := convert_stringtonumber(rowinfo.start_no);
					number_temp2 := convert_stringtonumber(startno_tmp);
					-- 明细开始号大于临时开始号，表明从临时号到明细结束号这一段号码中临时开始号到明细开始号减1为未出售的库存号码段
					IF number_temp1 > number_temp2 THEN
						-- 临时开始号到明细开始号减1保存临时结束号,若有0前序需要进行补0操作
						endno_tmp := trim(convert_numbertostring(convert_stringtonumber(trim(rowinfo.start_no))-1,startno_tmp));
						-- 记录这一段未出售的号码段
						remainingNumber := remainingNumber||'['||startno_tmp||'~'||endno_tmp||'] ';
						-- 临时的开始号变为明细结束号+1
						startno_tmp := trim(convert_numbertostring(convert_stringtonumber(trim(rowinfo.end_no))+1,trim(rowinfo.end_no)));
						-- 临时结束号等于明细结束号。
						endno_tmp := trim(rowinfo.end_no);
					END IF;
					-- 明细开始号等于临时开始号,历史开始号到明细结束号这一段为已出售的
					IF number_temp1=number_temp2	THEN
						startno_tmp := trim(convert_numbertostring(convert_stringtonumber(trim(rowinfo.end_no))+1,trim(rowinfo.end_no)));
						endno_tmp:= trim(rowinfo.end_no);
					END IF;
			END LOOP;	
			-- 循环结束,若最后一条明细结束号小于采购单的结束号，则范围[最后一条明细的结束号+1，采购单的结束号]为库存号码段
			IF convert_stringtonumber(endno_tmp)<convert_stringtonumber(trim(end_no)) THEN
						startno_tmp= trim(convert_numbertostring(convert_stringtonumber(endno_tmp)+1,endno_tmp));
						endno_tmp=trim(end_no);
						remainingNumber := remainingNumber||'['||startno_tmp||'~'||endno_tmp||'] '; 
			END IF;
			-- 返回统计好的库存号码段
			RETURN remainingNumber;

	END IF;
END;
$BODY$
LANGUAGE plpgsql;
 
-- 字符串转数字函数
CREATE OR REPLACE FUNCTION convert_stringtonumber(string_ character varying)
	RETURNS integer  AS
$BODY$
DECLARE
		-- 定义变量
		number_ integer;
		text_expression character varying;
		length_ integer;
		i integer;
BEGIN
	-- 检测字符串的长度
	length_ := char_length(trim(string_));
	text_expression := '';
	FOR i IN 1..length_
	LOOP
	-- 生成匹配的表达式
	text_expression := text_expression||'9';
	END LOOP;
	number_ := to_number(string_,text_expression);
	return number_;
END;
$BODY$
 LANGUAGE plpgsql;

-- 数字转字符串函数
CREATE OR REPLACE FUNCTION convert_numbertostring(int_ integer,text_ character varying)
	RETURNS character varying  AS
$BODY$
DECLARE
		-- 定义变量
		string_ character varying;
		text_expression character varying;
		length_ integer;
		i integer;
BEGIN
	-- 检测字符串的长度
	length_ := char_length(trim(text_));
	text_expression := '';
	FOR i IN 1..length_
	LOOP 
	-- 生成匹配的表达式
	text_expression := text_expression||'0';
	END LOOP;
	string_ := trim(to_char(int_,text_expression));
	RETURN string_;
END;
$BODY$
LANGUAGE plpgsql;
 
-- 统计剩余数量函数
CREATE OR REPLACE FUNCTION getbalancecountbyinvoicebuyid(bid integer)
	RETURNS integer AS
$BODY$
DECLARE
	-- 定义变量
	-- 采购数量
	buy_count INTEGER;
	-- 销售数量
	sell_count INTEGER;
BEGIN
	select b.count_,sum(d.count_) 
	into buy_count,sell_count
	from bs_invoice_buy b
		left join bs_invoice_sell_detail d on d.buy_id=b.id and d.status_=0
		where b.id=bid 
		group by b.id;
		-- 若为空时，表示还没销售，所以剩余数量应该等于采购数量
		IF sell_count is null THEN
			return buy_count;
		ELSE 
			return buy_count-sell_count;
		END IF;
END
$BODY$
LANGUAGE plpgsql;

 -- 判断发票销售开始号、结束号、数量异常函数
CREATE OR REPLACE FUNCTION checkI4SellDetailCount(sell_count INTEGER,start_no CHARACTER VARYING,end_no CHARACTER VARYING)
	RETURNS INTEGER  AS
$BODY$
DECLARE
		-- 定义变量
		count_ INTEGER;
		-- 数字类型临时变量
		start_temp INTEGER;
		-- 数字类型临时变量
		end_temp INTEGER;
BEGIN
	start_temp := convert_stringtonumber(start_no);
	end_temp :=	convert_stringtonumber(end_no);
	count_ := (end_temp-start_temp+1)/100;
		IF sell_count = count_ THEN
			RETURN 0;
		ELSE
			RETURN 1;
		END IF;
END;
$BODY$
 LANGUAGE plpgsql
 IMMUTABLE;
 