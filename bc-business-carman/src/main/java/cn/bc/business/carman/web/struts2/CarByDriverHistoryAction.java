/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.service.CarByDriverHistoryService;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
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
	public Map<String, String> statusesValueList;// 状态列表
	public Map<String, String> moveTypeValueList;// 迁移类型列表
	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public CarManService carManService;
	public CarService carService;
	public CarByDriverService carByDriverService;
	public Long carManId;
	public Long carId;
	public Long toCarId;
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
			// 填写迁移类型为：车辆到车辆 ,公司到公司，注销未有去向，交回未注销。司机的原车辆信息
			if (moveType == CarByDriverHistory.MOVETYPE_CLDCL
					|| moveType == CarByDriverHistory.MOVETYPE_GSDGSYZX
					|| moveType == CarByDriverHistory.MOVETYPE_ZXWYQX
					|| moveType == CarByDriverHistory.MOVETYPE_JHWZX) {
				setCarInfoByCarManId();

			}
			this.getE().setDriver(this.carManService.load(carManId));

		}
		if (toCarId != null || fromCarId != null) {
			// 如果车辆Id不为空，直接转到转车队页面
			if (toCarId != null) {
				// 如果该车辆为执行新入职，由外公司迁入，车辆到车辆操作后则取迁往车辆的Id加载车辆信息[车辆转车辆操作的优先取取迁往车辆的Id]
				Car fromCar = this.carService.load(toCarId);
				this.getE().setFromCar(fromCar);
				this.getE().setFromMotorcadeId(fromCar.getMotorcade().getId());
			} else {
				// 如果该车辆为执行公司到公司，交回未注销，注销未有去向操作后则取迁往车辆的Id加载车辆信息
				Car fromCar = this.carService.load(fromCarId);
				this.getE().setFromCar(fromCar);
				this.getE().setFromMotorcadeId(fromCar.getMotorcade().getId());
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
			} else {
				// 执行交回未注销操作后执行公司到公司，注销未有去向，交回未注销取回最新的迁移记录
				this.getE().setFromCar(carByDriverHistory.getFromCar());
				this.getE().setFromMotorcadeId(
						carByDriverHistory.getFromMotorcadeId());
				// 设置原班次
				this.getE().setFromClasses(carByDriverHistory.getFromClasses());
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
		return type;
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();

		if (editable) {// edit,create
			// 添加默认的保存按钮
			pageOption.addButton(this.getDefaultSaveButtonOption());
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
	protected void initForm(boolean editable) {
		super.initForm(editable);

		// 状态列表
		statusesValueList = this.getBSStatuses1();

		// 迁移类型列表
		moveTypeValueList = this.getMoveType();

		// 车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
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

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		if (this.getE().getMoveType() == CarByDriverHistory.MOVETYPE_DINGBAN) {
			return super.buildFormPageOption(editable).setWidth(430)
					.setMinWidth(320).setHeight(550).setMinHeight(200)
					.setModal(true);
		} else {
			return super.buildFormPageOption(editable).setWidth(735)
					.setMinWidth(320).setHeight(400).setMinHeight(200)
					.setModal(true);
		}

	}

}
