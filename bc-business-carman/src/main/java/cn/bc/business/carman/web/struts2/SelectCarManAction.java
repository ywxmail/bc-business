/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 选择司机、责任人Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectCarManAction extends FileEntityAction<Long, CarMan> {
	// private static Log logger = LogFactory.getLog(SelectCarManAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = "R_ADMIN";// 管理角色的编码
	public boolean isManager;
	public CarManService carManService;
	public String portrait;
	public Long typeId;

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
		this.setCrudService(carManService);
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("orderNo", Direction.Desc);
	}

	/*
	 * @Override protected Condition getSpecalCondition() { return null; }
	 */
	@Override
	protected String[] getSearchFields() {
		return new String[] { "name" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();

		columns.add(new TextColumn("name", getText("carMan.name"))
				.setSortable(true));
		return columns;
	}

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}

	/** 构建视图页面的表格 */
	protected Grid buildGrid() {
		List<Column> columns = this.buildGridColumns();

		// id列
		Grid grid = new Grid();
		grid.setGridHeader(this.buildGridHeader(columns));
		grid.setGridData(this.buildGridData(columns));
		grid.setRemoteSort("true"
				.equalsIgnoreCase(getText("app.grid.remoteSort")));
		grid.setColumns(columns);
		// name属性设为bean的名称
		grid.setName(getText(StringUtils.uncapitalize(getEntityConfigName())));
		// 多选及双击行编辑
		grid.setSingleSelect(false).setDblClickRow(
				"bc.business.selectCarMan.dblickOk");
		// 分页条
		grid.setFooter(buildGridFooter(grid));
		return grid;
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super
				.buildListPageOption()
				.setWidth(300)
				.setMinWidth(260)
				.setHeight(400)
				.setMinHeight(220)
				.setModal(true)
				.addButton(
						new ButtonOption(getText("selectCarMan.clickOk"), null,
								"bc.business.selectCarMan.clickOk"));
	}

	/** 页面需要另外加载的js文件，逗号连接多个文件 */
	protected String getJs() {
		return this.getContextPath() + "/bc-business/carMan/selectCarMan.js";
	}

	@Override
	protected Toolbar buildToolbar() {
		isManager = isManager();
		Toolbar tb = new Toolbar();
		tb.addStyle("height", "30px");

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	// 获取Action名
	protected String getEntityConfigName() {
		return "SelectCarMan";
	}

	// 视图特殊条件
	@Override
	protected Condition getSpecalCondition() {
		if (typeId != null) {
			return new InCondition("type", new Integer[] { CarMan.TYPE_DRIVER,
					CarMan.TYPE_DRIVER_AND_CHARGER });
		} else {
			return null;
		}
	}
}
