/**
 * 
 */
package cn.bc.business.policyt.service;

import cn.bc.business.policy.dao.BlacklistDao;
import cn.bc.business.policy.domain.Policy;
import cn.bc.core.service.DefaultCrudService;

/**
 * 黑名单Service的实现
 * 
 * @author dragon
 */
public class BlacklistServiceImpl extends DefaultCrudService<Policy> implements
		BlacklistService {
	private BlacklistDao blacklistDao;

	public BlacklistDao getBlacklistDao() {
		return blacklistDao;
	}

	public void setBlacklistDao(BlacklistDao blacklistDao) {
		this.blacklistDao = blacklistDao;
		this.setCrudDao(blacklistDao);
	}
}