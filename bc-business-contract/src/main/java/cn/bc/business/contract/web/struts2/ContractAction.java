/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.service.ContractService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.CalendarRangeFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 合同Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ContractAction extends FileEntityAction<Long, Contract> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	public ContractService contractService;
	private String MANAGER_KEY = "R_ADMIN";// 管理角色的编码
	public boolean isManager;
	public String contractType;

	public Map<String, String> statusesValue;

	@Autowired
	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
		this.setCrudService(contractService);
	}

	@SuppressWarnings("static-access")
	@Override
	public String create() throws Exception {
		this.readonly = false;
		Contract e = this.contractService.create();
		this.setE(e);

		this.getE().setUid(
				this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		this.formPageOption = buildFormPageOption();

		return "showdialog";
	}


	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(150).setMinWidth(250)
				.setMinHeight(170).setModal(false);
		option.addButton(new ButtonOption(getText("label.save"), "save"));
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("code");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return null;// new OrderCondition("fileDate", Direction.Desc);
	}

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "code", "wordNo" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("code", getText("contract.code"),150)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("type", getText("contract.type"),120)
				.setSortable(true).setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(getEntityTypes())));
		columns.add(new TextColumn("signDate", getText("contract.signDate"))
				.setSortable(true).setValueFormater(
						new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("startDate", getText("contract.deadline"),200)
				.setSortable(true).setValueFormater(
						new CalendarRangeFormater("yyyy-MM-dd") {
							@Override
							public Calendar getToDate(Object context,
									Object value) {
								Contract contract = (Contract) context;
								return contract.getEndDate();
							}
						}));
		columns.add(new TextColumn("transactor", getText("contract.transactor"))
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						Contract contract = (Contract) context;
						Actor transactor = contract.getTransactor();
						if (transactor == null) {
							return null;
						} else {
							return contract.getTransactor().getName();
						}
					}
				}));

		return columns;
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

		// 单选及双击行编辑
		grid.setSingleSelect(true).setDblClickRow("bc.contractList.edit");

		// 分页条
		grid.setFooter(buildGridFooter(grid));

		return grid;
	}

	// 自定义视图加载的js
	@Override
	protected String getJs() {
		return contextPath + "/bc-business/contract/list.js";
	}

	@Override
	protected Toolbar buildToolbar() {
		isManager = isManager();
		Toolbar tb = new Toolbar();

		if (isManager) {
			// 新建按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.create"))
					.setClick("bc.contractList.create"));

			// 编辑按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.edit"))
					.setClick("bc.contractList.edit"));

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

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}
	
	/**
	 * 获取Contract的合同类型列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Contract.TYPE_LABOUR),
				getText("contract.select.labour"));
		types.put(String.valueOf(Contract.TYPE_CHARGER),
				getText("contract.select.charger"));
		return types;
	}
}