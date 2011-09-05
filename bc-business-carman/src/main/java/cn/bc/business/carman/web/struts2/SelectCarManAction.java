/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.BSConstants;
import cn.bc.business.carman.service.CarManService;
import cn.bc.core.CrudOperations;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择司机、责任人Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectCarManAction extends AbstractSelectPageAction {
	private static final long serialVersionUID = 1L;
	public CarManService carManService;
	public String types; // 类型控制

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Override
	protected CrudOperations<? extends Object> getGridDataService() {
		return this.carManService;
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE + "/selectCarMan";
	}

	@Override
	protected String getClickOkMethod() {
		return "bs.carManSelectDialog.clickOk";
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = super.getGridColumns();
		columns.add(new TextColumn("name", getText("carMan.name"))
				.setSortable(true));
		return columns;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + BSConstants.NAMESPACE
				+ "/carMan/select.js";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "name" };
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "name";
	}

	@Override
	protected String getHtmlPageTitle() {
		if ("0,2".equals(this.types))
			return this.getText("carMan.title.selectDriver");
		else if ("0,1".equals(this.types))
			return this.getText("carMan.title.selectCharger");
		else
			return this.getText("carMan.title.selectCarMan");
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if (this.types == null || this.types.length() == 0)
			return null;

		// 用空格分隔多个查询条件的值的处理
		String[] values = this.types.split(",");
		Integer[] _values = StringUtils.stringArray2IntegerArray(values);

		// 添加查询条件
		InCondition in = new InCondition("type", _values);
		return in;
	}

	@Override
	protected Json getGridExtrasData() {
		if (this.types == null || this.types.length() == 0) {
			return null;
		} else {
			Json json = new Json();
			json.put("types", types);
			return json;
		}
	}
}
