/**
 * 
 */
package cn.bc.business.contract.dao;

import java.util.List;
import java.util.Map;

import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.core.Page;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.condition.Condition;


/**
 * 司机劳动合同Dao
 * 
 * @author dragon
 */
public interface ContractLabourDao extends CrudDao<Contract4Labour> {

	/**
	 * 删除单个CarManNContract
	 * @parma contractId 
	 * @return
	 */
	void deleteCarManNContract(Long contractId);

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
	List<? extends Object> list4carMan(Condition condition, Long carManId);

	/**
	 * 查找劳动合同分页
	 * @parma condition 
	 * @parma carId 
	 * @return
	 */
	Page<? extends Object> page4carMan(Condition condition, int pageNo,
			int pageSize);

	/**
	 * 根据carManId查找cert信息
	 * @parma carManId 
	 * @return
	 */
	Map<String, Object> findCertByCarManId(Long carManId);

}