/**
 * 
 */
package cn.bc.business.contract.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;


/**
 * 责任人合同Service
 * 
 * @author dragon
 */
public interface ContractChargerService extends CrudService<Contract4Charger> {

	/**
	 * 删除单个CarNContract
	 * @parma contractId 
	 * @return
	 */
	void deleteCarNContract(Long contractId);

	/**
	 * 删除批量CarNContract
	 * @parma contractIds 
	 * @return
	 */
	void deleteCarNContract(Long[] contractIds);

	/**
	 * 保存合同与车辆的关联表信息
	 * @parma carId 
	 * @parma contractId 
	 * @return
	 */
	void carNContract4Save(Long carId, Long contractId);
	
	/**
	 * 查找车辆合同列表
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	List<Map<String, Object>> list4car(Condition condition, Long carId);

	/**
	 * 查找车辆合同分页
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	Page<Map<String,Object>> page4car(Condition condition, int pageNo,
			int pageSize);

	/**
	 * 根据contractId查找car信息
	 * @parma contractId 
	 * @return
	 */
	Map<String, Object> findCarInfoByContractId(Long contractId);
}