/**
 * 
 */
package cn.bc.business.policy.service;

import java.util.Calendar;

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
	 * @param policyId  原车保id
	 */
	void doLogout(Long policyId);
}