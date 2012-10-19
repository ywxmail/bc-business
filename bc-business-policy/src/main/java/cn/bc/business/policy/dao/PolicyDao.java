/**
 * 
 */
package cn.bc.business.policy.dao;

import java.util.Calendar;
import java.util.List;

import cn.bc.business.policy.domain.Policy;
import cn.bc.core.dao.CrudDao;

/**
 * 车辆保单Dao
 * 
 * @author dragon
 */
public interface PolicyDao extends CrudDao<Policy> {

	/**
	 * 获取相关保单
	 * 
	 * @param carId
	 *            车辆ID
	 * @param happenTime
	 *            事故发生时间
	 * @return
	 */
	List<Policy> getPolicise(Long carId, Calendar happenTime);

	/**
	 * 注销两险已过期的保单
	 * 
	 * @param instance注销时间
	 */
	void logoutPastDuePolicy(Calendar instance);

}