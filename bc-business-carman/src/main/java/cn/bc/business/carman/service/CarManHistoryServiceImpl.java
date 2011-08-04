/**
 * 
 */
package cn.bc.business.carman.service;

import cn.bc.business.carman.dao.CarManHistoryDao;
import cn.bc.business.carman.domain.CarManHistory;
import cn.bc.core.service.DefaultCrudService;

/**
 * 司机迁移历史Service的实现
 * 
 * @author dragon
 */
public class CarManHistoryServiceImpl extends DefaultCrudService<CarManHistory> implements
		CarManHistoryService {
	private CarManHistoryDao carManHistoryDao;

	public CarManHistoryDao getCarManHistoryDao() {
		return carManHistoryDao;
	}

	public void setCarManHistoryDao(CarManHistoryDao carManHistoryDao) {
		this.carManHistoryDao = carManHistoryDao;
		this.setCrudDao(carManHistoryDao);
	}
}