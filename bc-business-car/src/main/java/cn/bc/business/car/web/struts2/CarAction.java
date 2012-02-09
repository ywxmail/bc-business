/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carmodel.domain.CarModel;
import cn.bc.business.carmodel.service.CarModelService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

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
	private CarService	carService;
	private CarModelService carModelService;
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
	public Map<String, String> statusesValue;
	public Json json;

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
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 车辆管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(790)
				.setMinWidth(250).setHeight(500).setMinHeight(200);
	}

	@Override
	protected void afterCreate(Car entity) {
		super.afterCreate(entity);
		// 自动生成uid
		this.getE().setUid(this.getIdGeneratorService().next(Car.KEY_UID));

		// 初始化车辆的状态
		this.getE().setStatus(Car.CAR_STAUTS_NORMAL);
		// 车辆表单初始化信息
		this.getE().setLevel("一级");// 车辆定级
		this.getE().setColor("绿灰");// 车辆颜色
		this.getE().setFuelType("汽油"); // 燃料类型 
		// // 设置默认的原归属单位信息
		// this.getE().setOldUnitName(getText("app.oldUnitName"));

		// // 自动生成自编号
		// this.getE().setCode(
		// this.getIdGeneratorService().nextSN4Month(Car.KEY_CODE));

	}

	@Override
	protected void afterOpen(Car entity) {
		if (isReadonly()) {
			this.getE().setCertNo2("******");
		}
	}

	@Override
	protected void beforeSave(Car entity) {
		super.beforeSave(entity);
		if (entity.isLogout()) {
			entity.setStatus(Car.CAR_STAUTS_LOGOUT);
			entity.setScrapDate(entity.getReturnDate());
		} else {
			entity.setStatus(Car.CAR_STAUTS_NORMAL);
		}
	}

	@Override
	protected void initForm(boolean editable) {
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

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.CAR_RANK, OptionConstants.CAR_BRAND,
						OptionConstants.CAR_FUEL_TYPE,
						OptionConstants.CAR_COLOR,
						OptionConstants.CAR_TAXIMETERFACTORY,
						OptionConstants.CAR_COMPANY,
						OptionConstants.CAR_LOGOUT_REASON });

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

		// 所属单位列表
		this.companyList = optionItems.get(OptionConstants.CAR_COMPANY);
		OptionItem.insertIfNotExist(companyList, null, getE().getCompany());

		// 注销原因列表
		this.logoutReasonList = optionItems
				.get(OptionConstants.CAR_LOGOUT_REASON);
	}

	/**
	 * 状态值转换列表：在案|注销|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getCarStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Car.CAR_STAUTS_NORMAL),
				getText("bs.status.active"));
		statuses.put(String.valueOf(Car.CAR_STAUTS_LOGOUT),
				getText("bs.status.logout"));
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

	public String carModelInfo(){
		json = new Json();
		CarModel obj = this.carModelService.findcarModelByFactoryModel(factoryModel);
		
		json.put("factoryType" ,obj.getFactoryType()); // 厂牌类型
		json.put("engineType",obj.getEngineType());// 发动机类型
		json.put("fuelType",obj.getFuelType());// 燃料类型
		json.put("displacement",obj.getDisplacement());// 排量
		json.put("power",obj.getPower());// 功率
		json.put("turnType",obj.getTurnType()); // 转向方式
		json.put("tireCount",obj.getTireCount());// 轮胎数
		json.put("tireFrontDistance",obj.getTireFrontDistance());//前轮距
		json.put("tireBehindDistance",obj.getTireBehindDistance());//后轮距
		json.put("tireStandard",obj.getTireStandard());// 轮胎规格
		json.put("axisDistance",obj.getAxisDistance());// 轴距
		json.put("axisCount",obj.getAxisCount());// 轴数
		json.put("pieceCount",obj.getPieceCount());// 后轴钢板弹簧片数
		json.put("dimLen",obj.getDimLen());// 外廓尺寸：长
		json.put("dimWidth",obj.getDimWidth());// 外廓尺寸：宽
		json.put("dimHeight",obj.getDimHeight());// 外廓尺寸：高
		json.put("totalWeight",obj.getTotalWeight());// 总质量
		json.put("accessWeight",obj.getAccessWeight());// 核定承载量
		json.put("accessCount",obj.getAccessCount()); // 载客人数
		return "json";
	}
	
	// ======== 通过factoryModel查找车型配置的相关信息结束 ========
	
	
	// ======== 通过自编号生成原车号开始 ========
	
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 *	通过自编号是否被其他车辆使用过,并且将使用过此编号的车辆的车牌号生成到新车的原车号.
	 * 	如果返回多辆车只取最新登记日期那辆车牌号.
	 */
	public String autoSetOriginNo(){
		json = new Json();
		Car obj = this.carService.findcarOriginNoByCode(code);
		if(obj != null && obj.getPlateNo() != null){
			json.put("plateNo", obj.getPlateNo());
		}
		return "json";
	}
	
	// ======== 通过自编号生成原车号结束 ========
	
	
	// ========判断经济合同自编号唯一代码开始========
	public String checkCodeIsExist() {
		json = new Json();
		List<Map<String, Object>> list = this.carService.checkCodeIsExist(this.code); 
		if(list != null && list.size() > 0){
			json.put("id", list.get(0).get("id"));
			json.put("isExist", "true"); //存在重复自编号
			json.put("msg", getText("car.code.exist"));
		}else{
			json.put("isExist", "false");
		}
		return "json";
	}
	// ========判断经济合同自编号唯一代码结束========
	
}