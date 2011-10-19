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
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.SqlObject;
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
public class SelectCarManAction extends AbstractSelectPageAction<CarMan> {
	private static final long serialVersionUID = 1L;
	public CarManService carManService;
	public String types; // 类型控制
	public String status; // 状态，多个用逗号连接

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Override
	protected Query<CarMan> getQuery() {
		return this.carManService.createQuery();
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
		Condition c1 = null;
		if (this.types != null && this.types.length() > 0) {
			// 用空格分隔多个查询条件的值的处理
			String[] values = this.types.split(",");
			Integer[] _values = StringUtils.stringArray2IntegerArray(values);

			// 添加查询条件
			c1 = new InCondition("type", _values);
		}

		Condition c2 = null;
		// status
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");

			if (ss.length == 1) {
				c2 = new EqualsCondition("status", new Integer(ss[0]));
			} else {
				c2 = new InCondition("status",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		if (c1 != null && c2 == null) {
			return c1;
		} else if (c1 == null && c2 != null) {
			return c2;
		} else if (c1 != null && c2 != null) {
			return new AndCondition().add(c1).add(c2);
		} else {
			return null;
		}
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = null;
		if (this.types != null && this.types.length() > 0) {
			json = new Json();
			json.put("types", types);
		}

		if (this.status != null && this.status.length() > 0) {
			if (json == null)
				json = new Json();
			json.put("status", status);
		}
		return json;
	}

	@Override
	protected String getFormActionName() {
		return null;
	}

	@Override
	protected SqlObject<CarMan> getSqlObject() {
		// TODO Auto-generated method stub
		return null;
	}
}
