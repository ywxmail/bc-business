/**
 * 
 */
package cn.bc.business.sync.service;

import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.business.sync.dao.JinDunJTWFDao;
import cn.bc.core.service.DefaultCrudService;

/**
 * 交委交通违章Service的实现
 * 
 * @author wis
 */
public class JinDunJTWFServiceImpl extends DefaultCrudService<JinDunJTWF> implements
		JinDunJTWFService {
	private JinDunJTWFDao jinDunJTWFDao;

	public JinDunJTWFDao getJinDunJTWFDao() {
		return jinDunJTWFDao;
	}

	public void setJinDunJTWFDao(JinDunJTWFDao jinDunJTWFDao) {
		this.jinDunJTWFDao = jinDunJTWFDao;
		this.setCrudDao(jinDunJTWFDao);
	}
}