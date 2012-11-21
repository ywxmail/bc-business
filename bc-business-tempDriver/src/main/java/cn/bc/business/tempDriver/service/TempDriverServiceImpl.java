package cn.bc.business.tempDriver.service;

import cn.bc.business.tempDriver.dao.TempDriverDao;
import cn.bc.business.tempDriver.domain.TempDriver;
import cn.bc.core.service.DefaultCrudService;

/**
 * 安排Service的实现
 * 
 * @author dragon
 * 
 */
public class TempDriverServiceImpl extends DefaultCrudService<TempDriver> implements
		TempDriverService {
	private TempDriverDao tempDriverDao;

	public void setTempDriverDao(TempDriverDao tempDriverDao) {
		this.tempDriverDao = tempDriverDao;
		this.setCrudDao(tempDriverDao);
	}
}
