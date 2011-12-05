/**
 * 
 */
package cn.bc.business.injury.web.struts2;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.injury.domain.Injury;
import cn.bc.business.injury.service.InjuryService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.CalendarRangeFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
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
	// private static Log logger = LogFactory.getLog(InjuryAction.class);
	private static final long serialVersionUID = 1L;
	public InjuryService injuryService;
	public boolean isManager;
	public Long contractId;

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

	@SuppressWarnings("static-access")
	@Override
	public String create() throws Exception {
		 Injury e = this.injuryService.create();
		 this.setE(e);
		
		 this.getE().setUid(
		 this.getIdGeneratorService().next(this.getE().KEY_UID));
		 this.formPageOption = super.buildFormPageOption();

		return "form";
	}

//	@Override
//	protected PageOption buildFormPageOption() {
//		PageOption option = new PageOption().setWidth(150).setMinWidth(250)
//				.setMinHeight(170).setModal(false);
//		option.addButton(new ButtonOption(getText("label.save"), "save"));
//		return option;
//	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("code");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "injury.code" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("compensation",
				getText("injury.compensation"), 90));
		columns.add(new TextColumn("happenDate",
				getText("injury.happenDate"), 90).setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("confirmDate",
				getText("injury.confirmDate"), 90).setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("startDate",
				getText("injury.startDate"))
				.setValueFormater(new CalendarRangeFormater("yyyy-MM-dd") {
					@Override
					public Calendar getToDate(Object context, Object value) {
						Injury injury = (Injury) context;
						return injury.getEndDate();
					}
				}));
		columns.add(new TextColumn("code", getText("injury.code"), 80)
				.setUseTitleFromLabel(true));
		return columns;
	}

	 // 视图特殊条件
	 @Override
	 protected Condition getSpecalCondition() {
		 if (this.contractId != null) {
			 return new EqualsCondition("contractId", this.contractId);
		 }else {
			 return null;
		 }
	 }

	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (this.contractId != null)
			page.setAttr("data-extras", new Json().put("contractId", this.contractId)
					.toString());
		return page;
	}


}