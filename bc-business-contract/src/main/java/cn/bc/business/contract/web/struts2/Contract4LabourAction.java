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
import java.util.LinkedHashSet;
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
import cn.bc.business.contract.domain.ContractFeeDetail;
import cn.bc.business.contract.service.Contract4LabourService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.docs.domain.Attach;
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
	public List<Map<String, String>> culturalDegreeList; // 可选文化程度列表
	public List<Map<String, String>> maritalStatusList; // 可选婚姻状况列表

	public boolean isMoreCar; // 是否存在多辆车
	public boolean isMoreCarMan; // 是否存在多个司机
	public boolean isNullCar; // 此司机没有车
	public boolean isNullCarMan; // 此车没有司机
	public boolean isExistContract; // 是否存在合同
	public boolean isSupply; // 是否补录
	public boolean isDoMaintenance = false;// 是否进行维护操作
	public boolean isDoChangeCar = false;// 是否执行转车操作

	// public CertService certService;

	@Autowired
	public void setContract4LabourService(
			Contract4LabourService contract4LabourService) {
		this.contract4LabourService = contract4LabourService;
		this.setCrudService(contract4LabourService);
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

	public boolean isEntering() {
		// 劳动合同草稿信息录入
		SystemContext context = (SystemContext) this.getContext();
		return context
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

		// 操作类型设置为维护
		// 如果是草稿状态下打开，操作类型保持为原来的操作类型
		if (entity.getStatus() != BCConstants.STATUS_DRAFT) {
			// 将次版本号加1
			entity.setVerMinor(entity.getVerMinor() + 1);
			// 操作类型设置为维护
			entity.setOpType(Contract.OPTYPE_MAINTENANCE);
		}
		// 如果是草稿状态的合同表单需要获取上份合同的结束日期
		if (entity.getStatus() == BCConstants.STATUS_DRAFT) {
			this.stopDate = entity.getExt_str3();
		}

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
						OptionConstants.LB_BUYUNIT,
						OptionConstants.CULTURAL_DEGREE,
						OptionConstants.MARITAL_STATUS });

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
		OptionItem.insertIfNotExist(this.houseTypeList, null, this.getE()
				.getHouseType());

		// 加载可选购买单位列表
		this.buyUnitList = optionItems.get(OptionConstants.LB_BUYUNIT);
		OptionItem.insertIfNotExist(this.buyUnitList, this.getE().getBuyUnit(),
				null);
		// 加载可选文化程度列表
		this.culturalDegreeList = optionItems
				.get(OptionConstants.CULTURAL_DEGREE);
		// 加载可选婚姻状况列表
		this.maritalStatusList = optionItems
				.get(OptionConstants.MARITAL_STATUS);
	}

	@Override
	public String save() throws Exception {
		// 如果Pid不为空，且为新建的则该合同执行的是转车或续约操作
		SystemContext context = this.getSystyemContext();

		// 将收费明细设置为空

		this.getE()
				.setContractFeeDetail(new LinkedHashSet<ContractFeeDetail>());

		if (this.getE().isNew() && this.getE().getPid() != null) {
			Contract4Labour e = this.getE();
			// 设置创建人
			e.setAuthor(context.getUserHistory());
			e.setFileDate(Calendar.getInstance());

			// 获取旧合同id
			Long fromContractId = e.getPid();
			Contract newContract = null;
			String msg = "";
			Json json = new Json();
			// // 合同实际结束日期
			// Calendar stopDate4Charger = DateUtils.getCalendar(this.stopDate);
			newContract = this.contract4LabourService.doOperate(carId,
					this.getE(), fromContractId, this.stopDate);
			if (e.getOpType() == Contract.OPTYPE_RENEW) {// 续约
				msg = getText("contract4Labour.renew.success");
			} else if (e.getOpType() == Contract.OPTYPE_CHANGECAR) {// 转车
				msg = getText("contract4Labour.changeCar.success");
			}
			json.put("id", newContract.getId());
			json.put("oldId", fromContractId);
			json.put("success", true);
			json.put("msg", msg);
			this.json = json.toString();
			return "json";
		} else {
			// 正常操作
			Json json = new Json();
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
				this.json = json.toString();
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
				this.json = json.toString();
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
		Json json = new Json();
		Long carManId = this.contract4LabourService.checkInsurCodeIsExist(
				this.excludeId, this.insurCode);
		if (carManId != null) {
			json.put("carManId", carManId);
			json.put("isExist", "true"); // 存在重复自编号
			json.put("msg", getText("contract4Labour.insurCode.exist"));
		} else {
			json.put("isExist", "false");
		}
		this.json = json.toString();
		return "json";
	}

	// ========判断经济合同自编号唯一代码结束========

	private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = Contract4Labour.ATTACH_TYPE;
		String puid = this.getE().getUid();
		boolean readonly = forceReadonly ? true : (this.isEntering()
				&& this.getE().getStatus() == BCConstants.STATUS_DRAFT ? false
				: this.isReadonly());
		AttachWidget attachsUI = this.buildAttachsUI(isNew, readonly, ptype,
				puid);

		// 自定义附件总控制按钮
		if (!attachsUI.isReadOnly()) {
			attachsUI.addHeadButton(AttachWidget.createButton("添加模板", null,
					"bc.contract4LabourForm.addAttachFromTemplate", null));// 添加模板
		}
		attachsUI.addHeadButton(AttachWidget
				.defaultHeadButton4DownloadAll(null));// 打包下载
		if (!attachsUI.isReadOnly()) {
			attachsUI.addHeadButton(AttachWidget
					.defaultHeadButton4DeleteAll(null));// 删除
		}

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
				// pageOption.addButton(new ButtonOption(
				// getText("label.save4Draft"), null,
				// "bc.contract4LabourForm.save"));
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
		// 如果有录入,管理员权限的就有保存为草稿按钮
		if ((this.isEntering() || !isReadonly())
				&& this.getE().getStatus() == BCConstants.STATUS_DRAFT) {
			pageOption.addButton(new ButtonOption(getText("label.save4Draft"),
					null, "bc.contract4LabourForm.save"));

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

		PageOption pageOption = new PageOption().setWidth(740).setHelp(
				"laodonghetong");

		if (this.useFormPrint())
			pageOption.setPrint("default.form");

		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.put("readonly", readonly);
			// 如果有录入权限且状态为草稿的可以进行修改
		} else if (this.getE().getStatus() == BCConstants.STATUS_DRAFT
				&& this.isEntering()) {
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

		Json json = new Json();
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
		this.json = json.toString();
		return "json";
	}

	public String carManInfo() {
		Json json = new Json();
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
		this.json = json.toString();
		return "json";
	}

	/** 判断指定的司机是否已经存在劳动合同 */
	public String isExistContract() {
		Json json = new Json();
		json.put("isExistContract",
				this.contract4LabourService.isExistContractByDriverId(driverId));
		this.json = json.toString();
		return "json";
	}

	/**
	 * 入库
	 * 
	 * @return
	 */
	public String warehousing() {
		// 将收费明细设置为空
		this.getE()
				.setContractFeeDetail(new LinkedHashSet<ContractFeeDetail>());
		SystemContext context = this.getSystyemContext();
		// 设置创建人
		this.getE().setAuthor(context.getUserHistory());
		this.getE().setFileDate(Calendar.getInstance());
		// 如果是新建将实际停保日期设置到冗余字段
		if (this.getE().isNew() || this.stopDate.length() > 0) {
			this.getE().setExt_str3(this.stopDate);
		}
		this.json = this.contract4LabourService.doWarehousing(carId, driverId,
				this.getE());
		return "json";
	}

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

	// 从模板添加附件
	@Override
	protected Attach buildAttachFromTemplate() throws Exception {
		return this.contract4LabourService.doAddAttachFromTemplate(
				this.getId(), this.tpl);
	}
}