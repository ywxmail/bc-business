/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.Date;
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
import cn.bc.core.util.DateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.struts2.AbstractRichInputWithJpaAction;

/**
 * Ajax查询经司机信息的Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FindCarManAction extends
		AbstractRichInputWithJpaAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = BCConstants.STATUS_ENABLED + ","
			+ BCConstants.STATUS_DISABLED;// 默认的车辆状态条件

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
		c.add(new OrderCondition("c.status_", Direction.Asc).add("c.work_date",
				Direction.Desc));

		// 左右like的自动判断处理
		if (value.startsWith("%")) {
			if (!value.endsWith("%")) {
				c.add(new LikeRightCondition("c.name", value));// like右边
				return c;
			}
		} else if (value.endsWith("%")) {
			c.add(new LikeLeftCondition("c.name", value));// like左边
			return c;
		}
		c.add(new LikeCondition("c.name", value));// 左右都like

		return c;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.status_,c.name,c.cert_fwzg,c.work_date,c.classes");
		sql.append(",c.cert_driving_first_date,c.work_date,c.type_ from BS_CARMAN c");
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
				map.put("name", rs[i++]); // 姓名
				map.put("cert4FWZG", rs[i++]);// 服务资格证
				map.put("workDate", DateUtils.formatDate((Date) rs[i++]));// 入职日期
				map.put("classes", rs[i++]);// 营运班次
				map.put("certDriverFirstDate",
						DateUtils.formatDate((Date) rs[i++]));// 初次领驾驶证日期
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