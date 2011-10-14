/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.runcase.domain.Case4Accident;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.runcase.service.CaseAccidentService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 事故理赔Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseAccidentAction extends FileEntityAction<Long, Case4Accident> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = "R_ADMIN";// 管理角色的编码
	public boolean isManager;
	public Long carId;

	@SuppressWarnings("unused")
	private CaseAccidentService caseAccidentService;
	private MotorcadeService motorcadeService;
	private OptionService optionService;
	private AttachService attachService;
	public AttachWidget attachsUI;
	public OptionConstants optionConstants;
	public CarByDriverService carByDriverService;

	public List<Motorcade> motorcadeList; // 可选车队列表
	public List<OptionItem> dutyList; // 可选责任列表
	public List<OptionItem> sortList; // 可选性质列表
	public List<OptionItem> degreeList; // 可选程度列表
	public List<OptionItem> certList; // 可选没收证件列表
	public List<OptionItem> departmentList; // 可选执法机关列表
	public List<OptionItem> companyList; // 可选保险公司列表

	public Map<String, String> statusesValue;
	public Map<String, String> sourcesValue;

	public Long carManId;
	public CarManService carManService;
	public CarService carService;

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void CarByDriverService(CarByDriverService carByDriverService) {
		this.carByDriverService = carByDriverService;
	}

	@Autowired
	public void setCaseAccidentService(CaseAccidentService caseAccidentService) {
		this.caseAccidentService = caseAccidentService;
		this.setCrudService(caseAccidentService);
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}

	@SuppressWarnings("static-access")
	private AttachWidget buildAttachsUI(boolean isNew) {
		isManager = isManager();
		// 构建附件控件
		String ptype = "contractLabour.main";
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(this.isFlashUpload());
		attachsUI.addClazz("formAttachs");
		if (!isNew)
			attachsUI.addAttach(this.attachService.findByPtype(ptype, this
					.getE().getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
		attachsUI.setReadOnly(!this.getE().isNew());
		return attachsUI;
	}

	// 复写搜索URL方法
	protected String getEntityConfigName() {
		return "caseAccident";
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(MANAGER_KEY);
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(850).setMinWidth(250)
				.setMinHeight(200).setModal(false).setHeight(500);
		if (!isReadonly()) {
			// 特殊处理结案按钮
			if (Case4InfractTraffic.STATUS_ACTIVE == getE().getStatus()
					&& !getE().isNew()) {
				ButtonOption buttonOption = new ButtonOption(
						getText("label.closefile"), null,
						"bc.caseAccidentForm.closefile");
				buttonOption.put("id", "bcSaveDlgButton");
				option.addButton(buttonOption);
			}
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("caseNo");
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar buildToolbar() {
		isManager = isReadonly();
		Toolbar tb = new Toolbar();

		if (!isManager) {
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

	// 搜索条件
	@Override
	protected String[] getSearchFields() {
		return new String[] { "caseNo", "carPlate", "driverName", "driverCert",
				"motorcadeName" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isReadonly();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status", getText("runcase.status"), 50)
				.setSortable(true).setValueFormater(
						new EntityStatusFormater(getCaseStatuses())));
		columns.add(new TextColumn("code", getText("runcase.caseNo3"))
				.setSortable(true));
		columns.add(new TextColumn("sort", getText("runcase.sort"), 80)
				.setSortable(true));
		columns.add(new TextColumn("motorcadeName",
				getText("runcase.motorcadeName"), 80).setSortable(true));
		
		columns.add(new TextColumn("carPlate", getText("runcase.carPlate"), 100)
				.setSortable(true));
		columns.add(new TextColumn("driverName", getText("runcase.driverName"),
				70).setSortable(true));
		columns.add(new TextColumn("happenDate", getText("runcase.happenDate"),
				150).setSortable(true).setValueFormater(
				new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("address", getText("runcase.address"), 120)
				.setSortable(true));
		columns.add(new TextColumn("driverCert", getText("runcase.driverCert"),
				80).setSortable(true));
		return columns;
	}

	@SuppressWarnings("static-access")
	@Override
	public String create() throws Exception {
		String r = super.create();
		if (carManId != null) {
			CarMan driver = this.carManService.load(carManId);
			Car car = this.caseAccidentService.selectAllCarByCarManId(carManId);
			if (car != null) {
				this.getE().setCarPlate(
						car.getPlateType() + "." + car.getPlateNo());
			}
			this.getE().setDriverId(carManId);
			this.getE().setDriverName(driver.getName());
			this.getE().setDriverCert(driver.getCert4FWZG());
		}
		if (carId != null) {
			Car car = this.carService.load(carId);
			this.getE()
					.setCarPlate(car.getPlateType() + "." + car.getPlateNo());
		}
		departmentList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CA_DEPARTMENT);
		companyList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CA_COMPANY);
		dutyList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CA_DUTY);
		sortList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CA_SORT);
		this.getE().setUid(
				this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));

		// 初始化信息
		this.getE().setType(CaseBase.TYPE_INFRACT_BUSINESS);
		this.getE().setStatus(CaseBase.STATUS_ACTIVE);
		statusesValue = this.getCaseStatuses();

		// 表单可选项的加载
		sourcesValue = this.getSourceStatuses();
		initSelects();
		// 构建附件控件
		attachsUI = buildAttachsUI(true);
		return r;
	}

	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		this.formPageOption = buildFormPageOption();

		// 表单可选项的加载
		statusesValue = this.getCaseStatuses();
		sourcesValue = this.getSourceStatuses();
		initSelects();

		departmentList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CA_DEPARTMENT);
		companyList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CA_COMPANY);
		dutyList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CA_DUTY);
		sortList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CA_SORT);

		// 构建附件控件
		attachsUI = buildAttachsUI(false);
		return "form";
	}

	@Override
	public String save() throws Exception {
		SystemContext context = this.getSystyemContext();
		Case4Accident e = this.getE();

		if (e != null && (e.getReceiverId() == null || e.getReceiverId() < 0)) {
			e.setReceiverId(context.getUserHistory().getId());
			e.setReceiverName(context.getUserHistory().getName());
		}
		// 设置最后更新人的信息
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		this.getCrudService().save(e);

		return "saveSuccess";
	}

	public Json json;

	public String closefile() {
		SystemContext context = this.getSystyemContext();

		this.getE().setStatus(CaseBase.STATUS_CLOSED);
		this.getE().setCloserId(context.getUserHistory().getActorId());
		this.getE().setCloserName(context.getUserHistory().getName());
		this.getE().setCloseDate(Calendar.getInstance(Locale.CHINA));

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String closeDateStr = df.format(this.getE().getCloseDate().getTime());
		json = new Json();
		json.put("status", this.getE().getStatus());
		json.put("closeDate", closeDateStr);
		json.put("closerId", context.getUserHistory().getActorId());
		json.put("closerName", context.getUserHistory().getName());
		return "json";
	}

	// 表单可选项的加载
	public void initSelects() {
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.createQuery().list();

		// 加载可选程度列表
		this.degreeList = this.optionService
				.findOptionItemByGroupKey(OptionConstants.IT_DEGREE);
		// 加载可选没收证件列表
		this.certList = this.optionService
				.findOptionItemByGroupKey(OptionConstants.BS_CERT);
		// 加载可选执法机关列表
		this.departmentList = this.optionService
				.findOptionItemByGroupKey(OptionConstants.CA_DEPARTMENT);
	}

	/**
	 * 获取Entity的状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getCaseStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(CaseBase.STATUS_ACTIVE),
				getText("runcase.select.status.active"));
		statuses.put(String.valueOf(CaseBase.STATUS_CLOSED),
				getText("runcase.select.status.closed"));
		return statuses;
	}

	/**
	 * 获取Entity的来源转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getSourceStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(CaseBase.SOURCE_SYS),
				getText("runcase.select.source.sys"));
		statuses.put(String.valueOf(CaseBase.SOURCE_SYNC),
				getText("runcase.select.source.sync"));
		statuses.put(String.valueOf(CaseBase.SOURCE_FROM_DRIVER),
				getText("runcase.select.source.fromdriver"));
		return statuses;
	}

	// 视图特殊条件
	@Override
	protected Condition getSpecalCondition() {
		if (carManId != null) {
			return new EqualsCondition("driverId", carManId);
		}
		if (carId != null) {
			return new EqualsCondition("carId", carId);
		} else {
			return null;
		}
	}

	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		if (carId != null)
			page.setAttr("data-extras", new Json().put("carId", carId)
					.toString());
		return page;
	}

}
