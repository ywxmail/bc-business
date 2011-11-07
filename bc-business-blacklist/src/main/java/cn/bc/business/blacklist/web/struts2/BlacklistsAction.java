/**
 * 
 */
package cn.bc.business.blacklist.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.blacklist.domain.Blacklist;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 黑名单 Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class BlacklistsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(Blacklist.STATUS_SUODING); // 黑名单的状态，多个用逗号连接
	public Long carManId;
	public Long carId;

	@Override
	public boolean isReadonly() {
		// 黑名单管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.blacklist"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建日期
		return new OrderCondition("b.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select b.id,b.status_,b.code,cm.name drivers,m.name motorcade_name,c.plate_type,c.plate_no,b.type_,b.subject,b.lock_date,l.name locker");
		sql.append(",b.unlock_date,u.name unlocker,b.locker_id,b.car_id from BS_BLACKLIST b inner join BS_CARMAN cm on cm.id=b.driver_id ");
		sql.append(" inner join BS_MOTORCADE m on m.id=b.motorcade_id");
		sql.append(" inner join BS_CAR c on c.id=b.car_id");
		sql.append(" inner join BC_IDENTITY_ACTOR l on l.id=b.locker_id");
		sql.append(" left join BC_IDENTITY_ACTOR u on u.id=b.unlocker_id");
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
				map.put("code", rs[i++]);
				map.put("drivers", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("plate", map.get("plate_type").toString() + "."
						+ map.get("plate_no").toString());
				map.put("type_", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("lock_date", rs[i++]);
				map.put("locker", rs[i++]);
				map.put("unlock_date", rs[i++]);
				map.put("unlocker", rs[i++]);

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
				getText("blacklist.status"), 60).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBLStatuses())));
		columns.add(new TextColumn4MapKey("b.code", "code",
				getText("blacklist.code"), 160).setSortable(true));
		if (carManId == null) {
			columns.add(new TextColumn4MapKey("cm.name", "drivers",
					getText("blacklist.driver"), 60).setSortable(true));
		}
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("blacklist.motorcade.name"), 60).setSortable(true));
		if (carId == null) {
			columns.add(new TextColumn4MapKey("c.plate_no", "plate",
					getText("blacklist.car.plateNo"), 80).setSortable(true));
		}
		columns.add(new TextColumn4MapKey("b.type_", "type_",
				getText("blacklist.type"), 100));
		columns.add(new TextColumn4MapKey("b.subject", "subject",
				getText("blacklist.subject"), 160).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.lock_date", "lock_date",
				getText("blacklist.lockDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("l.name", "locker",
				getText("blacklist.locker.name"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.unlock_date", "unlock_date",
				getText("blacklist.unlockDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("u.name", "unlocker",
				getText("blacklist.unlocker.name"), 80).setSortable(true)
				.setUseTitleFromLabel(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.code", "m.name ", "c.plate_type",
				"c.plate_no", "b.subject", "cm.name ", "l.name ", "u.name " };
	}

	@Override
	protected String getFormActionName() {
		return "blacklist";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("b.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("b.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		// // carManId条件
		// if (carManId != null) {
		// return new EqualsCondition("b.driver_id", carManId);
		// } else if (carId != null) {
		// return new EqualsCondition("b.car_id", carId);
		// } else {
		// return null;
		// }
		// carManId条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("b.driver_id", carManId);
		}
		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("b.car_id", carId);
		}
		// 合并条件
		return new AndCondition().add(statusCondition).add(carManIdCondition)
				.add(carIdCondition);

	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (this.status != null || this.status.length() != 0) {
			json.put("status", status);
		}
		// carManId条件
		if (carManId != null) {
			json.put("carManId", carManId);
		}
		// carId条件
		if (carId != null) {
			json.put("carId", carId);
		}
		return json.isEmpty() ? null : json;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return super.getHtmlPageToolbar()
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getBLStatuses(), "status", 0,
								getText("title.click2changeSearchStatus")));
	}

	/**
	 * 状态值转换列表：锁定|解锁|待锁定|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBLStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Blacklist.STATUS_SUODING),
				getText("blacklist.locker"));
		statuses.put(String.valueOf(Blacklist.STATUS_JIESUO),
				getText("blacklist.unlocker"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

}
