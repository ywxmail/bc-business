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
	
-- 统计采购单剩余数量函数
CREATE OR REPLACE FUNCTION get_balancecount_invoice_buyid(bid integer)
	RETURNS integer AS
$BODY$
DECLARE
	-- 定义变量
	-- 采购数量
	buy_count INTEGER;
	-- 销售数量
	sell_count INTEGER;
	-- 退票数量
	refund_count INTEGER;
BEGIN
  --采购数量
	select count_ into buy_count from bs_invoice_buy where id=bid;
	--销售数量
	select sum(count_) into sell_count
	from bs_invoice_sell_detail where buy_id=bid and status_=0 and type_=1;
	--退票数量
	select sum(count_) into refund_count
	from bs_invoice_sell_detail where buy_id=bid and status_=0 and type_=2;

		-- 若为空时
	IF sell_count is null THEN
		sell_count := 0;
	END IF;
	IF refund_count is null THEN
		refund_count := 0;
	END IF;

	RETURN buy_count-sell_count+refund_count;
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

-- 发票销售开始号、结束号、数量异常函数索引
CREATE INDEX BSIDX_INVOICESELLDETAIL_CHECKI ON bs_invoice_sell_detail(checkI4SellDetailCount(count_,start_no,end_no));
 
--获取金盾网的违章地址与金盾的相关信息的Id
CREATE OR REPLACE FUNCTION findJinDunByJiaoWei(syncCode IN varchar,plateNo IN varchar,happenDate IN timestamp) RETURNS varchar AS $$
DECLARE
	--定义变量
	jinDunInfo varchar(4000);
BEGIN
	select concat(jd.address,';',jd.id) into jinDunInfo
			from BS_SYNC_JINDUN_JTWF jd
			inner join BC_SYNC_BASE sb on sb.id=jd.id
			where sb.sync_code=syncCode or (jd.car_plate_no=plateNo and to_char(jd.happen_date,'YYYY-MM-DD HH:MI')=to_char(happenDate,'YYYY-MM-DD HH:MI'));
	return jinDunInfo;
END;
$$ LANGUAGE plpgsql;

-- 获取指定车辆最新的经济合同残值归属
-- 参数：cid - 车辆的id
CREATE OR REPLACE FUNCTION getContract4ChargerScrapTo(cid IN integer) RETURNS varchar AS $$
DECLARE
	--定义变量
	scrapToInfo varchar(4000);
BEGIN
	select ch.scrapto into scrapToInfo
		from bs_contract_charger ch 
			inner join bs_contract bc on bc.id=ch.id
			inner join bs_car_contract carc on ch.id = carc.contract_id
			where carc.car_id=cid  order by bc.file_date desc limit 1 ;
	return scrapToInfo;
END;
$$ LANGUAGE plpgsql;


-- 视图显示本期费用明细函数
-- 输入参数：费用id,费用类型1:本期实收明细type_,2:本期欠费明细
CREATE OR REPLACE FUNCTION getfeedetailbyfeeid(fee_id INTEGER,type_ INTEGER)
	RETURNS CHARACTER VARYING  AS
$BODY$
DECLARE
		-- 记录费用明细字符串
		feedetailStr CHARACTER VARYING;
		-- 记录费用名称字符串
		feenameStr CHARACTER VARYING;
		-- 记录费用
		feechargeStr CHARACTER VARYING;

		-- 变量一行结果的记录	
		rowinfo RECORD;
BEGIN
		-- 初始化费用明细字符串
		feedetailStr := '';

		-- 根据采购单ID查出对应的销售明细结果
		FOR rowinfo IN SELECT f.fee_name,f.charge
										FROM  bs_fee_detail f
										WHERE f.fid=fee_id and f.fee_type= type_
	  -- 循环开始
		LOOP
				feenameStr := rowinfo.fee_name;
				feechargeStr := trim(to_char(rowinfo.charge, '99999999D99'));

				feedetailStr := feedetailStr||'[费用名称: '||feenameStr||',费用金额: '||feechargeStr||'] ';

		END LOOP;
		-- 费用明细字符串
		RETURN feedetailStr;
END;
$BODY$
LANGUAGE plpgsql;


-- 视图显示前期欠费明细函数
-- 输入参数：车辆id,收费年份t_fee_year,收费月份t_fee_month
CREATE OR REPLACE FUNCTION getb4feedetailbyfeeid(t_car_id INTEGER,t_fee_year INTEGER,t_fee_month INTEGER)
	RETURNS CHARACTER VARYING  AS
$BODY$
DECLARE
		-- 记录费用明细字符串
		feedetailStr CHARACTER VARYING;
		-- 记录费用名称字符串
		feenameStr CHARACTER VARYING;
		-- 记录费用
		feechargeStr CHARACTER VARYING;
		-- 前期收费年
		b4_year INTEGER;
		-- 前期收费月
		b4_month INTEGER;
		-- 前期费用id
		b4_id INTEGER;

		-- 变量一行结果的记录	
		rowinfo RECORD;
BEGIN
		IF t_fee_month = 1 THEN
			b4_year = t_fee_year - 1;
			b4_month = 12;
		ELSE
			b4_year = t_fee_year;
			b4_month = t_fee_month - 1;
		END IF;

		select f.id
			into b4_id
			from bs_fee f 
			where f.car_id = t_car_id and f.fee_year = b4_year and f.fee_month = b4_month;

		-- 初始化费用明细字符串
		feedetailStr := '';

		IF b4_id is not null THEN

			-- 根据采购单ID查出对应的销售明细结果
			FOR rowinfo IN SELECT f.fee_name,f.charge
											FROM  bs_fee_detail f
											WHERE f.fid=b4_id and f.fee_type= 2
			-- 循环开始
			LOOP
					feenameStr := rowinfo.fee_name;
					feechargeStr := trim(to_char(rowinfo.charge, '99999999D99'));

					feedetailStr := feedetailStr||'[费用名称: '||feenameStr||',费用金额: '||feechargeStr||'] ';
			END LOOP;

		END IF;

		-- 费用明细字符串
		RETURN feedetailStr;
END;
$BODY$
LANGUAGE plpgsql;

	  
-- 社保收费规则视图明细列函数
CREATE OR REPLACE FUNCTION getsocialsecurityruledetail(pid INTEGER)
	RETURNS CHARACTER VARYING  AS
$BODY$
DECLARE
		-- 保存明细字符串
		details CHARACTER VARYING;
		-- 单位缴率
		ur CHARACTER VARYING;
		-- 个人缴率
		pr CHARACTER VARYING;
		--	基数
		bn CHARACTER VARYING;
		-- id临时变量用于保存函数输入的值。
		nid INTEGER;
		-- 一行结果的记录	
		rowinfo RECORD;
BEGIN
		-- 初始化变量
		details:='';
		nid:=pid;
		FOR rowinfo IN SELECT d.name,d.unit_rate,d.personal_rate,d.base_number
										FROM bs_socialsecurityrule_detail d
										WHERE d.pid=nid
		-- 循环开始
		LOOP
			ur:=rowinfo.unit_rate;
			pr:=rowinfo.personal_rate;
			bn:=rowinfo.base_number;
			details:=details||'('||rowinfo.name||',';
			details:=details||replace(ur,'.00','')||',';
			details:=details||replace(pr,'.00','')||',';
			details:=details||replace(bn,'.00','')||')';
		END LOOP;	
		RETURN details;
END;
$BODY$
LANGUAGE plpgsql;
