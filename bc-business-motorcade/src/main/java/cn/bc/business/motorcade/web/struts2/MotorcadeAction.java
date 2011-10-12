package cn.bc.business.motorcade.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.Entity;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 车队信息Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class MotorcadeAction extends FileEntityAction<Long, Motorcade> {
	// private static Log logger = LogFactory.getLog(MotorcadeAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = "R_MANAGER_MOTORCADE";// 车队管理角色的编码
	private OptionService optionService;
	private MotorcadeService motorcadeService;
	public List<OptionItem> paymentDates; // 可选缴费日列表

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
		this.setCrudService(motorcadeService);
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(MANAGER_KEY, getText("key.role.admin"));
	}

	@Override
	public String create() throws Exception {
		String r = super.create();
		Motorcade e = this.getE();

		// 所属单位
		e.setUnit(this.getSystyemContext().getUnit());
		e.setUid(this.getIdGeneratorService().next(Motorcade.KEY_UID));
		e.setType(Motorcade.TYPE_TEAM);

		// 初始状态
		e.setStatus(Entity.STATUS_ENABLED);

		// 表单可选项的加载
		initSelects();

		return r;
	}

	@Override
	public String edit() throws Exception {
		// 表单可选项的加载
		initSelects();
		
		return super.edit();
	}

	private void initSelects() {
		// 加载缴费日列表
		this.paymentDates = this.optionService
				.findOptionItemByGroupKey(OptionConstants.MOTORCADE_PAYMENT_DATE);
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(618).setMinWidth(618)
				.setMinHeight(250).setModal(false);
		if (!this.getE().isNew()) {
			option.addButton(new ButtonOption(
					getText("motorcade.historicalInformation"), null,
					"bc.business.motorcadeForm.check"));
		}
		if (!this.isReadonly()) {
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
		return new OrderCondition("status", Direction.Asc).add("code",
				Direction.Asc);
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(600).setMinWidth(300)
				.setHeight(500).setMinHeight(300);
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "code", "name", "unit.name", "principalName" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status", getText("label.status"), 60)
				.setSortable(true).setValueFormater(
						new KeyValueFormater(getEntityStatuses())));
		columns.add(new TextColumn("unit.name", getText("motorcade.unit"),
				120).setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("principalName",
				getText("motorcade.principal"), 80).setSortable(true));
		columns.add(new TextColumn("code", getText("label.code"), 80)
				.setSortable(true));
		columns.add(new TextColumn("name", getText("motorcade.name"))
				.setSortable(true).setUseTitleFromLabel(true));
		return columns;
	}

	// 保存
	public String save() throws Exception {
		Motorcade e = this.getE();

		// 处理所属父类
		if (e.getParent() != null && e.getParent().getId() == null)
			e.setParent(null);

		return super.save();
	}
}
