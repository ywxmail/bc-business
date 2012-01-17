/**
 * 
 */
package cn.bc.business.carman.service;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
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
	 * @param carId
	 *            新车辆ID
	 * @param newclasses
	 *            新营运班次
	 */
	void upDateCarByDriver(CarMan carMan, Car car, int newclasses);

	/**
	 * 查找司机最新营运记录
	 * 
	 * @param carManId
	 *            司机ID
	 * @return
	 */
	CarByDriverHistory findNewestCarByDriverHistory(Long carManId);
}