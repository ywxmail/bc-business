/**
 * 
 */
package cn.bc.business.blacklist.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.blacklist.domain.Blacklist;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.LinkFormater4DriverInfo;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
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
	public String status = String.valueOf(Blacklist.STATUS_LOCK); // 黑名单的状态，多个用逗号连接
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
		// 司机页签下的sql
		if (carManId != null) {
			// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
			StringBuffer sql = new StringBuffer();
			sql.append("select b.id,b.status_,b.file_date,b.code,b.drivers,b.company,unit.name,m.name motorcade_name");
			sql.append(",c.plate_type,c.plate_no,b.type_,b.subject,b.lock_date,l.name locker");
			sql.append(",b.unlock_date,u.name unlocker,b.car_id,cb.man_id,md.actor_name modifier,b.modified_date");
			sql.append(",c.return_date,c.company nowCompany,nm.name nowMotorcade,nbia.name nowUnitName");
			sql.append(" from BS_BLACKLIST b");
			sql.append(" left join BS_CARMAN_BLACKLIST cb on cb.blacklist_id=b.id");
			sql.append(" left join BS_CARMAN cm on cm.id=cb.man_id");
			sql.append(" left join BS_MOTORCADE m on m.id=b.motorcade_id");
			sql.append(" left join bc_identity_actor unit on unit.id=m.unit_id");
			sql.append(" left join BS_CAR c on c.id=b.car_id");
			sql.append(" left join BC_IDENTITY_ACTOR l on l.id=b.locker_id");
			sql.append(" left join BC_IDENTITY_ACTOR_HISTORY md on md.id=b.modifier_id");
			sql.append(" left join BC_IDENTITY_ACTOR u on u.id=b.unlocker_id");
			sql.append(" left join bs_motorcade nm on nm.id=c.motorcade_id");
			sql.append(" left join bc_identity_actor nbia on nbia.id=nm.unit_id");

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
					map.put("file_date", rs[i++]);
					map.put("code", rs[i++]);
					map.put("drivers", rs[i++]);
					map.put("company", rs[i++]);
					map.put("unit_name", rs[i++]);
					map.put("motorcade_name", rs[i++]);
					map.put("plate_type", rs[i++]);
					map.put("plate_no", rs[i++]);
					if (map.get("plate_type") == null
							&& map.get("plate_no") == null) {
						map.put("plate", null);
					} else {
						map.put("plate", map.get("plate_type").toString() + "."
								+ map.get("plate_no").toString());
					}
					map.put("type_", rs[i++]);
					map.put("subject", rs[i++]);
					map.put("lock_date", rs[i++]);
					map.put("locker", rs[i++]);
					map.put("unlock_date", rs[i++]);
					map.put("unlocker", rs[i++]);
					map.put("carId", rs[i++]);
					map.put("driverId", rs[i++]);
					map.put("modifier", rs[i++]);
					map.put("modified_date", rs[i++]);
					map.put("return_date", rs[i++]);
					map.put("nowCompany", rs[i++]);
					map.put("nowMotorcade", rs[i++]);
					map.put("nowUnitName", rs[i++]);

					return map;
				}
			});

		} else {
			// 视图的sql
			// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
			StringBuffer sql = new StringBuffer();
			sql.append("select b.id,b.status_,b.file_date,b.code,b.drivers,b.company,unit.name,m.name motorcade_name");
			sql.append(",c.plate_type,c.plate_no,b.type_,b.subject,b.lock_date,l.name locker");
			sql.append(",b.unlock_date,u.name unlocker,b.car_id,md.actor_name modifier,b.modified_date,b.appoint_date,b.conversion_type");
			sql.append(",c.return_date,r.name relatedDepartmennts,c.company nowCompany,nm.name nowMotorcade,nbia.name nowUnitName");
			sql.append(" from BS_BLACKLIST b");
			sql.append(" left join BS_MOTORCADE m on m.id=b.motorcade_id");
			sql.append(" left join bc_identity_actor unit on unit.id=m.unit_id");
			sql.append(" left join BS_CAR c on c.id=b.car_id");
			sql.append(" left join BC_IDENTITY_ACTOR l on l.id=b.locker_id");
			sql.append(" left join BC_IDENTITY_ACTOR_HISTORY md on md.id=b.modifier_id");
			sql.append(" left join BC_IDENTITY_ACTOR u on u.id=b.unlocker_id");
			sql.append(" left join BC_IDENTITY_ACTOR r on r.id=b.related_departmennts_id");
			sql.append(" left join bs_motorcade nm on nm.id=c.motorcade_id");
			sql.append(" left join bc_identity_actor nbia on nbia.id=nm.unit_id");

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
					map.put("file_date", rs[i++]);
					map.put("code", rs[i++]);
					map.put("drivers", rs[i++]);
					map.put("company", rs[i++]);
					map.put("unit_name", rs[i++]);
					map.put("motorcade_name", rs[i++]);
					map.put("plate_type", rs[i++]);
					map.put("plate_no", rs[i++]);
					if (map.get("plate_type") == null
							&& map.get("plate_no") == null) {
						map.put("plate", null);
					} else {
						map.put("plate", map.get("plate_type").toString() + "."
								+ map.get("plate_no").toString());
					}
					map.put("type_", rs[i++]);
					map.put("subject", rs[i++]);
					map.put("lock_date", rs[i++]);
					map.put("locker", rs[i++]);
					map.put("unlock_date", rs[i++]);
					map.put("unlocker", rs[i++]);
					map.put("carId", rs[i++]);
					map.put("modifier", rs[i++]);
					map.put("modified_date", rs[i++]);
					map.put("appoint_date", rs[i++]);
					map.put("conversion_type", rs[i++]);
					map.put("return_date", rs[i++]);
					map.put("relatedDepartmennts", rs[i++]);
					map.put("nowCompany", rs[i++]);
					map.put("nowMotorcade", rs[i++]);
					map.put("nowUnitName", rs[i++]);

					return map;
				}
			});
		}

		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("b.id", "id"));
		columns.add(new TextColumn4MapKey("b.status_", "status_",
				getText("blacklist.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBLStatuses())));
		columns.add(new TextColumn4MapKey("b.file_date", "file_date",
				getText("label.fileDate"), 85).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		// 营运司机
		columns.add(new TextColumn4MapKey("b.drivers", "drivers",
				getText("blacklist.driver"), 160)
				.setValueFormater(new LinkFormater4DriverInfo(this
						.getContextPath())));
		// 公司
		columns.add(new TextColumn4MapKey("c.company", "nowCompany",
				getText("blacklist.company"), 50).setSortable(true)
				.setUseTitleFromLabel(true));
		// 分公司
		columns.add(new TextColumn4MapKey("nbia.name", "nowUnitName",
				getText("blacklist.unit"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车队
		columns.add(new TextColumn4MapKey("nm.name", "nowMotorcade",
				getText("blacklist.motorcade.name"), 60).setSortable(true));

		if (carId == null) {
			// columns.add(new TextColumn4MapKey("c.plate_no", "plate",
			// getText("blacklist.car.plateNo"), 80).setSortable(true));

			columns.add(new TextColumn4MapKey("c.plate_no", "plate",
					getText("blacklist.car.plateNo"), 80)
					.setValueFormater(new LinkFormater4Id(this.getContextPath()
							+ "/bc-business/car/edit?id={0}", "car") {
						@SuppressWarnings("unchecked")
						@Override
						public String getIdValue(Object context, Object value) {
							return StringUtils
									.toString(((Map<String, Object>) context)
											.get("carId"));
						}

						@Override
						public String getTaskbarTitle(Object context,
								Object value) {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = (Map<String, Object>) context;
							return getText("blacklist.car.plateNo") + " - "
									+ map.get("plate");

						}
					}));

		}
		columns.add(new TextColumn4MapKey("b.type_", "type_",
				getText("blacklist.type"), 100)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						Date appointDate = (Date) map.get("appoint_date");
						String conversionType = (String) map
								.get("conversion_type");
						Date now = new Date();
						if (appointDate != null) {
							if (now.after(appointDate)
									|| appointDate.equals(now)) {
								return conversionType;
							} else {
								return (String) value;
							}
						} else {
							return (String) value;
						}
					}
				}));
		columns.add(new TextColumn4MapKey("b.subject", "subject",
				getText("blacklist.subject"), 160).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("r.name", "relatedDepartmennts",
				getText("blacklist.relatedDepartmennts"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("l.name", "locker",
				getText("blacklist.locker.name"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.unlock_date", "unlock_date",
				getText("blacklist.unlockDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("u.name", "unlocker",
				getText("blacklist.unlocker.name"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.return_date", "return_date",
				getText("blacklist.returnDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("md.name", "modifier",
				getText("blacklist.modifier.name"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.modified_date", "modified_date",
				getText("blacklist.modifiedDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("b.code", "code",
				getText("blacklist.code"), 160).setSortable(true));
		// 事发公司
		columns.add(new TextColumn4MapKey("b.company", "company",
				getText("blacklist.company4Happen"), 50).setSortable(true)
				.setUseTitleFromLabel(true));
		// 事发分公司
		columns.add(new TextColumn4MapKey("unit.name", "unit_name",
				getText("blacklist.unit4Happen"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		// 事发车队
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("blacklist.motorcade4Happen.name"), 60)
				.setSortable(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.code", "b.company", "unit.name", "m.name",
				"c.plate_type", "c.plate_no", "b.subject", "b.type_", "l.name",
				"u.name", "b.drivers" };

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
		return "'黑名单 ' + ['plate']";
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

		Condition carManIdCondition = null;
		if (carManId != null) {

			carManIdCondition = new EqualsCondition("cb.man_id", carManId);
		}
		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("b.car_id", carId);
		}
		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition,
				carManIdCondition, carIdCondition);

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
		statuses.put(String.valueOf(Blacklist.STATUS_LOCK),
				getText("blacklist.locker"));
		statuses.put(String.valueOf(Blacklist.STATUS_UNLOCK),
				getText("blacklist.unlocker"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	private MotorcadeService motorcadeService;
	private ActorService actorService;

	@Autowired
	public void setActorService(
			@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	public JSONArray motorcades;// 车队的下拉列表信息
	public JSONArray units;// 分公司的下拉列表信息

	@Override
	protected void initConditionsFrom() throws Exception {
		// 可选分公司列表
		units = OptionItem.toLabelValues(this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT }, (Integer[]) null), "name",
				"id");

		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));
	}

	// ==高级搜索代码结束==
}
