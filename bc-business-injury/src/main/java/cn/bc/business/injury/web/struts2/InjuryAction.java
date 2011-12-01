/**
 * 
 */
package cn.bc.business.injury.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.business.injury.domain.Injury;
import cn.bc.business.injury.service.InjuryService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.CalendarRangeFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 工伤Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class InjuryAction extends FileEntityAction<Long, Injury> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	public InjuryService injuryService;
	public boolean isManager;
	public Long carId;
	public Long carManId;

	public Map<String, String> statusesValue;

	@Autowired
	public void setInjuryService(InjuryService injuryService) {
		this.injuryService = injuryService;
		this.setCrudService(injuryService);
	}

	@Override
	public boolean isReadonly() {
		// 合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4charger"),
				getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
	}

	@Override
	public String create() throws Exception {
		// this.readonly = false;
		// Contract e = this.contractService.create();
		// this.setE(e);
		//
		// this.getE().setUid(
		// this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		// this.formPageOption = buildFormPageOption();

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
		return super.buildGridData(columns).setRowLabelExpression("['code']");
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
//		return new String[] { "contract.code", "contract.wordNo",
//				"contract.ext_str1", "contract.ext_str2" };
		return null;
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new TextColumn("['id']", "ID", 20));
		if(carManId != null){
			columns.add(new TextColumn("['ext_str1']", getText("contract.car"), 80));
		}
		if(carId != null){
			columns.add(new TextColumn("['ext_str2']",getText("contract.labour.driver"), 80));
		}
		columns.add(new TextColumn("['signDate']",
				getText("contract.signDate"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("['startDate']",
				getText("contract.deadline"))
				.setValueFormater(new CalendarRangeFormater("yyyy-MM-dd") {
					@SuppressWarnings("rawtypes")
					@Override
					public Calendar getToDate(Object context, Object value) {
						Map contract = (Map) context;
						return (Calendar) contract.get("endDate");
					}
				}));
		columns.add(new TextColumn("['code']", getText("contract.code"), 120)
				.setUseTitleFromLabel(true));

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
		grid.setSingleSelect(false).setDblClickRow("bc.contractList.edit");

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
		isManager = isReadonly();
		Toolbar tb = new Toolbar();

		if (!isManager) {
			// 新建按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.create"))
					.setClick("bc.contractList.create"));

			// 编辑按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.edit"))
					.setClick("bc.contractList.edit"));

			// 删除按钮
			// tb.addButton(getDefaultDeleteToolbarButton());
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.delete"))
					.setClick("bc.contractList.del"));
		} else {// 普通用户
			// 查看按钮
			tb.addButton(getDefaultOpenToolbarButton());
		}

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	// // 视图特殊条件
	// @Override
	// protected Condition getSpecalCondition() {
	// if (carId != null) {
	// return new EqualsCondition("carId", carId);
	// }
	// if (carManId != null) {
	// return new EqualsCondition("carManId", carManId);
	// }else {
	// return null;
	// }
	// }

	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carId != null)
			page.setAttr("data-extras", new Json().put("carId", carId)
					.toString());
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		return page;
	}


}