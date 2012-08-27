/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.web.struts2.LinkFormater4CarInfo;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
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
	public int status4tab;// 页签传递的车辆状态参数
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 迁移记录的状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 迁移记录或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(
				getText("key.role.bs.driverByDriverHistory"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|迁移日期
		return new OrderCondition("d.move_date", Direction.Desc);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String getGridRowClass(List<? extends Object> data,
			Object rowData, int index, int type) {
		Map<String, Object> row = (Map<String, Object>) rowData;
		if ((this.carId != null || this.toCarId != null)
				&& (Integer) row.get("move_type") != CarByDriverHistory.MOVETYPE_ZCD) {
			// 车辆的迁移记录页签：同一司机最后的那条记录如果是在案的就添加高亮显示样式
			Long _toCarId = (this.carId == null ? this.toCarId : this.carId);
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
					if ((Integer) r.get("move_type") != CarByDriverHistory.MOVETYPE_ZCD) {
						if (((Integer) r.get("driver_id")).intValue() == driverId
								&& moveDate.before((Date) r.get("move_date"))) {
							topId = (Integer) r.get("id");
						}
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
		sql.append("select d.id,d.status_,m.phone,m.name,nc.plate_type newPlateType,nc.plate_no newPlateNo,d.to_classes");
		sql.append(",nm.name newMotoreade,d.to_unit,oc.plate_type oldPlateType,oc.plate_no oldPlateNo,d.from_classes");
		sql.append(",om.name oldMotoreade,d.from_unit,d.move_type,d.move_date,d.driver_id,d.from_car_id,d.to_car_id");
		sql.append(",d.from_motorcade_id,d.to_motorcade_id,d.shiftwork,d.end_date,m.cert_fwzg,d.hand_papers_date");
		sql.append(",m.status_ carManStatus,d.desc_ from BS_CAR_DRIVER_HISTORY d");
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
				map.put("status", rs[i++]);
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
				map.put("hand_papers_date", rs[i++]);
				map.put("carManStatus", rs[i++]);
				map.put("desc_", rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("d.id", "id"));
		columns.add(new TextColumn4MapKey("d.status_", "status",
				getText("carByDriverHistory.statuses"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses())));
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
		columns.add(new TextColumn4MapKey("d.hand_papers_date",
				"hand_papers_date",
				getText("carByDriverHistory.handPapersDate"), 100).setSortable(
				true).setValueFormater(new CalendarFormater("yyyy-MM-dd")));
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
		columns.add(new TextColumn4MapKey("d.desc_", "desc_",
				getText("carByDriverHistory.description"), 220).setSortable(
				true).setUseTitleFromLabel(true));
		columns.add(new HiddenColumn4MapKey("carManStatus", "carManStatus"));// 司机的状态，用于是否能删除草稿状态的迁移记录
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
				"om.name", "d.from_unit", "m.cert_fwzg", "d.shiftwork" };
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
		// carId2ShiftworkCarId条件
		Condition carId2ShiftworkCarIdCondition = null;
		if (carId != null) {
			String shiftworkCarId = "%," + carId.toString() + ";%";
			carId2ShiftworkCarIdCondition = new LikeCondition("d.shiftwork",
					shiftworkCarId);
		}
		return ConditionUtils.mix2AndCondition(statusCondition,
				carManIdCondition, ConditionUtils.mix2OrCondition(
						carId2ToCarIdCondition, oldCarIdCondition,
						carId2ShiftworkCarIdCondition));

	}

	@Override
	protected String getGridDblRowMethod() {
		// 强制为只读表单
		return "bs.carByDriverHistoryView.dblclick";
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
		// 司机状态
		if (this.status == null || this.status.length() == 0) {
			return null;
		} else {
			json.put("status", status);
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
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_WJZZX),
				getText("carByDriverHistory.moveType.weijiaozhengzhuxiao"));
		return type;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(Toolbar
					.getDefaultOpenToolbarButton(getText("label.read")));
			// 搜索按钮
			tb.addButton(this.getDefaultSearchToolbarButton());

		} else {
			if (carId != null) {
				// 如果车辆Id不这空，调用标准的工具条
				// 草稿状态下不能转车，可以在车辆表单中进行修改
				if (status4tab == BCConstants.STATUS_DRAFT) {
					tb.addButton(new ToolbarButton().setIcon("ui-icon-pencil")
							.setText("编辑").setAction("edit"));

				} else {
					tb.addButton(
							new ToolbarButton().setIcon("ui-icon-document")
									.setText("转车队").setAction("create"))
							.addButton(
									new ToolbarButton()
											.setIcon("ui-icon-pencil")
											.setText("编辑").setAction("edit"));
				}
				// 删除按钮
				tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
						.setText("删除草稿").setAction("delete"));

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
						// 删除按钮
						.addButton(
								new ToolbarButton().setIcon("ui-icon-trash")
										.setText("删除草稿").setAction("delete"))

						// 搜索按钮
						.addButton(this.getDefaultSearchToolbarButton())
						.addButton(
								new ToolbarButton()
										.setIcon("ui-icon-document")
										.setText("顶班处理")
										.setClick(
												"bc.business.chuLiDingBan.create"));

			}
			// 如果有权限的用户可以看到草稿状态的车
			if (!isReadonly()) {
				tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
						this.getBSStatuses(), "status", 0,
						getText("title.click2changeSearchStatus")));

			}

		}
		return tb;
	}

	/**
	 * 状态值转换列表：草稿|入库|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("carByDriverHistory.status.warehoused"));
		statuses.put(String.valueOf(BCConstants.STATUS_DRAFT),
				getText("bc.status.draft"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	@Override
	protected LikeCondition getGridSearchCondition4OneField(String field,
			String value) {
		if (field.indexOf("plate_no") != -1 || field.indexOf("shiftwork") != -1) {
			return new LikeCondition(field, value != null ? value.toUpperCase()
					: value);
		} else {
			return super.getGridSearchCondition4OneField(field, value);
		}
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
				+ "/bc-business/carByDriverHistory/dingBan.js,"
				+ this.getContextPath()
				+ "/bc-business/carByDriverHistory/view.js";
	}

}
