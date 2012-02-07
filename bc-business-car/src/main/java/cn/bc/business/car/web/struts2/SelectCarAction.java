/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.BSConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
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
public class SelectCarAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|登记日期|车队
		return new OrderCondition("c.status_", Direction.Asc).add(
				"c.register_date", Direction.Desc).add("m.name", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.status_,c.plate_type,c.plate_no,c.register_date");
		sql.append(",c.motorcade_id,m.name,c.old_unit_name");
		sql.append(" from bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status_", rs[i++]);
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("register_date", rs[i++]);
				map.put("motorcade_id", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("old_unit_name", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("c.id", "id"));
		columns.add(new TextColumn4MapKey("c.plate_no", "plate_no",
				getText("car.plate"), 80).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> car = (Map<String, Object>) context;
						return car.get("plate_type") + "."
								+ car.get("plate_no");
					}
				}));
		columns.add(new TextColumn4MapKey("c.register_date", "register_date",
				getText("car.registerDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("car.motorcade"),80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.old_unit_name", "old_unit_name",
				getText("selectCar.old_unit_name"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new HiddenColumn4MapKey("motorcadeId", "motorcade_id"));
		columns.add(new HiddenColumn4MapKey("motorcadeName", "motorcade_name"));
		return columns;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("car.title.selectCar");
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_no", "m.name" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate_type'] + '.' + ['plate_no']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectCar");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/car/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				return new EqualsCondition("c.status_", new Integer(ss[0]));
			} else {
				return new InCondition("c.status_",
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

	@Override
	protected String getClickOkMethod() {
		return "bs.carSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}
}
