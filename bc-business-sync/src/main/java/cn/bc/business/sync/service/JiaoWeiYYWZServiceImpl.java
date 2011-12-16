/**
 * 
 */
package cn.bc.business.sync.service;

import cn.bc.business.sync.dao.JiaoWeiYYWZDao;
import cn.bc.business.sync.domain.JiaoWeiYYWZ;
import cn.bc.core.service.DefaultCrudService;

/**
 * 交委营运违章Service的实现
 * 
 * @author wis
 */
public class JiaoWeiYYWZServiceImpl extends DefaultCrudService<JiaoWeiYYWZ> implements
	JiaoWeiYYWZService {
	private JiaoWeiYYWZDao jiaoWeiYYWZDao;

	public JiaoWeiYYWZDao getJiaoWeiYYWZDao() {
		return jiaoWeiYYWZDao;
	}

	public void setJiaoWeiYYWZDao(JiaoWeiYYWZDao jiaoWeiYYWZDao) {
		this.jiaoWeiYYWZDao = jiaoWeiYYWZDao;
		this.setCrudDao(jiaoWeiYYWZDao);
	}
}