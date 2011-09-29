/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.runcase.dao.CaseTrafficDao;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.core.service.DefaultCrudService;

/**
 * 营运事件交通违章Service的实现
 * 
 * @author dragon
 */
public class CaseTrafficServiceImpl extends DefaultCrudService<Case4InfractTraffic> implements
		CaseTrafficService {
	private CaseTrafficDao caseTrafficDao;

	public CaseTrafficDao getCaseTrafficDao() {
		return caseTrafficDao;
	}

	public void setCaseTrafficDao(CaseTrafficDao caseTrafficDao) {
		this.caseTrafficDao = caseTrafficDao;
		this.setCrudDao(caseTrafficDao);
	}
}