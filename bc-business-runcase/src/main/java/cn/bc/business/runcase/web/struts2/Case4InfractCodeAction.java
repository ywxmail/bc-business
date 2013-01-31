/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.runcase.domain.Case4InfractCode;
import cn.bc.business.runcase.service.Case4InfractCodeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 违法代码管理Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Case4InfractCodeAction extends
		FileEntityAction<Long, Case4InfractCode> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private Case4InfractCodeService case4InfractCodeService;

	@Autowired
	public void setCase4InfractCodeService(
			Case4InfractCodeService case4InfractCodeService) {
		this.case4InfractCodeService = case4InfractCodeService;
		this.setCrudService(case4InfractCodeService);
	}

	@Override
	public boolean isReadonly() {
		// 违法代码管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infract.code"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(530)
				.setMinWidth(250).setHeight(260).setMinHeight(200);
	}

}
