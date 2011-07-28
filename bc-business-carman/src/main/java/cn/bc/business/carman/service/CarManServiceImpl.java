/**
 * 
 */
package cn.bc.business.carman.service;

import cn.bc.business.carman.dao.CarManDao;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.core.service.DefaultCrudService;

/**
 * 司机责任人Service的实现
 * 
 * @author dragon
 */
public class CarManServiceImpl extends DefaultCrudService<CarMan> implements
		CarManService {
	private CarManDao carManDao;

	public CarManDao getCarManDao() {
		return carManDao;
	}

	public void setCarManDao(CarManDao carManDao) {
		this.carManDao = carManDao;
		this.setCrudDao(carManDao);
	}
}