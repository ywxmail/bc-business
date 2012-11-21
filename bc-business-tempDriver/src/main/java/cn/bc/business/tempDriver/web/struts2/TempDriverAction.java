package cn.bc.business.tempDriver.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.tempDriver.domain.TempDriver;
import cn.bc.business.tempDriver.service.TempDriverService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 安排的表单Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TempDriverAction extends FileEntityAction<Long, TempDriver> {
	// private static Log logger = LogFactory.getLog(MotorcadeAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = getText("key.role.bs.arrange4driver");// 司机考勤管理角色的编码
	private TempDriverService arrangeService;

	@Autowired
	public void setArrangeService(TempDriverService arrangeService) {
		this.arrangeService = arrangeService;
		this.setCrudService(arrangeService);
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(MANAGER_KEY, getText("key.role.bc.admin"));
	}

	@Override
	public String create() throws Exception {
		String r = super.create();
		TempDriver e = this.getE();



		return r;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(618).setMinWidth(618)
				.setMinHeight(250).setModal(false);
		if (!this.getE().isNew()) {
			option.addButton(new ButtonOption(
					getText("motorcade.historicalInformation"), null,
					"bc.business.motorcadeForm.check"));
		}
		if (!this.isReadonly()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}
}
