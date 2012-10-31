/**
 * 
 */
package cn.bc.business.carman.service;

import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.core.service.CrudService;

/**
 * 迁移记录Service
 * 
 * @author dragon
 */
public interface CarByDriverHistoryService extends
		CrudService<CarByDriverHistory> {

	/**
	 * 更新该司机之前的营运班次记录的状态为注销
	 * 
	 * @param carManId
	 *            司机ID
	 * 
	 */
	void upDateCarByDriver(Long carMan);

	/**
	 * 查找司机最新迁移记录
	 * 
	 * @param carManId
	 *            司机ID
	 * @return
	 */
	CarByDriverHistory findNewestCarByDriverHistory(Long carManId);

	/**
	 * 更新车辆的营运司机
	 * 
	 * @param carId
	 *            车辆Id
	 */
	void upDateDriver4Car(Long carId);

	void saveShiftwork(CarByDriverHistory entity, Long[] carIds);

	/**
	 * 根据车辆Id和迁移类型获取最新的迁移记录
	 * 
	 * @param carId
	 *            车辆ID
	 * @param movetypeZcd
	 *            迁移类型
	 * @return
	 */
	CarByDriverHistory getNeWsetCarByDriverHistory4CarAndMoveType(Long carId,
			int movetypeZcd);

	/**
	 * 根据车辆ID获取最新的迁移记录
	 * 
	 * @param carId
	 * @return
	 */
	CarByDriverHistory findNewestCarByDriverHistoryByCarId(Long carId);

}