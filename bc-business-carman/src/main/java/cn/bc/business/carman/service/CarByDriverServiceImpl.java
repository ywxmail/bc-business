/**
 * 
 */
package cn.bc.business.carman.service;

import java.util.List;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.dao.CarByDriverDao;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.core.service.DefaultCrudService;

/**
 * 司机营运车辆Service的实现
 * 
 * @author dragon
 */
public class CarByDriverServiceImpl extends DefaultCrudService<CarByDriver>
		implements CarByDriverService {

	private CarByDriverDao carByDriverDao;

	public CarByDriverDao getCarByDriverDao() {
		return carByDriverDao;
	}

	public void setCarByDriverDao(CarByDriverDao carByDriverDao) {
		this.carByDriverDao = carByDriverDao;
		this.setCrudDao(carByDriverDao);
	}

	public Car selectCarByCarManId(Long id) {
		return (this.carByDriverDao.findBycarManId(id));
	}

	public CarByDriver selectCarByDriver4CarManId(Long id) {
		return (this.carByDriverDao.findCarByDriverBycarManId(id));
	}

	public List<CarByDriver> findCarByDriverInfo4DriverId(Long driverId) {
		return (this.carByDriverDao.findCarByDrivers4DriverId(driverId));
	}

	public List<CarByDriver> findCarByDriverInfoByPid(Long pid) {
		return this.carByDriverDao.findCarByDriverInfoByPid(pid);
	}
}