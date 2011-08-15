/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

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
import cn.bc.business.OptionConstants;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarRangeFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
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
	private String MANAGER_KEY = "R_ADMIN";// 管理角色的编码
	public boolean isManager;
	public CarByDriverService carByDriverService;
	public String portrait;
	public Map<String, String> statusesValue;
	public OptionService optionService;
	public CarManService carManService;
	public List<OptionItem> driverClasses;
	public OptionConstants optionConstants;
	public Long carManId;

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void setCarByDriverService(CarByDriverService carByDriverService) {
		this.carByDriverService = carByDriverService;
		this.setCrudService(carByDriverService);
	}

	/** 新建的url */
	protected String getCreateUrl() {
		return getPageNamespace() + "/create?carManId=" + carManId;
	}

	@Override
	public String create() throws Exception {
		String result = super.create();
		driverClasses = this.optionService
				.findOptionItemByGroupKey(optionConstants.DRIVER_CLASSES);
		this.getE().setStatus(RichEntity.STATUS_ENABLED);
		statusesValue = this.getEntityStatuses();
		CarMan driver = this.carManService.load(carManId);
		this.getE().setDriver(driver);
		return result;
	}

	@Override
	public String edit() throws Exception {
		String result = super.edit();
		statusesValue = this.getEntityStatuses();
		driverClasses = this.optionService
				.findOptionItemByGroupKey(optionConstants.DRIVER_CLASSES);
		
		return result;
	}

	// 视图特殊条件
	@Override
	protected Condition getSpecalCondition() {
		return new EqualsCondition("driver.id", carManId);
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(390).setMinWidth(250)
				.setMinHeight(200);
		if (isManager()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("driver.name");
	}

	
	@Override
	protected OrderCondition getDefaultOrderCondition() { return
	  new OrderCondition("fileDate", Direction.Desc); }
	 

	// 设置页面的尺寸

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(500).setMinHeight(100);
	}

	@Override
	protected Toolbar buildToolbar() {
		isManager = isManager();
		Toolbar tb = new Toolbar();

		if (isManager) {
			// 新建按钮
			tb.addButton(getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(getDefaultEditToolbarButton());

			// 删除按钮
			tb.addButton(getDefaultDeleteToolbarButton());
		} else {// 普通用户
			// 查看按钮
			tb.addButton(getDefaultOpenToolbarButton());
		}

		return tb;
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("car.plateNo",
				getText("carByDriver.car.plateNo"),200)
				.setValueFormater(new AbstractFormater() {
					@Override
					public String format(Object context, Object value) {
						CarByDriver carByDriver = (CarByDriver) context;
						return carByDriver.getCar().getPlateType() + " "
								+ carByDriver.getCar().getPlateNo();
					}
				}));
		columns.add(new TextColumn("classes", getText("carByDriver.classes"),200)
				.setSortable(true));
		columns.add(new TextColumn("startDate",
				getText("carByDriver.timeInterva"),320).setSortable(true)
				.setValueFormater(new CalendarRangeFormater() {
					@Override
					public Calendar getToDate(Object context, Object value) {
						CarByDriver carByDriver = (CarByDriver) context;
						return carByDriver.getEndDate();
					}
				}));
		return columns;
	}

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}

	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		return page;
	}
	

}
