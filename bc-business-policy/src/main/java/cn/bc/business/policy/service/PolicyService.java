/**
 * 
 */
package cn.bc.business.policy.service;

import java.util.Calendar;
import java.util.List;

import cn.bc.business.policy.domain.Policy;
import cn.bc.core.service.CrudService;

/**
 * 车辆保单Service
 * 
 * @author dragon
 */
public interface PolicyService extends CrudService<Policy> {
	/**
	 * 续保处理：新纪录、主版本号加1
	 * 
	 * @parma policyId 原车保id
	 * 
	 * @return 续签后的合同信息
	 */
	Policy doRenew(Long policyId);

	/**
	 * 停保处理：记录不变
	 * 
	 * @param policyId
	 *            原车保id
	 * 
	 * @param surrenderDate
	 *            指定的停保日期，为空则使用当前时间
	 */
	void doSurrender(Long policyId, Calendar surrenderDate);

	/**
	 * 注销处理：记录不变
	 * 
	 * @param policyId
	 *            原车保id
	 */
	void doLogout(Long policyId);

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
	 * 注销商业险和强制险都过期的保单[保险日期小于运行方法的当前时间]
	 */
	void doLogoutPastDuePolicy();
}