/**
 * 
 */
package cn.bc.business.carlpg.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.carlpg.dao.CarLPGDao;
import cn.bc.business.carlpg.domain.CarLPG;
import cn.bc.core.service.DefaultCrudService;

/**
 * LPG配置Service的实现
 * 
 * @author LBJ
 */
public class CarLPGServiceImpl extends DefaultCrudService<CarLPG> implements
		CarLPGService {
	private CarLPGDao carLPGDao;

	public CarLPGDao getcarLPGDao() {
		return carLPGDao;
	}

	public void setcarLPGDao(CarLPGDao carLPGDao) {
		this.carLPGDao = carLPGDao;
		this.setCrudDao(carLPGDao);
	}

	public List<Map<String, String>> findEnabled4Option() {
		return this.carLPGDao.findEnabled4Option();
	}

	public CarLPG findcarLPGByLPGModel(String name) {
		return this.carLPGDao.findcarLPGByLPGModel(name);
	}
}