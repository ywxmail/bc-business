/**
 * 
 */
package cn.bc.business.contract.service;

import cn.bc.business.contract.dao.ContractDao;
import cn.bc.business.contract.domain.Contract;
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
	
}