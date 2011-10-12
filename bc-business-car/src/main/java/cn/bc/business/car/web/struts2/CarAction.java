/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.Page;
import cn.bc.core.RichEntityImpl;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarAction extends FileEntityAction<Long, Car> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	public String MANAGER_KEY = "R_MANAGER_BUSINESS"; // 车辆管理员角色的编码

	private CarService carService;
	private MotorcadeService motorcadeService;
	private OptionService optionService;

	public List<Motorcade> motorcadeList; // 可选车队列表

	public List<OptionItem> businessTypeList; // 可选营运性质列表
	public List<OptionItem> levelTypeList; // 可选车辆定级列表
	public List<OptionItem> factoryTypeList; // 可选厂牌类型列表
	public List<OptionItem> fuelTypeList; // 可选燃料类型列表
	public List<OptionItem> colorTypeList; // 可选颜色类型列表
	public List<OptionItem> taximeterFactoryTypeList; // 可选计价器制造厂列表

	public Map<String, String> statusesValue;

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
		this.setCrudService(carService);
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(MANAGER_KEY,getText("key.role.admin"));
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = super.buildFormPageOption().setWidth(840).setMinWidth(250)
				.setHeight(700).setMinHeight(200).setModal(false);
		if (!this.isReadonly()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns)
				.setRowLabelExpression("['plateNo']");
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(1000).setMinWidth(400)
				.setHeight(500).setMinHeight(300);
	}

	// 搜索条件
	@Override
	protected String[] getSearchFields() {
		return new String[] { "plateType", "plateNo", "factoryType", "driver", "certNo2" };
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new TextColumn("['id']", "ID", 40));
		columns.add(new TextColumn("['status']", getText("car.status"), 50)
				.setSortable(true).setValueFormater(
				new EntityStatusFormater(getEntityStatuses())));
		columns.add(new TextColumn("['plateType']", getText("car.plate"), 100)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						Map car = (Map) context;
						return car.get("plateType") + " " + car.get("plateNo");
					}
				}));
		columns.add(new TextColumn("['unit']", getText("car.unit"), 80)
				.setSortable(true));
		columns.add(new TextColumn("['motorcade']", getText("car.motorcade"), 80)
				.setSortable(true));
		columns.add(new TextColumn("['registerDate']",getText("car.registerDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("['driver']", getText("car.carMan"), 100)
				.setUseTitleFromLabel(true).setValueFormater(
						new AbstractFormater<String>() {
							@Override
							public String format(Object context, Object value) {
								Map car = (Map) context;
								if (car.get("driver") != null) {
									return car.get("driver") + "("
											+ getText("car.by.driver.classes")
											+ ")";
								} else {
									return "";
								}
							}
						}));
		columns.add(new TextColumn("['certNo2']",
				getText("car.certNo2"), 100));
		columns.add(new TextColumn("['businessType']",
				getText("car.businessType"), 100));
		columns.add(new TextColumn("['factoryType']", getText("car.factory"),
				120).setValueFormater(new AbstractFormater<String>() {
			@Override
			public String format(Object context, Object value) {
				// 从上下文取出元素Map
				Map car = (Map) context;
				if (car.get("factoryType") != null
						&& car.get("factoryModel") != null) {
					return car.get("factoryType") + " "
							+ car.get("factoryModel");
				} else if (car.get("factoryModel") != null) {
					return car.get("factoryModel").toString();
				} else if (car.get("factoryType") != null) {
					return car.get("factoryType") + " ";
				} else {
					return "";
				}
			}
		}));
		columns.add(new TextColumn("['code']", getText("car.code"), 60)
				.setSortable(true));
		columns.add(new TextColumn("['originNo']", getText("car.originNo"), 100)
				.setSortable(true));
		columns.add(new TextColumn("['vin']", getText("car.vin"), 120));
		return columns;
	}

	@Override
	public String create() throws Exception {
		String r = super.create();
		this.getE().setUnit(this.getSystyemContext().getUnit());
		this.getE().setUid(
				this.getIdGeneratorService().next(Car.KEY_UID));

		// 初始化车辆的状态
		this.getE().setStatus(RichEntityImpl.STATUS_DISABLED);

		// 表单可选项的加载
		initSelects();

		return r;
	}

	@Override
	public String edit() throws Exception {
		String r = super.edit();
		this.setE(this.getCrudService().load(this.getId()));

		// 表单可选项的加载
		statusesValue = this.getEntityStatuses();

		initSelects();
		return r;
	}

	/**
	 * 根据请求的条件查找非分页信息对象
	 * 
	 * @return
	 */
	@Override
	protected List<? extends Object> findList() {
		return this.carService.list(this.getCondition());
	}

	/**
	 * 根据请求的条件查找分页信息对象
	 * 
	 * @return
	 */
	protected Page<? extends Object> findPage() {
		return this.carService.page(this.getCondition(), this.getPage()
				.getPageNo(), this.getPage().getPageSize());
	}

	// 表单可选项的加载
	public void initSelects() {
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findActive();
		// 加载可选营运性质列表
		this.businessTypeList = this.optionService
				.findOptionItemByGroupKey(OptionConstants.CAR_BUSINESS_NATURE);
		// 加载可选营运性质列表
		this.levelTypeList = this.optionService
				.findOptionItemByGroupKey(OptionConstants.CAR_RANK);
		// 加载可选厂牌类型列表
		this.factoryTypeList = this.optionService
				.findOptionItemByGroupKey(OptionConstants.CAR_BRAND);
		// 加载可选燃料类型列表
		this.fuelTypeList = this.optionService
				.findOptionItemByGroupKey(OptionConstants.CAR_FUEL_TYPE);
		// 加载可选颜色类型列表
		this.colorTypeList = this.optionService
				.findOptionItemByGroupKey(OptionConstants.CAR_COLOR);
		// 加载可选 计价器制造厂列表
		this.taximeterFactoryTypeList = this.optionService
				.findOptionItemByGroupKey(OptionConstants.CAR_TAXIMETERFACTORY);
	}

}
