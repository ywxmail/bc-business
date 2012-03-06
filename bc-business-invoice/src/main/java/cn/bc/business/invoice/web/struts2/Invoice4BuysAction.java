/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 票务采购视图Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4BuysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 票务的状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 票务管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.invoice"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态
		return new OrderCondition("i.status_", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select i.id,i.status_");
		sql.append(" from bs_invoice_base i");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status_", rs[i++]); // 状态
				map.put("code", rs[i++]); // 自编号
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("i.id", "id"));
		// 状态
		columns.add(new TextColumn4MapKey("i.status_", "status_",
				getText("invoice.status"), 40).setSortable(true).setValueFormater(
				new EntityStatusFormater(getBSStatuses1())));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return null;
	}

	@Override
	protected String getFormActionName() {
		return "invoice";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['code']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		return ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"i.status_");
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}
	}
}
