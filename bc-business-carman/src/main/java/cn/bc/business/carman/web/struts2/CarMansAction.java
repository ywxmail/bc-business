/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.web.struts2.LinkFormater4CarInfo;
import cn.bc.business.web.struts2.LinkFormater4ChargerInfo;
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
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.formater.SexFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 司机视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarMansAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 司机管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.driver"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("m.status_", Direction.Asc).add(
				"m.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select m.id,c.status_,m.type_,m.name drvierName,m.cert_fwzg,m.cert_fwzg_id,m.cert_identity");
		sql.append(",m.cert_cyzg,m.work_date,m.origin,m.former_unit,m.charger,m.cert_driving_first_date");
		sql.append(",m.cert_driving,m.cert_driving_start_date,m.cert_driving_end_date,m.file_date,m.phone,m.phone1,m.sex,m.birthdate");
		sql.append(",c.plate_type,c.plate_no,bia.name unit_name,mo.name as motorcade,h.move_type,cd.desc_,cd.status_,cd.classes");
		sql.append(" from BS_CARMAN m");
		sql.append(" left join bs_car_driver cd on cd.driver_id=m.id");
		sql.append(" left join bs_car_driver_history h on h.driver_id=m.id");
		sql.append(" left join bs_car c on cd.car_id=c.id");
		sql.append(" left join bs_motorcade mo on c.motorcade_id=mo.id");
		sql.append(" left join bc_identity_actor bia on bia.id=mo.unit_id");
		// sql.append(" where cd.status_=0 and h.main=0 and(cd.classes=1 or cd.classes=2 or cd.classes=4)");
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
				map.put("type_", rs[i++]);
				map.put("drvierName", rs[i++]);
				map.put("cert_fwzg", rs[i++]);
				map.put("cert_fwzg_id", rs[i++]);
				map.put("cert_identity", rs[i++]);
				map.put("cert_cyzg", rs[i++]);
				map.put("work_date", rs[i++]);
				map.put("origin", rs[i++]);
				map.put("former_unit", rs[i++]);
				map.put("charger", rs[i++]);
				map.put("cert_driving_first_date", rs[i++]);
				map.put("cert_driving", rs[i++]);
				map.put("cert_driving_start_date", rs[i++]);
				map.put("cert_driving_end_date", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("phone1", rs[i++]);
				map.put("phone2", rs[i++]);
				map.put("sex", rs[i++]);
				map.put("birth_date", rs[i++]);
				// ==========================
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("unit_name", rs[i++]);
				map.put("motorcade", rs[i++]);
				map.put("move_type", rs[i++]);
				map.put("desc_", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("m.id", "id"));
		columns.add(new TextColumn4MapKey("m.status_", "status_",
				getText("carMan.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses1())));
		columns.add(new TextColumn4MapKey("m.file_date", "file_date",
				getText("carMan.fileDate"), 85).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.work_date", "work_date",
				getText("carMan.workDate"), 85).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.type_", "type_",
				getText("carMan.type"), 80).setSortable(true).setValueFormater(
				new KeyValueFormater(getType())));
		columns.add(new TextColumn4MapKey("m.name", "drvierName",
				getText("carMan.name"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("m.sex", "sex",
				getText("carMan.sex"), 40).setSortable(true).setValueFormater(
				new SexFormater()));
		// =================
		columns.add(new TextColumn4MapKey("h.move_type", "move_type",
				getText("carMan.move_type"), 120)
				.setValueFormater(new KeyValueFormater(getMoveType())));
		columns.add(new TextColumn4MapKey("bia.name", "unit_name",
				getText("carMan.unit_name"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("mo.name", "motorcade",
				getText("carMan.motorcade"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("cd.desc_", "desc_",
				getText("carMan.operationCar"), 560)
				.setValueFormater(new LinkFormater4CarInfo(this
						.getContextPath())));
		// =================
		columns.add(new TextColumn4MapKey("m.birthdate", "birth_date",
				getText("carMan.birthdate"), 85).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.cert_fwzg", "cert_fwzg",
				getText("carMan.cert4FWZG"), 80));
		columns.add(new TextColumn4MapKey("m.phone", "phone1",
				getText("carMan.phone1"), 100).setSortable(false)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.phone1", "phone2",
				getText("carMan.phone2"), 100).setSortable(false)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.origin", "origin",
				getText("carMan.origin"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.former_unit", "former_unit",
				getText("carMan.formerUnit"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.cert_driving_first_date",
				"cert_driving_first_date",
				getText("carMan.cert4DrivingFirstDateView"), 120).setSortable(
				true).setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.charger", "charger",
				getText("carMan.charger"), 100)
				.setValueFormater(new LinkFormater4ChargerInfo(this
						.getContextPath())));
		columns.add(new TextColumn4MapKey("m.cert_identity", "cert_identity",
				getText("carMan.cert4Indentity"), 160).setSortable(true));
		columns.add(new TextColumn4MapKey("m.cert_cyzg", "cert_cyzg",
				getText("carMan.cert4CYZG"), 120));
		columns.add(new TextColumn4MapKey("m.cert_driving", "cert_driving",
				getText("carMan.cert4Driving"), 160));
		columns.add(new TextColumn4MapKey("m.cert_driving_start_date",
				"cert_driving_start_date",
				getText("carMan.cert4DrivingDeadline"), 180)
				.setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
					@Override
					public Date getToDate(Object context, Object value) {
						@SuppressWarnings("rawtypes")
						Map contract = (Map) context;
						return (Date) contract.get("cert_driving_end_date");
					}
				}));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "m.name", "m.origin", "m.cert_identity",
				"m.cert_cyzg", "m.cert_fwzg", "m.phone" };
	}

	@Override
	protected String getFormActionName() {
		return "carMan";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['drvierName']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("m.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("m.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		} else {
			return null;
		}
		// 营运班次为在案的条件
		Condition classesStatusCondition = null;
		classesStatusCondition = new EqualsCondition("cd.status_",
				BCConstants.STATUS_ENABLED);
		// 迁移记录为当前的条件
		Condition mainCondition = null;
		mainCondition = new EqualsCondition("h.main",
				CarByDriverHistory.MAIN_NOW);
		// 驾驶状态为正班的条件
		Condition zhengbanCondition = null;
		zhengbanCondition = new EqualsCondition("cd.classes",
				CarByDriver.TYPE_ZHENGBAN);
		// 驾驶状态为副班的条件
		Condition fubanCondition = null;
		fubanCondition = new EqualsCondition("cd.classes",
				CarByDriver.TYPE_FUBAN);
		// 驾驶状态为主挂的条件
		Condition zhuguaCondition = null;
		zhuguaCondition = new EqualsCondition("cd.classes",
				CarByDriver.TYPE_ZHUGUA);

		// 合并条件
		return ConditionUtils.mix2AndCondition(
				statusCondition,
				classesStatusCondition,
				mainCondition,
				ConditionUtils.mix2OrCondition(zhengbanCondition,
						fubanCondition, zhuguaCondition).setAddBracket(true));

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

	/**
	 * 获取司机分类值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getType() {
		Map<String, String> type = new HashMap<String, String>();
		type = new HashMap<String, String>();
		type.put(String.valueOf(CarMan.TYPE_DRIVER),
				getText("carMan.type.driver"));
		type.put(String.valueOf(CarMan.TYPE_CHARGER),
				getText("carMan.type.charger"));
		type.put(String.valueOf(CarMan.TYPE_DRIVER_AND_CHARGER),
				getText("carMan.type.driverAndCharger"));
		return type;
	}

	/**
	 * 获取迁移类型值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getMoveType() {
		Map<String, String> type = new HashMap<String, String>();
		type = new HashMap<String, String>();
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_CLDCL),
				getText("carByDriverHistory.moveType.cheliangdaocheliang"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_GSDGSYZX),
				getText("carByDriverHistory.moveType.gongsidaogongsiyizhuxiao"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_ZXWYQX),
				getText("carByDriverHistory.moveType.zhuxiaoweiyouquxiang"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_YWGSQH),
				getText("carByDriverHistory.moveType.youwaigongsiqianhui"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_JHWZX),
				getText("carByDriverHistory.moveType.jiaohuiweizhuxiao"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_XRZ),
				getText("carByDriverHistory.moveType.xinruzhi"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_ZCD),
				getText("carByDriverHistory.moveType.cheduidaochedui"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_DINGBAN),
				getText("carByDriverHistory.moveType.dingban"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_JHZC),
				getText("carByDriverHistory.moveType.jiaohuizhuanche"));
		return type;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
		} else {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());

			// 取消删除按钮
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		// 状态单选按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getBSStatuses1(),
				"status", 0, getText("title.click2changeSearchStatus")));
		return tb;
	}

	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
}
