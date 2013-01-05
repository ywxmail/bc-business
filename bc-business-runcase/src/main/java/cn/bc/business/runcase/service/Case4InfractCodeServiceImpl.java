/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.runcase.dao.Case4InfractCodeDao;
import cn.bc.business.runcase.domain.Case4InfractCode;
import cn.bc.core.service.DefaultCrudService;

/**
 * 违法代码管理Service的实现
 * 
 * @author zxr
 */
public class Case4InfractCodeServiceImpl extends
		DefaultCrudService<Case4InfractCode> implements Case4InfractCodeService {
	private Case4InfractCodeDao case4InfractCodeDao;

	public Case4InfractCodeDao getCase4InfractCodeDao() {
		return case4InfractCodeDao;
	}

	public void setCase4InfractCodeDao(Case4InfractCodeDao case4InfractCodeDao) {
		this.case4InfractCodeDao = case4InfractCodeDao;
		this.setCrudDao(case4InfractCodeDao);
	}

	public Case4InfractCode getEntityByCode(String code) {
		return this.case4InfractCodeDao.getEntityByCode(code);
	}

}