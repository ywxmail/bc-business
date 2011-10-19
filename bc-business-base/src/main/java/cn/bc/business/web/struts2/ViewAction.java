/**
 * 
 */
package cn.bc.business.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.BSConstants;
import cn.bc.web.struts2.jpa.ViewActionWithJpa;

/**
 * 基于JPA原生查询的视图Action封装
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
}
