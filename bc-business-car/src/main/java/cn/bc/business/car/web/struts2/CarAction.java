/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carlpg.domain.CarLPG;
import cn.bc.business.carlpg.service.CarLPGService;
import cn.bc.business.carmodel.domain.CarModel;
import cn.bc.business.carmodel.service.CarModelService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.exception.PermissionDeniedException;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

/**
 * 车辆Action
 * 
 * @author dragon
 * 
 */
/**
 * @author wis
 *
 */
/**
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarAction extends FileEntityAction<Long, Car> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private MotorcadeService motorcadeService;
	private CarService carService;
	private CarModelService carModelService;
	private CarLPGService carLPGService;
	private OptionService optionService;

	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> businessTypeList; // 可选营运性质列表
	public List<Map<String, String>> levelTypeList; // 可选车辆定级列表
	public List<Map<String, String>> factoryTypeList; // 可选厂牌类型列表
	public List<Map<String, String>> fuelTypeList; // 可选燃料类型列表
	public List<Map<String, String>> colorTypeList; // 可选颜色类型列表
	public List<Map<String, String>> taximeterFactoryTypeList; // 可选计价器制造厂列表
	public List<Map<String, String>> companyList; // 所属公司列表（宝城、广发）
	public List<Map<String, String>> logoutReasonList; // 注销原因列表
	public List<Map<String, String>> carModelList; // 车型配置列表
	// public List<Map<String, String>> carLPGList; // LPG配置列表

	public Map<String, String> statusesValue;
	public JSONArray vinPrefixes;// 车辆车架号前缀
	public JSONArray taximeterTypes; // 计价器型号
	public JSONArray carTvScreenList;
	public JSONArray carLPGList;
	public String vinPrefix;// 车架号前缀
	public String vinSuffix;// 车架号后缀
	public Long carId;// 车辆Id
	public Long manageNo;// 管理号

	@Autowired
	public void setCarService(CarService carService) {
		this.setCrudService(carService);
		this.carService = carService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setCarModelService(CarModelService carModelService) {
		this.carModelService = carModelService;
	}

	@Autowired
	public void setCarLPGService(CarLPGService carLPGService) {
		this.carLPGService = carLPGService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public String getPageTitle() {
		if (this.getE() != null && !this.getE().isNew())
			return this.getE().getPlate();
		else
			return super.getPageTitle();
	}

	@Override
	public boolean isReadonly() {
		// 车辆管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bc.admin"));
	}

	public boolean isEntering() {
		// 车辆草稿信息录入
		SystemContext context = (SystemContext) this.getContext();
		return context.hasAnyRole(getText("key.role.bs.car.entering"));
	}

	public boolean isCheckOwnershipNumber() {
		// 车辆经营权号查看
		SystemContext context = (SystemContext) this.getContext();
		return context.hasAnyRole(
				getText("key.role.bs.car.ownershipNumber.check"),
				getText("key.role.bs.ownership"),
				getText("key.role.bs.ownership.check"),
				getText("key.role.bs.ownership.check_advanced"),
				getText("key.role.bs.car"), getText("key.role.bc.admin"));
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		// 特殊处理的部分
		if (!this.isReadonly()) {// 有权限
			if (editable && this.getE().getStatus() != BCConstants.STATUS_DRAFT) {// 编辑状态显示保存按钮
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.carForm.save"));
				pageOption.addButton(new ButtonOption(
						getText("label.saveAndClose"), null,
						"bc.carForm.saveAndClose"));

			} else {
				// pageOption.addButton(new ButtonOption(
				// getText("label.save4Draft"), null, "bc.carForm.save"));
				pageOption.addButton(new ButtonOption(
						getText("label.warehousing"), null,
						"bc.carForm.warehousing"));

			}
		}
		// 如果有录入权限的就有保存按钮
		if ((this.isEntering() || !isReadonly())
				&& this.getE().getStatus() == BCConstants.STATUS_DRAFT) {
			pageOption.addButton(new ButtonOption(getText("label.save4Draft"),
					null, "bc.carForm.save"));
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		PageOption pageOption = new PageOption().setWidth(765).setMinWidth(250)
				.setMinHeight(200).setHelp("cheliangxinxi");

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

	@Override
	protected Car createEntity() {
		Car car = super.createEntity();
		car.setStatus(BCConstants.STATUS_DRAFT);
		return car;
	}

	@Override
	protected void afterCreate(Car entity) {
		super.afterCreate(entity);
		// 自动生成uid
		this.getE().setUid(this.getIdGeneratorService().next(Car.KEY_UID));

		// 初始化车辆的状态:草稿
		this.getE().setStatus(BCConstants.STATUS_DRAFT);
		// this.getE().setStatus(Car.CAR_STAUTS_NORMAL);
		// 车辆表单初始化信息
		this.getE().setLevel("一级");// 车辆定级
		this.getE().setColor("绿灰");// 车辆颜色
		this.getE().setFuelType("汽油"); // 燃料类型
		this.getE().setBusinessType("承包合同");// 营运性质
		// // 设置默认的原归属单位信息
		// this.getE().setOldUnitName(getText("app.oldUnitName"));

		// // 自动生成自编号
		// this.getE().setCode(
		// this.getIdGeneratorService().nextSN4Month(Car.KEY_CODE));

	}

	@Override
	protected void afterOpen(Car entity) {
		if (!this.isCheckOwnershipNumber()
				&& this.getE().getStatus() != BCConstants.STATUS_DRAFT) {
			this.getE().setCertNo2("******");
		}
	}

	@Override
	protected void beforeSave(Car entity) {
		super.beforeSave(entity);
		if (entity.isLogout()) {
			entity.setStatus(Car.CAR_STAUTS_LOGOUT);
			entity.setScrapDate(entity.getReturnDate());
			// 车辆注销后在自编号后附加交车日期,如:xxxxxx_20001010
			if (entity.getReturnDate() != null
					&& entity.getCode().indexOf("_") < 0) {
				Date date = entity.getReturnDate().getTime();
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				entity.setCode(entity.getCode() + "_" + df.format(date));
			}
		}
		// else {
		// entity.setStatus(Car.CAR_STAUTS_NORMAL);
		// }
	}

	@Override
	public String save() throws Exception {
		Json json = new Json();
		Car e = this.getE();
		Long existsId = null;
		// 保存之前检测车牌号是否唯一:仅在新建时检测
		if (e.getId() == null) {
			existsId = this.carService.checkPlateIsExists(e.getId(),
					e.getPlateType(), e.getPlateNo());
		}
		// 保存之前在案车辆管理号的唯一性检测
		Long existsManageNo = null;
		if (this.getE().getStatus() != BCConstants.STATUS_DISABLED) {
			existsManageNo = this.carService.checkManageNoIsExists(this.getE()
					.getId(), this.getE().getManageNo());
		}
		if (existsId != null) {
			json.put("success", false);
			json.put("msg", getText("car.error.plateIsExists2"));
		} else if (existsManageNo != null) {
			// json.put("id", existsManageNo);
			json.put("success", false);
			json.put("msg", getText("car.error.manageNoIsExists"));
		} else {
			// // 合并车架号的前缀和后缀
			// this.getE().setVin(this.vinPrefix + this.vinSuffix);

			// 执行基类的保存
			super.save();
			json.put("id", e.getId());
			json.put("success", true);
			json.put("msg", getText("form.save.success"));
		}
		this.json = json.toString();
		return "json";

	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 状态列表
		statusesValue = this.getCarStatuses();

		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		Motorcade m = this.getE().getMotorcade();
		if (m != null) {
			OptionItem.insertIfNotExist(this.motorcadeList, m.getId()
					.toString(), m.getName());
		}

		// 加载可选车型配置列表
		this.carModelList = this.carModelService.findEnabled4Option();
		OptionItem.insertIfNotExist(carModelList, null, getE()
				.getFactoryModel());

		// 加载可选LPG配置列表
		this.carLPGList = OptionItem.toLabelValues(this.carLPGService
				.findEnabled4Option());

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.CAR_RANK, OptionConstants.CAR_BRAND,
						OptionConstants.CAR_FUEL_TYPE,
						OptionConstants.CAR_COLOR,
						OptionConstants.CAR_TAXIMETERFACTORY,
						OptionConstants.CAR_COMPANY,
						OptionConstants.CAR_LOGOUT_REASON,
						OptionConstants.CAR_VIN_PREFIX,
						OptionConstants.CAR_TAXIMETER_TYPE });

		// 加载可选营运性质列表
		this.businessTypeList = optionItems
				.get(OptionConstants.CAR_BUSINESS_NATURE);

		// 加载可选营运性质列表
		this.levelTypeList = optionItems.get(OptionConstants.CAR_RANK);

		// 加载可选厂牌类型列表
		this.factoryTypeList = optionItems.get(OptionConstants.CAR_BRAND);

		// 加载可选燃料类型列表
		this.fuelTypeList = optionItems.get(OptionConstants.CAR_FUEL_TYPE);

		// 加载可选颜色类型列表
		this.colorTypeList = optionItems.get(OptionConstants.CAR_COLOR);

		// 加载可选 计价器制造厂列表
		this.taximeterFactoryTypeList = optionItems
				.get(OptionConstants.CAR_TAXIMETERFACTORY);

		// 车辆车架号前缀
		this.vinPrefixes = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.CAR_VIN_PREFIX));

		// 计价器型号
		this.taximeterTypes = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.CAR_TAXIMETER_TYPE));

		// 所属单位列表
		this.companyList = optionItems.get(OptionConstants.CAR_COMPANY);
		OptionItem.insertIfNotExist(companyList, null, getE().getCompany());

		// 注销原因列表
		this.logoutReasonList = optionItems
				.get(OptionConstants.CAR_LOGOUT_REASON);

		// 分拆出车架号的前缀和后缀
		if (editable) {
			String vin = this.getE().getVin();
			if (vin == null || vin.length() < 12) {
				this.vinPrefix = vin;
				this.vinSuffix = "";
			} else {
				this.vinPrefix = vin.substring(0, 11);
				this.vinSuffix = vin.substring(11);
			}
		}

		// 加载车载电视
		this.carTvScreenList = OptionItem.toLabelValues(this.getCarTvScreen());
	}

	// 车载电视屏参数
	protected List<Map<String, String>> getCarTvScreen() {
		List<Map<String, String>> tvList = new ArrayList<Map<String, String>>();
		Map<String, String> tvMap = new HashMap<String, String>();
		tvMap.put("key", "0");
		tvMap.put("value", "触动传媒Q屏");
		tvList.add(tvMap);
		tvMap = new HashMap<String, String>();
		tvMap.put("key", "1");
		tvMap.put("value", "城市电视");
		tvList.add(tvMap);
		return tvList;
	}

	/**
	 * 状态值转换列表：在案|注销|草稿|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getCarStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Car.CAR_STAUTS_NORMAL),
				getText("bs.status.active"));
		statuses.put(String.valueOf(Car.CAR_STAUTS_LOGOUT),
				getText("bs.status.logout"));
		statuses.put(String.valueOf(BCConstants.STATUS_DRAFT),
				getText("bc.status.draft"));
		statuses.put(" ", getText("bs.status.all"));
		return statuses;
	}

	// ======== 通过factoryModel查找车型配置的相关信息开始 ========

	private String factoryModel;

	public String getFactoryModel() {
		return factoryModel;
	}

	public void setFactoryModel(String factoryModel) {
		this.factoryModel = factoryModel;
	}

	public String carModelInfo() {
		Json json = new Json();
		CarModel obj = this.carModelService
				.findcarModelByFactoryModel(factoryModel);

		json.put("factoryType", obj.getFactoryType()); // 厂牌类型
		json.put("engineType", obj.getEngineType());// 发动机类型
		json.put("fuelType", obj.getFuelType());// 燃料类型
		json.put("displacement", obj.getDisplacement());// 排量
		json.put("power", obj.getPower());// 功率
		json.put("turnType", obj.getTurnType()); // 转向方式
		json.put("tireCount", obj.getTireCount());// 轮胎数
		json.put("tireFrontDistance", obj.getTireFrontDistance());// 前轮距
		json.put("tireBehindDistance", obj.getTireBehindDistance());// 后轮距
		json.put("tireStandard", obj.getTireStandard());// 轮胎规格
		json.put("axisDistance", obj.getAxisDistance());// 轴距
		json.put("axisCount", obj.getAxisCount());// 轴数
		json.put("pieceCount", obj.getPieceCount());// 后轴钢板弹簧片数
		json.put("dimLen", obj.getDimLen());// 外廓尺寸：长
		json.put("dimWidth", obj.getDimWidth());// 外廓尺寸：宽
		json.put("dimHeight", obj.getDimHeight());// 外廓尺寸：高
		json.put("totalWeight", obj.getTotalWeight());// 总质量
		json.put("accessWeight", obj.getAccessWeight());// 核定承载量
		json.put("accessCount", obj.getAccessCount()); // 载客人数
		this.json = json.toString();
		return "json";
	}

	// ======== 通过factoryModel查找车型配置的相关信息结束 ========

	// ======== 通过lpgName查找车型配置的相关信息开始 ========
	private String lpgName;

	public String getLpgName() {
		return lpgName;
	}

	public void setLpgName(String lpgName) {
		this.lpgName = lpgName;
	}

	public String carLPGInfo() {
		Json json = new Json();
		CarLPG obj = this.carLPGService.findcarLPGByLPGModel(lpgName);
		json.put("lpgModel", obj.getModel());
		json.put("lpgGpModel", obj.getGpmodel());
		json.put("lpgJcfModel", obj.getJcfmodel());
		json.put("lpgQhqModel", obj.getQhqmodel());
		json.put("lpgPsqModel", obj.getPsqmodel());
		this.json = json.toString();
		return "json";
	}

	// ======== 通过lpgName查找车型配置的相关信息开始 ========

	// ======== 通过以营权号生成原车号开始 ========

	private String ownershipNo;
	private String code;
	private Calendar fileDate;

	public Calendar getFileDate() {
		return fileDate;
	}

	public void setFileDate(Calendar fileDate) {
		this.fileDate = fileDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOwnershipNo() {
		return ownershipNo;
	}

	public void setOwnershipNo(String ownershipNo) {
		this.ownershipNo = ownershipNo;
	}

	/**
	 * 通过经营权号是否被其他车辆使用过,并且将使用过此编号的车辆的车牌号生成到新车的原车号. 如果返回多辆车只取最新登记日期那辆车牌号.
	 */
	public String autoSetOriginNo() {
		Json json = new Json();
		Car obj = this.carService.findcarOriginNoByOwnership(ownershipNo,
				fileDate);
		if (obj != null && obj.getPlateNo() != null) {
			json.put("plateNo", obj.getPlateNo());
		}
		this.json = json.toString();
		return "json";
	}

	// ======== 通过自编号生成原车号结束 ========

	// ========判断车辆自编号唯一性代码开始========
	private Long excludeId;

	public Long getExcludeId() {
		return excludeId;
	}

	public void setExcludeId(Long excludeId) {
		this.excludeId = excludeId;
	}

	public String checkCodeIsExists() {
		Json json = new Json();
		Long existsId = this.carService.checkCodeIsExists(this.excludeId,
				this.code);
		if (existsId != null) {
			json.put("id", existsId);
			json.put("isExists", "true"); // 存在重复自编号
			json.put("msg", getText("car.error.codeIsExists"));
		} else {
			json.put("isExists", "false");
		}
		this.json = json.toString();
		return "json";
	}

	// ========判断车辆自编号唯一性代码结束========

	// ========判断车辆管理号唯一性代码开始========

	public String checkManageNoIsExists() {
		Json json = new Json();
		Long existsId = this.carService.checkManageNoIsExists(this.carId,
				this.manageNo);
		if (existsId != null) {
			json.put("id", existsId);
			json.put("isExists", "true"); // 存在重复管理号
			json.put("msg", getText("car.error.manageNoIsExists2"));
		} else {
			json.put("isExists", "false");
		}
		this.json = json.toString();
		return "json";
	}

	// ========判断车辆自编号唯一性代码结束========

	// ========判断车牌号唯一性代码开始========
	public String plateType;
	public String plateNo;

	public String checkPlateIsExists() {
		Json json = new Json();
		Long existsId = this.carService.checkPlateIsExists(this.excludeId,
				this.plateType, this.plateNo);
		if (existsId != null) {
			json.put("id", existsId);
			json.put("isExists", "true"); // 存在重复自编号
			json.put("msg", getText("car.error.plateIsExists"));
		} else {
			json.put("isExists", "false");
		}
		this.json = json.toString();
		return "json";
	}

	// ========判断车牌号唯一性代码结束========

	// 提示只能删除草稿状态的车辆
	@Override
	protected String getDeleteExceptionMsg(Exception e) {
		//
		if (e instanceof PermissionDeniedException) {
			return "只能删除草稿状态的车辆";
		}
		return super.getDeleteExceptionMsg(e);
	}

	// ============查找下一个月需要交车的车辆信息===开始=========
	public Long unitId;

	public String findRetiredCarsOfNextMonth() {
		JsonArray jsonArray = new JsonArray();
		Json json = null;
		Calendar month = Calendar.getInstance();
		// 下一个月
		month.add(Calendar.MONTH, 1);
		for (Map<String, Object> codMap : this.carService
				.findRetiredCarsOfMonth(month, unitId)) {
			if (codMap != null) {
				json = new Json();
				for (String key : codMap.keySet()) {
					json.put(key, codMap.get(key));
				}
				jsonArray.add(json);
			}
		}
		this.json = jsonArray.toString();
		return "json";
	}
	// ============查找下一个月需要交车的车辆信息===结束=========

}