/**
 * 
 */
package cn.bc.business.sync.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.sync.domain.Sync4InfractTraffic;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.sync.domain.SyncBase;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 交委接口的交通违章信息视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Sync4InfractTrafficsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(Sync4InfractTraffic.STATUS_NEW); // 状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractTraffic"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|违章时间
		return new OrderCondition("b.status_", Direction.Asc).add(
				"t.happen_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select b.id,b.status_,b.sync_type,b.sync_id,b.sync_from,b.sync_date,b.author_id,a.actor_name");
		sql.append(",t.happen_date,t.car_plate,t.driver_cert,t.driver_name,t.jeom,t.content");
		sql.append(" from bs_sync_infract_traffic t");
		sql.append(" inner join bc_sync_base b on b.id=t.id");
		sql.append(" inner join bc_identity_actor_history a on a.id=b.author_id");
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
				map.put("sync_type", rs[i++]);
				map.put("sync_id", rs[i++]);
				map.put("sync_from", rs[i++]);
				map.put("sync_date", rs[i++]);
				map.put("author_id", rs[i++]);
				map.put("actor_name", rs[i++]);
				map.put("happen_date", rs[i++]);
				map.put("car_plate", rs[i++]);
				map.put("driver_cert", rs[i++]);
				map.put("driver_name", rs[i++]);
				map.put("jeom", rs[i++]);
				map.put("content", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("b.id", "id"));
		columns.add(new TextColumn4MapKey("b.status_", "status_",
				getText("bs.sync.status"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getSyncStatuses())));
		columns.add(new TextColumn4MapKey("c.car_plate", "car_plate",
				getText("sync4InfractTraffic.car_plate"), 80));
		columns.add(new TextColumn4MapKey("c.content", "content",
				getText("sync4InfractTraffic.content"))
		.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.happen_date", "happen_date",
				getText("sync4InfractTraffic.happen_date"), 140).setSortable(
						true)
						.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("c.driver_name", "driver_name",
				getText("sync4InfractTraffic.driver_name"), 80)
		.setSortable(true));
		columns.add(new TextColumn4MapKey("c.driver_cert", "driver_cert",
				getText("sync4InfractTraffic.driver_cert"), 80)
		.setSortable(true));
		columns.add(new TextColumn4MapKey("b.syncId", "syncId",
				getText("sync4InfractTraffic.syncId"), 120));
		columns.add(new TextColumn4MapKey("c.jeom", "jeom",
				getText("sync4InfractTraffic.jeom"), 60));
		columns.add(new TextColumn4MapKey("b.syncDate", "syncDate",
				getText("syncBase.syncDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}

	private Map<String, String> getSyncStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(SyncBase.STATUS_NEW),
				getText("bs.sync.status.new"));
		statuses.put(String.valueOf(SyncBase.STATUS_DONE),
				getText("bs.sync.status.done"));
		return statuses;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.syncId", "c.plate_no", "c.driver_name",
				"c.driver_cert", "c.content" };
	}

	@Override
	protected String getFormActionName() {
		return "sync4InfractTraffic";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['syncId']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		return ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"b.status_");
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
		return super.getHtmlPageToolbar()
		// 状态单选按钮组
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getSyncStatuses(), "status", 0,
								getText("title.click2changeSearchStatus")));
	}

	// @Override
	// protected String getHtmlPageJs() {
	// return this.getContextPath() + "/bc-business/car/view.js";
	// }
}
