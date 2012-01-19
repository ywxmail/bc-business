/**
 * 
 */
package cn.bc.business.carman.dao;

import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.core.dao.CrudDao;

/**
 * 迁移记录Dao
 * 
 * @author dragon
 */
public interface CarByDriverHistoryDao extends CrudDao<CarByDriverHistory> {

	/**
	 * 更新该司机之前的营运车辆记录为注销状态
	 * 
	 * @param carMan
	 *            司机Id
	 * 
	 */
	void upDateCar4Driver(Long carManId);

	/**
	 * 查找司机最新营运记录
	 * 
	 * @param carManId
	 *            司机
	 * @return
	 */
	CarByDriverHistory findNewestCar(Long carManId);

	/**
	 * 更新车辆的营运司机
	 * 
	 * @param carId
	 *            车辆Id
	 */
	void updateDriver4Car(Long carId);
}