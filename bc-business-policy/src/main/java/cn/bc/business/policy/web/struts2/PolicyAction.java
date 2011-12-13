/**
 * 
 */
package cn.bc.business.policy.web.struts2;

import java.util.Calendar;
import java.util.Date;
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
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.policy.domain.Policy;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 黑名单Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PolicyAction extends FileEntityAction<Long, Policy> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	public Long carManId;
	public OptionService optionService;
	public List<Map<String, String>> blackLevelList;// 黑名单等级列表
	public List<Map<String, String>> blackTypeList;// 黑名单限制项目
	public String carPlate;// 车牌号码
	public String unitName;// 车辆所属单位名
	public String motorcadeName;// 车辆所属车队名
	private CarService carService;
	public Long carId;
	public Long unitId;
	public Long motorcadeId;
	public Map<String, String> statusesValue;
	public boolean isMoreCar;// 标识是否一个司机对应有多辆车
	public boolean isMoreCarMan;// 标识是否一辆车对应多个司机
	public boolean isNullCar;// 标识是否没有车和司机对应
	public boolean isNullCarMan;// 标识是否没有司机和车对应

	public Long getCarManId() {
		return carManId;
	}

	public void setCarManId(Long carManId) {
		this.carManId = carManId;
	}

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	

	@Override
	public boolean isReadonly() {
		// if (this.getE() != null) {// 表单
		// return this.getE().getStatus() != Blacklist.STATUS_DAISUODING;
		// } else {// 视图
		// 黑名单管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.blacklist"),
				getText("key.role.bc.admin"));
		// }
	}

	@Override
	public String create() throws Exception {

		String result = super.create();

		// if (carManId != null) {
		// Car car = this.carByDriverService.selectCarByCarManId(new Long(
		// carManId));
		// CarMan driver = this.carManService.load(carManId);
		// this.getE().setCar(car);
		// this.getE().setDriver(driver);
		// this.getE().setOldUnitName(car.getOldUnitName());
		// this.getE().setMotorcade(car.getMotorcade());
		//
		// }
//		if (carManId != null) {
//			CarMan driver = this.carManService.load(carManId);
//			List<Car> car = this.carService.selectAllCarByCarManId(carManId);
//			if (car.size() == 1) {
//				this.getE().setCar(car.get(0));
//				this.getE().setMotorcade(car.get(0).getMotorcade());
//			} else if (car.size() > 1) {
//				isMoreCar = true;
//			} else {
//				isNullCar = true;
//			}
//			this.getE().setDriver(driver);
//
//		}
//		if (carId != null) {
//			Car car = this.carService.load(carId);
//			this.getE().setCar(car);
//
//			this.getE().setMotorcade(car.getMotorcade());
//			List<CarMan> carMan = this.carManService
//					.selectAllCarManByCarId(carId);
//			if (carMan.size() == 1) {
//				this.getE().setDriver(carMan.get(0));
//
//			} else if (carMan.size() > 1) {
//				isMoreCarMan = true;
//			} else {
//				isNullCarMan = true;
//			}
//		}
		initSelects();
		return result;
	}

//	@Override
//	protected void afterCreate(Policy entity) {
//		SystemContext context = this.getSystyemContext();
//		// entity.setStatus(Blacklist.STATUS_CREATE);
//		entity.setStatus(Policy.STATUS_LOCK);
//		this.getE().setLocker(context.getUser());
//		this.getE().setLockDate(Calendar.getInstance());
//		this.getE().setCode(
//				this.getIdGeneratorService().nextSN4Month(Policy.KEY_CODE));
//
//		// 设置创建人信息
//		entity.setFileDate(Calendar.getInstance());
//		entity.setAuthor(context.getUserHistory());
//	}

//	@Override
//	public String save() throws Exception {
//		SystemContext context = this.getSystyemContext();
//		// 解决object references an unsaved transient instance 错误，（表单隐藏域以新建一个
//		// 对象,但没有保存相关的数据而出现的错误）
//		Policy e = this.getE();
//		if (e.getUnlocker() != null && e.getUnlocker().getId() == null) {
//			e.setUnlocker(null);
//		}
//
//		// 设置最后更新人的信息
//		if (this.getE().getStatus() == Policy.STATUS_LOCK) {
//			this.getE().setModifier(context.getUserHistory());
//			this.getE().setModifiedDate(Calendar.getInstance());
//		} else {
//			this.getE().setUnlocker(context.getUser());
//			this.getE().setUnlockDate(Calendar.getInstance());
//		}
//		super.save();
//		return "saveSuccess";
//	}

//	@Override
//	public String edit() throws Exception {
//		String result = super.edit();
//		statusesValue = this.getBLStatuses();
//		SystemContext context = this.getSystyemContext();
//		if (this.getE().getStatus() == Policy.STATUS_LOCK) {
//			this.getE().setUnlockDate(Calendar.getInstance());
//			this.getE().setUnlocker(context.getUser());
//		}
//		initSelects();
//		return result;
//	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = super.buildFormPageOption().setWidth(720)
				.setMinWidth(250).setMinHeight(200);

		// 新建时状态为2，表单只显示锁定按钮
		// if (isReadonly() == false
		// && this.getE().getStatus() == Blacklist.STATUS_CREATE) {
		// option.addButton(new ButtonOption(getText("blacklist.locker"),
		// null, "bc.business.blacklistForm.lcoker"));
		// // 状态为锁定时，只显示解锁按钮
		// }
