/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.RichEntity;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

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
		this.getE().setStatus(RichEntity.STATUS_ENABLED);
		statusesValueList = this.getEntityStatuses();
		if (carManId != null) {
			CarMan driver = this.carManService.load(carManId);
			this.getE().setDriver(driver);
		} else if (carId != null) {
			Car car = this.carService.load(carId);
			this.getE().setCar(car);
		}
		return result;
	}

	@Override
	public String edit() throws Exception {
		String result = super.edit();
		statusesValueList = this.getEntityStatuses();
		return result;
	}

	// 视图特殊条件
	@Override
	protected Condition getSpecalCondition() {
		if (carManId != null) {
			return new EqualsCondition("driver.id", carManId);
		}
		if (carId != null) {
			return new EqualsCondition("car.id", carId);
		} else {
			return null;
		}
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(390).setMinWidth(250)
				.setMinHeight(200);
		if (!isReadonly()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns)
				.setRowLabelExpression("driver.name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}

	// 设置页面的尺寸

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(100);
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status", getText("carByDriver.statuses"),
				100).setSortable(true).setValueFormater(
				new KeyValueFormater(getEntityStatuses())));
		if (carManId != null || (carManId == null && carId == null)) {
			columns.add(new TextColumn("car.plateNo",
					getText("carByDriver.car.plateNo"), 150)
					.setValueFormater(new LinkFormater4Id(this.getContextPath()
							+ "/bc-business/car/edit?id={0}", "car") {
						@Override
						public String getIdValue(Object context, Object value) {
							return ((CarByDriver) context).getCar().getId()
									.toString();
						}

						@Override
						public String getTaskbarTitle(Object context,
								Object value) {
							CarByDriver carByDriver = (CarByDriver) context;
							return getText("car") + " - "
									+ carByDriver.getCar().getPlate();
						}

						@Override
						public String getLinkText(Object context, Object value) {
							CarByDriver carByDriver = (CarByDriver) context;
							return carByDriver.getCar().getPlate();
						}
					}));

		}
		if (carId != null || (carManId == null && carId == null)) {
			columns.add(new TextColumn("driver.name",
					getText("carByDriver.driver"), 100)
					.setValueFormater(new LinkFormater4Id(this.getContextPath()
							+ "/bc-business/carMan/edit?id={0}", "driver") {
						@Override
						public String getIdValue(Object context, Object value) {
							return ((CarByDriver) context).getDriver().getId()
									.toString();
						}

						@Override
						public String getTaskbarTitle(Object context,
								Object value) {
							return getText("carMan.type.driver") + " - "
									+ value;
						}
					}));
		}

		columns.add(new TextColumn("classes", getText("carByDriver.classes"),
				100).setSortable(true).setValueFormater(
				new KeyValueFormater(getType())));

		columns.add(new TextColumn("description",
				getText("carMan.description"), 270).setSortable(true));

		return columns;
	}

	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		if (carId != null)
			page.setAttr("data-extras", new Json().put("carId", carId)
					.toString());
		return page;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "car.plateType", "car.plateNo", "driver.name",
				"classes" };
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
	public String save() throws Exception {
		SystemContext context = this.getSystyemContext();

		// 设置最后更新人的信息
		this.getE().setModifier(context.getUserHistory());
		this.getE().setModifiedDate(Calendar.getInstance());

		super.save();
		this.carByDriverService.updateCar4Driver(this.getE().getCar().getId());
		return "saveSuccess";
	}
}
