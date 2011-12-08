/**
 * 
 */
package cn.bc.business.car.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.car.domain.Car;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;


/**
 * 车辆Service
 * 
 * @author dragon
 */
public interface CarService extends CrudService<Car> {



	/**
	 * 查找汽车列表
	 * @parma condition 
	 * @return
	 */
	List<Map<String,Object>> list(Condition condition);
	
	/**
	 * 查找汽车分页
	 * @parma condition 
	 * @parma Page 
	 * @return
	 */
	Page<? extends Object> page(Condition condition,int pageNo, int pageSize);

	/** 根据司机ID查找返回状态为启用中相关辆信息 */
	List<Car> selectAllCarByCarManId(Long id);

	/**
	 * 根据车牌号查找车牌号
	 * @parma carPlateNo 
	 * @return Long
	 */
	Long findcarInfoByCarPlateNo(String carPlateNo);
}