//		if (isReadonly() == false && this.getE().isNew()) {
//			option.addButton(new ButtonOption(getText("blacklist.locker"),
//					null, "bc.business.blacklistForm.lcoker"));
//			// 状态为锁定时，只显示解锁按钮
//		} else if (isReadonly() == false
//				&& this.getE().getStatus() == Policy.STATUS_LOCK) {
//			option.addButton(new ButtonOption(getText("blacklist.unlocker"),
//					null, "bc.business.blacklistForm.unlcoker"));
//		}

		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns)
				.setRowLabelExpression("driver.name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(700).setMinWidth(300)
				.setHeight(350).setMinHeight(300);
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "driver.name", "motorcade.name", "type",
				"unit.name", "locker.name", "subject", "car.plateNo", "code" };
	}

	@Override
//	protected List<Column> buildGridColumns() {
//		List<Column> columns = super.buildGridColumns();
//		if (carManId == null) {
//			columns.add(new TextColumn("code", getText("blacklist.code"), 120)
//					.setSortable(true));
//			columns.add(new TextColumn("driver.name",
//					getText("blacklist.driver"), 40)
//					.setValueFormater(new AbstractFormater<String>() {
//						@Override
//						public String format(Object context, Object value) {
//							Policy blacklist = (Policy) context;
//							CarMan driver = blacklist.getDriver();
//							if (driver == null) {
//								return null;
//							} else {
//								return blacklist.getDriver().getName();
//							}
//
//						}
//					}));
//			columns.add(new TextColumn("motorcade.name",
//					getText("blacklist.motorcade.name"), 30)
//					.setValueFormater(new AbstractFormater<String>() {
//						@Override
//						public String format(Object context, Object value) {
//							Policy blacklist = (Policy) context;
//							Motorcade motorcade = blacklist.getMotorcade();
//							if (motorcade == null) {
//								return null;
//							} else {
//								return blacklist.getMotorcade().getName();
//							}
//						}
//					}));
//
//			columns.add(new TextColumn("car.plateNo",
//					getText("blacklist.car.plateNo"), 60)
//					.setValueFormater(new AbstractFormater<String>() {
//						@Override
//						public String format(Object context, Object value) {
//							Policy blacklist = (Policy) context;
//							return blacklist.getCar().getPlateType() + "."
//									+ blacklist.getCar().getPlateNo();
//						}
//					}));
//
//		} else {
//			columns.add(new TextColumn("code", getText("blacklist.code"), 70)
//					.setSortable(true));
//			columns.add(new TextColumn("motorcade.name",
//					getText("blacklist.motorcade.name"), 50)
//					.setValueFormater(new AbstractFormater<String>() {
//						@Override
//						public String format(Object context, Object value) {
//							Policy blacklist = (Policy) context;
//							return blacklist.getMotorcade().getName();
//
//						}
//					}));
//			columns.add(new TextColumn("car.plateNo",
//					getText("blacklist.car.plateNo"), 60)
//					.setValueFormater(new AbstractFormater<String>() {
//						@Override
//						public String format(Object context, Object value) {
//							Policy blacklist = (Policy) context;
//							return blacklist.getCar().getPlateType() + "."
//									+ blacklist.getCar().getPlateNo();
//						}
//					}));
//
//		}
//		columns.add(new TextColumn("type", getText("blacklist.type"), 70)
//				.setSortable(true).setUseTitleFromLabel(true));
//		columns.add(new TextColumn("subject", getText("blacklist.subject"), 110)
//				.setSortable(true).setUseTitleFromLabel(true));
//		columns.add(new TextColumn("lockDate", getText("blacklist.lockDate"),
//				100).setSortable(true).setDir(Direction.Desc)
//				.setUseTitleFromLabel(true)
//				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
//		columns.add(new TextColumn("locker.name",
//				getText("blacklist.locker.name"), 50)
//				.setValueFormater(new AbstractFormater<String>() {
//					@Override
//					public String format(Object context, Object value) {
//						Policy blacklist = (Policy) context;
//						return blacklist.getLocker().getName();
//					}
//				}));
//		columns.add(new TextColumn("unlockDate",
//				getText("blacklist.unlockDate"), 100).setSortable(true)
//				.setDir(Direction.Desc).setUseTitleFromLabel(true)
//				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
//		columns.add(new TextColumn("unlocker.name",
//				getText("blacklist.unlocker.name"), 60)
//				.setValueFormater(new AbstractFormater<String>() {
//					@Override
//					public String format(Object context, Object value) {
//						Policy blacklist = (Policy) context;
//						Actor locker = blacklist.getUnlocker();
//						if (locker == null) {
//							return null;
//						} else {
//							return blacklist.getUnlocker().getName();
//						}
//					}
//				}));
//		return columns;
//	}

	

	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		return page;
	}

	// 视图特殊条件
	@Override
	protected Condition getSpecalCondition() {
		if (carManId != null) {
			return new EqualsCondition("driver.id", carManId);
		} else {
			return null;
		}
	}

	// 表单可选项的加载
	public void initSelects() {
		Date startTime = new Date();

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CARMAN_LEVEL,
						OptionConstants.BLACKLIST_TYPE, });

		// 加载黑名单等级列表
		this.blackLevelList = optionItems.get(OptionConstants.CARMAN_LEVEL);
		// 加载黑名单限制项目
		this.blackTypeList = optionItems.get(OptionConstants.BLACKLIST_TYPE);

		if (logger.isInfoEnabled())
			logger.info("findOptionItem耗时：" + DateUtils.getWasteTime(startTime));
	}

	

}