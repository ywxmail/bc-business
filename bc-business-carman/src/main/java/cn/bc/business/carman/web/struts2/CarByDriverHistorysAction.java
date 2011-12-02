/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
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
import cn.bc.web.formater.CalendarFormater;
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
 * 迁移记录视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarByDriverHistorysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	// public String status = String.valueOf(Entity.STATUS_ENABLED); //
	// 车辆的状态，多个用逗号连接
	public Long carManId;
	public Long carId;

	@Override
	public boolean isReadonly() {
		// 车辆管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bs.driver"), getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("d.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select d.id,m.cert_fwzg,m.name,nc.plate_type newPlateType,nc.plate_no newPlateNo,d.to_classes");
		sql.append(",nm.name newMotoreade,d.to_unit,oc.plate_type oldPlateType,oc.plate_no oldPlateNo,d.from_classes");
		sql.append(",om.name oldMotoreade,d.from_unit,d.move_type,d.move_date,d.driver_id,d.from_car_id,d.to_car_id");
		sql.append(",d.from_motorcade_id,d.to_motorcade_id from BS_CAR_DRIVER_HISTORY d");
		sql.append(" left join BS_CARMAN m on m.id=d.driver_id");
		sql.append(" left join BS_CAR  oc on oc.id=d.from_car_id");
		sql.append(" left join BS_CAR  nc on nc.id=d.to_car_id");
		sql.append(" left join BS_Motorcade  om on om.id=d.from_motorcade_id");
		sql.append(" left join BS_Motorcade  nm on nm.id=d.to_motorcade_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("cert_fwzg", rs[i++]);
				map.put("driver", rs[i++]);
				map.put("newPlateType", rs[i++]);
				map.put("newPlateNo", rs[i++]);
				if (map.get("newPlateType") == null
						&& map.get("newPlateNo") == null) {
					map.put("oldPlate", null);
				} else {
					map.put("newPlate", map.get("newPlateType").toString()
							+ "." + map.get("newPlateNo").toString());
				}
				map.put("to_classes", rs[i++]);
				map.put("newMotoreade", rs[i++]);
				map.put("to_unit", rs[i++]);
				map.put("oldPlateType", rs[i++]);
				map.put("oldPlateNo", rs[i++]);
				if (map.get("oldPlateType") == null
						&& map.get("oldPlateNo") == null) {
					map.put("oldPlate", null);
				} else {
					map.put("oldPlate", map.get("oldPlateType").toString()
							+ "." + map.get("oldPlateNo").toString());
				}
				map.put("from_classes", rs[i++]);
				map.put("oldMotoreade", rs[i++]);
				map.put("from_unit", rs[i++]);
				map.put("move_type", rs[i++]);
				map.put("move_date", rs[i++]);
				map.put("driver_id", rs[i++]);
				map.put("from_car_id", rs[i++]);
				map.put("to_car_id", rs[i++]);
				map.put("from_motorcade_id", rs[i++]);
				map.put("to_motorcade_id", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("d.id", "id"));
		columns.add(new TextColumn4MapKey("d.cert_fwzg", "cert_fwzg",
				getText("carByDriverHistory.cert_fwzg"), 80).setSortable(true));
		if (carId != null || (carManId == null && carId == null)) {
			columns.add(new TextColumn4MapKey("d.name", "driver",
					getText("carByDriverHistory.driver"), 80)
					.setValueFormater(new LinkFormater4Id(this.getContextPath()
							+ "/bc-business/carMan/edit?id={0}", "driver") {
						@SuppressWarnings("unchecked")
						@Override
						public String getIdValue(Object context, Object value) {
							return StringUtils
									.toString(((Map<String, Object>) context)
											.get("driver_id"));
						}

						@Override
						public String getTaskbarTitle(Object context,
								Object value) {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = (Map<String, Object>) context;
							return getText("carByDriverHistory.driver") + " - "
									+ map.get("driver");
						}
					}));
		}
		// if (carManId != null || (carManId == null && carId == null)) {
		columns.add(new TextColumn4MapKey("nc.newPlateType", "newPlate",
				getText("carByDriverHistory.newCar"), 100)
				.setValueFormater(new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/car/edit?id={0}", "newPlate") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("to_car_id"));
					}

					@Override
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("car") + " - " + map.get("newPlate");

					}
				}));
		// }
		columns.add(new TextColumn4MapKey("d.to_classes", "to_classes",
				getText("carByDriverHistory.newDriverState"), 50)
				.setValueFormater(new KeyValueFormater(getType())));
		columns.add(new TextColumn4MapKey("d.to_motorcade_id", "newMotoreade",
				getText("carByDriverHistory.newMotorcade"), 120)
				.setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/motorcade/edit?id={0}",
								"newMotoreade") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("to_motorcade_id"));
							}
						}));
		columns.add(new TextColumn4MapKey("d.to_unit", "to_unit",
				getText("carByDriverHistory.toUnit"), 100).setSortable(true));

		columns.add(new TextColumn4MapKey("d.oldPlateType", "oldPlate",
				getText("carByDriverHistory.oldCar"), 100)
				.setValueFormater(new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/car/edit?id={0}", "oldPlate") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("from_car_id"));
					}

					@Override
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("car") + " - " + map.get("oldPlate");

					}
				}));

		columns.add(new TextColumn4MapKey("d.from_classes", "from_classes",
				getText("carByDriverHistory.oldDriverState"), 50)
				.setValueFormater(new KeyValueFormater(getType())));
		columns.add(new TextColumn4MapKey("d.oldMotoreade", "oldMotoreade",
				getText("carByDriverHistory.oldMotorcade"), 120)
				.setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/motorcade/edit?id={0}",
								"oldMotoreade") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("from_motorcade_id"));
							}
						}));
		columns.add(new TextColumn4MapKey("d.from_unit", "from_unit",
				getText("carByDriverHistory.fromUnit"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("d.move_type", "move_type",
				getText("carByDriverHistory.moveType"), 140)
				.setValueFormater(new KeyValueFormater(getMoveType())));
		columns.add(new TextColumn4MapKey("d.move_date", "move_date",
				getText("carByDriverHistory.moveDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		return columns;
	}

	@Override
	protected String getFormActionName() {
		return "carByDriverHistory";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(550).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "name";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "m.name", "nc.plate_type", "nc.plate_no",
				"nm.name", "d.to_unit", "oc.plate_type", "oc.plate_no",
				"om.name", "d.from_unit", "m.cert_fwzg" };
	}

	@Override
	protected Condition getGridSpecalCondition() {

		// carManId条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("d.driver_id", carManId);
		}
		// newCarId条件
		Condition newCarIdCondition = null;
		if (carId != null) {
			newCarIdCondition = new EqualsCondition("d.to_car_id", carId);
		}
		// newCarId条件
		Condition oldCarIdCondition = null;
		if (carId != null) {
			oldCarIdCondition = new EqualsCondition("d.from_car_id", carId);
		}
		// Condition carIdCondition = new OrCondition().add(newCarIdCondition)
		// .add(oldCarIdCondition);
		// 合并条件
		return ConditionUtils.mix2AndCondition(carManIdCondition,
				ConditionUtils.mix2OrCondition(newCarIdCondition,
						oldCarIdCondition));
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();

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
		type = new HashMap<String, String>();
		type.put(String.valueOf(CarByDriver.TYPE_WEIDINGYI),
				getText("carByDriver.classes.weidingyi"));
		type.put(String.valueOf(CarByDriver.TYPE_ZHENGBAN),
				getText("carByDriver.classes.zhengban"));
		type.put(String.valueOf(CarByDriver.TYPE_FUBAN),
				getText("carByDriver.classes.fuban"));
		type.put(String.valueOf(CarByDriver.TYPE_DINGBAN),
				getText("carByDriver.classes.dingban"));
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
		return type;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		tb.addButton(
				new ToolbarButton().setIcon("ui-icon-document").setText("新建")
						.setClick("bc.business.MoveTypeList.select"))
				.addButton(
						new ToolbarButton().setIcon("ui-icon-pencil")
								.setText("编辑").setAction("edit"))
				.addButton(
						new ToolbarButton().setIcon("ui-icon-trash")
								.setText("删除").setAction("delete"))
				.addButton(
						Toolbar.getDefaultSearchToolbarButton(getText("title.click2search")));

		return tb;
	}

	protected String getHtmlPageJs() {
		return this.getContextPath()
				+ "/bc-business/carByDriverHistory/list.js";
	}
}
