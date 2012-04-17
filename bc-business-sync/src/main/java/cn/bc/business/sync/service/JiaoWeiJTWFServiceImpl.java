/**
 * 
 */
package cn.bc.business.sync.service;

import java.util.Calendar;

import cn.bc.business.sync.dao.JiaoWeiJTWFDao;
import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.core.service.DefaultCrudService;

/**
 * 交委交通违章Service的实现
 * 
 * @author wis
 */
public class JiaoWeiJTWFServiceImpl extends DefaultCrudService<JiaoWeiJTWF>
		implements JiaoWeiJTWFService {
	private JiaoWeiJTWFDao jiaoWeiJTWFDao;

	public JiaoWeiJTWFDao getJiaoWeiJTWFDao() {
		return jiaoWeiJTWFDao;
	}

	public void setJiaoWeiJTWFDao(JiaoWeiJTWFDao jiaoWeiJTWFDao) {
		this.jiaoWeiJTWFDao = jiaoWeiJTWFDao;
		this.setCrudDao(jiaoWeiJTWFDao);
	}

	public String getJinDunAddress(String syncCode, String plateNo,
			Calendar happenDate) {
		return this.jiaoWeiJTWFDao.findJinDunAddress(syncCode, plateNo,
				happenDate);
	}
}