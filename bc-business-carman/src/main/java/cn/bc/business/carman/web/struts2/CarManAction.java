/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.RichEntity;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.identity.domain.ActorDetail;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 司机责任人Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarManAction extends FileEntityAction<Long, CarMan> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private IdGeneratorService idGeneratorService;
	public CarManService carManService;
	public String portrait;
	public Map<String, String> statusesValue;
	public OptionService optionService;
	public OptionConstants optionConstants;
	public List<Map<String, String>> carManHouseTypeList;// 司机责任人户口性质列表
	public List<Map<String, String>> carManLevelList;// 司机责任人等级列表
	public List<Map<String, String>> carManModelList;// 司机责任人准驾车型列表

	public IdGeneratorService getIdGeneratorService() {
		return idGeneratorService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
		this.setCrudService(carManService);
	}

	@Override
	public boolean isReadonly() {
		// 司机管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.driver"),
				getText("key.role.bc.admin"));
	}

	@Override
	public String create() throws Exception {
		String result = super.create();
		this.getE().setStatus(RichEntity.STATUS_ENABLED);
		statusesValue = this.getBSStatuses1();
		this.getE().setUid(this.getIdGeneratorService().next(CarMan.KEY_UID));
		this.getE().setOrderNo(
				this.getIdGeneratorService().nextSN4Month(Car.KEY_CODE));

		this.getE().setSex(ActorDetail.SEX_MAN);
		this.initSelects();
		// 获取相片的连接
		portrait = "/bc/libs/themes/default/images/portrait/1in110x140.png";

		return result;
	}

	@Override
	public String open() throws Exception {
		String result = super.open();

		// 获取相片的连接
		// portrait = "/bc/libs/themes/default/images/portrait/1in110x140.png";

		return result;
	}

	@Override
	public String edit() throws Exception {
		// String result = super.edit();
		// statusesValue = this.getEntityStatuses();
		// this.initSelects();
		// 获取相片的连接
		portrait = "/bc/libs/themes/default/images/portrait/1in110x140.png";

		// return result;
		Date startTime = new Date();
		String r = super.edit();

		if (logger.isInfoEnabled())
			logger.info("edit耗时1："
					+ DateUtils.getWasteTime(startTime, new Date()));
		this.setE(this.getCrudService().load(this.getId()));

		// 表单可选项的加载
		statusesValue = this.getBSStatuses1();

		initSelects();

		if (logger.isInfoEnabled())
			logger.info("edit耗时："
					+ DateUtils.getWasteTime(startTime, new Date()));
		return r;
	}

	// 视图特殊条件
	@Override
	protected Condition getSpecalCondition() {
		return null;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = super.buildFormPageOption().setWidth(830)
				.setMinWidth(250).setHeight(590).setMinHeight(200);
		if (!this.isReadonly()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(900).setMinWidth(400)
				.setHeight(500).setMinHeight(300);
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "name", "origin", "cert4Indentity", "cert4CYZG",
				"cert4FWZG" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status", getText("carMan.status"), 60)
				.setSortable(true).setValueFormater(
						new KeyValueFormater(getEntityStatuses())));
		columns.add(new TextColumn("type", getText("carMan.type"), 80)
				.setSortable(true).setValueFormater(
						new KeyValueFormater(getType())));
		columns.add(new TextColumn("name", getText("carMan.name"), 80)
				.setSortable(true));
		columns.add(new TextColumn("cert4FWZG", getText("carMan.cert4FWZG"),
				100).setSortable(true));
		columns.add(new TextColumn("cert4Indentity",
				getText("carMan.cert4Indentity"), 160).setSortable(true));
		columns.add(new TextColumn("cert4CYZG", getText("carMan.cert4CYZG"),
				120).setSortable(true));
		columns.add(new TextColumn("workDate", getText("carMan.workDate"), 120)
				.setSortable(true).setValueFormater(
						new CalendarFormater("yyyy-MM-dd ")));
		columns.add(new TextColumn("origin", getText("carMan.origin"), 100)
				.setSortable(true));
		columns.add(new TextColumn("formerUnit", getText("carMan.formerUnit"),
				80).setSortable(true));
		return columns;
	}

	/**
	 * 获取分类值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getType() {
		Map<String, String> type = new HashMap<String, String>();
		type = new HashMap<String, String>();
		type.put(String.valueOf(CarMan.TYPE_DRIVER),
				getText("carMan.type.driver"));
		type.put(String.valueOf(CarMan.TYPE_CHARGER),
				getText("carMan.type.charger"));
		type.put(String.valueOf(CarMan.TYPE_DRIVER_AND_CHARGER),
				getText("carMan.type.driverAndCharger"));
		return type;
	}

	// 表单可选项的加载
	public void initSelects() {
		Date startTime = new Date();
		logger.info("motorcadeList耗时：" + DateUtils.getWasteTime(startTime));

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CARMAN_HOUSETYPE,
						OptionConstants.CARMAN_LEVEL,
						OptionConstants.CARMAN_MODEL, });
		// 司机责任人户口性质列表
		this.carManHouseTypeList = optionItems
				.get(OptionConstants.CARMAN_HOUSETYPE);
		// 司机责任人等级列表
		this.carManLevelList = optionItems.get(OptionConstants.CARMAN_LEVEL);
		// 司机责任人准驾车型列表
		this.carManModelList = optionItems.get(OptionConstants.CARMAN_MODEL);

		if (logger.isInfoEnabled())
			logger.info("findOptionItem耗时：" + DateUtils.getWasteTime(startTime));
	}

}
