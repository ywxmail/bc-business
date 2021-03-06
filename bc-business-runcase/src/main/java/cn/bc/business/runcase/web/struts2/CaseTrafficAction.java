/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

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
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.runcase.service.CaseBaseService;
import cn.bc.business.runcase.service.CaseTrafficService;
import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.business.sync.service.JiaoWeiJTWFService;
import cn.bc.business.sync.service.JinDunJTWFService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.sync.domain.SyncBase;
import cn.bc.sync.service.SyncBaseService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;
import cn.bc.workflow.service.WorkflowModuleRelationService;

/**
 * 交通违章Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseTrafficAction extends
		FileEntityAction<Long, Case4InfractTraffic> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private WorkflowModuleRelationService workflowModuleRelationService;

	private Long carId;
	private Long carManId;
	private Long syncId; // 同步ID
	private String syncIds; // 多个同步ID;
	public boolean isMoreCar; // 是否存在多个车
	public boolean isMoreCarMan; // 是否存在多个司机
	public boolean isNullCar; // 此司机现在没有驾驶任何车辆
	public boolean isNullCarMan; // 此车辆现在没有任何司机驾驶
	private CaseTrafficService caseTrafficService;
	private CaseBaseService caseBaseService;
	private MotorcadeService motorcadeService;
	private OptionService optionService;
	private CarManService carManService;
	private CarService carService;
	private SyncBaseService syncBaseService; // 平台同步基类Serivce
	private JiaoWeiJTWFService jiaoWeiJTWFService; // 交委Service
	private JinDunJTWFService jinDunJTWFService; // 金盾Service
	private String sourceStr;
	private String chargers;
	public String happenDate;// 违法日期

	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> dutyList; // 可选责任列表
	public List<Map<String, String>> properitesList; // 可选性质列表

	public Map<String, String> statusesValue;
	public Map<String, String> sourcesValue;
	private Map<String, List<Map<String, String>>> allList;
	public List<Map<String, Object>> carTrafficHandleFlowList; // 交通违法流程集合
	
	public AttachWidget attachsUI;

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

	public String getChargers() {
		return chargers;
	}

	public void setChargers(String chargers) {
		this.chargers = chargers;
	}

	@Autowired
	public void setWorkflowModuleRelationService(
			WorkflowModuleRelationService workflowModuleRelationService) {
		this.workflowModuleRelationService = workflowModuleRelationService;
	}

	@Autowired
	public void setCaseTrafficService(CaseTrafficService caseTrafficService) {
		this.caseTrafficService = caseTrafficService;
		this.setCrudService(caseTrafficService);
	}

	@Autowired
	public void setCaseBaseService(CaseBaseService caseBaseService) {
		this.caseBaseService = caseBaseService;
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

	@Autowired
	public void setSyncBaseService(SyncBaseService syncBaseService) {
		this.syncBaseService = syncBaseService;
	}

	@Autowired
	public void setJiaoWeiJTWFService(JiaoWeiJTWFService jiaoWeiJTWFService) {
		this.jiaoWeiJTWFService = jiaoWeiJTWFService;
	}

	@Autowired
	public void setJinDunJTWFService(JinDunJTWFService jinDunJTWFService) {
		this.jinDunJTWFService = jinDunJTWFService;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("status", Direction.Asc).add("fileDate",
				Direction.Desc);
	}

	// 复写搜索URL方法
	protected String getEntityConfigName() {
		return "caseTraffic";
	}

	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractTraffic"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(690)
				.setMinWidth(250).setHeight(460).setMinHeight(200);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();

		if (this.useFormPrint()) {
			// 添加打印按钮
			pageOption.addButton(this.getDefaultPrintButtonOption());
		}

		if (editable && !readonly) {
			// 特殊处理结案按钮
			if (Case4InfractTraffic.STATUS_CLOSED != getE().getStatus()
					&& !getE().isNew()) {
				ButtonOption buttonOption = new ButtonOption(
						getText("label.closefile"), null,
						"bc.caseTrafficForm.closefile");
				buttonOption.put("id", "bcSaveDlgButton");
				pageOption.addButton(buttonOption);
			}
			if (CaseBase.STATUS_ACTIVE == getE().getStatus()) {
				// 添加默认的保存按钮
				pageOption.addButton(this.getDefaultSaveButtonOption());
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected void afterCreate(Case4InfractTraffic entity) {
		super.afterCreate(entity);

		if (syncId != null) { // 判断同步id是否为空
			SyncBase syncBase = this.syncBaseService.load(syncId);
			if (syncBase.getSyncType().equals(JinDunJTWF.KEY_TYPE)) { // 判断是否金盾网同步
				JinDunJTWF jinDunJTWF = this.jinDunJTWFService.load(syncId);
				// 根据车牌号码查找carId
				findCarId(jinDunJTWF.getCarPlateNo());
				this.getE().setCaseNo(jinDunJTWF.getSyncCode());
				this.getE().setAddress(jinDunJTWF.getAddress());
				this.getE().setHappenDate(jinDunJTWF.getHappenDate());
				this.getE().setJeom(jinDunJTWF.getJeom());
				this.getE().setFrom(
						getText("runcase.jindun") + jinDunJTWF.getSource());
			} else { // 交委同步
				JiaoWeiJTWF jiaoWeiJTWF = this.jiaoWeiJTWFService.load(syncId);
				findCarId(jiaoWeiJTWF.getCarPlateNo());
				this.getE().setCaseNo(jiaoWeiJTWF.getSyncCode());
				this.getE().setSubject(jiaoWeiJTWF.getContent());
				this.getE().setJeom(jiaoWeiJTWF.getJeom());
				this.getE().setHappenDate(jiaoWeiJTWF.getHappenDate());
				this.getE().setFrom(getText("runcase.jiaowei"));
				// 设置金盾网违法地地址
				String address = this.jiaoWeiJTWFService.getJinDunAddress(
						jiaoWeiJTWF.getSyncCode(), jiaoWeiJTWF.getCarPlateNo(),
						jiaoWeiJTWF.getHappenDate());
				if (address != null) {
					String[] vvs = address.split(";");
					this.getE().setAddress(vvs[0]);
				}

				/**
				 * TODO //根据违章顺序号查找金盾网交通违章记录
				 * if(jiaoWeiJTWF.getSyncCode().length() > 0){ JinDunJTWF
				 * jinDunJTWF =
				 * this.jinDunJTWFService.findJinDunJTWFBySyscCode(jiaoWeiJTWF
				 * .getSyncCode()); if(null != jinDunJTWF &&
				 * jinDunJTWF.getAddress() != null &&
				 * jinDunJTWF.getAddress().trim().length() >
				 * 0){//判断此记录是否存在并且违章地点不为空
				 * this.getE().setAddress(jinDunJTWF.getAddress());//设置违章时间 } }
				 **/

			}
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
				this.getE().setCharger(car.get(0).getCharger());

				// 组装责任人
				this.chargers = formatChargers(car.get(0).getCharger());

			} else if (car.size() > 1) {
				isMoreCar = true;
			} else {
				isNullCar = true;
			}
			this.getE().setDriverId(carManId);
			this.getE().setDriverName(driver.getName());
			this.getE().setDriverCert(driver.getCert4FWZG());
		}
		if (carId != null) {
			Car car = this.carService.load(carId);
			this.getE()
					.setCarPlate(car.getPlateType() + "." + car.getPlateNo());
			this.getE().setCarId(carId);
			this.getE().setMotorcadeId(car.getMotorcade().getId());
			this.getE().setMotorcadeName(car.getMotorcade().getName());
			this.getE().setCharger(car.getCharger());

			// 组装责任人
			this.chargers = formatChargers(car.getCharger());

			List<CarMan> carMan = this.carManService
					.selectAllCarManByCarId(carId);
			if (carMan.size() == 1) {
				this.getE().setDriverName(carMan.get(0).getName());
				this.getE().setDriverId(carMan.get(0).getId());
				this.getE().setDriverCert(carMan.get(0).getCert4FWZG());
			} else if (carMan.size() > 1) {
				isMoreCarMan = true;
			} else {
				isNullCarMan = true;
			}
		}

		// 初始化信息
		this.getE().setUid(
				this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		// 自动生成自编号
		this.getE().setCode(
				this.getIdGeneratorService().nextSN4Month(
						Case4InfractTraffic.KEY_CODE));
		this.getE().setType(CaseBase.TYPE_INFRACT_TRAFFIC);
		this.getE().setStatus(CaseBase.STATUS_ACTIVE);
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
	public String edit() throws Exception {
		if (syncId != null) {// 根据syncId查找已存在CaseBase的记录
			CaseBase cb = this.caseBaseService.findCaseBaseBysyncId(syncId);
			this.setE(this.getCrudService().load(cb.getId()));
		} else {
			this.setE(this.getCrudService().load(this.getId()));
		}
		// 表单可选项的加载
		sourceStr = getSourceStatuses().get(this.getE().getSource() + "");
		this.formPageOption = buildFormPageOption(true);
		// 初始化表单的其他配置
		this.initForm(true);
		// 组装责任人
		this.chargers = formatChargers(this.getE().getCharger());
		return "form";
	}

	@Override
	protected void afterOpen(Case4InfractTraffic entity) {
		super.afterOpen(entity);
		sourceStr = getSourceStatuses().get(this.getE().getSource() + "");
	}

	@Override
	public String save() throws Exception {
		SystemContext context = this.getSystyemContext();
		// 设置最后更新人的信息
		Case4InfractTraffic e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());

		// 设置结案信息
		if (e.getStatus() == 1) {
			e.setStatus(CaseBase.STATUS_CLOSED);
			e.setCloserId(context.getUserHistory().getId());
			e.setCloserName(context.getUserHistory().getName());
			e.setCloseDate(Calendar.getInstance(Locale.CHINA));
		}

		SyncBase sb = null;
		if (syncId != null) { // 处理相应的来源信息的状态
			sb = this.syncBaseService.load(syncId);
			sb.setStatus(SyncBase.STATUS_GEN);
			this.beforeSave(e);
			// 保存并更新Sycn对象的状态
			e = this.caseTrafficService.save(e, sb);
			this.afterSave(e);
		} else {
			this.getCrudService().save(e);
		}

		return "saveSuccess";
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		statusesValue = this.getCaseStatuses();
		sourcesValue = this.getSourceStatuses();
		// 表单可选项的加载
		initSelects();
		Case4InfractTraffic c = this.getE();
		if (!c.isNew()) {
			carTrafficHandleFlowList = this.workflowModuleRelationService
					.findList(c.getId(), Case4InfractTraffic.ATTACH_TYPE, new String[]{"subject"});
		}
		
		this.attachsUI=this.buildAttachsUI(this.getE().isNew()
				, this.isReadonly()||!editable||this.getE().getStatus()==CaseBase.STATUS_CLOSED
				, Case4InfractTraffic.ATTACH_TYPE, this.getE().getUid());

	}

	// ========批量生成交通违法代码开始========
	public String getSyncIds() {
		return syncIds;
	}

	public void setSyncIds(String syncIds) {
		this.syncIds = syncIds;
	}

	public String doPatchSave() throws Exception {
		List<Case4InfractTraffic> traffics = this.caseTrafficService
				.doPatchSave(syncIds);
		Json json = new Json();
		json.put("msg", getText("runcase.doPatchSave.success") + " 共生成 "
				+ traffics.size() + " 条交通违章信息");
		this.json = json.toString();
		return "json";
	}

	// ---发起流程---开始---
	public String tdIds;

	public String startFlow() {
		Json json = new Json();
		// 去掉最后一个逗号
		String[] _ids = tdIds.substring(0, tdIds.lastIndexOf(",")).split(",");
		String count = this.caseTrafficService.doStartFlow(
				getText("runcase.startFlow.key4CarTrafficHandle"),
				StringUtils.stringArray2LongArray(_ids));
		if (count.equals("0")) {
			json.put("success", false);
			json.put("msg", getText("runcase.startFlow.success.false"));
		} else {
			json.put("success", true);
			// json.put("msg", getText("runcase.startFlow.success.true"));
			json.put("msg", getText("成功发起" + count + "条交通违法处理流程"));
		}
		this.json = json.toString();
		return "json";
	}

	// ---发起流程结束---

	// 根据司机ID和违法时间查找司机在该违法周期内所有的违法信息
	public String getCaseTrafficInfoByCarManId() {
		Calendar happenDate = DateUtils.getCalendar(this.happenDate);

		this.json = this.caseTrafficService.getCaseTrafficInfoByCarManId(
				carManId, happenDate);
		return "json";

	}

	// ========批量生成交通违法代码结束========

	public String selectCarMansInfo() {
		List<CarMan> drivers = this.carManService.selectAllCarManByCarId(carId);
		JsonArray jsons = new JsonArray();
		Json o;
		for (CarMan driver : drivers) {
			o = new Json();
			o.put("name", driver.getName());
			o.put("id", driver.getId());
			o.put("cert4FWZG", driver.getCert4FWZG());
			jsons.add(o);
		}
		json = jsons.toString();
		return "json";
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
						OptionConstants.IT_DUTY, OptionConstants.IT_PROPERITES });
		// 可选责任列表
		this.dutyList = allList.get(OptionConstants.IT_DUTY);
		// 可选性质列表
		this.properitesList = allList.get(OptionConstants.IT_PROPERITES);
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
		statuses.put(String.valueOf(CaseBase.STATUS_HANDLING),
				getText("runcase.select.status.handling"));
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

}
