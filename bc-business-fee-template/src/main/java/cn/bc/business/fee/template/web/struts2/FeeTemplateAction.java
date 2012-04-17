/**
 * 
 */
package cn.bc.business.fee.template.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.business.web.struts2.FileEntityAction;

/**
 * Action
 * 
 * @author 
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FeeTemplateAction extends FileEntityAction<Long, FeeTemplate> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;

}