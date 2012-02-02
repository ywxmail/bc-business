/**
 * 
 */
package cn.bc.business.carman.dao;

import java.util.List;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.core.dao.CrudDao;

/**
 * 司机营运车辆Dao
 * 
 * @author dragon
 */
public interface CarByDriverDao extends CrudDao<CarByDriver> {
	// 根据司机ID查询营运车辆信息
	Car findBycarManId(Long id);

	/**
	 * 更新车辆模块的司机信息
	 * 
	 * @param carId
	 *            车辆的id
	 */
	void updateCar4Driver(Long carId);

	/**
	 * 根据司机ID查找返回司机营运班次的信息
	 * 
	 * @param id
	 * @return
	 */
	CarByDriver findCarByDriverBycarManId(Long id);

	/**
	 * 根据Pid找出与迁移类型为顶班的迁移记录相对应的营运班次
	 * 
	 * @param pid
	 * @return
	 */
	List<CarByDriver> find4Shiftwork(Long pid);
}