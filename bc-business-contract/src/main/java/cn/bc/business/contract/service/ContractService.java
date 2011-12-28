/**
 * 
 */
package cn.bc.business.contract.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.contract.domain.Contract;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;

/**
 * 合同Service
 * 
 * @author dragon
 */
public interface ContractService extends CrudService<Contract> {
	/**
	 * 合同的转车处理：主版本号加1
	 * 
	 * @parma contractId 原合同id
	 * @parma newCarId 新车id
	 * @return 转车处理后的合同信息
	 */
	Contract doChangeCar(Long contractId, Long newCarId);

	/**
	 * 合同的维护处理：主版本号不变，次版本号加1
	 * 
	 * @parma contract 被维护修改后的合同信息
	 * @parma newCarId 新车id
	 * @return 维护处理后的合同信息
	 */
	Contract doMaintain(Contract contract);

	List<Map<String, Object>> list4Car(Condition condition, Long carId);

	List<Map<String, Object>> list4CarMan(Condition condition, Long carManId);

}