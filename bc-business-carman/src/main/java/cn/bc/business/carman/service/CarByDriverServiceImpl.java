/**
 * 
 */
package cn.bc.business.carman.service;

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
}