/**
 * 
 */
package cn.bc.business.contract.service;

import java.io.Serializable;

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
	
	@Override
	public void delete(Serializable id) {
		//删除合同
		this.contractDao.delete(id);
	}
	
	@Override
	public void delete(Serializable[] ids) {
		//批量合同
		this.contractDao.delete(ids);
	}
}