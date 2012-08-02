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
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.service.CarByDriverHistoryService;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 司机营运车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ShiftworkByDriverAction extends
		FileEntityAction<Long, CarByDriverHistory> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	public CarByDriverService carByDriverService;
	public CarByDriverHistoryService carByDriverHistoryService;
	public CarManService carManService;
	public CarService carService;
	public Map<Long, String> cars;
	public Long carManId;
	public int moveType;// 迁移类型
	public String shiftwork;// 顶班车辆
	public Map<String, String> statusesValue;// 状态列表

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setCarByDriverHistoryService(
			CarByDriverHistoryService carByDriverHistoryService) {
		this.carByDriverHistoryService = carByDriverHistoryService;
		this.setCrudService(carByDriverHistoryService);
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public boolean isReadonly() {
		// 车辆管理/司机管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bs.driver"), getText("key.role.bc.admin"));
	}

	@Override
	protected void afterCreate(CarByDriverHistory entity) {
		super.afterCreate(entity);
		// 设置迁移类型为顶班
		this.getE().setMoveType(CarByDriverHistory.MOVETYPE_DINGBAN);
		this.getE().setStatus(BCConstants.STATUS_DRAFT);
		// 状态列表
		statusesValue = this.getBSStatuses3();

		// 顶班车辆
		cars = new HashMap<Long, String>();
		if (carManId != null) {
			this.getE().setDriver(this.carManService.load(carManId));
		}
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {

		if (editable) {// edit,create
			// 添加默认的保存按钮
			// 如果是草稿状态的，有保存和入库两个按钮
			if (this.getE().getStatus() == BCConstants.STATUS_DRAFT) {
				pageOption.addButton(this.getDefaultSaveButtonOption());
				pageOption.addButton(new ButtonOption(
						getText("label.warehousing"), null,
						"bc.business.carByDriverHistoryForm.warehousing"));

			}
		}
	}

	public String save() throws Exception {
		// 创建CarByDriver列表
		// List<CarByDriver> toSaves = new ArrayList<CarByDriver>();
		this.beforeSave(this.getE());
		this.getE().setFromCar(null);
		// 设置迁往主挂车的信息
		this.getE().setToClasses(CarByDriver.TYPE_ZHUGUA);
		Car toCar = new Car();
		toCar = this.carService.load(this.getE().getToCar().getId());
		this.getE().setToMotorcadeId(toCar.getMotorcade().getId());
		this.getE().setToUnit(toCar.getCompany());

		// 将顶班车辆组装成字符串赋值给shiftwork字段：plate1,id1;plate2,id2;...
		Long[] carIds = null;
		String shiftwork = this.getE().getShiftwork().trim();
		if (shiftwork.length() != 0) {
			this.getE().setShiftwork(shiftwork + ";");
			String[] shiftworks = shiftwork.split(";");
			carIds = new Long[shiftworks.length];
			if (carIds.length > 0) {
				for (int i = 0; i < shiftworks.length; i++) {
					carIds[i] = new Long(shiftworks[i].split(",")[1]);
				}
			}
		}

		this.carByDriverHistoryService.saveShiftwork(this.getE(), carIds);
		return "saveSuccess";
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(430)
				.setMinWidth(320).setMinHeight(200).setModal(true);

	}
}
