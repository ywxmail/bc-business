/**
 * 
 */
package cn.bc.business.contract.service;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.bc.business.contract.dao.ContractLabourDao;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
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

	public void setContractLabourDao(ContractLabourDao contractLabourDao) {
		this.contractLabourDao = contractLabourDao;
		this.setCrudDao(contractLabourDao);
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

	/**
	 * 删除单个CarManNContract
	 * @parma contractId 
	 * @return
	 */
	public void deleteCarManNContract(Long contractId) {
		if(contractId != null){
			contractLabourDao.deleteCarManNContract(contractId);
		}
	}

	/**
	 * 删除批量CarManNContract
	 * @parma contractIds[] 
	 * @return
	 */
	public void deleteCarManNContract(Long[] contractIds) {
		if(contractIds != null && contractIds.length>0){
			this.contractLabourDao.deleteCarManNContract(contractIds);
		}
	}

	/**
	 * 保存合同与司机的关联表信息
	 * @parma carManId 
	 * @parma contractId 
	 * @return
	 */
	public void carManNContract4Save(Long carManId, Long contractId) {
		this.contractLabourDao.carManNContract4Save(carManId,contractId);
	}

	/**
	 * 查找劳动合同列表
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public List<? extends Object> list4carMan(Condition condition, Long carManId) {
		return this.contractLabourDao.list4carMan(condition,carManId);
	}

	/**
	 * 查找劳动合同分页
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public Page<? extends Object> page4carMan(Condition condition, int pageNo,
			int pageSize) {
		return this.contractLabourDao.page4carMan(condition,pageNo,pageSize);
	}

	/**
	 * 根据carManId查找cert信息
	 * @parma carManId 
	 * @return
	 */
	public Map<String, Object> findCertByCarManId(Long carManId) {
		Map<String,Object> queryMap = null;
		queryMap = this.contractLabourDao.findCertByCarManId(carManId);
		return queryMap;
	}
	
}