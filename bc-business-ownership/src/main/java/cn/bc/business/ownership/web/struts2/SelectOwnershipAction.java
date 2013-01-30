/**
 * 
 */
package cn.bc.business.ownership.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.BSConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择司机Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectOwnershipAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status;// 默认的经营权状态条件
	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：最新修改日期|创建日期
		return new OrderCondition("o.modified_date", Direction.Desc).add("o.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select o.id,o.status_,o.number_ from bs_car_ownership o");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]); // 状态
				map.put("number", rs[i++]); // 经营权号

				return map;
			}
		});
		return sqlObject;
	}
	

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		columns.add(new TextColumn4MapKey("o.status_", "status",
				getText("label.status"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(getStatus())));
		columns.add(new TextColumn4MapKey("o.number_", "number",
				getText("ownership.number")).setSortable(true)
				.setUseTitleFromLabel(true));;

		return columns;
	}
	
	/**
	 * 状态值转换列表：在案|注销|全部
	 * 
	 * @return
	 */
	private Map<String, String> getStatus() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("bs.status.active"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("bs.status.logout"));
		statuses.put("0,1", getText("bs.status.all"));
		return statuses;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("ownership.title.select");
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "o.number_" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['number']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectOwnership");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/ownership/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition ac=new AndCondition();

		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				ac.add(new EqualsCondition("t.status_", new Integer(
						ss[0])));
			} else {
				ac.add(new InCondition("t.status_",
						StringUtils.stringArray2IntegerArray(ss)));
			}
		}

		return ac.isEmpty()?null:ac;
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();

		// 状态条件
		if (this.status != null && this.status.length() > 0) {
			json.put("status", status);
		}

		return json.isEmpty() ? null : json;
	}

	@Override
	protected String getClickOkMethod() {
		return "bs.ownershipSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}

}
