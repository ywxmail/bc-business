/**
 * 
 */
package cn.bc.business.car.service;

import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.core.service.DefaultCrudService;

/**
 * 车辆Service的实现
 * 
 * @author dragon
 */
public class CarServiceImpl extends DefaultCrudService<Car> implements
		CarService {
	private CarDao carDao;

	public CarDao getCarDao() {
		return carDao;
	}

	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
		this.setCrudDao(carDao);
	}
}