/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.runcase.dao.CaseAdviceDao;
import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.core.service.DefaultCrudService;

/**
 * 营运事件交通违章Service的实现
 * 
 * @author dragon
 */
public class CaseAdviceServiceImpl extends DefaultCrudService<Case4Advice> implements
		CaseAdviceService {
	private CaseAdviceDao caseAdviceDao;

	public CaseAdviceDao getCaseAdviceDao() {
		return caseAdviceDao;
	}

	public void setCaseAdviceDao(CaseAdviceDao caseAdviceDao) {
		this.caseAdviceDao = caseAdviceDao;
		this.setCrudDao(caseAdviceDao);
	}
}