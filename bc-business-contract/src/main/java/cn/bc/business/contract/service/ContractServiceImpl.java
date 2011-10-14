/**
 * 
 */
package cn.bc.business.contract.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.contract.dao.ContractDao;
import cn.bc.business.contract.domain.Contract;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;

/**
 * 合同Service的实现
 * 
 * @author dragon
 */
public class ContractServiceImpl extends DefaultCrudService<Contract> implements
		ContractService {
	private ContractDao contractDao;

	public ContractDao getContractDao() {
		return contractDao;
	}

	public void setContractDao(ContractDao certDao) {
		this.contractDao = certDao;
		this.setCrudDao(certDao);
	}

	public List<Map<String,Object>> list4Car(Condition condition,Long carId) {
		return this.contractDao.list4Car(condition,carId);
	}

	public List<Map<String, Object>> list4CarMan(Condition condition,
			Long carManId) {
		return this.contractDao.list4CarMan(condition,carManId);
	}
	
}