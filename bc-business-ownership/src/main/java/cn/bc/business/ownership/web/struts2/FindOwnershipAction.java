/**
 * 
 */
package cn.bc.business.ownership.web.struts2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.LikeLeftCondition;
import cn.bc.core.query.condition.impl.LikeRightCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.struts2.AbstractRichInputWithJpaAction;

/**
 * Ajax查询经营权信息的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FindOwnershipAction extends
		AbstractRichInputWithJpaAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = BCConstants.STATUS_ENABLED + ","
			+ BCConstants.STATUS_DISABLED;// 默认的经营权状态条件

	@Override
	protected Condition getCondition(String value) {
		if (value == null || value.length() == 0)
			return null;

		AndCondition c = new AndCondition();

		// 车辆状态条件
		Condition statuesCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.status, "o.status_");
		if (statuesCondition != null)
			c.add(statuesCondition);

		// 排序条件
		c.add(new OrderCondition("o.status_", Direction.Asc).add("o.number_",
				Direction.Desc));

		// 左右like的自动判断处理
		if (value.startsWith("%")) {
			if (!value.endsWith("%")) {
				c.add(new LikeRightCondition("o.number_", value));// like右边
				return c;
			}
		} else if (value.endsWith("%")) {
			c.add(new LikeLeftCondition("o.number_", value));// like左边
			return c;
		}
		c.add(new LikeCondition("o.number_", value));// 左右都like

		return c;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select o.id,o.status_,o.number_ from bs_car_ownership o");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]); // 状态
				map.put("number", rs[i++]); // 经营权号

				return map;
			}
		});
		return sqlObject;
	}

}