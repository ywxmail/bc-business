/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.runcase.dao.CasePraiseDao;
import cn.bc.business.runcase.domain.Case4Praise;
import cn.bc.core.service.DefaultCrudService;

/**
 * 营运事件事故理赔Service的实现
 * 
 * @author dragon
 */
public class CasePraiseServiceImpl extends DefaultCrudService<Case4Praise> implements
		CasePraiseService {
	private CasePraiseDao casePraiseDao;

	public CasePraiseDao getCasePraiseDao() {
		return casePraiseDao;
	}

	public void setCasePraiseDao(CasePraiseDao casePraiseDao) {
		this.casePraiseDao = casePraiseDao;
		this.setCrudDao(casePraiseDao);
	}
}