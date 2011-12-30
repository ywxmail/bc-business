/**
 * 
 */
package cn.bc.business.contract.service;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bc.business.contract.dao.Contract4ChargerDao;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;

/**
 * 责任人合同Service的实现
 * 
 * @author dragon
 */
public class Contract4ChargerServiceImpl extends DefaultCrudService<Contract4Charger> implements
		Contract4ChargerService {
	private Contract4ChargerDao contract4ChargerDao;

	public Contract4ChargerDao getContract4ChargerDao() {
		return contract4ChargerDao;
	}

	public void setContract4ChargerDao(Contract4ChargerDao contract4ChargerDao) {
		this.contract4ChargerDao = contract4ChargerDao;
		this.setCrudDao(contract4ChargerDao);
	}
	
	@Override
	public void delete(Serializable id) {
		//删除合同
		this.contract4ChargerDao.delete(id);
	}
	
	@Override
	public void delete(Serializable[] ids) {
		//批量合同
		this.contract4ChargerDao.delete(ids);
	}

	/**
	 * 删除单个CarNContract
	 * @parma contractId 
	 * @return
	 */
	public void deleteCarNContract(Long contractId) {
		if(contractId != null){
			this.contract4ChargerDao.deleteCarNContract(contractId);
		}
		
	}

	/**
	 * 删除批量CarNContract
	 * @parma contractId 
	 * @return
	 */
	public void deleteCarNContract(Long[] contractIds) {
		if(contractIds != null && contractIds.length>0){
			this.contract4ChargerDao.deleteCarNContract(contractIds);
		}
	}
	
	/**
	 * 保存合同与车辆的关联表信息
	 * @parma carId 
	 * @parma contractId 
	 * @return
	 */
	public void carNContract4Save(Long carId, Long contractId) {
		this.contract4ChargerDao.carNContract4Save(carId,contractId);
	}

	/**
	 * 查找车辆合同列表
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public List<Map<String, Object>> list4car(Condition condition, Long carId) {
		return this.contract4ChargerDao.list4car(condition,carId);
	}

	/**
	 * 查找车辆合同分页
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	public Page<Map<String,Object>> page4car(Condition condition, int pageNo,
			int pageSize) {
		return this.contract4ChargerDao.page4car(condition,pageNo,pageSize);
	}

	/**
	 * 根据contractId查找car信息
	 * @parma contractId 
	 * @return
	 */
	public Map<String, Object> findCarInfoByContractId(Long contractId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4ChargerDao.findCarInfoByContractId(contractId);
		return queryMap;
	}

	/**
	 * 根据contractId查找car信息
	 * @parma contractId 
	 * @return
	 */
	public List<String> findChargerIdByContractId(Long contractId) {
		List<String> list = new ArrayList<String>();
		list = this.contract4ChargerDao.findChargerIdByContractId(contractId);
		return list;
	}

	/**
	 * 根据责任人ID和合同ID.保存到人员与合同中间表,不存在插入新纪录,存在删除.重新插入
	 * @param assignChargerIds
	 * @param contractId
	 */
	public void carMansNContract4Save(String assignChargerIds, Long contractId) {
		this.contract4ChargerDao.carMansNContract4Save(assignChargerIds,contractId);
	}

	/**
	 * 根据合同ID查找关联责任人
	 * @param contractId
	 * @return
	 */
	public Long findCarIdByContractId(Long contractId) {
		Long carId = null;
		carId = contract4ChargerDao.findCarIdByContractId(contractId);
		return carId;
	}

	/**
	 * 更新车辆表的负责人信息
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCar4dirverName(String assignChargerNames, Long carId) {
		this.contract4ChargerDao.updateCar4dirverName(assignChargerNames,carId);
	}

	/**
	 * 更新司机表的负责人信息
	 * @param assignChargerNames
	 * @param carId
	 */
	public void updateCarMan4dirverName(String assignChargerNames, Long carId) {
		this.contract4ChargerDao.updateCarMan4dirverName(assignChargerNames,carId);
	}

	/**
	 * 根据车辆ID查找车辆信息
	 * @param carId
	 * @return
	 */
	public Map<String, Object> findCarByCarId(Long carId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4ChargerDao.findCarByCarId(carId);
		return queryMap;
	}

	/**
	 * 根据司机ID查找车辆信息
	 * @param carManId
	 * @return
	 */
	public Map<String, Object> findCarByCarManId(Long carManId) {
		Map<String, Object> queryMap = null;
		queryMap = this.contract4ChargerDao.findCarByCarManId(carManId);
		return queryMap;
	}
	
}