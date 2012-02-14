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

	/**
	 * 更新车辆的所属公司和所属车队信息
	 * 
	 * @param carId
	 *            车辆Id
	 * @param motorcadeId
	 *            车队Id
	 * @param unit
	 *            公司名
	 */
	void updateCar4UnitAndMotorcade(Long carId, String unit, Long motorcadeId);

	/**
	 * 更新该司机营运车辆信息，主车辆，和迁移类型
	 * 
	 * @param driverId
	 *            司机Id
	 * @param mainCarId
	 *            主车辆Id
	 * @param moveType
	 *            迁移类型
	 * @param classes
	 *            驾驶状态
	 */
	void updateDriverOperationCar(Long driverId, Long mainCarId, int moveType,
			int classes);

	/**
	 * 更新司机营运班次记录：将不属于该司机迁移记录产生的其他营运记录的状态更新为注销
	 * 
	 * @param driverId
	 *            司机Id
	 * @param pid
	 *            产生该营运记录的迁移记录Id
	 */
	void updateCarByDriverStatus(Long driverId, Long pid);

}