/**
 * 
 */
package cn.bc.business.certLost.web.struts2;

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
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 证照遗失视图Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CertLostsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	// public String status = String.valueOf(BCConstants.STATUS_ENABLED); //
	// 车辆的状态，多个用逗号连接
	public String classes;// 默认全部
	public Long carManId;
	public Long carId;

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建日期
		return new OrderCondition("l.file_date", Direction.Desc);
	}

	@Override
	public boolean isReadonly() {
		// 车辆管理/司机管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bs.driver"), getText("key.role.bc.admin"));
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select i.id,i.cert_name,c.code,c.plate_type,c.plate_no,l.lost_date,i.is_replace,i.replace_date");
		sql.append(",l.handler_name,l.driver,l.driver_id,l.car_id from bs_cert_lost_item i");
		sql.append(" left join bs_cert_lost l on l.id=i.pid");
		sql.append(" left join bs_car c on c.id=l.car_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("cert_name", rs[i++]);
				map.put("code", rs[i++]);
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("plate", map.get("plate_type").toString() + "."
						+ map.get("plate_no").toString());
				map.put("lost_date", rs[i++]);
				map.put("is_replace", rs[i++]);
				map.put("replace_date", rs[i++]);
				map.put("handler_name", rs[i++]);
				map.put("driver", rs[i++]);
				map.put("driverId", rs[i++]);
				map.put("carId", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("i.id", "id"));
		columns.add(new TextColumn4MapKey("i.cert_name", "cert_name",
				getText("certLost.certName")).setSortable(true));
		columns.add(new TextColumn4MapKey("i.code", "code",
				getText("certLost.carCode")).setSortable(true));
		columns.add(new TextColumn4MapKey("c.plate_no", "plate",
				getText("certLost.plate"), 100)
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
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("car") + " - " + map.get("plate");

					}
				}));
		columns.add(new TextColumn4MapKey("l.lost_date", "lost_date",
				getText("certLost.lostDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("i.is_replace", "is_replace",
				getText("certLost.isReplace"), 75).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("i.replace_date", "replace_date",
				getText("certLost.replaceDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("l.driver", "driver",
				getText("certLost.driver"), 100)
				.setValueFormater(new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/carMan/edit?id={0}", "driver") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("driverId"));
					}

					@Override
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("certLost.driver") + " - "
								+ map.get("driver");
					}
				}));
		columns.add(new TextColumn4MapKey("l.handler_name", "handler_name",
				getText("certLost.handlerName")).setSortable(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_type", "c.plate_no", "i.cert_name",
				"c.code", "l.driver", "l.handler_name" };
	}

	@Override
	protected String getFormActionName() {
		return "certLost";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(780).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate'] ";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// // 状态条件
		// Condition statusCondition = ConditionUtils
		// .toConditionByComma4IntegerValue(status, "d.status_");

		// carManId条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("d.driver_id", carManId);
		}

		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("d.car_id", carId);
		}

		// classes条件
		Condition classesCondition = ConditionUtils
				.toConditionByComma4IntegerValue(classes, "d.classes");

		// 合并条件
		return ConditionUtils.mix2AndCondition(carManIdCondition,
				carIdCondition, classesCondition);
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// // 状态条件
		// if (this.status != null || this.status.length() != 0) {
		// json.put("status", status);
		// }
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

	// @Override
	// protected Toolbar getHtmlPageToolbar() {
	// if (this.carId != null || this.carManId != null) {
	// return super.getHtmlPageToolbar();
	// } else {
	// return super.getHtmlPageToolbar().addButton(
	// Toolbar.getDefaultToolbarRadioGroup(getDriverClasses(),
	// "classes", 4,
	// getText("title.click2changeSearchClasses")));
	// }
	// }

	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.read")));
		} else {
			// 新建按钮
			tb.addButton(Toolbar
					.getDefaultCreateToolbarButton(getText("label.create")));

			// 批量处理顶班按钮
			// tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
			// .setText("批量处理顶班")
			// .setClick("bc.business.chuLiDingBan.create"));

			// 编辑按钮
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.edit")));

			if (useDisabledReplaceDelete) {
				// 禁用按钮
				tb.addButton(Toolbar
						.getDefaultDisabledToolbarButton(getText("label.disabled")));
			} else {
				// 删除按钮
				tb.addButton(Toolbar
						.getDefaultDeleteToolbarButton(getText("label.delete")));
			}
		}

		// 搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));

		return tb;
	}

	
}
