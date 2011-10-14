/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.runcase.dao.CaseBaseDao;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.core.service.DefaultCrudService;

/**
 * 营运事件Service的实现
 * 
 * @author dragon
 */
public class CaseBaseServiceImpl extends DefaultCrudService<CaseBase> implements
		CaseBaseService {
	private CaseBaseDao caseBaseDao;

	public CaseBaseDao getCaseBaseDao() {
		return caseBaseDao;
	}

	public void setCaseBaseDao(CaseBaseDao caseBaseDao) {
		this.caseBaseDao = caseBaseDao;
		this.setCrudDao(caseBaseDao);
	}

	public void findCarManNameNCertCodeByCarId(Long carId) {
		// TODO Auto-generated method stub
		
	}

	public void findCarPlateNCertCodeByCarManId(Long carManId) {
		// TODO Auto-generated method stub
		
	}
}