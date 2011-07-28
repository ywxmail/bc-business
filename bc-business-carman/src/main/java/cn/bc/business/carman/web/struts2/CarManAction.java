/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.CrudAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 司机责任人Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarManAction extends CrudAction<Long, CarMan> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = "R_MANAGER_BUSINESS";// 管理角色的编码
	public boolean isManager;
	public CarManService carManService;

	@Autowired
	public void setCarManService( CarManService carManService) {
		this.carManService = carManService;
		this.setCrudService(carManService);
	}

	@Override
	public String create() throws Exception {
		this.readonly = false;
		CarMan e = this.carManService.create();
		this.setE(e);

		// 构建附件控件
		attachsUI = buildAttachsUI(true);

		// 构建对话框参数
		this.formPageOption = buildFormPageOption();

		return "form";
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(680).setMinWidth(250)
				.setMinHeight(200).setModal(false);
		if (isManager()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	// 提交反馈
	@Override
	public String save() throws Exception {
		CarMan e = this.getE();
		this.carManService.save(e);
		return "saveSuccess";
	}

	public AttachWidget attachsUI;

	@Override
	public String edit() throws Exception {
		this.readonly = false;
		this.setE(this.carManService.load(this.getId()));
		this.formPageOption = buildFormPageOption();

		// 构建附件控件
		attachsUI = buildAttachsUI(false);

		// TODO 获取回复信息列表

		return "form";
	}

	private AttachWidget buildAttachsUI(boolean isNew) {
		isManager = isManager();
		// 构建附件控件
//		String ptype = "car.main";
//		AttachWidget attachsUI = new AttachWidget();
//		attachsUI.setFlashUpload(this.isFlashUpload());
//		attachsUI.addClazz("formAttachs");
//		if (!isNew)
//			attachsUI.addAttach(this.attachService.findByPtype(ptype, this
//					.getE().getUid()));
//		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);
//
//		// 上传附件的限制
//		attachsUI.addExtension(getText("app.attachs.extensions"))
//				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
//				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
//		attachsUI.setReadOnly(!this.getE().isNew());
		return attachsUI;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
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
		return new String[] { "name", "description" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("name", getText("label.subject"))
				.setSortable(true).setUseTitleFromLabel(true));
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
