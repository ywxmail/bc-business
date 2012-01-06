/**
 * 
 */
package cn.bc.business.injury.web.struts2;

import java.util.ArrayList;
import java.util.Date;
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
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 劳动合同视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class InjurysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 合同的状态，多个用逗号连接

	public Long contractId;

	@Override
	public boolean isReadonly() {
		// 合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4charger"),
				getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：登记日期|状态
		return new OrderCondition("i.file_date", Direction.Desc).add(
				"i.status_", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select i.id,i.compensation,i.happen_date,i.confirm_date,i.start_date,i.end_date,i.code");
		sql.append(" from bs_industrial_injury i");

		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("compensation", rs[i++]);
				map.put("happen_date", rs[i++]);
				map.put("confirm_date", rs[i++]);
				map.put("start_date", rs[i++]);
				map.put("end_date", rs[i++]);
				map.put("code", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("i.id", "id"));
		columns.add(new TextColumn4MapKey("i.compensation", "compensation",
				getText("injury.compensation"), 90)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("i.happen_date", "happen_date",
				getText("injury.happenDate"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("i.confirm_date", "confirm_date",
				getText("injury.confirmDate"), 90)
		.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("i.start_date", "start_date",
				getText("injury.startDate")).setUseTitleFromLabel(true)
				.setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
					@Override
					public Date getToDate(Object context, Object value) {
						@SuppressWarnings("rawtypes")
						Map contract = (Map) context;
						return (Date) contract.get("end_date");
					}
				}));
		columns.add(new TextColumn4MapKey("i.code", "code",
				getText("injury.code"), 80)
				.setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "i.code", "i.compensation" };
	}

	@Override
	protected String getFormActionName() {
		return "injury";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(490).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['code']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 合同条件
		Condition idCondtion = new EqualsCondition("i.contract_id", contractId);

		return ConditionUtils.mix2AndCondition(idCondtion);
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		if (contractId != null) {
			json.put("contractId", contractId);
		}

	}

}