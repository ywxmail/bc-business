/**
 * 
 */
package cn.bc.business.certLost.web.struts2;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.certLost.domain.CertLost;
import cn.bc.business.certLost.service.CertLostService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 证照遗失Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CertLostAction extends FileEntityAction<Long, CertLost> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	public CertLostService carByDriverService;
	public String portrait;
	public Map<String, String> statusesValueList;// 状态列表
	public CarManService carManService;
	public CarService carService;
	public Long carManId;
	public Long carId;

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setCarByDriverService(CertLostService carByDriverService) {
		this.carByDriverService = carByDriverService;
		this.setCrudService(carByDriverService);
	}

	@Override
	public boolean isReadonly() {
		// 车辆管理/司机管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bs.driver"), getText("key.role.bc.admin"));
	}

	@Override
	protected void afterCreate(CertLost entity) {
		super.afterCreate(entity);
		

	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(460)
				.setMinWidth(250).setHeight(350);
	}

	
	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 状态列表
		statusesValueList = this.getBSStatuses1();
	}

}
