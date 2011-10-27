/**
 * 
 */
package cn.bc.business.blacklist.web.struts2;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.blacklist.domain.Blacklist;
import cn.bc.business.blacklist.service.BlacklistService;
import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.motorcade.domain.Motorcade;
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
public class BlacklistAction extends FileEntityAction<Long, Blacklist> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	public BlacklistService blacklistService;
	public Long carManId;
	public OptionService optionService;
	public List<Map<String, String>> blackLevelList;// 黑名单等级列表
	public List<Map<String, String>> blackTypeList;// 黑名单限制项目
	public CarByDriverService carByDriverService;
	public String carPlate;// 车牌号码
	public String unitName;// 车辆所属单位名
	public String motorcadeName;// 车辆所属车队名
	public CarManService carManService;
	public Long carId;
	public Long unitId;
	public Long motorcadeId;

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void setCarByDriverService(CarByDriverService carByDriverService) {
		this.carByDriverService = carByDriverService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setBlacklistService(BlacklistService blacklistService) {
		this.blacklistService = blacklistService;
		this.setCrudService(blacklistService);
	}

	@Override
	public boolean isReadonly() {
		// 黑名单管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.blacklist"),
				getText("key.role.bc.admin"));
	}

	@Override
	public String create() throws Exception {
		String result = super.create();
		if (carManId != null) {
			Car car = this.carByDriverService.selectCarByCarManId(new Long(
					carManId));
			CarMan driver = this.carManService.load(carManId);
			this.getE().setCar(car);
			this.getE().setDriver(driver);
			this.getE().setOldUnitName(car.getOldUnitName());
			this.getE().setMotorcade(car.getMotorcade());

		}

		// blackLevelList = this.optionService
		// .findOptionItemByGroupKey(OptionConstants.CARMAN_LEVEL);
		// blackTypeList = this.optionService
		// .findOptionItemByGroupKey(OptionConstants.BLACKLIST_TYPE);
		// this.formPageOption = buildFormPageOption();
		initSelects();
		return result;
	}

	@Override
	public String edit() throws Exception {
		String result = super.edit();

		initSelects();
		return result;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(720).setMinWidth(250)
				.setMinHeight(200).setModal(false);
		option.addButton(new ButtonOption(getText("label.save"), "save"));
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
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		if (carManId == null) {
			columns.add(new TextColumn("code", getText("blacklist.code"), 120)
					.setSortable(true));
			columns.add(new TextColumn("driver.name",
					getText("blacklist.driver"), 40)
					.setValueFormater(new AbstractFormater<String>() {
						@Override
						public String format(Object context, Object value) {
							Blacklist blacklist = (Blacklist) context;
							CarMan driver = blacklist.getDriver();
							if (driver == null) {
								return null;
							} else {
								return blacklist.getDriver().getName();
							}

						}
					}));
			columns.add(new TextColumn("motorcade.name",
					getText("blacklist.motorcade.name"), 30)
					.setValueFormater(new AbstractFormater<String>() {
						@Override
						public String format(Object context, Object value) {
							Blacklist blacklist = (Blacklist) context;
							Motorcade motorcade = blacklist.getMotorcade();
							if (motorcade == null) {
								return null;
							} else {
								return blacklist.getMotorcade().getName();
							}
						}
					}));

			columns.add(new TextColumn("car.plateNo",
					getText("blacklist.car.plateNo"), 60)
					.setValueFormater(new AbstractFormater<String>() {
						@Override
						public String format(Object context, Object value) {
							Blacklist blacklist = (Blacklist) context;
							return blacklist.getCar().getPlateType() + "."
									+ blacklist.getCar().getPlateNo();
						}
					}));

		} else {
			columns.add(new TextColumn("code", getText("blacklist.code"), 70)
					.setSortable(true));
			columns.add(new TextColumn("motorcade.name",
					getText("blacklist.motorcade.name"), 50)
					.setValueFormater(new AbstractFormater<String>() {
						@Override
						public String format(Object context, Object value) {
							Blacklist blacklist = (Blacklist) context;
							return blacklist.getMotorcade().getName();

						}
					}));
			columns.add(new TextColumn("car.plateNo",
					getText("blacklist.car.plateNo"), 60)
					.setValueFormater(new AbstractFormater<String>() {
						@Override
						public String format(Object context, Object value) {
							Blacklist blacklist = (Blacklist) context;
							return blacklist.getCar().getPlateType() + "."
									+ blacklist.getCar().getPlateNo();
						}
					}));

		}
		columns.add(new TextColumn("type", getText("blacklist.type"), 70)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("subject", getText("blacklist.subject"), 110)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("lockDate", getText("blacklist.lockDate"),
				100).setSortable(true).setDir(Direction.Desc)
				.setUseTitleFromLabel(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
		columns.add(new TextColumn("locker.name",
				getText("blacklist.locker.name"), 50)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						Blacklist blacklist = (Blacklist) context;
						return blacklist.getLocker().getName();
					}
				}));
		columns.add(new TextColumn("unlockDate",
				getText("blacklist.unlockDate"), 100).setSortable(true)
				.setDir(Direction.Desc).setUseTitleFromLabel(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
		columns.add(new TextColumn("unlocker.name",
				getText("blacklist.unlocker.name"), 60)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						Blacklist blacklist = (Blacklist) context;
						Actor locker = blacklist.getUnlocker();
						if (locker == null) {
							return null;
						} else {
							return blacklist.getUnlocker().getName();
						}
					}
				}));
		return columns;
	}

	// 返回所选司机的信息
	public Json json;

	public String carManMess() {
		Car car = this.carByDriverService
				.selectCarByCarManId(new Long(carManId));
		if (car != null) {
			carId = car.getId();
			motorcadeId = car.getMotorcade().getId();
			carPlate = car.getPlateType() + car.getPlateNo();
			unitName = car.getOldUnitName();
			motorcadeName = car.getMotorcade().getName();
		}
		json = new Json();
		json.put("carId", carId);
		json.put("carPlate", carPlate);
		json.put("motorcadeId", motorcadeId);
		json.put("unitName", unitName);
		json.put("motorcadeName", motorcadeName);
		return "json";

	}

	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		return page;
	}

	@Override
	public String save() throws Exception {

		Blacklist e = this.getE();
		if (e.getUnlocker() != null && e.getUnlocker().getId() == null) {
			e.setUnlocker(null);
		}

		super.save();
		return "saveSuccess";
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