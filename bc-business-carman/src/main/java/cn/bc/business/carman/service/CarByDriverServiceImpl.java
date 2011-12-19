/**
 * 
 */
package cn.bc.business.carman.service;

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

	@Override
	public CarByDriver save(CarByDriver entity) {
		//默认的保存处理
		entity = super.save(entity);
		
		//更新车辆的司机信息
		this.carByDriverDao.updateCar4Driver(entity.getCar().getId());
		
		entity = this.forceLoad(entity.getId());
		
		return entity;
	}
}