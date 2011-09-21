/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.business.carman.service.CarManService;
import cn.bc.business.cert.domain.Cert;
import cn.bc.business.cert.web.struts2.CertAction;
import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.CalendarRangeFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
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
 * 司机证件Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Driver4CertAction extends CertAction {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private String 				MANAGER_KEY 	= 	"R_MANAGER_BUSINESS";// 管理角色的编码
	public 	boolean 			isManager;
	public 	Long				carManId;
	public	CarManService		carManService;

	public 	Map<String, String> statusesValue;

	@Autowired
	public void CarManService(CarManService carManService) {
		this.carManService = carManService;
	}
	
	
	@Override
	public Class<? extends Cert> getEntityClass() {
		return Cert.class;
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

	public AttachWidget attachsUI;

//	@SuppressWarnings("static-access")
	@Override
	public String create() throws Exception {

		return "showdialog";
	}
	
	// 删除
	@Override
	public String delete() throws Exception {
		if (this.getId() != null) {// 删除一条
			//单个删除中间表carman_cert表
			this.certService.deleteCarManNCert(this.getId());
			//单个删除本表
			this.getCrudService().delete(this.getId());
		} else {// 删除一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				//批量删除中间表carman_cert表
				Long[] certIds = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.certService.deleteCarManNCert(certIds);
				//批量删除本表
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.getCrudService().delete(ids);
			} else {
				throw new CoreException("must set property id or ids");
			}
		}
		return "deleteSuccess";
	}

	

	@SuppressWarnings("unused")
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
		return super.buildGridData(columns).setRowLabelExpression("['name']");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return null;// new OrderCondition("fileDate", Direction.Desc);
	}

	// 自定义视图加载的js
	@Override
	protected String getJs() {
		return contextPath + "/bc-business/cert/driver4cert/list.js";
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
		grid.setSingleSelect(false).setDblClickRow("bc.certdriverList.edit");

		// 分页条
		grid.setFooter(buildGridFooter(grid));

		return grid;
	}
	
	@Override
	protected Toolbar buildToolbar() {
		isManager = isManager();
		Toolbar tb = new Toolbar();
		
		if (isManager) {
			// 新建按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.create"))
					.setClick("bc.certdriverList.create"));
			
			// 编辑按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.edit"))
					.setClick("bc.certdriverList.edit"));

			
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

	//搜索条件
	@Override
	protected String[] getSearchFields() {
		return new String[] { "cert.certCode", "cert.certName", "cman.name" };
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		//List<Column> columns = super.buildGridColumns();
		List<Column> columns = new ArrayList<Column>();
		columns.add(new TextColumn("['id']", "ID",20));
		columns.add(new TextColumn("['status']",getText("cert.status"),		50)
				.setSortable(true).setValueFormater(new EntityStatusFormater(getEntityStatuses())));
		columns.add(new TextColumn("['name']", getText("cert.carMan"),		60)
				.setSortable(true).setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						Map carManName = (Map) context;
						if(carManName.get("name") != null){
							return carManName.get("name")+""; 
						}else{
							return "";
						}
					}
				}));
		columns.add(new TextColumn("['certCode']",	getText("cert.certCode"),120)
				.setSortable(true));
		columns.add(new TextColumn("['type']", getText("cert.type"),80)
				.setSortable(true).setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(getEntityTypes())));
		columns.add(new TextColumn("['issueDate']", getText("cert.issueDate"),100)
				.setSortable(true).setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("['startDate']", getText("cert.effectiveDate"),180)
				.setSortable(true).setValueFormater(new CalendarRangeFormater("yyyy-MM-dd") {
					@Override
					public Calendar getToDate(Object context,Object value) {
						Map date = (Map) context;
						return (Calendar) date.get("endDate");
					}
				}));
		columns.add(new TextColumn("['licencer']",	getText("cert.licencer"),80)
				.setSortable(true));
		return columns;
	}
	
	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}

	
	/**
	 * 根据请求的条件查找非分页信息对象
	 * 
	 * @return
	 */
	@Override
	protected List<? extends Object> findList() {
		return this.certService.list4man(this.getCondition(),carManId);
	}
	
	
	/**
	 * 根据请求的条件查找分页信息对象
	 * 
	 * @return
	 */
	protected Page<? extends Object> findPage() {
		return this.certService.page4man(
					this.getCondition(),this.getPage().getPageNo(), 
					this.getPage().getPageSize());
	}
	
	//复写搜索URL方法
	protected String getEntityConfigName() {
		return "driver4cert";
	}
	
	
	
	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		return page;
	}
	
	
}
