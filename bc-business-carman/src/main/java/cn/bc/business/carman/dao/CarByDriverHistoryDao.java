/**
 * 
 */
package cn.bc.business.carman.dao;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.core.dao.CrudDao;

/**
 * 迁移记录Dao
 * 
 * @author dragon
 */
public interface CarByDriverHistoryDao extends CrudDao<CarByDriverHistory> {

	/**
	 * 更新该司机的营运车辆
	 * 
	 * @param carMan
	 *            司机
	 * @param car
	 *            车辆
	 * @param classes
	 *            新营运班次
	 */
	void upDateCar4Driver(CarMan carMan, Car car, int classes);

	/**
	 * 查找司机最新营运记录
	 * 
	 * @param carManId
	 *            司机
	 * @return
	 */
	CarByDriverHistory findNewestCar(Long carManId);
}