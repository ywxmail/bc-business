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
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.Entity;
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
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
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
	public String status = String.valueOf(Entity.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
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
		return new OrderCondition("d.status_", Direction.Asc).add(
				"d.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select d.id,d.status_,m.cert_fwzg,m.name,nc.plate_type newPlateType,nc.plate_no newPlateNo,d.new_driver_state");
		sql.append(",nm.name newMotoreade,d.to_unit,oc.plate_type oldPlateType,oc.plate_no oldPlateNo,d.old_driver_state");
		sql.append(",om.name oldMotoreade,d.from_unit,d.move_type,d.move_date,d.driver_id,d.old_car_id,d.new_car_id");
		sql.append(",d.old_motorcade_id,d.new_motorcade_id from BS_CAR_DRIVER d");
		sql.append(" left join BS_CARMAN m on m.id=d.driver_id");
		sql.append(" left join BS_CAR  oc on oc.id=d.old_car_id");
		sql.append(" left join BS_CAR  nc on nc.id=d.new_car_id");
		sql.append(" left join BS_Motorcade  om on om.id=d.old_motorcade_id");
		sql.append(" left join BS_Motorcade  nm on nm.id=d.new_motorcade_id");
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
				map.put("new_driver_state", rs[i++]);
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
				map.put("old_driver_state", rs[i++]);
				map.put("oldMotoreade", rs[i++]);
				map.put("from_unit", rs[i++]);
				map.put("move_type", rs[i++]);
				map.put("move_date", rs[i++]);
				map.put("driver_id", rs[i++]);
				map.put("old_car_id", rs[i++]);
				map.put("new_car_id", rs[i++]);
				map.put("old_motorcade_id", rs[i++]);
				map.put("new_motorcade_id", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("d.id", "id"));
		columns.add(new TextColumn4MapKey("d.status_", "status_",
				getText("carByDriver.statuses"), 50).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses1())));
		columns.add(new TextColumn4MapKey("d.cert_fwzg", "cert_fwzg",
				getText("carByDriver.cert_fwzg"), 80).setSortable(true));
		if (carId != null || (carManId == null && carId == null)) {
			columns.add(new TextColumn4MapKey("d.name", "driver",
					getText("carByDriver.driver"), 80)
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
							return getText("carByDriver.driver") + " - "
									+ map.get("driver");
						}
					}));
		}
		// if (carManId != null || (carManId == null && carId == null)) {
		columns.add(new TextColumn4MapKey("d.newPlateType", "newPlate",
				getText("carByDriver.newCar"), 100)
				.setValueFormater(new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/car/edit?id={0}", "newPlate") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("new_car_id"));
					}

					@Override
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("car") + " - " + map.get("newPlate");

					}
				}));
		// }
		columns.add(new TextColumn4MapKey("d.new_driver_state",
				"new_driver_state", getText("carByDriver.newDriverState"), 50)
				.setValueFormater(new KeyValueFormater(getType())));
		columns.add(new TextColumn4MapKey("d.newMotoreade", "newMotoreade",
				getText("carByDriver.newMotorcade"), 120)
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
												.get("new_motorcade_id"));
							}
						}));
		columns.add(new TextColumn4MapKey("d.to_unit", "to_unit",
				getText("carByDriver.toUnit"), 100).setSortable(true));

		columns.add(new TextColumn4MapKey("d.oldPlateType", "oldPlate",
				getText("carByDriver.oldCar"), 100)
				.setValueFormater(new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/car/edit?id={0}", "oldPlate") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("old_car_id"));
					}

					@Override
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("car") + " - " + map.get("oldPlate");

					}
				}));

		columns.add(new TextColumn4MapKey("d.old_driver_state",
				"old_driver_state", getText("carByDriver.oldDriverState"), 50)
				.setValueFormater(new KeyValueFormater(getType())));
		columns.add(new TextColumn4MapKey("d.oldMotoreade", "oldMotoreade",
				getText("carByDriver.oldMotorcade"), 120)
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
												.get("old_motorcade_id"));
							}
						}));
		columns.add(new TextColumn4MapKey("d.from_unit", "from_unit",
				getText("carByDriver.fromUnit"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("d.move_type",
				"move_type", getText("carByDriver.moveType"), 100)
				.setValueFormater(new KeyValueFormater(getMoveType())));
		columns.add(new TextColumn4MapKey("d.move_date", "move_date",
				getText("carByDriver.moveDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

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
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(550).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate'] ";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("d.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("d.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}
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
	 * @return
	 */
	protected Map<String, String> getMoveType() {
		Map<String, String> type = new HashMap<String, String>();
		type = new HashMap<String, String>();
		type.put(String.valueOf(CarByDriver.MOVETYPE_CLDCL),
				getText("carByDriver.moveType.cheliangdaocheliang"));
		type.put(String.valueOf(CarByDriver.MOVETYPE_GSDGSYZX),
				getText("carByDriver.moveType.gongsidaogongsiyizhuxiao"));
		type.put(String.valueOf(CarByDriver.MOVETYPE_ZXWYQX),
				getText("carByDriver.moveType.zhuxiaoweiyouquxiang"));
		type.put(String.valueOf(CarByDriver.MOVETYPE_YWGSQH),
				getText("carByDriver.moveType.youwaigongsiqianhui"));
		type.put(String.valueOf(CarByDriver.MOVETYPE_JHWZX),
				getText("carByDriver.moveType.jiaohuiweizhuxiao"));
		type.put(String.valueOf(CarByDriver.MOVETYPE_XRZ),
				getText("carByDriver.moveType.xinruzhi"));
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
