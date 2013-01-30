/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.business.carman.service.CarManRiskService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 司机人意险Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarManRiskAction extends FileEntityAction<Long, CarManRisk> {
	private static final long serialVersionUID = 1L;

	// private CarManRiskService carManRiskService;

	@Autowired
	public void setCarManRiskService(CarManRiskService carManRiskService) {
		// this.carManRiskService = carManRiskService;
		this.setCrudService(carManRiskService);
	}

	@Override
	public boolean isReadonly() {
		// 司机人意险管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.carManRisk"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected CarManRisk createEntity() {
		CarManRisk carManRisk = super.createEntity();
		carManRisk.setBuyType(CarManRisk.BUY_TYPE_NONE);
		return carManRisk;
	}

	@Override
	protected void afterCreate(CarManRisk entity) {
		super.afterCreate(entity);

		// 额外参数的设置
		this.getE().setUid(
				this.getIdGeneratorService().next(CarManRisk.KEY_UID));
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(760)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	public String delete() throws Exception {
		return super.delete();
	}
}