/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.runcase.dao.CaseBusinessDao;
import cn.bc.business.runcase.domain.Case4InfractBusiness;
import cn.bc.core.service.DefaultCrudService;

/**
 * 营运事件营运违章Service的实现
 * 
 * @author dragon
 */
public class CaseBusinessServiceImpl extends DefaultCrudService<Case4InfractBusiness> implements
		CaseBusinessService {
	private CaseBusinessDao caseBusinessDao;

	public CaseBusinessDao getCaseBusinessDao() {
		return caseBusinessDao;
	}

	public void setCaseBusinessDao(CaseBusinessDao caseBusinessDao) {
		this.caseBusinessDao = caseBusinessDao;
		this.setCrudDao(caseBusinessDao);
	}
}