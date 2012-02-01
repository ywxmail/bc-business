/**
 * 
 */
package cn.bc.business.carmodel.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.carmodel.dao.CarModelDao;
import cn.bc.business.carmodel.domain.CarModel;
import cn.bc.core.service.DefaultCrudService;

/**
 * 车型配置Service的实现
 * 
 * @author dragon
 */
public class CarModelServiceImpl extends DefaultCrudService<CarModel> implements
		CarModelService {
	private CarModelDao carModelDao;

	public CarModelDao getCarModelDao() {
		return carModelDao;
	}

	public void setCarModelDao(CarModelDao carModelDao) {
		this.carModelDao = carModelDao;
		this.setCrudDao(carModelDao);
	}

	public List<Map<String, String>> findEnabled4Option() {
		return this.carModelDao.findEnabled4Option();
	}

	public CarModel findcarModelByFactoryModel(String factoryModel) {
		return this.carModelDao.findcarModelByFactoryModel(factoryModel);
	}

	
}