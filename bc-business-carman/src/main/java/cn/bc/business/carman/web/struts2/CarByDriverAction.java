/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 司机营运车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarByDriverAction extends FileEntityAction<Long, CarByDriver> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	public CarByDriverService carByDriverService;
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
	public void setCarByDriverService(CarByDriverService carByDriverService) {
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
	public String create() throws Exception {
		String result = super.create();

		if (carManId != null) {
			CarMan driver = this.carManService.load(carManId);
			this.getE().setDriver(driver);
		} else if (carId != null) {
			Car car = this.carService.load(carId);
			this.getE().setCar(car);
		}
		this.getE().setStatus(BCConstants.STATUS_ENABLED);
		statusesValueList = this.getBSStatuses1();
		return result;
	}

	@Override
	public String edit() throws Exception {
		String result = super.edit();
		statusesValueList = this.getBSStatuses1();
		return result;
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(460)
				.setMinWidth(250).setHeight(350);
	}

	/**
	 * 获取营运班次值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getType() {
		Map<String, String> type = new HashMap<String, String>();
		type = new HashMap<String, String>();
		type.put(String.valueOf(CarByDriver.TYPE_WEIDINGYI),
				getText("carByDriver.classes.weidingyi"));
		type.put(String.valueOf(CarByDriver.TYPE_ZHENGBAN),
				getText("carByDriver.classes.zhengban"));
		type.put(String.valueOf(CarByDriver.TYPE_FUBAN),
				getText("carByDriver.classes.fuban"));
		type.put(String.valueOf(CarByDriver.TYPE_DINGBAN),
				getText("carByDriver.classes.dingban"));
		return type;
	}

	@Override
	protected void initForm(boolean editable) {
		super.initForm(editable);

		// 状态列表
		statusesValueList = this.getBSStatuses1();
	}

}
