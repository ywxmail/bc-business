/**
 * 
 */
package cn.bc.business.car.web.struts2;

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
public class FindCarAction extends
		AbstractRichInputWithJpaAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = BCConstants.STATUS_ENABLED + ","
			+ BCConstants.STATUS_DISABLED;// 默认的车辆状态条件

	@Override
	public String getTerm() {
		if (super.getTerm() != null) {
			// 忽略“粤A.”前缀、自动转换为大写的处理
			int index = super.getTerm().indexOf(".");
			if (index != -1 && index < super.getTerm().length() - 1) {
				return super.getTerm().substring(index + 1).toUpperCase();
			} else {
				return super.getTerm().toUpperCase();
			}
		}
		return super.getTerm();
	}

	@Override
	protected Condition getCondition(String value) {
		if (value == null || value.length() == 0)
			return null;

		AndCondition c = new AndCondition();

		// 车辆状态条件
		Condition statuesCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.status, "c.status_");
		if (statuesCondition != null)
			c.add(statuesCondition);

		// 排序条件
		c.add(new OrderCondition("c.status_", Direction.Asc).add(
				"c.register_date", Direction.Desc));

		// 左右like的自动判断处理
		if (value.startsWith("%")) {
			if (!value.endsWith("%")) {
				c.add(new LikeRightCondition("c.plate_no", value));// like右边
				return c;
			}
		} else if (value.endsWith("%")) {
			c.add(new LikeLeftCondition("c.plate_no", value));// like左边
			return c;
		}
		c.add(new LikeCondition("c.plate_no", value));// 左右都like

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
				map.put("status", rs[i++]); // 状态
				map.put("statusCN", getStatusCN((Integer) map.get("status"))); // 状态描述
				map.put("plateType", rs[i++]); // 车牌类型
				map.put("plateNo", rs[i++]); // 车牌号码
				map.put("code", rs[i++]); // 自编号

				map.put("company", rs[i++]);// 公司
				map.put("motorcadeId", rs[i++]);// 车队ID
				map.put("motorcadeName", rs[i++]);// 车队名称
				map.put("unitId", rs[i++]); // 分公司ID
				map.put("unitName", rs[i++]); // 分公司名称

				return map;
			}
		});
		return sqlObject;
	}

	/**
	 * 状态值转换列表：在案|注销|草稿|全部
	 * 
	 * @return
	 */
	private String getStatusCN(int status) {
		if (status == BCConstants.STATUS_ENABLED) {
			return getText("bs.status.active");
		} else if (status == BCConstants.STATUS_DISABLED) {
			return getText("bs.status.logout");
		} else if (status == BCConstants.STATUS_DRAFT) {
			return getText("bc.status.draft");
		} else {
			return String.valueOf(status);
		}
	}

}