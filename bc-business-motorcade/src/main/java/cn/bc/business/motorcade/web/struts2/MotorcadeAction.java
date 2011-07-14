package cn.bc.business.motorcade.web.struts2;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.web.struts2.CrudAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.KeyValueFormater;
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
public class MotorcadeAction extends CrudAction<Long, Motorcade> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = "R_MANAGER_BUSINESS";// 管理角色的编码
	public boolean isManager;

	@Autowired
	public void setMotorcadeService(
			@Qualifier(value = "motorcadeService") CrudService<Motorcade> crudService) {
		this.setCrudService(crudService);
	}

	@Override
	public String create() throws Exception {
		this.readonly = false;
		SystemContext context = (SystemContext) this.getContext();
		Motorcade e = this.getCrudService().create();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUser());
		e.setDepartId(context.getBelong().getId());
		e.setDepartName(context.getBelong().getName());
		e.setUnitId(context.getUnit().getId());
		e.setUnitName(context.getUnit().getName());
		this.setE(e);

		// 构建对话框参数
		this.formPageOption = buildFormPageOption();

		return "form";
	}

	

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("addrevation");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return null;// new OrderCondition("fileDate", Direction.Desc);
	}

	@Override
	protected Condition getSpecalCondition() {
		return null;
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(840).setMinWidth(300)
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
		return new String[] { "code", "addrevation","benelux" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("code", getText("label.code"),130).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn("addrevation", getText("motorcade.addrevation"),130)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("benelux", getText("motorcade.benelux"),400).setSortable(
				true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("status", getText("label.status"), 130)
				.setSortable(true).setValueFormater(
						new KeyValueFormater(getEntityStatuses())));
		return columns;
	}

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}

	//
	// @Override
	// protected String getJs() {
	// return contextPath + "/bc-business/car/list.js";
	// }
}
