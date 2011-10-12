package cn.bc.business.motorcade.web.struts2;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 车队信息Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class MotorcadeAction extends FileEntityAction<Long, Motorcade> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = "R_MANAGER_BUSINESS";// 管理角色的编码
	private MotorcadeService motorcadeService;
	public boolean isManager;

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
		this.setCrudService(motorcadeService);
	}

	public MotorcadeService getMotorcadeService() {
		return motorcadeService;
	}

	@Override
	public String create() throws Exception {
//		this.readonly = false;
		SystemContext context = (SystemContext) this.getContext();
		Motorcade e = this.getMotorcadeService().create();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		e.setModifier(context.getUserHistory());
		this.setE(e);

		// 构建对话框参数
		this.formPageOption = buildFormPageOption();

		return "form";
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(620).setMinWidth(250)
				.setMinHeight(250).setModal(false);
		if (!this.getE().isNew()) {
			option.addButton(new ButtonOption(
					getText("motorcade.historicalInformation"), null,
					"bc.business.motorcadeForm.check"));
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		} else {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("status", Direction.Asc).add("fileDate", Direction.Desc);
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
		return new String[] { "code", "name", "fullName" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("code", getText("label.code"), 130)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("name", getText("motorcade.name"), 130)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("fullName", getText("motorcade.fullName"),
				300).setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("status", getText("label.status"), 130)
				.setSortable(true).setValueFormater(
						new KeyValueFormater(getEntityStatuses())));
		return columns;
	}

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}

	// 保存
	public String save() throws Exception {
		SystemContext context = (SystemContext) this.getContext();
		this.getE().setModifiedDate(Calendar.getInstance());
		this.getE().setModifier(context.getUserHistory());
		this.getCrudService().save(this.getE());
		return "saveSuccess";
	}
}
