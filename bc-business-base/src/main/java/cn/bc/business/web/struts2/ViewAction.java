/**
 * 
 */
package cn.bc.business.web.struts2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.BSConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.LikeCondition;

/**
 * 营运系统各模块视图Action的基类封装
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class ViewAction<T extends Object> extends
		cn.bc.web.struts2.ViewAction<T> {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}

	/**
	 * 复写基类的查询条件构建方法，使查询车辆号码时不区分大小写：交委规定车牌的字母为大写
	 * 
	 * @see cn.bc.web.struts2.AbstractGridPageAction#getGridSearchCondition4OneField(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	protected Condition getGridSearchCondition4OneField(String field,
			String value) {
		if (field.endsWith(".plate_no")) {// 车牌号
			return buildDefaultLikeCondition(field,
					value != null ? value.toUpperCase() : value);
		} else if (field.endsWith(".manage_no")) {// 管理号
			try {
				return new EqualsCondition(field, new Integer(value));
			} catch (NumberFormatException e) {
				return null;
			}
		} else {
			return super.getGridSearchCondition4OneField(field, value);
		}
	}

	/**
	 * 状态值转换列表：在案|注销|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses1() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("bs.status.active"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("bs.status.logout"));
		statuses.put("0,1", getText("bs.status.all"));
		return statuses;
	}

	/**
	 * 状态值转换列表：在案|注销|草稿|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses3() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("bs.status.active"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("bs.status.logout"));
		statuses.put(String.valueOf(BCConstants.STATUS_DRAFT),
				getText("bc.status.draft"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	/**
	 * 状态值转换列表：在案|结案|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses2() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("bs.status.active"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("bs.status.closed"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	@Override
	public String getAdvanceSearchConditionsJspPath() {
		return BSConstants.NAMESPACE + "/" + this.getFormActionName();
	}
}
