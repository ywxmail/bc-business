/**
 * 
 */
package cn.bc.business.carmodel.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 车型视图视图Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarModelsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 合同的状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 车辆管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：登记日期|状态
		return new OrderCondition("cm.file_date", Direction.Desc).add(
				"cm.status_", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select cm.id,cm.factory_model,cm.factory_type,cm.engine_type,cm.fuel_type,cm.turn_type,");
		sql.append("cm.displacement,cm.power,cm.tire_count,cm.tire_standard,cm.tire_front_distance,cm.tire_behind_distance,cm.axis_distance,");
		sql.append("cm.axis_count,cm.piece_count,cm.dim_len,cm.dim_width,cm.dim_height,cm.total_weight,cm.access_weight,cm.access_count");
		sql.append(" from bs_car_model cm");

		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("factory_model", rs[i++]);
				map.put("factory_type", rs[i++]);
				map.put("engine_type", rs[i++]);
				map.put("fuel_type", rs[i++]);
				map.put("turn_type", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("cm.id", "id"));
		columns.add(new TextColumn4MapKey("cm.factory_model", "factory_model",
				getText("carModel.factoryModel"))
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("cm.factory_type", "factory_type",
				getText("carModel.factoryType"), 120)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("cm.engine_type", "engine_type",
				getText("carModel.engineType"), 80)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("cm.fuel_type", "fuel_type",
				getText("carModel.fuelType"), 100)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("cm.turn_type", "turn_type",
				getText("carModel.turnType"), 90)
				.setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "cm.factory_type", "cm.factory_model", 
				"cm.engine_type", "cm.fuel_type", "cm.turn_type"};
	}
	
	@Override
	protected String getFormActionName() {
		return "carModel";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(650).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['factoryType']";
	}


}