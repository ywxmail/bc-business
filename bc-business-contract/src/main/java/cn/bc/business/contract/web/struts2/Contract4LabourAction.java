/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.business.contract.service.Contract4LabourService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 司机劳动合同Action
 * 
 * @author wis.ho
 * 
 */
/**
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Contract4LabourAction extends
		FileEntityAction<Long, Contract4Labour> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	private Contract4LabourService contract4LabourService;
	private OptionService optionService;
	private AttachService attachService;
	private Long driverId;
	private Long carId;
	public String certCode; // 证件编号
	public AttachWidget attachsUI;
	public Map<String, String> statusesValue;
	public Map<String, Object> certInfoMap; // 证件信息map
	public Map<String, Object> carInfoMap; // 车辆信息map
	public Map<String, Object> carManInfoMap; // 司机信息map
	public List<Map<String, Object>> infoList;

	// public List<Map<String, String>> businessTypeList; // 可选营运性质列表
	public List<Map<String, String>> insurancetypeList; // 可选营运性质列表
	public List<Map<String, String>> houseTypeList; // 可选户口类型列表
	public List<Map<String, String>> buyUnitList; // 可选购买单位列表

	public boolean isMoreCar; // 是否存在多辆车
	public boolean isMoreCarMan; // 是否存在多个司机
	public boolean isNullCar; // 此司机没有车
	public boolean isNullCarMan; // 此车没有司机
	public boolean isExistContract; // 是否存在合同
	public boolean isSupply; // 是否补录
	public boolean isDoMaintenance = false;// 是否进行维护操作
	public Json json;

	public boolean isDoChangeCar = false;// 是否执行转车操作

	// public CertService certService;

	@Autowired
	public void setContract4LabourService(
			Contract4LabourService contract4LabourService) {
		this.contract4LabourService = contract4LabourService;
		this.setCrudService(contract4LabourService);
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long carManId) {
		this.driverId = carManId;
	}

	public boolean isSupply() {
		return isSupply;
	}

	public void setSupply(boolean isSupply) {
		this.isSupply = isSupply;
	}

	@Override
	public boolean isReadonly() {
		// 劳动合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
	}

	private boolean isEntering() {
		// 劳动合同录入管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context
				.hasAnyRole(getText("key.role.bs.contract4labour.entering"));
	}

	// ## 显示最新的车辆司机信息 ##

	private Calendar registerDate; // 车辆登记日期
	private String bsType; // 车辆营运性质
	private String certNo; // 司机服务资格证
	private Calendar birthDate; // 司机出生日期
	private String origin; // 司机籍贯
	private String certIdentity; // 司机身份证

	public Calendar getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Calendar registerDate) {
		this.registerDate = registerDate;
	}

	public String getBsType() {
		return bsType;
	}

	public void setBsType(String bsType) {
		this.bsType = bsType;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public Calendar getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Calendar birthDate) {
		this.birthDate = birthDate;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getCertIdentity() {
		return certIdentity;
	}

	public void setCertIdentity(String certIdentity) {
		this.certIdentity = certIdentity;
	}

	// 执行转车，续约，操作时需要的参数
	public Long contractId;
	public int opType;
	public String stopDate;// 合同实际结束日期

	// 新建表单
	public String create() throws Exception {

		// 如果是转车操作就复制上一份合同
		if (opType == Contract.OPTYPE_CHANGECAR
				|| opType == Contract.OPTYPE_RENEW) {
			Contract4Labour newContract = this.contract4LabourService
					.doCopyContract(contractId, this.opType);
			// 初始化E
			this.setE(newContract);

		} else {
			// 初始化E
			this.setE(createEntity());

		}

		// 初始化表单的配置信息
		this.formPageOption = buildFormPageOption(true);

		// 初始化表单的其他配置
		this.initForm(true);

		this.afterCreate(this.getE());

		return "form";
	}

	@Override
	protected void afterCreate(Contract4Labour entity) {

		super.afterCreate(entity);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;

		if (carId != null && driverId == null) {// 车辆页签中的新建

			// 根据carId查找车辆以及司机id
			carInfoMap = this.contract4LabourService.findCarByCarId(carId);
			infoList = this.contract4LabourService
					.selectRelateCarManByCarId(carId);

			if (infoList.size() == 1) {
				// 填写司机信息forceReadOnly
				driverId = Long.valueOf(isNullObject(infoList.get(0).get(
						"driver_id")));
				entity.setExt_str2(isNullObject(infoList.get(0).get("name")));
				entity.setSex(Integer.valueOf(infoList.get(0).get("sex") + ""));
				this.certNo = isNullObject(infoList.get(0).get("cert_fwzg"));
				this.certIdentity = isNullObject(infoList.get(0).get(
						"cert_identity"));
				this.origin = isNullObject(infoList.get(0).get("origin"));
				entity.setHouseType(isNullObject(infoList.get(0).get(
						"house_type")));

				if (getDateToString(infoList.get(0).get("birthdate")).length() > 0) {
					try {
						date = sdf.parse(infoList.get(0).get("birthdate") + "");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					birthDate = Calendar.getInstance();
					birthDate.setTime(date); // 出生日期
				}
				//
				// entity.setAge(
				// Integer.valueOf(getBirthDateToString(infoList.get(0)
				// .get("birthdate"))));
			} else if (infoList.size() > 1) {
				isMoreCarMan = true;
			} else {
				isNullCarMan = true;
			}
			// 填写车辆信息
			entity.setExt_str1(isNullObject(carInfoMap.get("plate_type")) + "."
					+ isNullObject(carInfoMap.get("plate_no")));
			if (getDateToString(carInfoMap.get("register_date")).length() > 0) {
				try {
					date = sdf.parse(carInfoMap.get("register_date") + "");
				} catch (ParseException e) {
					e.printStackTrace();
				}
				registerDate = Calendar.getInstance();
				registerDate.setTime(date); // 车辆登记日期
			}
			this.bsType = isNullObject(carInfoMap.get("bs_type"));
		}

		if (driverId != null && carId == null) {// 司机页签中的新建
			if (isSupply == false
					&& (opType != Contract.OPTYPE_RENEW && opType != Contract.OPTYPE_CHANGECAR)) { // 新建
				// 查找此司机是否存在劳动合同,若存在前台提示
				isExistContract = this.contract4LabourService
						.isExistContractByDriverId(driverId);
			}

			if (isExistContract == false) {
				// 根据carManId查找司机以及车辆id
				carManInfoMap = this.contract4LabourService
						.findCarManByCarManId(driverId);
				infoList = this.contract4LabourService
						.selectRelateCarByCarManId(driverId);
				if (infoList.size() == 1) {
					carId = Long.valueOf(isNullObject(infoList.get(0).get(
							"car_id")));
					// 填写车辆信息
					entity.setExt_str1(isNullObject(infoList.get(0).get(
							"plate_type"))
							+ "."
							+ isNullObject(infoList.get(0).get("plate_no")));
					if (getDateToString(infoList.get(0).get("register_date"))
							.length() > 0) {
						try {
							date = sdf.parse(infoList.get(0).get(
									"register_date")
									+ "");
						} catch (ParseException e) {
							e.printStackTrace();
						}
						registerDate = Calendar.getInstance();
						registerDate.setTime(date); // 车辆登记日期
					}
					this.bsType = isNullObject(infoList.get(0).get("bs_type"));
				} else if (infoList.size() > 1) {
					isMoreCar = true;
				} else {
					isNullCar = true;
				}
				// 填写司机信息
				entity.setExt_str2(isNullObject(carManInfoMap.get("name")));
				entity.setSex(Integer.valueOf(carManInfoMap.get("sex") + ""));
				this.certNo = isNullObject(carManInfoMap.get("cert_fwzg"));
				this.certIdentity = isNullObject(carManInfoMap
						.get("cert_identity"));
				this.origin = isNullObject(carManInfoMap.get("origin"));
				entity.setHouseType(isNullObject(carManInfoMap
						.get("house_type")));

				if (getDateToString(carManInfoMap.get("birthdate")).length() > 0) {
					try {
						date = sdf.parse(carManInfoMap.get("birthdate") + "");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					birthDate = Calendar.getInstance();
					birthDate.setTime(date); // 出生日期
				}

				// if(carManInfoMap.get("birthdate") != null){
				// entity.setAge(
				// Integer.valueOf(getBirthDateToString(carManInfoMap
				// .get("birthdate"))));
				// }
			}
		}
		// 非转车，续约的情况下
		if (opType != Contract.OPTYPE_RENEW
				&& opType != Contract.OPTYPE_CHANGECAR) {
			if (this.isSupply == false) { // 新建
				entity.setMain(Contract.MAIN_NOW);
				entity.setVerMajor(Contract.MAJOR_DEFALUT);
				entity.setVerMinor(Contract.MINOR_DEFALUT);
				entity.setOpType(Contract.OPTYPE_CREATE);
			} else { // 补录
				entity.setMain(Contract.MAIN_HISTORY);
				entity.setVerMajor(Contract.SUPPLY_MAJOR_DEFALUT);
				entity.setVerMinor(Contract.SUPPLY_MINOR_DEFALUT);
				entity.setStatus(Contract.STATUS_LOGOUT);
				entity.setOpType(Contract.OPTYPE_CREATE);
			}

			// 自动生成UID
			entity.setUid(this.getIdGeneratorService().next(
					Contract4Labour.KEY_UID));
			// 自动生成批号：与uid相同
			entity.setPatchNo(entity.getUid());
			entity.setType(Contract.TYPE_LABOUR);
			entity.setInsuranceType(getText("contract.wujin"));
			entity.setBuyUnit(getText("contract.baocheng"));
			entity.setIdentityCards(true);
			entity.setHealthForm(true);
			entity.setPhoto(true);
			// 构建附件控件
			attachsUI = buildAttachsUI(true, false);

		} else {
			// 设置附件
			attachsUI = buildAttachsUI(false, false);
		}
		// 自动生成合同编号
		entity.setCode(this.getIdGeneratorService().nextSN4Month(
				Contract4Labour.KEY_CODE));

		entity.setStatus(BCConstants.STATUS_DRAFT);

	}

	@Override
	protected void afterEdit(Contract4Labour entity) {
		// == 合同维护处理
		// 构建附件控件
		attachsUI = buildAttachsUI(false, false);

		// 组装车辆和司机信息
		setupCarAndDriverInfo();

		// 将次版本号加1
		entity.setVerMinor(entity.getVerMinor() + 1);

		// 操作类型设置为维护
		entity.setOpType(Contract.OPTYPE_MAINTENANCE);
	}

	@Override
	protected void afterOpen(Contract4Labour entity) {
		// 构建附件控件
		attachsUI = buildAttachsUI(false, true);

		// 组装车辆和司机信息
		setupCarAndDriverInfo();
	}

	/**
	 * 组装车辆和司机信息
	 */
	private void setupCarAndDriverInfo() {
		// 根据合同id查找车辆id和司机id
		carId = this.contract4LabourService.findCarIdByContractId(this.getE()
				.getId());
		driverId = this.contract4LabourService.findCarManIdByContractId(this
				.getE().getId());

		// 根据carId查找车辆以及司机id
		carInfoMap = this.contract4LabourService.findCarByCarId(carId);

		// 根据carManId查找司机以及车辆id
		carManInfoMap = this.contract4LabourService
				.findCarManByCarManId(driverId);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;

		// 车辆信息
		if (getDateToString(carInfoMap.get("register_date")).length() > 0) {
			try {
				date = sdf.parse(carInfoMap.get("register_date") + "");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			registerDate = Calendar.getInstance();
			registerDate.setTime(date); // 车辆登记日期
		}
		this.bsType = isNullObject(carInfoMap.get("bs_type")); // 营运性质

		// 司机信息
		this.certNo = isNullObject(carManInfoMap.get("cert_fwzg")); // 资格证
		this.certIdentity = isNullObject(carManInfoMap.get("cert_identity")); // 身份证
		this.origin = isNullObject(carManInfoMap.get("origin")); // 籍贯

		if (getDateToString(carManInfoMap.get("birthdate")).length() > 0) {
			try {
				date = sdf.parse(carManInfoMap.get("birthdate") + "");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			birthDate = Calendar.getInstance();
			birthDate.setTime(date); // 出生日期
		}
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 状态列表
		statusesValue = this.getBSStatuses3();

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.LB_INSURANCETYPE,
						OptionConstants.CARMAN_HOUSETYPE,
						OptionConstants.LB_BUYUNIT });

		// 加载可选营运性质列表
		// this.businessTypeList = optionItems
		// .get(OptionConstants.CAR_BUSINESS_NATURE);
		// 加载可选社保险种列表
		this.insurancetypeList = optionItems
				.get(OptionConstants.LB_INSURANCETYPE);
		OptionItem.insertIfNotExist(this.insurancetypeList, this.getE()
				.getInsuranceType(), null);
		// 加载可选户口类型列表
		this.houseTypeList = optionItems.get(OptionConstants.CARMAN_HOUSETYPE);
		OptionItem.insertIfNotExist(this.houseTypeList, null, this.getE().getHouseType());
		
		// 加载可选购买单位列表
		this.buyUnitList = optionItems.get(OptionConstants.LB_BUYUNIT);
		OptionItem.insertIfNotExist(this.buyUnitList, this.getE().getBuyUnit(),
				null);
	}

	@Override
	public String save() throws Exception {
		// 如果Pid不为空，且为新建的则该合同执行的是转车或续约操作
		SystemContext context = this.getSystyemContext();
		if (this.getE().isNew() && this.getE().getPid() != null) {
			Contract4Labour e = this.getE();
			// 设置创建人
			e.setAuthor(context.getUserHistory());
			e.setFileDate(Calendar.getInstance());

			// 获取旧合同id
			Long fromContractId = e.getPid();
			Contract newContract = null;
			String msg = "";
			json = new Json();
			// 合同实际结束日期
			Calendar stopDate4Charger = DateUtils.getCalendar(this.stopDate);

			newContract = this.contract4LabourService.doOperate(carId,
					this.getE(), fromContractId, stopDate4Charger);
			if (e.getOpType() == Contract.OPTYPE_RENEW) {// 续约
				msg = getText("contract4Labour.renew.success");
			} else if (e.getOpType() == Contract.OPTYPE_CHANGECAR) {// 转车
				msg = getText("contract4Labour.changeCar.success");
			}
			json.put("id", newContract.getId());
			json.put("oldId", fromContractId);
			json.put("success", true);
			json.put("msg", msg);
			return "json";
		} else {
			// 正常操作
			json = new Json();
			Contract4Labour e = this.getE();
			Long carManId = null;
			// 保存之前检测社保号是否唯一:仅在新建时检测,补录不检测
			if (this.isSupply == false && e.getId() == null) {
				carManId = this.contract4LabourService.checkInsurCodeIsExist(
						e.getId(), e.getInsurCode());
			}
			if (carManId != null) {
				json.put("success", false);
				json.put("msg", getText("contract4Labour.insurCode.exist2"));
				return "json";
			} else {
				this.beforeSave(e);

				e.setModifier(context.getUserHistory());
				e.setModifiedDate(Calendar.getInstance());

				this.contract4LabourService.save(e, this.getCarId(),
						this.getDriverId());

				this.afterSave(e);

				json.put("id", e.getId());
				json.put("success", true);
				json.put("msg", getText("form.save.success"));
				return "json";
			}
		}
	}

	// ========判断经济合同自编号唯一代码开始========
	private Long excludeId;
	private String insurCode;

	public Long getExcludeId() {
		return excludeId;
	}

	public void setExcludeId(Long excludeId) {
		this.excludeId = excludeId;
	}

	public String getInsurCode() {
		return insurCode;
	}

	public void setInsurCode(String insurCode) {
		this.insurCode = insurCode;
	}

	public String checkInsurCodeIsExist() {
		json = new Json();
		Long carManId = this.contract4LabourService.checkInsurCodeIsExist(
				this.excludeId, this.insurCode);
		if (carManId != null) {
			json.put("carManId", carManId);
			json.put("isExist", "true"); // 存在重复自编号
			json.put("msg", getText("contract4Labour.insurCode.exist"));
		} else {
			json.put("isExist", "false");
		}
		return "json";
	}

	// ========判断经济合同自编号唯一代码结束========

	private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = Contract4Labour.KEY_UID;
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(isFlashUpload());
		attachsUI.addClazz("formAttachs");
		if (!isNew)
			attachsUI.addAttach(this.attachService.findByPtype(ptype, this
					.getE().getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));

		// 只读控制
		attachsUI.setReadOnly(forceReadonly ? true : this.isReadonly());
		return attachsUI;
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {

		// 特殊处理的部分
		if (!this.isReadonly()) {// 有权限
			if (this.getE().getStatus() != BCConstants.STATUS_DRAFT
					&& (opType != Contract.OPTYPE_RENEW && opType != Contract.OPTYPE_CHANGECAR)) {// 编辑状态显示保存按钮
				// 非草稿状态下双击
				if (!isDoMaintenance
						&& this.getE().getMain() == Contract.MAIN_NOW
						&& this.getE().getStatus() != Contract.STATUS_LOGOUT) {
					pageOption.addButton(new ButtonOption(
							getText("contract4Labour.optype.maintenance"),
							null, "bc.contract4LabourForm.doMaintenance"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Labour.optype.changeCar"), null,
							"bc.contract4LabourForm.doChangeCar"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Labour.optype.renew"), null,
							"bc.contract4LabourForm.doRenew"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Labour.optype.resign"), null,
							"bc.contract4LabourForm.doResign"));
				}
			} else {
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.contract4LabourForm.save"));
				pageOption.addButton(new ButtonOption(
						getText("label.warehousing"), null,
						"bc.contract4LabourForm.warehousing"));

			}
			// 维护操作
			if (isDoMaintenance) {
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.contract4LabourForm.save"));
				pageOption.addButton(new ButtonOption(
						getText("label.saveAndClose"), null,
						"bc.contract4LabourForm.saveAndClose"));

			}
		}
		// 如果有录入权限的就有保存按钮
		if (!this.isEntering()
				&& this.getE().getStatus() == BCConstants.STATUS_DRAFT) {
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.contract4LabourForm.save"));

		}

	}

	@Override
	protected Contract4Labour createEntity() {
		Contract4Labour c = super.createEntity();
		c.setStatus(BCConstants.STATUS_DRAFT);
		return c;
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {

		PageOption pageOption = new PageOption().setWidth(740).setHeight(490);

		if (this.useFormPrint())
			pageOption.setPrint("default.form");

		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.put("readonly", readonly);
			// 如果有录入权限且状态为草稿的可以进行修改
		} else if (this.getE().getStatus() == BCConstants.STATUS_DRAFT
				&& !this.isEntering()) {
			pageOption.put("readonly", false);
		} else {
			pageOption.put("readonly", true);
		}

		// 添加按钮
		buildFormPageButtons(pageOption, editable);

		return pageOption;
	}

	public String certInfo() {
		// 根据carManId查找司机以及车辆id
		carManInfoMap = this.contract4LabourService
				.findCarManByCarManId(driverId);

		json = new Json();
		if (carManInfoMap != null && carManInfoMap.size() > 0) {
			json.put("sex", Integer.valueOf(carManInfoMap.get("sex") + ""));
			json.put("cert_fwzg", isNullObject(carManInfoMap.get("cert_fwzg")));
			json.put("certIdentity",
					isNullObject(carManInfoMap.get("cert_identity")));
			json.put("origin", isNullObject(carManInfoMap.get("origin")));
			json.put("house_type",
					isNullObject(carManInfoMap.get("house_type")));
			if (getDateToString(carManInfoMap.get("birthdate")).length() > 0) {
				json.put("birthDate",
						getDateToString(carManInfoMap.get("birthdate")));
			}
		}
		return "json";
	}

	public String carManInfo() {
		json = new Json();
		infoList = this.contract4LabourService.selectRelateCarManByCarId(carId);
		if (infoList.size() == 1) {
			json.put("id", isNullObject(infoList.get(0).get("driver_id")));
			json.put("name", isNullObject(infoList.get(0).get("name")));
			json.put("certNo", isNullObject(infoList.get(0).get("cert_fwzg")));
			json.put("isMore", "false");
		}
		if (infoList.size() > 1) {
			json.put("isMore", "true");
		}
		return "json";
	}

	/** 判断指定的司机是否已经存在劳动合同 */
	public String isExistContract() {
		json = new Json();
		json.put("isExistContract",
				this.contract4LabourService.isExistContractByDriverId(driverId));
		return "json";
	}

	// /**
	// * 获取合同的状态列表
	// *
	// * @return
	// */
	// private Map<String, String> getContractStatuses() {
	// Map<String, String> types = new HashMap<String, String>();
	// types.put(String.valueOf(Contract.STATUS_NORMAL),
	// getText("contract.status.normal"));
	// types.put(String.valueOf(Contract.STATUS_LOGOUT),
	// getText("contract.status.logout"));
	// types.put(String.valueOf(Contract.STATUS_RESGIN),
	// getText("contract.status.resign"));
	// return types;
	// }

	// /**
	// * 获取合同的状态列表
	// *
	// * @return
	// */
	// private Map<String, String> getContractOpType() {
	// Map<String, String> types = new HashMap<String, String>();
	// types.put(String.valueOf(Contract.OPTYPE_MAINTENANCE),
	// getText("contract4Labour.optype.maintenance"));
	// types.put(String.valueOf(Contract.OPTYPE_CHANGECAR),
	// getText("contract4Labour.optype.changeCar"));
	// types.put(String.valueOf(Contract.OPTYPE_RENEW),
	// getText("contract4Labour.optype.renew"));
	// types.put(String.valueOf(Contract.OPTYPE_RESIGN),
	// getText("contract4Labour.optype.resign"));
	//
	// return types;
	// }

	private String isNullObject(Object obj) {
		if (null != obj) {
			return obj.toString();
		} else {
			return "";
		}
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	private String getDateToString(Object object) {
		if (null != object) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			StringBuffer str = new StringBuffer(df.format(object));
			return str.toString();
		} else {
			return "";
		}
	}

	/**
	 * 计算当前岁数
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getBirthDateToString(Object object) {
		String birthDay = getDateToString(object);
		if (birthDay.length() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			Date myDate = null;
			try {
				myDate = sdf.parse(birthDay);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long day = (date.getTime() - myDate.getTime())
					/ (24 * 60 * 60 * 1000) + 1;
			birthDay = new DecimalFormat("#,00").format(day / 365f);
			birthDay = birthDay.split(",")[0];
			// //得到当前的年份
			// String cYear = sdf.format(new Date()).substring(0,4);
			// //得到生日年份
			// String birthYear = birthDay.substring(0,4);
			// //计算当前年龄
			// int age = Integer.parseInt(cYear) - Integer.parseInt(birthYear);
			// birthDay = age+"";
		}
		return birthDay;
	}
}