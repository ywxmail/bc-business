/**
 * 
 */
package cn.bc.business.blacklist.service;

import cn.bc.business.blacklist.dao.BlacklistDao;
import cn.bc.business.blacklist.domain.Blacklist;
import cn.bc.core.service.DefaultCrudService;

/**
 * 黑名单Service的实现
 * 
 * @author dragon
 */
public class BlacklistServiceImpl extends DefaultCrudService<Blacklist>
		implements BlacklistService {
	private BlacklistDao blacklistDao;

	public BlacklistDao getBlacklistDao() {
		return blacklistDao;
	}

	public void setBlacklistDao(BlacklistDao blacklistDao) {
		this.blacklistDao = blacklistDao;
		this.setCrudDao(blacklistDao);
	}

}