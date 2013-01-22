/**
 * 
 */
package cn.bc.business.carman.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.carman.dao.CarManRiskDao;
import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.core.service.DefaultCrudService;

/**
 * 司机人意险Service的实现
 * 
 * @author dragon
 */
public class CarManRiskServiceImpl extends DefaultCrudService<CarManRisk>
		implements CarManRiskService {
	private CarManRiskDao carManRiskDao;

	@Autowired
	public void setCarManRiskDao(CarManRiskDao carManRiskDao) {
		this.carManRiskDao = carManRiskDao;
		this.setCrudDao(carManRiskDao);
	}

	public Map<String, Object> getCarManInfo(String identity) {
		return this.carManRiskDao.getCarManInfo(identity);
	}
}