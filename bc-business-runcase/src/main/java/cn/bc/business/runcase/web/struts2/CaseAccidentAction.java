/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.policy.domain.BuyPlant;
import cn.bc.business.policy.domain.Policy;
import cn.bc.business.policy.service.PolicyService;
import cn.bc.business.runcase.domain.Case4Accident;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.runcase.service.CaseAccidentService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

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

	private Long carId;
	public String isClosed;
	public boolean isMoreCar;// 标识是否一个司机对应有多辆车
	public boolean isMoreCarMan;// 标识是否一辆车对应多个司机
	public boolean isNullCar;// 标识是否没有车和司机对应
	public boolean isNullCarMan;// 标识是否没有司机和车对应
	public boolean isPayManage;// 标识是否司机受款管理
	public boolean isPayRead;// 标识是否司机查看
	public boolean isManage;// 标识事故理赔管理员

	private CaseAccidentService caseAccidentService;
	private MotorcadeService motorcadeService;
	private OptionService optionService;
	private AttachService attachService;
	public AttachWidget attachsUI;

	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> dutyList; // 可选责任列表
	public List<Map<String, String>> sortList; // 可选性质列表
	public JSONArray departmentList; // 可选执法机关列表
	public List<Map<String, String>> companyList; // 可选保险公司列表

	public Map<String, String> statusesValue;
	public Map<String, String> sourcesValue;

	private Long carManId;
	private CarManService carManService;
	private CarService carService;

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	public Long getCarManId() {
		return carManId;
	}

	public void setCarManId(Long carManId) {
		this.carManId = carManId;
	}

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
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

	@SuppressWarnings("static-access")
	private AttachWidget buildAttachsUI(boolean isNew) {
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
		// 状态为结案时显示只读状态
		attachsUI.setReadOnly(this.getE().getStatus()==CaseBase.STATUS_CLOSED);
		return attachsUI;
	}

	// 复写搜索URL方法
	protected String getEntityConfigName() {
		return "caseAccident";
	}

	@Override
	public boolean isReadonly() {
		// 事故理赔管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.accident"),
				getText("key.role.bs.accident.pay.manage"),
				getText("key.role.bc.admin"));
	}

	// ======= 司机受款管理权限开始 ========
	// 对事故理赔进行操作(不包括司机受款) 返回false表示没权限
	public boolean isManage() {
		// 事故理赔司机受款管理
		SystemContext context = (SystemContext) this.getContext();
		return context.hasAnyRole(getText("key.role.bs.accident"),
				getText("key.role.bc.admin"));
	}

	// 对司机受款信息进行操作 返回false表示没权限
	public boolean isPayManage() {
		// 事故理赔司机受款管理
		SystemContext context = (SystemContext) this.getContext();
		return context.hasAnyRole(getText("key.role.bs.accident.pay.manage"),
				getText("key.role.bc.admin"));
	}

	// 查看司机受款信息 返回false表示没权限
	public boolean isPayRead() {
		// 事故理赔司机受款信息
		SystemContext context = (SystemContext) this.getContext();
		return context.hasAnyRole(getText("key.role.bs.accident.pay.read"),
				getText("key.role.bc.admin"));
	}

	// ======= 司机受款管理权限结束========

	// 设置页面的尺寸
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(830)
				.setMinWidth(300).setHeight(450).setMinHeight(300);
	}

	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {

		if (this.useFormPrint())
			// 添加打印按钮
			pageOption.addButton(this.getDefaultPrintButtonOption());

		// 不可编辑且没有权限
		if (!editable && isReadonly())
			return;

		if (Case4Accident.STATUS_ACTIVE == getE().getStatus() && isManage())
			pageOption.addButton(new ButtonOption(getText("label.closefile"),
					null, "bc.caseAccidentForm.closefile")
					.setId("caseAccidentCloseFile"));

		// 状态在案且有权限
		if (Case4Accident.STATUS_ACTIVE == getE().getStatus() && !isReadonly())
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.caseAccidentForm.save").setId("caseAccidentSave"));

	}

	@Override
	protected void afterCreate(Case4Accident entity) {
		super.afterCreate(entity);
		if (carManId != null) {
			// 如果司机Id不为空(在司机页签中新建事故理赔表单)
			CarMan driver = this.carManService.load(carManId);
			List<Car> car = this.carService.selectAllCarByCarManId(carManId);
			if (car.size() == 1) {
				this.getE().setCarId(car.get(0).getId());
				this.getE().setCarPlate(
						car.get(0).getPlateType() + "."
								+ car.get(0).getPlateNo());
				this.getE().setMotorcadeId(car.get(0).getMotorcade().getId());
				// 公司
				this.getE().setCompany(car.get(0).getCompany());
			} else if (car.size() > 1) {
				isMoreCar = true;
			} else {
				isNullCar = true;
			}
			this.getE().setDriverId(carManId);
			this.getE().setDriverName(driver.getName());
			this.getE().setDriverCert(driver.getCert4FWZG());
			this.getE().setDriverArea(driver.getRegion());
			this.getE().setDriverClasses(driver.getClasses());
			this.getE().setOrigin(driver.getOrigin());
			this.getE().setDriverType(driver.getType());
		}
		if (carId != null) {
			// 如果车辆Id不为空(在司机页签中新建事故理赔表单)
			Car car = this.carService.load(carId);
			this.getE()
					.setCarPlate(car.getPlateType() + "." + car.getPlateNo());
			this.getE().setCarId(carId);
			this.getE().setMotorcadeId(car.getMotorcade().getId());
			// 公司
			this.getE().setCompany(car.getCompany());
			List<CarMan> carMan = this.carManService
					.selectAllCarManByCarId(carId);
			if (carMan.size() == 1) {
				this.getE().setDriverName(carMan.get(0).getName());
				this.getE().setDriverId(carMan.get(0).getId());
				this.getE().setDriverCert(carMan.get(0).getCert4FWZG());
				this.getE().setDriverArea(carMan.get(0).getRegion());
				this.getE().setDriverClasses(carMan.get(0).getClasses());
				this.getE().setOrigin(carMan.get(0).getOrigin());
				this.getE().setDriverType(carMan.get(0).getType());
			} else if (carMan.size() > 1) {
				isMoreCarMan = true;
			} else {
				isNullCarMan = true;
			}
		}
		this.getE().setUid(
				this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		// 事故编号自动生成为流水号
		this.getE().setCode(
				this.getIdGeneratorService().nextSN4Day(Case4Accident.KEY_CODE,
						"00"));

		// 初始化信息
		this.getE().setType(CaseBase.TYPE_INFRACT_BUSINESS);
		this.getE().setStatus(CaseBase.STATUS_ACTIVE);
		this.getE().setHappenDate(Calendar.getInstance());
		this.getE().setReceiveDate(Calendar.getInstance());
		statusesValue = this.getBSStatuses2();

		// 构建附件控件
		attachsUI = buildAttachsUI(true);
	}

	@Override
	protected void afterOpen(Case4Accident entity) {
		super.afterOpen(entity);

	}

	@Override
	protected void beforeSave(Case4Accident entity) {
		super.beforeSave(entity);
		Case4Accident e = entity;
		if(!e.isNew()){
			Case4Accident currentE = this.caseAccidentService.load(e.getId());		
			// 没有司机受款管理权限时
			if (!isPayManage() && isManage()) {
				e.setPayMoney(currentE.getPayMoney());
				e.setPayDesc(currentE.getPayDesc());
				e.setPayMoneyTwo(currentE.getPayMoneyTwo());
				e.setPayDescTwo(currentE.getPayDescTwo());
			}else if (isPayManage() && !isManage()){
				currentE.setPayDriverId(e.getPayDriverId());
				currentE.setPayDriver(e.getPayDriver());
				currentE.setPayDate(e.getPayDate());
				currentE.setPay(e.isPay());
				currentE.setPayDriverIdTwo(e.getPayDriverIdTwo());
				currentE.setPayDriverTwo(e.getPayDriverTwo());
				currentE.setPayDateTwo(e.getPayDateTwo());
				currentE.setPayTwo(e.isPayTwo());
				currentE.setPayMoney(e.getPayMoney());
				currentE.setPayDesc(e.getPayDesc());
				currentE.setPayMoneyTwo(e.getPayMoneyTwo());
				currentE.setPayDescTwo(e.getPayDescTwo());
				currentE.setModifier(e.getModifier());
				currentE.setModifiedDate(e.getModifiedDate());
				this.setE(currentE);
			}
		}
		
		// 设置结案信息
		if (isClosed.length() > 0 && isClosed.equals("1")) {
			SystemContext context = this.getSystyemContext();
			e.setStatus(CaseBase.STATUS_CLOSED);
			e.setCloserId(context.getUserHistory().getId());
			e.setCloserName(context.getUserHistory().getName());
			e.setCloseDate(Calendar.getInstance(Locale.CHINA));
		}
	}

	@Override
	protected void afterEdit(Case4Accident entity) {
		super.afterEdit(entity);
		// 构建附件控件
		attachsUI = buildAttachsUI(true);
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
		statuses.put(String.valueOf(CaseBase.SOURCE_GENERATION),
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
				getText("runcase.select.source.sync.auto"));
		statuses.put(String.valueOf(CaseBase.SOURCE_GENERATION),
				getText("runcase.select.source.sync.gen"));
		return statuses;
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		Date startTime = new Date();
		// 表单可选项的加载
		statusesValue = this.getCaseStatuses();
		sourcesValue = this.getSourceStatuses();

		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		if (this.getE().getMotorcadeId() != null)
			OptionItem.insertIfNotExist(this.motorcadeList, this.getE()
					.getMotorcadeId().toString(), this.getE()
					.getMotorcadeName());
		logger.info("motorcadeList耗时：" + DateUtils.getWasteTime(startTime));

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CA_DUTY, OptionConstants.CA_SORT,
						OptionConstants.CA_DEPARTMENT,
						OptionConstants.CA_COMPANY, });

		// 加载可选责任列表
		this.dutyList = optionItems.get(OptionConstants.CA_DUTY);
		// 加载可选营运性质列表
		this.sortList = optionItems.get(OptionConstants.CA_SORT);
		// 加载可选执法机关列表
		this.departmentList = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.CA_DEPARTMENT));

		// 加载可选保险公司列表
		this.companyList = optionItems.get(OptionConstants.CA_COMPANY);

		if (logger.isInfoEnabled())
			logger.info("findOptionItem耗时：" + DateUtils.getWasteTime(startTime));

	}

	// =======自动加载司机相关信息开始 =========
	public String selectCarMansInfo() {
		List<CarMan> drivers = this.carManService.selectAllCarManByCarId(carId);
		JsonArray jsons = new JsonArray();
		Json o;
		for (CarMan driver : drivers) {
			o = new Json();
			o.put("name", driver.getName());
			o.put("id", driver.getId());
			o.put("cert4FWZG", driver.getCert4FWZG());
			o.put("region", driver.getRegion());
			o.put("drivingStatus", driver.getClasses());
			o.put("origin", driver.getOrigin());
			o.put("type", driver.getType());
			jsons.add(o);
		}

		json = jsons.toString();
		return "json";

	}

	// ====== 自动加载司机相关信息结束 ========

	// ======= 自动加载相关保单开始 =========
	private Calendar happenTime;
	private PolicyService policyService;

	public PolicyService getPolicyService() {
		return policyService;
	}

	@Autowired
	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public Calendar getHappenTime() {
		return happenTime;
	}

	public void setHappenTime(Calendar happenTime) {
		this.happenTime = happenTime;
	}

	public String loadPolicyInfo() {
		if (carId != null && happenTime != null) {
			List<Policy> pList = this.policyService.getPolicise(carId,
					happenTime);
			JsonArray jsons = new JsonArray();
			JsonArray jsonsbp = new JsonArray();
			Json op;
			Json obp;
			SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
			for (Policy policy : pList) {
				op = new Json();
				// 商业险号
				op.put("commerialNo", policy.getCommerialNo());
				op.put("commerialCompany", policy.getCommerialCompany());
				op.put("commerialStartDate", date_format.format(policy
						.getCommerialStartDate().getTime()));
				op.put("commerialEndDate", date_format.format(policy
						.getCommerialEndDate().getTime()));
				// 责任险号
				op.put("liabilityNo", policy.getLiabilityNo());
				// 强制险号
				op.put("greenslipNo", policy.getGreenslipNo() == null ? ""
						: policy.getGreenslipNo());
				op.put("greenslipCompany",
						policy.getGreenslipCompany() == null ? "" : policy
								.getGreenslipCompany());
				op.put("greenslipStartDate",
						policy.getGreenslipStartDate() == null ? ""
								: date_format.format(policy
										.getGreenslipStartDate().getTime()));
				op.put("greenslipEndDate",
						policy.getGreenslipEndDate() == null ? "" : date_format
								.format(policy.getGreenslipEndDate().getTime()));
				for (BuyPlant bp : policy.getBuyPlants()) {
					obp = new Json();
					obp.put("name", bp.getName());
					obp.put("coverage",
							bp.getCoverage() == null ? "" : bp.getCoverage());
					obp.put("description", bp.getDescription() == null ? ""
							: bp.getDescription());
					jsonsbp.add(obp);
				}
				op.put("buyPlant", jsonsbp);
				jsons.add(op);
			}

			json = jsons.toString();
		}
		return "json";
	}

	// ======= 自动加载相关保单单结束 =========

}
