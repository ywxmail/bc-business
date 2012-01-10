-- 更新车辆的营运司机信息
update bs_car set driver=getDriverInfoByCarId(id);
select file_date,id,driver,getDriverInfoByCarId(id) from bs_car order by file_date desc;

-- 更新车辆的责任人信息
update bs_car set charger=getChargerInfoByCarId(id);
select file_date,id,charger,getChargerInfoByCarId(id) from bs_car order by file_date desc;

-- 更新司机的责任人信息
update bs_carman set charger=getChargerInfoByDriverId(id);
select file_date,id,charger,getChargerInfoByDriverId(id) from bs_carman order by file_date desc;
select file_date,id,charger,getChargerInfoByDriverId(id) from bs_carman where id=100840 order by file_date desc;

-- 更新经济合同的责任人信息
update bs_contract set ext_str2=getChargerInfoByContractId(id)
	where id in (
		select c.id from bs_contract c inner join bs_contract_charger ch on ch.id=c.id
);
