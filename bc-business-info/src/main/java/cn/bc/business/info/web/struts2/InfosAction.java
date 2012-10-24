/**
 * 
 */
package cn.bc.business.info.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.info.domain.Info;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 信息视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class InfosAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status;

	/**
	 * 信息类型
	 * 
	 * @return
	 */
	protected abstract int getType();

	@Override
	protected String getFormActionName() {
		return "info";
	}

	@Override
	protected String getGridDblRowMethod() {
		// TODO Auto-generated method stub
		return super.getGridDblRowMethod();
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole("BC_ADMIN");
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("i.status_", Direction.Asc).add(
				"i.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select i.id id,i.status_ status,i.subject subject,i.source_ source");
		sql.append(",i.send_date sendDate,i.file_date fileDate,uh.actor_name authorName");
		sql.append(" from bs_info i");
		sql.append(" inner join bc_identity_actor_history uh on uh.id = i.author_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("source", rs[i++]);
				map.put("sendDate", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("authorName", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("i.id", "id"));

		boolean isManager = !this.isReadonly();
		if (isManager) {
			columns.add(new TextColumn4MapKey("i.status_", "status",
					getText("info.status"), 60).setSortable(true)
					.setValueFormater(new KeyValueFormater(getStatuses())));
		}
		columns.add(new TextColumn4MapKey("i.send_date", "sendDate",
				getText("info.sendDate"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("i.subject", "subject",
				getText("info.subject")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("i.source_", "source",
				getText("info.source"), 90).setSortable(true)
				.setUseTitleFromLabel(true));
		if (isManager) {
			columns.add(new TextColumn4MapKey("i.file_date", "fileDate",
					getText("info.fileDate"), 90).setSortable(true)
					.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
			columns.add(new TextColumn4MapKey("uh.actor_name", "authorName",
					getText("info.authorName"), 80).setSortable(true));
		}

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "i.subject", "i.source_", "uh.actor_name" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = super.getHtmlPageToolbar();
		if (!this.isReadonly())
			tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
					this.getStatuses(), "status", 1,
					getText("title.click2changeSearchStatus")));
		return tb;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.status, "i.status_");

		// TODO 按权限的条件
		Condition securityCondition = null;

		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition,
				securityCondition);
	}

	/**
	 * 获取状态值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(Info.STATUS_DRAFT),
				getText("info.status.draft"));
		statuses.put(String.valueOf(Info.STATUS_ISSUED),
				getText("info.status.issued"));
		statuses.put(String.valueOf(Info.STATUS_ARCHIVED),
				getText("info.status.archived"));
		return statuses;
	}
}