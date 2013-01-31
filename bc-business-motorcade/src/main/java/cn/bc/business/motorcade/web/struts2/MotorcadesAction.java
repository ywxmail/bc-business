/**
 * 
 */
package cn.bc.business.motorcade.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 车队视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class MotorcadesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status; // 车队的状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 车队管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.motorcade"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|编码
		return new OrderCondition("m.status_", Direction.Asc).add("m.code",
				Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select m.unit_id unitId,u.name unitName,m.id id,m.name as name,m.status_ status,m.code code");
		sql.append(",m.principal_id principalId,m.principal_name principalName,m.file_date fileDate,a.actor_name authorName");
		sql.append(" from bs_motorcade m");
		sql.append(" inner join bc_identity_actor u on u.id=m.unit_id");
		sql.append(" inner join bc_identity_actor_history a on a.id=m.author_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("unitId", StringUtils.toString(rs[i++]));
				map.put("unitName", StringUtils.toString(rs[i++]));
				map.put("id", StringUtils.toString(rs[i++]));
				map.put("name", StringUtils.toString(rs[i++]));
				map.put("status", StringUtils.toString(rs[i++]));
				map.put("code", StringUtils.toString(rs[i++]));
				map.put("principalId", StringUtils.toString(rs[i++]));
				map.put("principalName", StringUtils.toString(rs[i++]));
				map.put("fileDate", rs[i++]);
				map.put("authorName", StringUtils.toString(rs[i++]));
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("m.id", "id"));
		columns.add(new TextColumn4MapKey("m.status_", "status",
				getText("label.status"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getEntityStatuses())));
		columns.add(new TextColumn4MapKey("u.name", "unitName",
				getText("motorcade.unit"), 85).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.name", "name",
				getText("motorcade.name"), 150).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.principal_name", "principalName",
				getText("motorcade.principal"), 80)
				.setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc/user/edit?id={0}", "user") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("principalId"));
							}
						}));
		columns.add(new TextColumn4MapKey("m.code", "code",
				getText("label.code"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("a.actor_name", "authorName",
				getText("label.authorName"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("m.file_date", "fileDate",
				getText("label.fileDate")).setSortable(true).setValueFormater(
				new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "u.name", "m.name", "m.principal_name" };
	}

	@Override
	protected String getFormActionName() {
		return "motorcade";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(700).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		return ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"m.status_");
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return getHtmlPageToolbar(true);
	}
}
