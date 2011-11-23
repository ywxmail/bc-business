/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.service.CarByDriverHistoryService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

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
	public CarManService carManService;
	public CarService carService;
	public Long carManId;
	public Long carId;
	public int moveType;

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

	@Override
	public boolean isReadonly() {
		// 车辆管理/司机管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bs.driver"), getText("key.role.bc.admin"));
	}


	public String create() throws Exception {
		// String result = super.create();
		if (moveType == 0) {
			return "a";
		}
		else{
			return null;
		}
	}

	@Override
	public String edit() throws Exception {
		String result = super.edit();
		statusesValueList = this.getBSStatuses1();
		return result;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(390).setMinWidth(250)
				.setMinHeight(200);
		if (!isReadonly()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
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
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		if (carId != null)
			page.setAttr("data-extras", new Json().put("carId", carId)
					.toString());
		return page;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "car.plateType", "car.plateNo", "driver.name",
				"classes" };
	}

	@Override
	public String save() throws Exception {
		SystemContext context = this.getSystyemContext();

		// 设置最后更新人的信息
		this.getE().setModifier(context.getUserHistory());
		this.getE().setModifiedDate(Calendar.getInstance());

		super.save();

		return "saveSuccess";
	}
}
