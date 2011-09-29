package cn.bc.business.charger.web.struts2;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.charger.domain.Charger;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 负责人Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ChargerAction extends FileEntityAction<Long, Charger> {
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
	public String create() throws Exception {
//		this.readonly = false;
		SystemContext context = (SystemContext) this.getContext();
		Charger e = this.getCrudService().create();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUserHistory());
		e.setUid(this.getIdGeneratorService().next("charger.uid"));
		e.setSex("不设置");
		this.setE(e);

		// 构建对话框参数
		this.formPageOption = buildFormPageOption();

		return "form";
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
		return super.buildListPageOption().setWidth(1200).setMinWidth(300)
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
		return new String[] { "orderId", "name", "card" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("orderId", getText("charger.orderId"), 70)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("name", getText("charger.name"), 70)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("sex", getText("charger.sex"), 70)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("card", getText("charger.card"), 140)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("TWIC", getText("charger.TWIC"), 70)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("brithdate", getText("charger.brithdate"),
				150).setSortable(true).setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd ")));
		columns.add(new TextColumn("area", getText("charger.area"), 70)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("nativePlace",
				getText("charger.nativePlace"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn("phone", getText("label.phone"), 140)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("idAddress", getText("charger.idAddress"),
				140).setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("temporaryAddress",
				getText("charger.temporaryAddress"), 140).setSortable(true)
				.setUseTitleFromLabel(true));

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
