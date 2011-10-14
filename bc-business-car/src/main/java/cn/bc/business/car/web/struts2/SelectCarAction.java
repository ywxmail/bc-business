/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.BSConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectCarAction extends AbstractSelectPageAction<Car> {
	private static final long serialVersionUID = 1L;
	public CarService carService;
	public String status; // 车辆的状态，多个用逗号连接

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	@Override
	protected Query<Car> getQuery() {
		return this.carService.createQuery();
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE + "/selectCar";
	}

	@Override
	protected String getClickOkMethod() {
		return "bs.carSelectDialog.clickOk";
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = super.getGridColumns();
		columns.add(new TextColumn("plateNo", getText("car.plate"))
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						Car car = (Car) context;
						return car.getPlateType() + "." + car.getPlateNo();
					}
				}));
		return columns;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + BSConstants.NAMESPACE + "/car/select.js";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "plateType", "plateNo" };
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "plateType + ' ' + plateNo";
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("car.title.selectCar");
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				return new EqualsCondition("status", new Integer(ss[0]));
			} else {
				return new InCondition("status",
						StringUtils.stringArray2IntegerArray(ss));
			}
		} else {
			return null;
		}
	}

	@Override
	protected Json getGridExtrasData() {
		if (this.status == null || this.status.length() == 0) {
			return null;
		} else {
			Json json = new Json();
			json.put("status", status);
			return json;
		}
	}
}
