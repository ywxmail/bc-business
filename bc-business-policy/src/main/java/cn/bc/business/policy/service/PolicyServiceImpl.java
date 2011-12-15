/**
 * 
 */
package cn.bc.business.policy.service;

import cn.bc.business.policy.dao.PolicyDao;
import cn.bc.business.policy.domain.Policy;
import cn.bc.core.service.DefaultCrudService;

/**
 * 车辆保单Service的实现
 * 
 * @author dragon
 */
public class PolicyServiceImpl extends DefaultCrudService<Policy> implements
		PolicyService {
	private PolicyDao policyDao;

	public PolicyDao getPolicyDao() {
		return policyDao;
	}

	public void setPolicyDao(PolicyDao policyDao) {
		this.policyDao = policyDao;
		this.setCrudDao(policyDao);
	}

}