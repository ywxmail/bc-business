/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.runcase.dao.CaseLostDao;
import cn.bc.business.runcase.domain.Case4Lost;
import cn.bc.core.service.DefaultCrudService;

/**
 * 营运事件报失Service的实现
 * 
 * @author wis
 */
public class CaseLostServiceImpl extends DefaultCrudService<Case4Lost> implements
		CaseLostService {
	private CaseLostDao caseLostDao;
	
	public CaseLostDao getCaseLostDao() {
		return caseLostDao;
	}

	public void setCaseLostDao(CaseLostDao caseLostDao) {
		this.caseLostDao = caseLostDao;
		this.setCrudDao(caseLostDao);
	}

}