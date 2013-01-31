/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

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
 * Ajax查询经违法代码信息的Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FindCase4InfractCodeAction extends
		AbstractRichInputWithJpaAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Condition getCondition(String value) {
		if (value == null || value.length() == 0)
			return null;

		AndCondition c = new AndCondition();

		// 排序条件
		c.add(new OrderCondition("c.code", Direction.Asc));

		// 左右like的自动判断处理
		if (value.startsWith("%")) {
			if (!value.endsWith("%")) {
				c.add(new LikeRightCondition("c.code", value));// like右边
				return c;
			}
		} else if (value.endsWith("%")) {
			c.add(new LikeLeftCondition("c.code", value));// like左边
			return c;
		}
		c.add(new LikeCondition("c.code", value));// 左右都like

		return c;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.code,c.subject,c.according,c.jeom,c.penalty");
		sql.append(" from bs_case_infract_code c");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("code", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("according", rs[i++]);
				map.put("jeom", rs[i++]);
				map.put("penalty", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

}