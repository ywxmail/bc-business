/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.web.struts2.LinkFormater4CarInfo;
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
import cn.bc.web.formater.DateRangeFormater;
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
	public Long toCarId;

	@Override
	public boolean isReadonly() {
		// 车辆管理/司机管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bs.driver"), getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("d.file_date", Direction.Desc);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String getGridRowClass(List<? extends Object> data,
			Object rowData, int index, int type) {
		if (this.carId != null || this.toCarId != null) {
			// 车辆的迁移记录页签：同一司机最后的那条记录如果是在案的就添加高亮显示样式
			Long _toCarId = (this.carId == null ? this.toCarId : this.carId);
			Map<String, Object> row = (Map<String, Object>) rowData;
			Integer thisToCarId = (Integer) row.get("to_car_id");
			if (thisToCarId != null
					&& thisToCarId.intValue() == _toCarId.intValue()) {
				int id = (Integer) row.get("id");
				int driverId = (Integer) row.get("driver_id");
				Date moveDate = (Date) row.get("move_date");
				int moveType = (Integer) row.get("move_type");

				// 查此司机所在列表中最新的那条信息
				int topId = 0;
				for (Map<String, Object> r : (List<Map<String, Object>>) data) {
					if (((Integer) r.get("driver_id")).intValue() == driverId
							&& moveDate.before((Date) r.get("move_date"))) {
						topId = (Integer) r.get("id");
					}
				}

				if ((topId == 0 || topId == id)
						&& CarByDriverHistory.isActive(moveType)) {
					return "ui-state-active";// 高亮样式
				}
			}
		}
		return null;
	}

	@Override
	protected List<Map<String, Object>> rebuildGridData(
			List<Map<String, Object>> data) {
		return super.rebuildGridData(data);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select d.id,m.phone,m.name,nc.plate_type newPlateType,nc.plate_no newPlateNo,d.to_classes");
		sql.append(",nm.name newMotoreade,d.to_unit,oc.plate_type oldPlateType,oc.plate_no oldPlateNo,d.from_classes");
		sql.append(",om.name oldMotoreade,d.from_unit,d.move_type,d.move_date,d.driver_id,d.from_car_id,d.to_car_id");
		sql.append(",d.from_motorcade_id,d.to_motorcade_id,d.shiftwork,d.end_date,m.cert_fwzg from BS_CAR_DRIVER_HISTORY d");
		sql.append(" left join bs_carman m on m.id=d.driver_id");
		sql.append(" left join bs_car  oc on oc.id=d.from_car_id");
		sql.append(" left join bs_car  nc on nc.id=d.to_car_id");
		sql.append(" left join bs_motorcade  om on om.id=d.from_motorcade_id");
		sql.append(" left join bs_motorcade  nm on nm.id=d.to_motorcade_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("phone", rs[i++]);
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
				map.put("shiftwork", rs[i++]);
				map.put("end_date", rs[i++]);
				map.put("cert_fwzg", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("d.id", "id"));
		columns.add(new TextColumn4MapKey("m.cert_fwzg", "cert_fwzg",
				getText("carByDriverHistory.cert_fwzg"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
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
		columns.add(new TextColumn4MapKey("m.phone", "phone",
				getText("carByDriverHistory.phone"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("d.move_type", "move_type",
				getText("carByDriverHistory.moveType"), 140)
				.setValueFormater(new KeyValueFormater(getMoveType())));
		columns.add(new TextColumn4MapKey("d.move_date", "move_date",
				getText("carByDriverHistory.vMoveDate"), 180)
				.setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
					@Override
					public Date getToDate(Object context, Object value) {
						@SuppressWarnings("rawtypes")
						Map contract = (Map) context;
						return (Date) contract.get("end_date");
					}
				}.setUseEmptySymbol(true)));
		// 迁往
		columns.add(new TextColumn4MapKey("nc.plate_no", "newPlate",
				getText("carByDriverHistory.moveTo"), 220)
				.setUseTitleFromLabel(true).setValueFormater(
						new LinkFormater4Move(this.getContextPath()
								+ "/bc-business/car/edit?id={0}", "car",
								"to_unit", "newMotoreade", "newPlate",
								"to_car_id", "to_classes", getType(), true)

				));
		// 迁自
		columns.add(new TextColumn4MapKey("oc.plate_no", "oldPlate",
				getText("carByDriverHistory.moveFrom"), 220)
				.setUseTitleFromLabel(true).setValueFormater(
						new LinkFormater4Move(this.getContextPath()
								+ "/bc-business/car/edit?id={0}", "car",
								"from_unit", "oldMotoreade", "oldPlate",
								"from_car_id", "from_classes", getType(), true)

				));
		columns.add(new TextColumn4MapKey("d.shiftwork", "shiftwork",
				getText("carByDriverHistory.shiftwork"), 400)
				.setValueFormater(new LinkFormater4CarInfo(this
						.getContextPath())));
		return columns;
	}

	@Override
	protected String getFormActionName() {
		return "carByDriverHistory";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['driver']!=null ? ['driver']:['oldPlate']";
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
		// toCarId条件
		Condition newCarIdCondition = null;
		if (toCarId != null) {
			newCarIdCondition = new EqualsCondition("d.to_car_id", toCarId);
		}
		// toCar4FromCar条件
		Condition toCar4FromCarCondition = null;
		if (toCarId != null) {
			toCar4FromCarCondition = new EqualsCondition("d.from_car_id",
					toCarId);
		}
		// newCarId条件
		Condition oldCarIdCondition = null;
		if (carId != null) {
			oldCarIdCondition = new EqualsCondition("d.from_car_id", carId);
		}
		// CarId2ToCarId条件
		Condition carId2ToCarIdCondition = null;
		if (carId != null) {
			carId2ToCarIdCondition = new EqualsCondition("d.to_car_id", carId);
		}
		// 合并条件
		return ConditionUtils.mix2AndCondition(carManIdCondition,
				ConditionUtils.mix2OrCondition(newCarIdCondition,
						carId2ToCarIdCondition, oldCarIdCondition,
						toCar4FromCarCondition));
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
			json.put("toCarId", carId);
		}
		// toCarId条件
		if (toCarId != null) {
			json.put("toCarId", toCarId);
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
		type.put(String.valueOf(CarByDriver.TYPE_WEIDINGYI),
				getText("carByDriver.classes.weidingyi"));
		type.put(String.valueOf(CarByDriver.TYPE_ZHENGBAN),
				getText("carByDriver.classes.zhengban"));
		type.put(String.valueOf(CarByDriver.TYPE_FUBAN),
				getText("carByDriver.classes.fuban"));
		type.put(String.valueOf(CarByDriver.TYPE_DINGBAN),
				getText("carByDriver.classes.dingban"));
		type.put(String.valueOf(CarByDriver.TYPE_ZHUGUA),
				getText("carByDriver.classes.zhugua"));
		return type;
	}

	/**
	 * 获取迁移类型值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getMoveType() {
		Map<String, String> type = new HashMap<String, String>();
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
	protected String getGridDblRowMethod() {
		return "bc.page.open";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(Toolbar
					.getDefaultOpenToolbarButton(getText("label.read")));
			// 搜索按钮
			tb.addButton(Toolbar
					.getDefaultSearchToolbarButton(getText("title.click2search")));

		} else {
			if (carId != null) {
				// 如果车辆Id不这空，调用标准的工具条
				tb.addButton(
						new ToolbarButton().setIcon("ui-icon-document")
								.setText("转车队").setAction("create")).addButton(
						new ToolbarButton().setIcon("ui-icon-pencil")
								.setText("编辑").setAction("edit"));
				// 不能删除历史记录
				// .addButton(
				// new ToolbarButton().setIcon("ui-icon-trash")
				// .setText("删除").setAction("delete"))
				// 搜索按钮
				tb.addButton(this.getDefaultSearchToolbarButton());
			} else {
				tb.addButton(
						new ToolbarButton().setIcon("ui-icon-document")
								.setText("新建")
								.setClick("bc.business.MoveTypeList.select"))
						.addButton(
								new ToolbarButton().setIcon("ui-icon-pencil")
										.setText("编辑").setAction("edit"))
						// 不能删除历史记录
						// .addButton(
						// new ToolbarButton().setIcon("ui-icon-trash")
						// .setText("删除").setAction("delete"))
						// 搜索按钮
						.addButton(this.getDefaultSearchToolbarButton())
						.addButton(
								new ToolbarButton()
										.setIcon("ui-icon-document")
										.setText("顶班处理")
										.setClick(
												"bc.business.chuLiDingBan.create"));
			}

		}
		return tb;
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	public JSONArray moveTypes;// 迁移类型

	@Override
	protected void initConditionsFrom() throws Exception {
		// 可选迁移类型列表
		moveTypes = new JSONArray();
		Map<String, String> mt = getMoveType();
		if (mt != null) {
			JSONObject json;
			Iterator<String> iterator = mt.keySet().iterator();
			String key;
			while (iterator.hasNext()) {
				key = iterator.next();
				json = new JSONObject();
				json.put("label", mt.get(key));
				json.put("value", key);
				moveTypes.put(json);
			}
		}
	}

	// ==高级搜索代码结束==
	protected String getHtmlPageJs() {
		return this.getContextPath()
				+ "/bc-business/carByDriverHistory/list.js,"
				+ this.getContextPath()
				+ "/bc-business/carByDriverHistory/dingBan.js";
	}

}
