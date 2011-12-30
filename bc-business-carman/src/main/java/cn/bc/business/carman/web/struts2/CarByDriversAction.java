/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.Entity;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 司机营运车辆视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarByDriversAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
	public String classes;// 默认全部
	public Long carManId;
	public Long carId;

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("d.status_", Direction.Asc).add(
				"d.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select d.id,d.status_,d.classes,d.desc_,c.plate_type,c.plate_no,b.name,d.car_id");
		sql.append(",d.driver_id from BS_CAR_DRIVER d ");
		sql.append(" inner join BS_CAR c on c.id=d.car_id");
		sql.append(" inner join BS_CARMAN b on b.id=d.driver_id");
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
				map.put("classes", rs[i++]);
				map.put("desc_", rs[i++]);
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("plate", map.get("plate_type").toString() + "."
						+ map.get("plate_no").toString());
				map.put("driver", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("driverId", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("c.id", "id"));
		columns.add(new TextColumn4MapKey("d.status_", "status_",
				getText("carByDriver.statuses"), 60).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses1())));
		if (carManId != null || (carManId == null && carId == null)) {
			columns.add(new TextColumn4MapKey("c.plate_no", "plate",
					getText("carByDriver.car.plateNo"), 100)
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
							return getText("car") + " - " + map.get("plate");

						}
					}));
		}
		if (carId != null || (carManId == null && carId == null)) {
			columns.add(new TextColumn4MapKey("b.name", "driver",
					getText("carByDriver.driver"), 100)
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
						public String getTaskbarTitle(Object context,
								Object value) {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = (Map<String, Object>) context;
							return getText("carByDriver.driver") + " - "
									+ map.get("driver");
						}
					}));
		}
		columns.add(new TextColumn4MapKey("d.classes", "classes",
				getText("carByDriver.classes"), 100)
				.setValueFormater(new KeyValueFormater(getType())));
		columns.add(new TextColumn4MapKey("d.desc_", "desc_",
				getText("carMan.description")).setSortable(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_type", "c.plate_no", "b.name",
				"d.classes" };
	}

	@Override
	protected String getFormActionName() {
		return "carByDriver";
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
		// 状态条件
		Condition statusCondition = ConditionUtils
				.toConditionByComma4IntegerValue(status, "d.status_");

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
		return ConditionUtils.mix2AndCondition(statusCondition,
				carManIdCondition, carIdCondition, classesCondition);
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

	/**
	 * 获取营运班次值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getType() {
		Map<String, String> type = new HashMap<String, String>();
		type = getDriverClasses();
		type.put(String.valueOf(CarByDriver.TYPE_WEIDINGYI),
				getText("carByDriver.classes.weidingyi"));
		return type;
	}

	/**
	 * 营运班次基本类型：正副班\正班\副班\顶班\全部
	 * 
	 * @return
	 */
	private Map<String, String> getDriverClasses() {
		Map<String, String> type;
		type = new LinkedHashMap<String, String>();
		type.put(CarByDriver.TYPE_ZHENGBAN + "," + CarByDriver.TYPE_FUBAN,
				getText("carByDriver.classes.zhengfuban"));
		type.put(String.valueOf(CarByDriver.TYPE_ZHENGBAN),
				getText("carByDriver.classes.zhengban"));
		type.put(String.valueOf(CarByDriver.TYPE_FUBAN),
				getText("carByDriver.classes.fuban"));
		type.put(String.valueOf(CarByDriver.TYPE_DINGBAN),
				getText("carByDriver.classes.dingban"));
		type.put("", getText("carByDriver.classes.quanbu"));
		return type;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		if (this.carId != null || this.carManId != null) {
			return super.getHtmlPageToolbar();
		} else {
			return super.getHtmlPageToolbar().addButton(
					Toolbar.getDefaultToolbarRadioGroup(getDriverClasses(),
							"classes", 4,
							getText("title.click2changeSearchClasses")));
		}
	}

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
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText("批量处理顶班")
					.setClick("bc.business.chuLiDingBan.create"));

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

	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc-business/carByDriver/dingBan.js";
	}
}
