/**
 * 
 */
package cn.bc.business.contract.dao;

import java.util.List;
import java.util.Map;

import cn.bc.business.contract.domain.Contract;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.condition.Condition;


/**
 * 合同Dao
 * 
 * @author dragon
 */
public interface ContractDao extends CrudDao<Contract> {

	List<Map<String, Object>> list4Car(Condition condition,Long carId);

	List<Map<String, Object>> list4CarMan(Condition condition, Long carManId);


}