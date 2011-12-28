/**
 * 
 */
package cn.bc.business.contract.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.ContractCarManRelation;
import cn.bc.business.contract.domain.ContractCarRelation;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.condition.Condition;

/**
 * 合同Dao
 * 
 * @author dragon
 */
public interface ContractDao extends CrudDao<Contract> {
	/**
	 * 保存合同与车辆的关联关系
	 * 
	 * @param relation
	 *            要保存的关系
	 */
	void saveContractCarRelation(ContractCarRelation relation);

	/**
	 * 保存合同与车辆的关联关系
	 * 
	 * @param relations
	 *            要保存的关系
	 */
	void saveContractCarRelation(Collection<ContractCarRelation> relations);

	/**
	 * 保存合同与司机、责任人的关联关系,
	 * 
	 * @param relation
	 *            要保存的关系
	 */
	void saveContractCarManRelation(ContractCarManRelation relation);

	/**
	 * 保存合同与司机、责任人的关联关系
	 * 
	 * @param relations
	 *            要保存的关系
	 */
	void saveContractCarManRelation(Collection<ContractCarManRelation> relations);

	/**
	 * 更新合同与车辆的单一关联关系
	 * <ul>
	 * <li>处理逻辑：先根据合同查找现存的关联关系，然后对比现有关系与需新建立的关系，相同的不作处理，不存在的就添加保存</li>
	 * </ul>
	 * 
	 * @param contractId
	 *            合同的id
	 * @param carId
	 *            车辆的id
	 */
	void updateContractCarRelation(Long contractId, Long carId);

	/**
	 * 更新合同与车辆的关联关系
	 * <ul>
	 * <li>车辆与合同关联关系的准则：一个合同可以与多台车建立关系，但在实际中通常都是与一台车产生关系，如劳动合同、经济合同</li>
	 * <li>处理逻辑：先根据合同查找现存的关联关系，然后对比现有关系与需新建立的关系，相同的不作处理，不存在的就添加保存，多余的就删除</li>
	 * </ul>
	 * 
	 * @param contractId
	 *            合同的id
	 * @param carIds
	 *            车辆的id列表
	 */
	void updateContractCarRelation(Long contractId, Long[] carIds);

	/**
	 * 更新合同与司机、责任人的单一关联关系，如劳动合同与司机的关系
	 * <ul>
	 * <li>处理逻辑：先根据合同查找现存的关联关系，然后对比现有关系与需新建立的关系，相同的不作处理，不存在的就添加保存</li>
	 * </ul>
	 * 
	 * @param contractId
	 *            合同的id
	 * @param carManId
	 *            车辆的id
	 */
	void updateContractCarManRelation(Long contractId, Long carManId);

	/**
	 * 更新合同与司机、责任人的关联关系，如经济合同与责任人的关系
	 * <ul>
	 * <li>合同与司机、责任人关联关系的准则：一个合同可以与个司机、责任人建立关系</li>
	 * <li>处理逻辑：先根据合同查找现存的关联关系，然后对比现有关系与需新建立的关系，相同的不作处理，不存在的就添加保存，多余的就删除</li>
	 * </ul>
	 * 
	 * @param contractId
	 *            合同的id
	 * @param carManIds
	 *            车辆的id列表
	 */
	void updateContractCarManRelation(Long contractId, Long[] carManIds);
	
	/**
	 * 获取合同与车辆的关联信息
	 * @param contractId 合同的id
	 * @return
	 */
	List<ContractCarRelation> findContractCarRelation(Long contractId);
	
	/**
	 * 获取合同与司机、责任人的关联信息
	 * @param contractId 合同的id
	 * @return
	 */
	List<ContractCarManRelation> findContractCarManRelation(Long contractId);

	List<Map<String, Object>> list4Car(Condition condition, Long carId);

	List<Map<String, Object>> list4CarMan(Condition condition, Long carManId);
}