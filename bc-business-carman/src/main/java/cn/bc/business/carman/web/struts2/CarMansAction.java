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
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
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
		return new OrderCondition("c.status_", Direction.Asc).add(
				"c.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.status_,c.type_,c.name,c.cert_fwzg,c.cert_fwzg_id,c.cert_identity");
		sql.append(",c.cert_cyzg,c.work_date,c.origin,c.former_unit,c.cert_driving_first_date");
		sql.append(",c.cert_driving_start_date,c.cert_driving_end_date,c.cert_driving");
		sql.append(",c.file_date from BS_CARMAN c");
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
				map.put("name", rs[i++]);
				map.put("cert_fwzg", rs[i++]);
				map.put("cert_fwzg_id", rs[i++]);
				map.put("cert_identity", rs[i++]);
				map.put("cert_cyzg", rs[i++]);
				map.put("work_date", rs[i++]);
				map.put("origin", rs[i++]);
				map.put("former_unit", rs[i++]);
				map.put("cert_driving_first_date", rs[i++]);
				map.put("cert_driving_start_date", rs[i++]);
				map.put("cert_driving_end_date", rs[i++]);
				map.put("cert_driving", rs[i++]);
				map.put("file_date", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("c.id", "id"));
		columns.add(new TextColumn4MapKey("c.status_", "status_",
				getText("carMan.status"), 60).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses1())));
		columns.add(new TextColumn4MapKey("c.type_", "type_",
				getText("carMan.type"), 80).setSortable(true).setValueFormater(
				new KeyValueFormater(getType())));
		columns.add(new TextColumn4MapKey("c.name", "name",
				getText("carMan.name"), 80).setSortable(true));
		// columns.add(new TextColumn4MapKey("c.cert_fwzg_id", "cert_fwzg_id",
		// getText("carMan.cert4FWZGID"), 80));
		columns.add(new TextColumn4MapKey("c.cert_fwzg", "cert_fwzg",
				getText("carMan.cert4FWZG"), 80));
		columns.add(new TextColumn4MapKey("c.cert_identity", "cert_identity",
				getText("carMan.cert4Indentity"), 160).setSortable(true));
		columns.add(new TextColumn4MapKey("c.cert_cyzg", "cert_cyzg",
				getText("carMan.cert4CYZG"), 120));
		columns.add(new TextColumn4MapKey("c.cert_driving", "cert_driving",
				getText("carMan.cert4Driving"), 160));
		columns.add(new TextColumn4MapKey("c.cert_driving_first_date",
				"cert_driving_first_date",
				getText("carMan.cert4DrivingFirstDateView"), 120).setSortable(
				true).setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.cert_driving_start_date",
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
		columns.add(new TextColumn4MapKey("c.work_date", "work_date",
				getText("carMan.workDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.origin", "origin",
				getText("carMan.origin"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("c.former_unit", "former_unit",
				getText("carMan.formerUnit"), 80).setSortable(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.name", "c.origin", "c.cert_identity",
				"c.cert_cyzg", "c.cert_fwzg" };
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
		return "['name']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("c.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("c.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		} else {
			return null;
		}
		return statusCondition;
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

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return super.getHtmlPageToolbar()
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getBSStatuses1(), "status", 0,
								getText("title.click2changeSearchStatus")));
	}
}
