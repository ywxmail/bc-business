/**
 * 
 */
package cn.bc.business.contract.service;


import java.io.Serializable;

import cn.bc.business.contract.dao.ContractChargerDao;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.core.service.DefaultCrudService;

/**
 * 责任人合同Service的实现
 * 
 * @author dragon
 */
public class ContractChargerServiceImpl extends DefaultCrudService<Contract4Charger> implements
		ContractChargerService {
	private ContractChargerDao contractChargerDao;

	public ContractChargerDao getContractChargerDao() {
		return contractChargerDao;
	}

	public void setContractChargerDao(ContractChargerDao contractChargerDao) {
		this.contractChargerDao = contractChargerDao;
		this.setCrudDao(contractChargerDao);
	}
	
	@Override
	public void delete(Serializable id) {
		//删除合同
		this.contractChargerDao.delete(id);
	}
	
	@Override
	public void delete(Serializable[] ids) {
		//批量合同
		this.contractChargerDao.delete(ids);
	}
	
}