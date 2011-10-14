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

	List<Map<String,Object>> list4Car(Condition condition,Long carId);

	List<Map<String, Object>> list4CarMan(Condition condition, Long carManId);

}