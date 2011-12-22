/**
 * 
 */
package cn.bc.business.contract.service;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bc.business.contract.dao.ContractChargerDao;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
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

	/**
	 * 删除单个CarNContract
	 * @parma contractId 
	 * @return
	 */
	public void deleteCarNContract(Long contractId) {
		if(contractId != null){
			this.contractChargerDao.deleteCarNContract(contractId);
		}
		
	}

	/**
	 * 删除批量CarNContract
	 * @parma contractId 
	 * @return
	 */
	public void deleteCarNContract(Long[] contractIds) {
		if(contractIds != null && contractIds.length>0){
			this.contractChargerDao.deleteCarNContract(contractIds);
		}
	}
	
	/**
	 * 保存合同与车辆的关联表信息
	 * @parma carId 
	 * @parma contractId 
	 * @return
	 */
	public void carNContract4Save(Long carId, Long contractId) {
		this.contractChargerDao.carNContract4Save(carId,contractId);
	}

	/**
	 * 查找车辆合同列表
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public List<Map<String, Object>> list4car(Condition condition, Long carId) {
		return this.contractChargerDao.list4car(condition,carId);
	}

	/**
	 * 查找车辆合同分页
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public Page<Map<String,Object>> page4car(Condition condition, int pageNo,
			int pageSize) {
		return this.contractChargerDao.page4car(condition,pageNo,pageSize);
	}

	/**
	 * 根据contractId查找car信息
	 * @parma contractId 
	 * @return
	 */
	public Map<String, Object> findCarInfoByContractId(Long contractId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contractChargerDao.findCarInfoByContractId(contractId);
		return queryMap;
	}

	/**
	 * 根据contractId查找car信息
	 * @parma contractId 
	 * @return
	 */
	public List<String> findChargerIdByContractId(Long contractId) {
		List<String> list = new ArrayList<String>();
		list = this.contractChargerDao.findChargerIdByContractId(contractId);
		return list;
	}

	/**
	 * 根据责任人ID和合同ID.保存到人员与合同中间表,不存在插入新纪录,存在删除.重新插入
	 * @param assignChargerIds
	 * @param contractId
	 */
	public void carMansNContract4Save(String assignChargerIds, Long contractId) {
		this.contractChargerDao.carMansNContract4Save(assignChargerIds,contractId);
	}

	/**
	 * 根据合同ID查找关联责任人
	 * @param contractId
	 * @return
	 */
	public Long findCarIdByContractId(Long contractId) {
		Long carId = null;
		carId = contractChargerDao.findCarIdByContractId(contractId);
		return carId;
	}

	/**
	 * 更新车辆表的负责人信息
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCar4dirverName(String assignChargerNames, Long carId) {
		this.contractChargerDao.updateCar4dirverName(assignChargerNames,carId);
	}

	/**
	 * 更新司机表的负责人信息
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCarMan4dirverName(String assignChargerNames, Long carId) {
		this.contractChargerDao.updateCarMan4dirverName(assignChargerNames,carId);
	}

	/**
	 * 根据车辆ID查找车辆信息
	 * @param carId
	 * @return
	 */
	public Map<String, Object> findCarByCarId(Long carId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contractChargerDao.findCarByCarId(carId);
		return queryMap;
	}

	/**
	 * 根据司机ID查找车辆信息
	 * @param carManId
	 * @return
	 */
	public Map<String, Object> findCarByCarManId(Long carManId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contractChargerDao.findCarByCarManId(carManId);
		return queryMap;
	}
	
}