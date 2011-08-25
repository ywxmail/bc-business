/**
 * 
 */
package cn.bc.business.contract.service;


import java.io.Serializable;

import cn.bc.business.contract.dao.ContractLabourDao;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.core.service.DefaultCrudService;

/**
 * 司机劳动合同Service的实现
 * 
 * @author dragon
 */
public class ContractLabourServiceImpl extends DefaultCrudService<Contract4Labour> implements
		ContractLabourService {
	private ContractLabourDao contractLabourDao;

	public ContractLabourDao getContractLabourDao() {
		return contractLabourDao;
	}

	public void setContractLabourDao(ContractLabourDao certDao) {
		this.contractLabourDao = certDao;
		this.setCrudDao(certDao);
	}
	
	@Override
	public void delete(Serializable id) {
		//删除合同
		this.contractLabourDao.delete(id);
	}
	
	@Override
	public void delete(Serializable[] ids) {
		//批量合同
		this.contractLabourDao.delete(ids);
	}
	
}