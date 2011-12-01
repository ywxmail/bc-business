/**
 * 
 */
package cn.bc.business.injury.service;

import cn.bc.business.injury.dao.InjuryDao;
import cn.bc.business.injury.domain.Injury;
import cn.bc.core.service.DefaultCrudService;

/**
 * 合同Service的实现
 * 
 * @author dragon
 */
public class InjuryServiceImpl extends DefaultCrudService<Injury> implements
		InjuryService {
	private InjuryDao injuryDao;

	public InjuryDao getInjuryDao() {
		return injuryDao;
	}

	public void setInjuryDao(InjuryDao injuryDao) {
		this.injuryDao = injuryDao;
		this.setCrudDao(injuryDao);
	}

	
}