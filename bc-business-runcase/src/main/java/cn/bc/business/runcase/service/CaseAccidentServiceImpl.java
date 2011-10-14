/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.List;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.runcase.dao.CaseAccidentDao;
import cn.bc.business.runcase.domain.Case4Accident;
import cn.bc.core.service.DefaultCrudService;

/**
 * 营运事件事故理赔Service的实现
 * 
 * @author dragon
 */
public class CaseAccidentServiceImpl extends DefaultCrudService<Case4Accident> implements
		CaseAccidentService {
	private CaseAccidentDao caseAccidentDao;

	public CaseAccidentDao getCaseAccidentDao() {
		return caseAccidentDao;
	}

	public void setCaseAccidentDao(CaseAccidentDao caseAccidentDao) {
		this.caseAccidentDao = caseAccidentDao;
		this.setCrudDao(caseAccidentDao);
	}

	public Car selectAllCarByCarManId(Long id) {
		// TODO Auto-generated method stub
		return  (this.caseAccidentDao.findAllcarBycarManId(id));
	}

	public CarMan selectCarManByCarId(Long id) {
		// TODO Auto-generated method stub
		return (this.caseAccidentDao.findcarManBycarcarId(id));
	}
}