/**
 * 
 */
package cn.bc.business.carlpg.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.carlpg.domain.CarLPG;
import cn.bc.business.carlpg.service.CarLPGService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.PageOption;

/**
 * LPG配置Action
 * 
 * @author lpg
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarLPGAction extends FileEntityAction<Long, CarLPG> {
	// private static Log logger = LogFactory.getLog(CarModelAction.class);
	private static final long serialVersionUID = 1L;
	public CarLPGService carLPGService;
	public boolean isManager;


	@Autowired
	public void setcarLPGServicee(CarLPGService carLPGService) {
		this.carLPGService = carLPGService;
		this.setCrudService(carLPGService);
	}
	
	@Override
	public boolean isReadonly() {
		// 车辆管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bc.admin"));
	}
	
	
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return	super.buildFormPageOption(editable).setWidth(535).setHeight(400)
				.setMinWidth(250).setMinHeight(170);
	}


}