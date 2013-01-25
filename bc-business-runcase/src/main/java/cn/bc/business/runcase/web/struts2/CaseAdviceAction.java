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

import org.jsoup.helper.StringUtil;
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
import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.runcase.domain.CaseBase4AdviceAndPraise;
import cn.bc.business.runcase.service.CaseAdviceService;
import cn.bc.business.runcase.service.CaseBaseService;
import cn.bc.business.sync.domain.JiaoWeiADVICE;
import cn.bc.business.sync.service.JiaoWeiADVICEService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.identity.domain.ActorDetail;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.sync.domain.SyncBase;
import cn.bc.sync.service.SyncBaseService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

/**
 * 投诉建议Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseAdviceAction extends FileEntityAction<Long, Case4Advice> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	public Long carId;
	public Long carManId;
	private Long syncId; // 同步ID
	private String type; // 投诉类型

	public boolean isMoreCar;
	public boolean isMoreCarMan;
	public boolean isNullCar;
	public boolean isNullCarMan;
	private boolean multiple;
	// public String isClosed;
	private CaseAdviceService caseAdviceService;
	private MotorcadeService motorcadeService;
	private OptionService optionService;
	private CarManService carManService;
	private CarService carService;
	private SyncBaseService syncBaseService; // 平台同步基类Serivce
	private JiaoWeiADVICEService jiaoWeiADVICEService; // 平台同步投诉与建议Service
	private CaseBaseService caseBaseService;
	private String sourceStr;

	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> dutyList; // 可选责任列表
	public List<Map<String, String>> degreeList; // 可选程度列表
	public List<Map<String, String>> certList; // 可选没收证件列表
	public List<Map<String, String>> sourceList; // 可选投诉建议来源

	public Map<String, String> statusesValue;
	public Map<String, String> handlestatusesValue;
	public Map<String, String> sourcesValue;
	private Map<String, List<Map<String, String>>> allList;
	public String happenDate;// 事发日期

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

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

	public Long getSyncId() {
		return syncId;
	}

	public void setSyncId(Long syncId) {
		this.syncId = syncId;
	}

	public String getSourceStr() {
		return sourceStr;
	}

	public void setSourceStr(String sourceStr) {
		this.sourceStr = sourceStr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Autowired
	public void setCaseAdviceService(CaseAdviceService caseAdviceService) {
		this.caseAdviceService = caseAdviceService;
		this.setCrudService(caseAdviceService);
	}

	@Autowired
	public void setCaseBaseService(CaseBaseService caseBaseService) {
		this.caseBaseService = caseBaseService;
	}

	@Autowired
	public void setSyncBaseService(SyncBaseService syncBaseService) {
		this.syncBaseService = syncBaseService;
	}

	@Autowired
	public void setJiaoWeiADVICEService(
			JiaoWeiADVICEService jiaoWeiADVICEService) {
		this.jiaoWeiADVICEService = jiaoWeiADVICEService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("status", Direction.Asc).add("fileDate",
				Direction.Desc);
	}

	// 复写搜索URL方法
	protected String getEntityConfigName() {
		return "caseAdvice";
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		if (CaseBase.TYPE_COMPLAIN == getE().getType()) { // 客管投诉管理员和超级管理员
			return !context.hasAnyRole(getText("key.role.bs.advice.keguan"),
					getText("key.role.bc.admin"));
		} else { // 公司投诉管理员和超级管理员
			return !context.hasAnyRole(getText("key.role.bs.advice.gongsi"),
					getText("key.role.bc.admin"));
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(745)
				.setMinWidth(250).setHeight(500).setMinHeight(200);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (this.useFormPrint()) {
			// 添加打印按钮
			pageOption.addButton(this.getDefaultPrintButtonOption());
		}

		boolean readonly = this.isReadonly();
		// if (editable && !readonly) {
		// if(!getE().isNew()){
		// //生成通知单
		// pageOption.addButton(new ButtonOption(
		// "生成通知单", null,
		// "bc.caseAdviceForm.doGenNotice"));
		// //生成表格
		// pageOption.addButton(new ButtonOption(
		// "生成表格", null,
		// "bc.caseAdviceForm.doGenForm"));
		// }
		// if(Case4Advice.STATUS_ACTIVE != getE().getStatus() &&
		// !getE().isNew()){
		// //维护按钮
		// // pageOption.addButton(new ButtonOption(
		// // getText("维护"), null,
		// // "bc.caseAdviceForm.doMaintenance"));
		//
		// ButtonOption buttonOption = new
		// ButtonOption("维护",null,"bc.caseAdviceForm.doMaintenance");
		// buttonOption.put("id", "maintenance");
		// pageOption.addButton(buttonOption);
		// }
		// //特殊处理结案按钮
		// if(Case4Advice.STATUS_ACTIVE == getE().getStatus() &&
		// !getE().isNew()){
		// ButtonOption buttonOption = new
		// ButtonOption(getText("label.closefile"),null,"bc.caseAdviceForm.closefile");
		// buttonOption.put("id", "bcSaveDlgButton");
		// pageOption.addButton(buttonOption);
		// }
		// if(CaseBase.TYPE_COMPANY_COMPLAIN == getE().getType()
		// && Case4Advice.STATUS_ACTIVE == getE().getStatus()
		// && Case4Advice.HANDLE_STATUS_NEW == getE().getHandleStatus()
		// && !getE().isNew()){
		// //核准按钮
		// pageOption.addButton(new ButtonOption(
		// "核准", null,
		// "bc.caseAdviceForm.doManage"));
		// }
		// }

		if (!readonly) {
			if (editable) {
				// 添加默认的保存按钮
				pageOption.addButton(this.getDefaultSaveButtonOption());
				// 特殊处理结案按钮
				if (Case4Advice.STATUS_ACTIVE == getE().getStatus()
						&& !getE().isNew()) {
					ButtonOption buttonOption = new ButtonOption(
							getText("label.closefile"), null,
							"bc.caseAdviceForm.closefile");
					buttonOption.put("id", "bcSaveDlgButton");
					pageOption.addButton(buttonOption);
				}
			} else {
				if (!getE().isNew()) {
					// 生成通知单
					pageOption.addButton(new ButtonOption("生成通知单", null,
							"bc.caseAdviceForm.doGenNotice"));
					// //生成表格
					// pageOption.addButton(new ButtonOption(
					// "生成表格", null,
					// "bc.caseAdviceForm.doGenForm"));
				}
				if (!getE().isNew()) {
					// 维护按钮
					pageOption.addButton(new ButtonOption(getText("维护"), null,
							"bc.caseAdviceForm.doMaintenance"));
				}
				if (CaseBase.TYPE_COMPANY_COMPLAIN == getE().getType()
						&& Case4Advice.STATUS_ACTIVE == getE().getStatus()
						&& Case4Advice.HANDLE_STATUS_NEW == getE()
								.getHandleStatus() && !getE().isNew()) {
					// 核准按钮
					pageOption.addButton(new ButtonOption("核准", null,
							"bc.caseAdviceForm.doManage"));
				}
				// 特殊处理结案按钮
				if (Case4Advice.STATUS_ACTIVE == getE().getStatus()
						&& !getE().isNew()) {
					ButtonOption buttonOption = new ButtonOption(
							getText("label.closefile"), null,
							"bc.caseAdviceForm.closefile");
					buttonOption.put("id", "bcSaveDlgButton");
					pageOption.addButton(buttonOption);
				}

			}
		}
	}

	// 显示车辆,司机相关信息临时变量 //
	private String chargers;
	private Calendar birthdate;
	private String origin;
	private Calendar workDate;
	private String businessType;
	private Calendar registerDate;
	private Calendar scrapDate;

	public String getChargers() {
		return chargers;
	}

	public void setChargers(String chargers) {
		this.chargers = chargers;
	}

	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Calendar getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Calendar workDate) {
		this.workDate = workDate;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Calendar getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Calendar registerDate) {
		this.registerDate = registerDate;
	}

	public Calendar getScrapDate() {
		return scrapDate;
	}

	public void setScrapDate(Calendar scrapDate) {
		this.scrapDate = scrapDate;
	}

	@SuppressWarnings("static-access")
	@Override
	protected void afterCreate(Case4Advice entity) {
		super.afterCreate(entity);

		if (syncId != null || Integer.valueOf(type) == CaseBase.TYPE_COMPLAIN) {// 客管投诉
			this.getE().setType(CaseBase.TYPE_COMPLAIN);
		} else {// 公司投诉
			this.getE().setType(CaseBase.TYPE_COMPANY_COMPLAIN);
			this.getE().setHandleStatus(Case4Advice.HANDLE_STATUS_NEW);
			handlestatusesValue = this.getHandleStatues();
		}
		if (syncId != null) { // 判断同步id是否为空
			JiaoWeiADVICE jiaoWeiADVICE = this.jiaoWeiADVICEService
					.load(syncId);
			String carPlateNo = "";
			carPlateNo = jiaoWeiADVICE.getCarPlate().replaceAll("粤A.",
					carPlateNo);
			if (carPlateNo.length() > 0) {
				// 根据车牌号码查找carId
				findCarId(carPlateNo);
			}
			// 设置从交委同步的信息
			this.getE().setCaseNo(jiaoWeiADVICE.getSyncCode());
			this.getE().setReceiveDate(jiaoWeiADVICE.getReceiveDate());
			this.getE().setReceiveCode(jiaoWeiADVICE.getSyncCode());
			this.getE().setAdvisorName(jiaoWeiADVICE.getAdvisorName());
			this.getE().setPathFrom(jiaoWeiADVICE.getPathFrom());
			this.getE().setPathTo(jiaoWeiADVICE.getPathTo());
			this.getE().setRidingStartTime(jiaoWeiADVICE.getRidingTimeStart());
			this.getE().setRidingEndTime(jiaoWeiADVICE.getRidingTimeEnd());
			this.getE().setHappenDate(this.getE().getRidingStartTime());
			this.getE().setAdvisorPhone(jiaoWeiADVICE.getAdvisorPhone());
			if (jiaoWeiADVICE.getAdvisorSex().equals("男")) {
				this.getE().setAdvisorSex(CaseBase4AdviceAndPraise.SEX_MAN);
			} else if (jiaoWeiADVICE.getAdvisorSex().equals("女")) {
				this.getE().setAdvisorSex(CaseBase4AdviceAndPraise.SEX_WOMAN);
			}
			if (jiaoWeiADVICE.getAdvisorAge() != null) {
				this.getE().setAdvisorAge(
						jiaoWeiADVICE.getAdvisorAge().intValue());
			}
			this.getE().setAdvisorCert(jiaoWeiADVICE.getAdvisorCert());
			this.getE().setDriverFeature(jiaoWeiADVICE.getDriverChar());
			this.getE().setDetail(jiaoWeiADVICE.getContent());
			this.getE().setResult(jiaoWeiADVICE.getResult());
			this.getE().setSubject(jiaoWeiADVICE.getSubject());
			this.getE().setSubject2(jiaoWeiADVICE.getSubject2());
			if (jiaoWeiADVICE.getMachinePrice() != null
					&& StringUtil.isNumeric(jiaoWeiADVICE.getMachinePrice())) {
				this.getE().setMachinePrice(
						Float.parseFloat(jiaoWeiADVICE.getMachinePrice()));
			}
			this.getE().setTicket(jiaoWeiADVICE.getTicket());
			if (jiaoWeiADVICE.getCharge() != null
					&& StringUtil.isNumeric(jiaoWeiADVICE.getCharge())) {
				this.getE().setCharge(
						Float.parseFloat(jiaoWeiADVICE.getCharge()));
			}
			this.getE().setCarColor(jiaoWeiADVICE.getBusColor());
			// 设置来源
			this.getE().setSource(CaseBase.SOURCE_GENERATION);
			// 设置syncId
			this.getE().setSyncId(syncId);

		}

		if (carManId != null) {
			CarMan driver = this.carManService.load(carManId);
			List<Car> car = this.carService.selectAllCarByCarManId(carManId);
			if (car.size() == 1) {
				this.getE().setCarId(car.get(0).getId());
				this.getE().setCarPlate(
						car.get(0).getPlateType() + "."
								+ car.get(0).getPlateNo());
				this.getE().setMotorcadeId(car.get(0).getMotorcade().getId());
				this.getE().setMotorcadeName(
						car.get(0).getMotorcade().getName());
				this.getE().setCompany(car.get(0).getCompany());
				this.getE().setCharger(car.get(0).getCharger());

				this.chargers = formatChargers(car.get(0).getCharger());
				this.businessType = car.get(0).getBusinessType();
				this.registerDate = car.get(0).getRegisterDate();
				this.scrapDate = car.get(0).getScrapDate();

			} else if (car.size() > 1) {
				isMoreCar = true;
			} else {
				isNullCar = true;
			}
			this.getE().setDriverId(carManId);
			this.getE().setDriverName(driver.getName());
			this.getE().setDriverCert(driver.getCert4FWZG());
			this.getE().setDriverSex(driver.getSex());

			this.birthdate = driver.getBirthdate();
			this.origin = driver.getOrigin();
			this.workDate = driver.getWorkDate();
		}
		if (carId != null) {
			Car car = this.carService.load(carId);
			this.getE()
					.setCarPlate(car.getPlateType() + "." + car.getPlateNo());
			this.getE().setCarId(carId);
			this.getE().setMotorcadeId(car.getMotorcade().getId());
			this.getE().setMotorcadeName(car.getMotorcade().getName());
			this.getE().setCompany(car.getCompany());
			this.getE().setCharger(car.getCharger());

			this.chargers = formatChargers(car.getCharger());
			this.businessType = car.getBusinessType();
			this.registerDate = car.getRegisterDate();
			this.scrapDate = car.getScrapDate();

			List<CarMan> carMan = this.carManService
					.selectAllCarManByCarId(carId);

			if (carMan.size() == 1) {
				this.getE().setDriverName(carMan.get(0).getName());
				this.getE().setDriverId(carMan.get(0).getId());
				this.getE().setDriverCert(carMan.get(0).getCert4FWZG());
				this.getE().setDriverSex(carMan.get(0).getSex());

				this.birthdate = carMan.get(0).getBirthdate();
				this.origin = carMan.get(0).getOrigin();
				this.workDate = carMan.get(0).getWorkDate();
			} else if (carMan.size() > 1) {
				isMoreCarMan = true;
			} else {
				isNullCarMan = true;
			}
		}

		// 初始化信息
		this.getE().setAdviceType(CaseBase.TYPE_COMPLAIN);
		this.getE().setDriverSex(ActorDetail.SEX_MAN);
		this.getE().setCarColor("绿灰");
		this.getE().setStatus(CaseBase.STATUS_ACTIVE);
		this.getE().setUid(
				this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		// 自动生成自编号
		this.getE()
				.setCode(
						this.getIdGeneratorService().nextSN4Month(
								Case4Advice.KEY_CODE));

		// 来源
		if (syncId == null) { // 不是同步过来的信息设为自建
			this.getE().setSource(CaseBase.SOURCE_SYS);
		}
		sourceStr = getSourceStatuses().get(this.getE().getSource() + "");
	}

	/** 根据车牌号查找carId */
	public void findCarId(String carPlateNo) {
		if (carPlateNo.length() > 0) { // 判断车牌号是否为空
			Long tempCarId = this.carService.findcarIdByCarPlateNo(carPlateNo);
			this.carId = tempCarId;
		}
	}

	@Override
	protected void afterOpen(Case4Advice entity) {
		super.afterOpen(entity);

		this.carId = this.getE().getCarId();
		// 设置显示车辆,司机相关信息
		if (carId != null) {
			Car car = this.carService.load(carId);

			this.chargers = formatChargers(this.getE().getCharger());
			this.businessType = car.getBusinessType();
			this.registerDate = car.getRegisterDate();
			this.scrapDate = car.getScrapDate();

			List<CarMan> carMan = this.carManService
					.selectAllCarManByCarId(carId);
			if (carMan.size() > 0) {
				if (carMan.get(0).getBirthdate() != null) {
					this.birthdate = carMan.get(0).getBirthdate();
				}
				this.origin = carMan.get(0).getOrigin();
				this.workDate = carMan.get(0).getWorkDate();
			}
		}

		if (this.getE().getType() == CaseBase.TYPE_COMPANY_COMPLAIN) {// 公司投诉
			handlestatusesValue = this.getHandleStatues();
		}
		sourceStr = getSourceStatuses().get(this.getE().getSource() + "");

	}

	@Override
	public String edit() throws Exception {
		if (syncId != null) {// 根据syncId查找已存在CaseBase的记录
			CaseBase cb = this.caseBaseService.findCaseBaseBysyncId(syncId);
			this.setE(this.getCrudService().load(cb.getId()));
		} else {
			this.setE(this.getCrudService().load(this.getId()));
		}
		// 表单可选项的加载
		this.formPageOption = buildFormPageOption(true);
		// 初始化表单的其他配置
		this.initForm(true);
		if (this.getE().getType() == CaseBase.TYPE_COMPANY_COMPLAIN) {// 公司投诉
			handlestatusesValue = this.getHandleStatues();
		}

		sourceStr = getSourceStatuses().get(this.getE().getSource() + "");

		this.carId = this.getE().getCarId();
		// 设置显示车辆,司机相关信息
		if (carId != null) {
			Car car = this.carService.load(carId);

			this.chargers = formatChargers(this.getE().getCharger());
			this.businessType = car.getBusinessType();
			this.registerDate = car.getRegisterDate();
			this.scrapDate = car.getScrapDate();

			List<CarMan> carMan = this.carManService
					.selectAllCarManByCarId(carId);
			if (carMan.size() > 0) {
				if (carMan.get(0).getBirthdate() != null) {
					this.birthdate = carMan.get(0).getBirthdate();
				}
				this.origin = carMan.get(0).getOrigin();
				this.workDate = carMan.get(0).getWorkDate();
			}
		}

		return "form";
	}

	@Override
	public String save() throws Exception {

		SystemContext context = this.getSystyemContext();

		// 设置最后更新人的信息
		Case4Advice e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());

		// 设置结案信息
		if (e.getStatus() == CaseBase.STATUS_CLOSED) {
			e.setStatus(CaseBase.STATUS_CLOSED);
			e.setCloserId(context.getUserHistory().getId());
			e.setCloserName(context.getUserHistory().getName());
			e.setCloseDate(Calendar.getInstance(Locale.CHINA));
		}

		if (syncId != null) { // 处理相应的来源信息的状态
			SyncBase sb = this.syncBaseService.load(syncId);
			sb.setStatus(SyncBase.STATUS_GEN);
			this.beforeSave(e);
			// 保存并更新Sycn对象的状态
			this.caseAdviceService.save(e, sb);
			this.afterSave(e);
		} else {
			this.getCrudService().save(e);
		}

		return "saveSuccess";
	}

	/*
	 * 业务变更注释 public Json json; public String closefile(){ SystemContext context
	 * = this.getSystyemContext();
	 * 
	 * this.getE().setStatus(CaseBase.STATUS_CLOSED);
	 * this.getE().setCloserId(context.getUserHistory().getId());
	 * this.getE().setCloserName(context.getUserHistory().getName());
	 * this.getE().setCloseDate(Calendar.getInstance(Locale.CHINA));
	 * 
	 * DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); String closeDateStr =
	 * df.format(this.getE().getCloseDate().getTime());
	 * 
	 * json = new Json(); json.put("status", this.getE().getStatus());
	 * json.put("closeDate", closeDateStr); json.put("closeId",
	 * this.getE().getCloserId()); json.put("closeName",
	 * this.getE().getCloserName()); return "json"; }
	 * 
	 * public String getCarNManInfo(){ if(carId != null){
	 * //从页面ajax请求carId参数不为空时通过carManId查找关联司机信息
	 * this.caseBaseService.findCarManNameNCertCodeByCarId(carId); } if(carManId
	 * != null){ //从页面ajax请求carManId参数不为空时通过carId查找关联车辆信息
	 * this.caseBaseService.findCarPlateNCertCodeByCarManId(carManId); } return
	 * "json"; }
	 */

	public String selectCarMansInfo() {
		List<CarMan> drivers = this.carManService.selectAllCarManByCarId(carId);
		JsonArray jsons = new JsonArray();
		Json o;
		for (CarMan driver : drivers) {
			o = new Json();
			o.put("name", driver.getName());
			o.put("id", driver.getId());
			o.put("cert4FWZG", driver.getCert4FWZG());
			o.put("origin", driver.getOrigin());
			o.put("birthDate", calendarToString(driver.getBirthdate()));
			o.put("workDate", calendarToString(driver.getWorkDate()));
			// o.put("region", driver.getRegion());
			// o.put("drivingStatus", driver.getDrivingStatus());
			jsons.add(o);
		}
		json = jsons.toString();
		return "json";
	}

	public String selectSubject() {
		return "showdialog";
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		statusesValue = this.getCaseStatuses();
		sourcesValue = this.getSourceStatuses();
		// 表单可选项的加载
		initSelects();
	}

	// 表单可选项的加载
	public void initSelects() {
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		if (this.getE().getMotorcadeId() != null)
			OptionItem.insertIfNotExist(this.motorcadeList, this.getE()
					.getMotorcadeId().toString(), this.getE()
					.getMotorcadeName());

		// 加载可选责任列表
		this.allList = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.AD_DUTY, OptionConstants.AD_SOURCE,
						OptionConstants.IT_DEGREE, OptionConstants.BS_CERT,

				});
		// 可选责任列表
		this.dutyList = allList.get(OptionConstants.AD_DUTY);
		// 可选程度列表
		this.degreeList = allList.get(OptionConstants.IT_DEGREE);
		// 可选没收证件列表
		this.certList = allList.get(OptionConstants.BS_CERT);
		// 可选投诉建议来源
		this.sourceList = allList.get(OptionConstants.AD_SOURCE);

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
	 * 获取Entity的处理状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getHandleStatues() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(Case4Advice.HANDLE_STATUS_NEW),
				getText("runcase.handle.status.new"));
		statuses.put(String.valueOf(Case4Advice.HANDLE_STATUS_DONE),
				getText("runcase.handle.status.done"));
		return statuses;
	}

	/**
	 * 获取Entity的来源值转换列表
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

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public String calendarToString(Calendar object) {
		if (null != object && object.toString().length() > 0) {
			Calendar calendar = object;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = df.format(calendar.getTime());
			return dateStr;
		} else {
			return "";
		}
	}

	/**
	 * 组装责任人姓名
	 * 
	 * @param chargers
	 * @return
	 */
	public String formatChargers(String chargersStr) {
		String chargers = "";
		if (null != chargersStr && chargersStr.trim().length() > 0) {
			String[] chargerAry = chargersStr.split(";");
			for (int i = 0; i < chargerAry.length; i++) {
				chargers += chargerAry[i].split(",")[0];
				if ((i + 1) < chargerAry.length)
					chargers += ",";
			}
		}
		return chargers;
	}

	// ---发起流程---开始---
	public String tdIds;

	public String startFlow() {
		Json json = new Json();
		// 去掉最后一个逗号
		String[] _ids = tdIds.substring(0, tdIds.lastIndexOf(",")).split(",");
		String count = this.caseAdviceService.doStartFlow(
				getText("runcase.startFlow.key4ComplainHandle"),
				StringUtils.stringArray2LongArray(_ids));
		if (count.equals("0")) {
			json.put("success", false);
			json.put("msg", getText("runcase.startFlow.success.false"));
		} else {
			json.put("success", true);
			// json.put("msg", getText("runcase.startFlow.success.true"));
			json.put("msg", getText("成功发起" + count + "条交客管投诉处理流程"));
		}
		this.json = json.toString();
		return "json";
	}

	// --流程结束---

	// 根据司机ID和事发时间查找司机在事发日期向前推算一年内的安全服务信息
	public String getSecurityServiceInfoByCarManId() {
		Calendar happenDate = DateUtils.getCalendar(this.happenDate);

		this.json = this.caseAdviceService.getCaseTrafficInfoByCarManId(
				carManId, happenDate);
		return "json";

	}

}
