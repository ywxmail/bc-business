/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.BSConstants;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择司机Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectCarManAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 司机的状态，多个用逗号连接
	public String types = String.valueOf(BCConstants.STATUS_ENABLED); // 司机的类型，多个用逗号连接

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("c.status_", Direction.Asc).add(
				"c.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.name,c.cert_fwzg,c.work_date,c.classes");
		sql.append(",c.cert_driving_first_date,c.work_date,c.type_ from BS_CARMAN c");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("name", rs[i++]);
				map.put("cert_fwzg", rs[i++]);
				map.put("work_date", rs[i++]);
				map.put("classes", rs[i++]);
				map.put("cert_driving_first_date",
						DateUtils.formatDate((Date) rs[i++]));
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("c.id", "id"));
		columns.add(new TextColumn4MapKey("c.name", "name",
				getText("carMan.name"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("c.cert_fwzg", "cert_fwzg",
				getText("carMan.cert4FWZG"), 80));
		columns.add(new TextColumn4MapKey("c.work_date", "work_date",
				getText("carMan.workDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.classes", "classes",
				getText("carMan.classes"), 80)
				.setValueFormater(new KeyValueFormater(getDriverClasses())));
		columns.add(new HiddenColumn4MapKey("certDriverFirstDate",
				"cert_driving_first_date"));

		return columns;
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
	protected String[] getGridSearchFields() {
		return new String[] { "c.name", "c.cert_fwzg" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectCarMan");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/carMan/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("c.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("c.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		// 类型条件
		Condition typeCondition = null;
		if (types != null && types.length() > 0) {
			String[] ss = types.split(",");
			if (ss.length == 1) {
				typeCondition = new EqualsCondition("c.type_", new Integer(
						ss[0]));
			} else {
				typeCondition = new InCondition("c.type_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		// 合并条件
		return new AndCondition().add(statusCondition).add(typeCondition);
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();

		// 状态条件
		if (this.status != null && this.status.length() > 0) {
			json.put("status", status);
		}

		// 类型条件
		if (this.types != null && this.types.length() > 0) {
			json.put("types", types);
		}

		return json.isEmpty() ? null : json;
	}

	@Override
	protected String getClickOkMethod() {
		return "bs.carManSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}

	/**
	 * 特殊的营运班次(3，4都显示为顶班)
	 * 
	 * @return
	 */
	private Map<String, String> getDriverClasses() {
		Map<String, String> type = new LinkedHashMap<String, String>();
		type.put(String.valueOf(CarByDriver.TYPE_ZHENGBAN),
				getText("carByDriver.classes.zhengban"));
		type.put(String.valueOf(CarByDriver.TYPE_FUBAN),
				getText("carByDriver.classes.fuban"));
		type.put(String.valueOf(CarByDriver.TYPE_DINGBAN),
				getText("carByDriver.classes.dingban"));
		type.put(String.valueOf(CarByDriver.TYPE_ZHUGUA),
				getText("carByDriver.classes.dingban"));
		type.put(String.valueOf(CarByDriver.TYPE_WEIDINGYI),
				getText("carByDriver.classes.weidingyi"));
		return type;
	}

}
