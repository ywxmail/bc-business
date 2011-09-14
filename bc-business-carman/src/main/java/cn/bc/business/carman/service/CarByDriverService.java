/**
 * 
 */
package cn.bc.business.carman.service;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.core.service.CrudService;

/**
 * 司机营运车辆Service
 * 
 * @author dragon
 */
public interface CarByDriverService extends CrudService<CarByDriver> {

	// 根据司机ID查找返回相关正班车辆信息
	Car selectCarByCarManId(Long id);

}