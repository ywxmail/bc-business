/**
 * 
 */
package cn.bc.business.web.struts2;

import java.io.Serializable;

import cn.bc.core.Entity;

/**
 * bc-business子系统Entity的CRUD通用Action
 * 
 * @author dragon
 * 
 */
public class EntityAction<K extends Serializable, E extends Entity<K>>
		extends cn.bc.web.struts2.EntityAction<K, E> {
	private static final long serialVersionUID = 1L;
	public static final String PATH_PREFIX = "/bc-business";

	@Override
	protected String getActionPathPrefix() {
		// 与配置文件对应：src/main/resources/cn/bc/business/web/struts2/struts.xml
		return PATH_PREFIX;
	} 
}