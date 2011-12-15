package cn.bc.business.arrange.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.arrange.domain.Arrange;
import cn.bc.business.arrange.service.ArrangeService;
import cn.bc.core.Entity;
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
public class ArrangeAction extends FileEntityAction<Long, Arrange> {
	// private static Log logger = LogFactory.getLog(MotorcadeAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = getText("key.role.bs.arrange4driver");// 司机考勤管理角色的编码
	private ArrangeService arrangeService;

	@Autowired
	public void setArrangeService(ArrangeService arrangeService) {
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
		Arrange e = this.getE();

		// 所属单位
		e.setUnit(this.getSystyemContext().getUnit());
		e.setUid(this.getIdGeneratorService().next(Arrange.KEY_UID));
		e.setType(Arrange.TYPE_MEETING);

		// 初始状态
		e.setStatus(Entity.STATUS_ENABLED);

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
