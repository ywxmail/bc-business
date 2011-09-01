package cn.bc.business.charger.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.business.charger.domain.Charger;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;

import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 选择负责人Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectChargerAction extends FileEntityAction<Long, Charger> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = "R_MANAGER_BUSINESS";// 管理角色的编码
	public boolean isManager;

	@Autowired
	public void setChargerService(
			@Qualifier(value = "chargerService") CrudService<Charger> crudService) {
		this.setCrudService(crudService);
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("orderId", Direction.Asc);
	}

	@Override
	protected Condition getSpecalCondition() {
		return null;
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super
				.buildListPageOption()
				.setWidth(370)
				.setMinWidth(260)
				.setHeight(340)
				.setMinHeight(220)
				.setModal(true)
				.addButton(
						new ButtonOption(getText("selectCharge.clickOk"), null,
								"bc.business.selectCharger.clickOk"));
	}

	/** 页面需要另外加载的js文件，逗号连接多个文件 */
	protected String getJs() {
		return  this.getContextPath()+"/bc-business/charger/selectCharger.js";
	}

	@Override
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();
		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());
		tb.addStyle("height", "30px");
		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "name", "unit", "nativePlace" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("name", getText("selectCharger.name"), 70)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("unit", getText("selectCharger.unit"), 140)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("nativePlace",
				getText("selectCharger.nativePlace"), 70).setSortable(true)
				.setUseTitleFromLabel(true));

		return columns;
	}

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}
    //获取Action名
	protected String getEntityConfigName() {
		return "SelectCharger";
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
		grid.setSingleSelect(false).setDblClickRow("bc.business.selectCharger.dblickOk");
		// 分页条
		grid.setFooter(buildGridFooter(grid));
		return grid;
	}
}
