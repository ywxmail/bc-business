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

import sun.tools.tree.ThisExpression;

import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.Entity;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
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
		sql.append("select d.id,d.status_,d.classes,d.desc_,c.plate_type,c.plate_no,b.name,c.id");
		sql.append(",b.id from BS_CAR_DRIVER d ");
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
		columns.add(new IdColumn(true, "['c.plate_type']+'.'+['c.plate_no']")
				.setId("d.id").setValueExpression("['id']"));
		columns.add(new TextColumn4MapKey("d.status_", "status_",
				getText("carByDriver.statuses"), 100).setSortable(true).setValueFormater(
				new EntityStatusFormater(getEntityStatuses())));
		if (carManId != null || (carManId == null && carId == null)) {
			columns.add(new TextColumn4MapKey("c.plate_no", "plate",
					getText("carByDriver.car.plateNo"), 150).setUseTitleFromLabel(true)
					.setValueFormater(
							new LinkFormater(this.getContextPath()
									+ "/bc-business/car/edit?id={0}", "car") {
								@Override
								public Object[] getParams(Object context,
										Object value) {
									Map<String, Object> map = (Map<String, Object>) context;
									Object[] args = new Object[1];
									args[0] = map.get("carId");
									return args;
								}

								@Override
								public String getTaskbarTitle(Object context,
										Object value) {
									Map<String, Object> map = (Map<String, Object>) context;
									return getText("car") + " - "
											+ map.get("plate");

								}

								@Override
								public String getWinId(Object context,
										Object value) {
									return "car"
											+ ((Map<String, Object>) context)
													.get("carId");

								}

								@Override
								public String getLinkText(Object context,
										Object value) {
									Map<String, Object> map = (Map<String, Object>) context;
									return getText("car") + " - "
											+ map.get("plate");
								}
							}));

		}
		if (carId != null || (carManId == null && carId == null)) {
			columns.add(new TextColumn4MapKey("b.name", "driver",
					getText("carByDriver.driver"), 100)
					.setValueFormater(new LinkFormater(this.getContextPath()
							+ "/bc-business/carMan/edit?id={0}", "driver") {
						@Override
						public Object[] getParams(Object context, Object value) {
							Map<String, Object> map = (Map<String, Object>) context;
							Object[] args = new Object[1];
							args[0] = map.get("driverId");
							return args;
						}

						@Override
						public String getTaskbarTitle(Object context,
								Object value) {
							Map<String, Object> map = (Map<String, Object>) context;
							return getText("carByDriver.driver") + " - "
									+ map.get("driver");
						}

						@Override
						public String getWinId(Object context, Object value) {
							Map<String, Object> map = (Map<String, Object>) context;
							return "driver" + map.get("driverId");
						}
					}));
		}
		columns.add(new TextColumn4MapKey("d.classes", "classes",
				getText("carByDriver.classes"), 100));
		columns.add(new TextColumn4MapKey("d.desc_", "desc_",
				getText("carMan.description"), 270).setSortable(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_no", "c.driver", "c.charger",
				"c.cert_no2", "c.factory_type", "m.name" };
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
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				return new EqualsCondition("c.status_", new Integer(ss[0]));
			} else {
				return new InCondition("c.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		} else {
			return null;
		}
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
}
