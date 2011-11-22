/**
 * 
 */
package cn.bc.business.carman.service;

import cn.bc.business.carman.dao.CarByDriverHistoryDao;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.core.service.DefaultCrudService;

/**
 * 迁移记录Service的实现
 * 
 * @author dragon
 */
public class CarByDriverHistoryServiceImpl extends
		DefaultCrudService<CarByDriverHistory> implements
		CarByDriverHistoryService {

	private CarByDriverHistoryDao carByDriverHistoryDao;

	public CarByDriverHistoryDao getCarByDriverHistoryDao() {
		return carByDriverHistoryDao;
	}

	public void setCarByDriverHistoryDao(
			CarByDriverHistoryDao carByDriverHistoryDao) {
		this.carByDriverHistoryDao = carByDriverHistoryDao;
		this.setCrudDao(carByDriverHistoryDao);
	}

}