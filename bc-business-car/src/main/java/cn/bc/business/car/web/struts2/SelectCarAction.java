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
import cn.bc.core.CrudOperations;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 选择车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectCarAction extends AbstractSelectPageAction {
	private static final long serialVersionUID = 1L;
	public CarService carService;
	public String types; // 类型控制

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	@Override
	protected CrudOperations<? extends Object> getGridDataService() {
		return this.carService;
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
		columns.add(new TextColumn("plateType", getText("car.plate"))
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
}
