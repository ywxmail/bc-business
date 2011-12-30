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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.business.contract.service.Contract4LabourService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;
import cn.bc.web.ui.json.Json;

/**
 * 司机劳动合同Action
 * 
 * @author wis.ho
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

	public List<Map<String, String>> businessTypeList; // 可选营运性质列表
	public List<Map<String, String>> insurancetypeList; // 可选营运性质列表
	public List<Map<String, String>> houseTypeList; // 可选户口类型列表
	public List<Map<String, String>> buyUnitList; // 可选购买单位列表

	public boolean isMoreCar; // 是否存在多辆车
	public boolean isMoreCarMan; // 是否存在多个司机
	public boolean isNullCar; // 此司机没有车
	public boolean isNullCarMan; // 此车没有司机
	public Json json;

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

	@Override
	public boolean isReadonly() {
		// 劳动合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
	}

	public String create() throws Exception {
		String r = super.create();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		Calendar date2 = Calendar.getInstance();

		if (carId != null && driverId == null) {// 车辆页签中的新建
			// 根据carId查找车辆以及司机id
			carInfoMap = this.contract4LabourService.findCarByCarId(carId);
			infoList = this.contract4LabourService
					.selectRelateCarManByCarId(carId);

			if (infoList.size() == 1) {
				// 填写司机信息forceReadOnly
				driverId = Long.valueOf(isNullObject(infoList.get(0).get(
						"driver_id")));
				this.getE().setExt_str2(
						isNullObject(infoList.get(0).get("name")));
				this.getE().setSex(
						Integer.valueOf(infoList.get(0).get("sex") + ""));
				this.getE().setCertNo(
						isNullObject(infoList.get(0).get("cert_fwzg")));
				this.getE().setCertIdentity(
						isNullObject(infoList.get(0).get("cert_identity")));
				this.getE().setOrigin(
						isNullObject(infoList.get(0).get("origin")));
				this.getE().setHouseType(
						isNullObject(infoList.get(0).get("house_type")));

				if (this.getE().getAge() == null
						&& getDateToString(infoList.get(0).get("birthdate"))
								.length() > 0) {
					date = sdf.parse(infoList.get(0).get("birthdate") + "");
					date2.setTime(date);
					this.getE().setBirthDate(date2);
				}

				this.getE().setAge(
						Integer.valueOf(getBirthDateToString(infoList.get(0)
								.get("birthdate"))));
			} else if (infoList.size() > 1) {
				isMoreCarMan = true;
			} else {
				isNullCarMan = true;
			}
			// 填写车辆信息
			this.getE().setExt_str1(
					isNullObject(carInfoMap.get("plate_type")) + "."
							+ isNullObject(carInfoMap.get("plate_no")));
			if (getDateToString(carInfoMap.get("register_date")).length() > 0) {
				date = sdf.parse(carInfoMap.get("register_date") + "");
				date2.setTime(date);
				this.getE().setRegisterDate(date2);
			}
			this.getE().setBsType(isNullObject(carInfoMap.get("bs_type")));
		}

		if (driverId != null && carId == null) {// 司机页签中的新建
			// 根据carManId查找司机以及车辆id
			carManInfoMap = this.contract4LabourService
					.findCarManByCarManId(driverId);
			infoList = this.contract4LabourService
					.selectRelateCarByCarManId(driverId);
			if (infoList.size() == 1) {
				carId = Long
						.valueOf(isNullObject(infoList.get(0).get("car_id")));
				// 填写车辆信息
				this.getE()
						.setExt_str1(
								isNullObject(infoList.get(0).get("plate_type"))
										+ "."
										+ isNullObject(infoList.get(0).get(
												"plate_no")));
				if (this.getE().getAge() == null
						&& getDateToString(infoList.get(0).get("register_date"))
								.length() > 0) {
					date = sdf.parse(infoList.get(0).get("register_date") + "");
					date2.setTime(date);
					this.getE().setRegisterDate(date2);
				}
				this.getE().setBsType(
						isNullObject(infoList.get(0).get("bs_type")));
			} else if (infoList.size() > 1) {
				isMoreCar = true;
			} else {
				isNullCar = true;
			}
			// 填写司机信息
			this.getE().setExt_str2(isNullObject(carManInfoMap.get("name")));
			this.getE().setSex(Integer.valueOf(carManInfoMap.get("sex") + ""));
			this.getE().setCertNo(isNullObject(carManInfoMap.get("cert_fwzg")));
			this.getE().setCertIdentity(
					isNullObject(carManInfoMap.get("cert_identity")));
			this.getE().setOrigin(isNullObject(carManInfoMap.get("origin")));
			this.getE().setHouseType(
					isNullObject(carManInfoMap.get("house_type")));

			if (getDateToString(carManInfoMap.get("birthdate")).length() > 0) {
				date = sdf.parse(carManInfoMap.get("birthdate") + "");
				date2.setTime(date);
				this.getE().setBirthDate(date2);
			}

			this.getE().setAge(
					Integer.valueOf(getBirthDateToString(carManInfoMap
							.get("birthdate"))));
		}

		// 自动生成UID
		this.getE().setUid(
				this.getIdGeneratorService().next(Contract4Labour.KEY_UID));
		// 自动生成合同编号
		this.getE().setCode(
				this.getIdGeneratorService().nextSN4Month(
						Contract4Labour.KEY_CODE));
		// 自动生成批号：与uid相同
		this.getE().setPatchNo(this.getE().getUid());
		this.getE().setType(Contract.TYPE_LABOUR);
		this.getE().setOpType(Contract.OPTYPE_CREATE);
		this.getE().setMain(Contract.MAIN_NOW);
		this.getE().setVerMajor(Contract.MAJOR_DEFALUT);
		this.getE().setVerMinor(Contract.MINOR_DEFALUT);
		this.getE().setInsuranceType(getText("contract.wujin"));
		this.getE().setBuyUnit(getText("contract.baocheng"));
		this.getE().setStatus(Contract.STATUS_NORMAL);

		// 构建附件控件
		attachsUI = buildAttachsUI(true, false);

		return r;
	}

	@Override
	protected void afterEdit(Contract4Labour entity) {
		// == 合同维护处理
		// 构建附件控件
		attachsUI = buildAttachsUI(false, false);

		// 将次版本号加1
		this.getE().setVerMinor(this.getE().getVerMinor() + 1);

		// 操作类型设置为维护
		this.getE().setOpType(Contract.OPTYPE_MAINTENANCE);
	}

	@Override
	protected void afterOpen(Contract4Labour entity) {
		// 构建附件控件
		attachsUI = buildAttachsUI(false, true);
	}

	@Override
	protected void initForm(boolean editable) {
		super.initForm(editable);

		// 状态列表
		statusesValue = this.getContractStatuses();

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.LB_INSURANCETYPE,
						OptionConstants.CARMAN_HOUSETYPE,
						OptionConstants.LB_BUYUNIT });

		// 加载可选营运性质列表
		this.businessTypeList = optionItems
				.get(OptionConstants.CAR_BUSINESS_NATURE);
		// 加载可选社保险种列表
		this.insurancetypeList = optionItems
				.get(OptionConstants.LB_INSURANCETYPE);
		OptionItem.insertIfNotExist(this.insurancetypeList, this.getE()
				.getInsuranceType(), null);
		// 加载可选户口类型列表
		this.houseTypeList = optionItems.get(OptionConstants.CARMAN_HOUSETYPE);
		// 加载可选购买单位列表
		this.buyUnitList = optionItems.get(OptionConstants.LB_BUYUNIT);
		OptionItem.insertIfNotExist(this.buyUnitList, this.getE().getBuyUnit(),
				null);
	}

	@Override
	public String save() throws Exception {
		SystemContext context = this.getSystyemContext();
		Contract4Labour e = this.getE();
		this.beforeSave(e);

		// 设置最后更新人的信息
		e.setFileDate(Calendar.getInstance());
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());

		this.contract4LabourService
				.save(e, this.getCarId(), this.getDriverId());

		this.afterSave(e);
		return "saveSuccess";
	}

	private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = "contract4Labour.main";
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
			if (editable) {// 编辑状态显示保存按钮
				pageOption.addButton(this.getDefaultSaveButtonOption());
			} else {// 只读状态显示操作按钮
				ToolbarMenuButton toolbarMenuButton = new ToolbarMenuButton(
						getText("contract4Labour.op"));
				toolbarMenuButton.setId("bcOpBtn");
				toolbarMenuButton
						.addMenuItem(
								getText("contract4Labour.optype.maintenance"),
								Contract.OPTYPE_MAINTENANCE + "")
						.addMenuItem(
								getText("contract4Labour.optype.changeCar"),
								Contract.OPTYPE_CHANGECAR + "")
						.addMenuItem(getText("contract4Labour.optype.renew"),
								Contract.OPTYPE_RENEW + "")
						.addMenuItem(getText("contract4Labour.optype.resign"),
								Contract.OPTYPE_RESIGN + "")
						.setChange(
								"bc.contract4LabourForm.selectMenuButtonItem");
				pageOption.addButton(toolbarMenuButton);
			}
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(775).setHeight(460);
	}

	public String certInfo() {
		certInfoMap = this.contract4LabourService.findCertByCarManId(driverId);
		json = new Json();
		if (certInfoMap != null && certInfoMap.size() > 0) {
			certCode = certInfoMap.get("cert_code") + "";
			json.put("cert_code", certCode);
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
				this.contract4LabourService.isExistContract(driverId));
		return "json";
	}

	/**
	 * 获取合同的状态列表
	 * 
	 * @return
	 */
	private Map<String, String> getContractStatuses() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Contract.STATUS_NORMAL),
				getText("contract.status.normal"));
		types.put(String.valueOf(Contract.STATUS_FAILURE),
				getText("contract.status.failure"));
		types.put(String.valueOf(Contract.STATUS_RESGIN),
				getText("contract.status.resign"));
		return types;
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