/**
 * 
 */
package cn.bc.business.car.web.struts2;

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
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;
import cn.bc.web.ui.json.Json;

/**
 * 车辆视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
	public Long carManId;
	public Long carId;

	@Override
	public boolean isReadonly() {
		// 车辆管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|登记日期|车队
		return new OrderCondition("c.status_", Direction.Asc).add(
				"c.register_date", Direction.Desc).add("m.name", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.status_,c.plate_type,c.plate_no,c.engine_no,c.driver,c.charger,c.factory_type,c.factory_model");
		sql.append(",c.cert_no2,c.register_date,c.bs_type,c.code,c.origin_no,c.vin");
		sql.append(",c.motorcade_id,m.name");
		sql.append(" from bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
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
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("engine_no", rs[i++]);
				map.put("driver", rs[i++]);
				map.put("charger", rs[i++]);
				map.put("factory_type", rs[i++]);
				map.put("factory_model", rs[i++]);
				map.put("cert_no2", rs[i++]);
				map.put("register_date", rs[i++]);
				map.put("bs_type", rs[i++]);
				map.put("code", rs[i++]);
				map.put("origin_no", rs[i++]);
				map.put("vin", rs[i++]);
				map.put("motorcade_id", rs[i++]);
				map.put("motorcade_name", rs[i++]);
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
				getText("car.status"), 60).setSortable(true).setValueFormater(
				new EntityStatusFormater(getBSStatuses1())));
		columns.add(new TextColumn4MapKey("c.register_date", "register_date",
				getText("car.registerDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("car.motorcade"), 80)
				.setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/motorcade/edit?id={0}",
								"motorcade") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("motorcade_id"));
							}
						}));
		columns.add(new TextColumn4MapKey("c.plate_no", "plate_no",
				getText("car.plate"), 80).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> car = (Map<String, Object>) context;
						return car.get("plate_type") + "."
								+ car.get("plate_no");
					}
				}));
		columns.add(new TextColumn4MapKey("c.engine_no", "engine_no",
				getText("car.engineNo"), 80));
		columns.add(new TextColumn4MapKey("c.bs_type", "bs_type",
				getText("car.businessType"), 100));
		columns.add(new TextColumn4MapKey("c.origin_no", "origin_no",
				getText("car.originNo"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.cert_no2", "cert_no2",
				getText("car.certNo2"), 100));
		columns.add(new TextColumn4MapKey("c.driver", "driver",
				getText("car.carMan")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.charger", "charger",
				getText("car.charger"), 100).setUseTitleFromLabel(true));

		columns.add(new TextColumn4MapKey("c.factory_type", "factory_type",
				getText("car.factory"), 120).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						// 从上下文取出元素Map
						@SuppressWarnings("unchecked")
						Map<String, Object> car = (Map<String, Object>) context;
						if (car.get("factory_type") != null
								&& car.get("factory_model") != null) {
							return car.get("factory_type") + " "
									+ car.get("factory_model");
						} else if (car.get("factory_model") != null) {
							return car.get("factory_model").toString();
						} else if (car.get("factory_type") != null) {
							return car.get("factory_type") + " ";
						} else {
							return "";
						}
					}
				}));
		columns.add(new TextColumn4MapKey("c.vin", "vin", getText("car.vin"),
				120).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("car.code"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_no", "c.driver", "c.charger",
				"c.cert_no2", "c.factory_type", "m.name" };
	}

	@Override
	protected String getFormActionName() {
		return "car";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate_type'] + '.' + ['plate_no']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		return ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"c.status_");
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
								this.getBSStatuses1(), "status", 0,
								getText("title.click2changeSearchStatus")))
				// 辅助操作
				.addButton(
						new ToolbarMenuButton("辅助操作")
								.addMenuItem("金盾网交通违法查询：跳转",
										"jinDun-jiaoTongWeiFa")
								.addMenuItem("金盾网交通违法查询：抓取",
										"jinDun-jiaoTongWeiFa-spider")
								.setChange("bs.carView.selectMenuButtonItem"));
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc-business/car/view.js";
	}
}
