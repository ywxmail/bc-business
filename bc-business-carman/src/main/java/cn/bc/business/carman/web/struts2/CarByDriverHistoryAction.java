/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.service.CarByDriverHistoryService;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 迁移记录Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarByDriverHistoryAction extends
		FileEntityAction<Long, CarByDriverHistory> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	public CarByDriverHistoryService carByDriverHistoryService;
	public String portrait;
	public Map<String, String> classes;
	public Map<String, String> statusesValueList;// 状态列表
	public Map<String, String> moveTypeValueList;// 迁移类型列表
	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> companyList; // 所属单位列表
	public JSONArray companyNames; // 公司名称列表
	public CarManService carManService;
	public CarService carService;
	public CarByDriverService carByDriverService;
	private OptionService optionService;
	public Long carManId;
	public Long carId;
	public Long fromCarId;
	public int moveType;
	private MotorcadeService motorcadeService;
	public Map<Long, String> cars;// 顶班车辆

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setCarByDriverHistoryService(
			CarByDriverHistoryService carByDriverHistoryService) {
		this.carByDriverHistoryService = carByDriverHistoryService;
		this.setCrudService(carByDriverHistoryService);
	}

	@Autowired
	public void setCarByDriverService(CarByDriverService carByDriverService) {
		this.carByDriverService = carByDriverService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 车辆管理/司机管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bs.driver"), getText("key.role.bc.admin"));
	}

	public String create() throws Exception {
		super.create();
		// 设置迁移类型
		this.getE().setMoveType(moveType);
		// 设置司机信息
		if (carManId != null) {
			// 填写迁移类型为：车辆到车辆 ,交回转车，公司到公司，注销未有去向，交回未注销。司机的原车辆信息
			if (moveType == CarByDriverHistory.MOVETYPE_CLDCL
					|| moveType == CarByDriverHistory.MOVETYPE_GSDGSYZX
					|| moveType == CarByDriverHistory.MOVETYPE_ZXWYQX
					|| moveType == CarByDriverHistory.MOVETYPE_JHWZX
					|| moveType == CarByDriverHistory.MOVETYPE_JHZC) {
				setCarInfoByCarManId();
				this.initForm(true);

			}
			this.getE().setDriver(this.carManService.load(carManId));

		}
		if (carId != null || fromCarId != null) {
			// 如果车辆Id不为空，直接转到转车队页面
			if (carId != null) {
				Car fromCar = this.carService.load(carId);
				this.getE().setFromCar(fromCar);
				this.getE().setFromMotorcadeId(fromCar.getMotorcade().getId());
				this.getE().setFromUnit(fromCar.getCompany());
				this.getE().setToCar(fromCar);
				// this.getE().setToUnit(fromCar.getCompany());
				// this.getE().setToMotorcadeId(fromCar.getMotorcade().getId());
			} else {
				Car fromCar = this.carService.load(fromCarId);
				this.getE().setFromCar(fromCar);
				this.getE().setFromMotorcadeId(fromCar.getMotorcade().getId());
				this.getE().setFromUnit(fromCar.getCompany());
				this.getE().setToCar(fromCar);
				// this.getE().setToUnit(fromCar.getCompany());
				// this.getE().setToMotorcadeId(fromCar.getMotorcade().getId());
			}
			// 设置迁移类型
			this.getE().setMoveType(CarByDriverHistory.MOVETYPE_ZCD);
			return "zhuanCheDui";
		} else {
			return this.getFormName(this.getE().getMoveType());
		}

	}

	/**
	 * 根据司机Id查找司机最新营运车辆的信息
	 */
	private void setCarInfoByCarManId() {
		CarByDriverHistory carByDriverHistory = this.carByDriverHistoryService
				.findNewestCarByDriverHistory(carManId);
		if (carByDriverHistory != null) {
			if (carByDriverHistory.getToCar() != null) {
				// 执行转车操作后执行公司到公司，注销未有去向，交回未注销取回最新的迁移记录
				this.getE().setFromCar(carByDriverHistory.getToCar());
				this.getE().setFromMotorcadeId(
						carByDriverHistory.getToMotorcadeId());
				// 设置原班次
				this.getE().setFromClasses(carByDriverHistory.getToClasses());
				// 设置原单位
				this.getE().setFromUnit(
						carByDriverHistory.getToCar().getCompany());
			} else {
				// 执行交回未注销操作后执行公司到公司，注销未有去向，交回未注销取回最新的迁移记录
				this.getE().setFromCar(carByDriverHistory.getFromCar());
				this.getE().setFromMotorcadeId(
						carByDriverHistory.getFromMotorcadeId());
				// 设置原班次
				this.getE().setFromClasses(carByDriverHistory.getFromClasses());
				// 设置原单位
				this.getE().setFromUnit(
						carByDriverHistory.getFromCar().getCompany());
			}
		}
	}

	// 根据不同的迁移类型转到相关迁移类型jsp
	private String getFormName(int moveType) {
		if (moveType == CarByDriverHistory.MOVETYPE_CLDCL) {
			return "zhuanChe";
		} else if (moveType == CarByDriverHistory.MOVETYPE_GSDGSYZX) {
			return "zhuanGongSi";
		} else if (moveType == CarByDriverHistory.MOVETYPE_ZXWYQX) {
			return "zhuXiao";
		} else if (moveType == CarByDriverHistory.MOVETYPE_YWGSQH) {
			return "qianHui";
		} else if (moveType == CarByDriverHistory.MOVETYPE_JHWZX) {
			return "jiaoHui";
		} else if (moveType == CarByDriverHistory.MOVETYPE_XRZ) {
			return "xinRuZhi";
		} else if (moveType == CarByDriverHistory.MOVETYPE_ZCD) {
			return "zhuanCheDui";
		} else if (moveType == CarByDriverHistory.MOVETYPE_DINGBAN) {
			return "dingban";
		} else if (moveType == CarByDriverHistory.MOVETYPE_JHZC) {
			return "jiaohuizhuanche";
		} else {
			return null;
		}
	}

	@Override
	public String edit() throws Exception {
		super.edit();
		return this.getFormName(this.getE().getMoveType());
	}

	@Override
	public String open() throws Exception {
		super.open();
		return this.getFormName(this.getE().getMoveType());
	}

	@Override
	protected void beforeSave(CarByDriverHistory entity) {
		super.beforeSave(entity);
		CarByDriverHistory e = this.getE();
		// 在个别迁移类型中，可能不给gteToCar,getFromCar,getDriver设值,需将其设为空
		if (e.getToCar() != null && e.getToCar().getId() == null) {
			e.setToCar(null);
		}
		if (e.getFromCar() != null && e.getFromCar().getId() == null) {
			e.setFromCar(null);
		}
		if (e.getDriver() != null && e.getDriver().getId() == null) {
			e.setDriver(null);
		}
		if (e.getMoveType() == CarByDriverHistory.MOVETYPE_ZCD) {
			// 独立模块新建转车队时
			if (e.getFromCar() != null)
				e.setToCar(e.getFromCar());
		}
	}

	/**
	 * 获取迁移类型值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getMoveType() {
		Map<String, String> type = new HashMap<String, String>();
		type = new HashMap<String, String>();
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_CLDCL),
				getText("carByDriverHistory.moveType.cheliangdaocheliang"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_GSDGSYZX),
				getText("carByDriverHistory.moveType.gongsidaogongsiyizhuxiao"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_ZXWYQX),
				getText("carByDriverHistory.moveType.zhuxiaoweiyouquxiang"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_YWGSQH),
				getText("carByDriverHistory.moveType.youwaigongsiqianhui"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_JHWZX),
				getText("carByDriverHistory.moveType.jiaohuiweizhuxiao"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_XRZ),
				getText("carByDriverHistory.moveType.xinruzhi"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_ZCD),
				getText("carByDriverHistory.moveType.cheduidaochedui"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_DINGBAN),
				getText("carByDriverHistory.moveType.dingban"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_JHZC),
				getText("carByDriverHistory.moveType.jiaohuizhuanche"));
		return type;
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();

		if (editable) {// edit,create
			// 添加默认的保存按钮
			pageOption.addButton(this.getDefaultSaveButtonOption());
			pageOption.addButton(new ButtonOption(
					getText("label.saveAndClose"), null,
					"bc.business.carByDriverHistoryForm.saveAndClose"));

			// }
		} else {// open
			if (!readonly) {
				pageOption.addButton(new ButtonOption(
						getText("carByDriverHistory.optype.doMaintenance"),
						null,
						"bc.business.carByDriverHistoryForm.doMaintenance"));
			}
		}
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		if (this.getE().getToMotorcadeId() != null) {
			getOldMotorcade(this.getE().getToMotorcadeId());
		}
		if (this.getE().getFromMotorcadeId() != null) {
			getOldMotorcade(this.getE().getFromMotorcadeId());
		}
		// 加载驾驶状态
		classes = this.getDriverClasses();
		String fromClass = String.valueOf(this.getE().getFromClasses());
		String toClass = String.valueOf(this.getE().getToClasses());
		this.insertIfNotExist(classes, fromClass);
		if (!this.getE().isNew()) {
			this.insertIfNotExist(classes, toClass);
		}

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_COMPANY,
						OptionConstants.COMPANY_NAME });
		// 公司名称列表
		this.companyNames = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.COMPANY_NAME));

		// 状态列表
		statusesValueList = this.getBSStatuses1();

		// 迁移类型列表
		moveTypeValueList = this.getMoveType();
		// 所属单位列表
		this.companyList = optionItems.get(OptionConstants.CAR_COMPANY);
		OptionItem.insertIfNotExist(companyList, null, getE().getToUnit());

		// 可选车队下拉框显示
		if (!this.getE().isNew()) {
			// 新建时不作处理
			if (this.getE().getToMotorcadeId() != null) {
				Motorcade tom = this.motorcadeService.load(this.getE()
						.getToMotorcadeId());
				if (tom != null) {
					OptionItem.insertIfNotExist(this.motorcadeList, tom.getId()
							.toString(), tom.getName());
				}
			}
			if (this.getE().getFromMotorcadeId() != null) {
				Motorcade frm = this.motorcadeService.load(this.getE()
						.getFromMotorcadeId());
				if (frm != null) {
					OptionItem.insertIfNotExist(this.motorcadeList, frm.getId()
							.toString(), frm.getName());
				}
			}
		}
		// 如果迁移类型是顶班时，再初始化cars
		if (this.getE().getMoveType() == CarByDriverHistory.MOVETYPE_DINGBAN) {
			// 顶班车辆
			cars = new HashMap<Long, String>();

			String shiftwork = this.getE().getShiftwork();
			String[] shiftworks = shiftwork.split(";");
			// 顶班车辆
			cars = new HashMap<Long, String>();
			for (int i = 0; i < shiftworks.length; i++) {
				cars.put(new Long(shiftworks[i].split(",")[1]),
						shiftworks[i].split(",")[0]);
			}
		}

	}

	private void insertIfNotExist(Map<String, String> classes, String clazz) {
		boolean exist = false;
		for (Map.Entry<String, String> e : classes.entrySet()) {
			if (e.getKey().equals(clazz)) {
				exist = true;
				break;
			}
		}
		if (!exist) {
			classes.put(clazz, getClassesDesc(Integer.parseInt(clazz)));
		}
	}

	private String getClassesDesc(int clazz) {
		if (clazz == CarByDriver.TYPE_ZHENGBAN)
			return getText("carByDriver.classes.zhengban");
		else if (clazz == CarByDriver.TYPE_FUBAN)
			return getText("carByDriver.classes.fuban");
		else if (clazz == CarByDriver.TYPE_DINGBAN)
			return getText("carByDriver.classes.dingban");
		else if (clazz == CarByDriver.TYPE_ZHUGUA)
			return getText("carByDriver.classes.zhugua");
		else if (clazz == CarByDriver.TYPE_WEIDINGYI)
			return getText("carByDriver.classes.weidingyi");
		return "";
	}

	/**
	 * 获取旧车队名
	 */
	private void getOldMotorcade(Long motorcadeId) {
		Motorcade m = this.motorcadeService.load(motorcadeId);
		OptionItem.insertIfNotExist(this.motorcadeList, m.getId().toString(),
				m.getName());
	}

	// 驾驶状态
	private Map<String, String> getDriverClasses() {
		Map<String, String> type = new LinkedHashMap<String, String>();
		type.put(String.valueOf(CarByDriver.TYPE_ZHENGBAN),
				getText("carByDriver.classes.zhengban"));
		type.put(String.valueOf(CarByDriver.TYPE_FUBAN),
				getText("carByDriver.classes.fuban"));
		return type;
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		// 设置批量处理顶班车辆的表单
		if (this.getE().getMoveType() == CarByDriverHistory.MOVETYPE_DINGBAN) {
			if (this.getE().isNew()) {
				return super.buildFormPageOption(editable).setWidth(430)
						.setMinWidth(320).setMinHeight(200).setModal(true);
			} else {
				return super.buildFormPageOption(editable).setWidth(430)
						.setMinWidth(320).setMinHeight(200).setModal(false);
			}

		} else {
			if (this.getE().isNew()) {
				return super.buildFormPageOption(editable).setWidth(745)
						.setMinWidth(320).setMinHeight(200).setModal(true);
			} else {
				return super.buildFormPageOption(editable).setWidth(745)
						.setMinWidth(320).setMinHeight(200).setModal(false);
			}
		}

	}
}
