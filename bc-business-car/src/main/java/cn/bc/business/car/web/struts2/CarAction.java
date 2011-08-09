/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

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
	private String MANAGER_KEY = "R_ADMIN";// 管理角色的编码
	public boolean isManager;
	private CarService carService;

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
		this.setCrudService(carService);
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(810).setMinWidth(250)
				.setMinHeight(200).setModal(false);
		if (isManager()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("plateNo");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("registerDate", Direction.Desc);
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
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

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "plateType", "plateNo" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status", getText("car.status"))
				.setSortable(true));
		columns.add(new TextColumn("code", getText("car.code"))
				.setSortable(true));
		columns.add(new TextColumn("plate", getText("car.plate"))
				.setValueFormater(new AbstractFormater() {
					@Override
					public String format(Object context, Object value) {
						Car car = (Car) context;
						return car.getPlateType() + " " + car.getPlateNo();
					}
				}));
		columns.add(new TextColumn("carMan", getText("car.carMan")));
		columns.add(new TextColumn("factory", getText("car.factory"))
				.setValueFormater(new AbstractFormater() {
					@Override
					public String format(Object context, Object value) {
						Car car = (Car) context;
						return car.getFactoryType() + " "
								+ car.getFactoryModel();
					}
				}));
		columns.add(new TextColumn("motorcade.name", getText("car.motorcade"))
				.setSortable(true));
		columns.add(new TextColumn("unit.name", getText("car.unit"))
				.setSortable(true));
		columns.add(new TextColumn("registerDate", getText("car.registerDate"))
				.setSortable(true).setValueFormater(new CalendarFormater()));
		columns.add(new TextColumn("originNo", getText("car.originNo"))
				.setSortable(true));
		return columns;
	}

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}
}
