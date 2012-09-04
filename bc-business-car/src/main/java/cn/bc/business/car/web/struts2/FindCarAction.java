/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
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
 * Ajax查询车辆信息的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FindCarAction extends
		AbstractRichInputWithJpaAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Condition getCondition(String value) {
		if (value == null || value.length() == 0)
			return null;

		AndCondition c = new AndCondition();
		c.add(new OrderCondition("c.status_", Direction.Asc).add(
				"c.register_date", Direction.Desc));

		if (value.startsWith("%")) {
			if (!value.endsWith("%")) {
				c.add(new LikeRightCondition("c.plate_no", value));
				return c;
			}
		} else if (value.endsWith("%")) {
			c.add(new LikeLeftCondition("c.plate_no", value));
			return c;
		}

		c.add(new LikeCondition("c.plate_no", value));
		return c;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id as id,c.status_ as status,c.plate_type as plate_type,c.plate_no as plate_no,c.code as code");
		sql.append(",c.company as company,c.motorcade_id as mid,m.name as mname,m.unit_id as uid,unit.name as uname");
		sql.append(" from bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sql.append(" inner join bc_identity_actor unit on unit.id=m.unit_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status_", rs[i++]); // 状态
				map.put("plate_type", rs[i++]); // 车牌类型
				map.put("plate_no", rs[i++]); // 车牌号码
				map.put("code", rs[i++]); // 自编号

				map.put("company", rs[i++]);// 公司
				map.put("motorcade_id", rs[i++]);// 车队ID
				map.put("motorcade_name", rs[i++]);// 车队名称
				map.put("unit_id", rs[i++]); // 分公司ID
				map.put("unit_name", rs[i++]); // 分公司名称

				return map;
			}
		});
		return sqlObject;
	}
}