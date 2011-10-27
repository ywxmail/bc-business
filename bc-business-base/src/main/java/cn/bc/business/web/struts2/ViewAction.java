/**
 * 
 */
package cn.bc.business.web.struts2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.BSConstants;
import cn.bc.core.Entity;
import cn.bc.core.RichEntity;
import cn.bc.web.struts2.jpa.ViewActionWithJpa;

/**
 * 营运系统各模块视图Action的基类封装
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class ViewAction<T extends Object> extends ViewActionWithJpa<T> {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}

	/**
	 * 状态值转换列表：在案|注销|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses1() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(RichEntity.STATUS_ENABLED),
				getText("bs.status.active"));
		statuses.put(String.valueOf(Entity.STATUS_DISABLED),
				getText("bs.status.logout"));
		statuses.put(" ", getText("bs.status.all"));
		return statuses;
	}

	/**
	 * 状态值转换列表：在案|结案|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses2() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(RichEntity.STATUS_ENABLED),
				getText("bs.status.active"));
		statuses.put(String.valueOf(Entity.STATUS_DISABLED),
				getText("bs.status.closed"));
		statuses.put(" ", getText("bs.status.all"));
		return statuses;
	}
}
