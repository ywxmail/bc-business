/**
 * 
 */
package cn.bc.business.contract.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;


/**
 * 司机劳动合同Service
 * 
 * @author dragon
 */
/**
 * @author wis
 *
 */
/**
 * @author wis
 *
 */
/**
 * @author wis
 *
 */
public interface ContractLabourService extends CrudService<Contract4Labour> {
	
	/**
	 * 删除单个CarManNContract
	 * @parma contractId 
	 * @return
	 */
	void deleteCarManNContract(Long id);

	/**
	 * 删除批量CarManNContract
	 * @parma contractIds[] 
	 * @return
	 */
	void deleteCarManNContract(Long[] contractIds);
	
	/**
	 * 保存合同与司机的关联表信息
	 * @parma carManId 
	 * @parma contractId 
	 * @return
	 */
	void carManNContract4Save(Long carManId, Long contractId);

	/**
	 * 查找劳动合同列表
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	List<Map<String, Object>> list4carMan(Condition condition, Long carManId);

	/**
	 * 查找劳动合同分页
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	Page<Map<String,Object>> page4carMan(Condition condition, int pageNo,
			int pageSize);


	/**
	 * 根据carManId查找cert信息
	 * @parma carManId 
	 * @return
	 */
	Map<String, Object> findCertByCarManId(Long carManId);

	
	/**
	 * 保存车辆与合同的关联信息
	 * jdbc查询BS_CAR_CONTRACT表是否存在相应carId和contractId的记录
	 * @param carId
	 * @param contractId
	 */
	void carNContract4Save(Long carId, Long contractId);
	
	/**
	 * 根据合同ID查找车辆ID
	 * @param contractId
	 * @return
	 */
	public Long findCarIdByContractId(Long contractId);
	
	/**
	 * 根据合同ID查找司机ID
	 * @param contractId
	 * @return
	 */
	public Long findCarManIdByContractId(Long contractId);

	
	/**
	 * 根据车辆Id查找车辆
	 * @param carId
	 * @return
	 */
	Map<String, Object> findCarManByCarId(Long carId);

	/**
	 * 根据司机ID相应的车
	 * @param carManId
	 * @return
	 */
	List<Map<String, Object>> selectRelateCarByCarManId(Long carManId);

	/**
	 * 根据司机ID查找司机
	 * @param carManId
	 * @return
	 */
	Map<String, Object> findCarManByCarManId(Long carManId);

	/**
	 * 根据车辆ID查找车辆
	 * @param carId
	 * @return
	 */
	Map<String, Object> findCarByCarId(Long carId);


	/**
	 * 根据车辆ID查找关联的司机
	 * @param carId
	 * @return
	 */
	List<Map<String, Object>> selectRelateCarManByCarId(Long carId);

	/**
	 * 根据司机ID查找关联的司机否存在劳动合同
	 * @parma carManId 
	 * @return
	 */
	List<Map<String, Object>> findCarManIsExistContract(Long carManId);

	/**
	 * 删除单个Injury
	 * @parma contractId
	 * @return
	 */
	void deleteInjury(Long contractId);
	
	/**
	 * 删除批量Injury
	 * @parma contractIds[] 
	 * @return
	 */
	void deleteInjury(Long[] contractIds);



}