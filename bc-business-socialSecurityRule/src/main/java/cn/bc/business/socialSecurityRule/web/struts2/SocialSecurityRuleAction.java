/**
 * 
 */
package cn.bc.business.socialSecurityRule.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.socialSecurityRule.domain.SocialSecurityRule;
import cn.bc.business.socialSecurityRule.service.SocialSecurityRuleService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * Action
 * 
 * @author
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SocialSecurityRuleAction extends FileEntityAction<Long, SocialSecurityRule> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	private SocialSecurityRuleService socialSecurityRuleService;

	public List<Map<String, String>> payTypeList;// 收费方式列表(每月、每季、每年、一次性)
	public List<Map<String, String>> templateList;// 模板列表

	@Autowired
	public void setSocialSecurityRuleService(
			SocialSecurityRuleService socialSecurityRuleService) {
		this.socialSecurityRuleService = socialSecurityRuleService;
		this.setCrudService(socialSecurityRuleService);
	}


	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin"),
				getText("key.role.bs.socialSecurityRule"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(380)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
	

	}

	
	


}