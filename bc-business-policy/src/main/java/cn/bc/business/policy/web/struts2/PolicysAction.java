/**
 * 
 */
package cn.bc.business.policy.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.policy.domain.Policy;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 车辆保单 Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PolicysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆保单的状态，多个用逗号连接
	public Long carId;

	@Override
	public boolean isReadonly() {
		// 车辆保单管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.policy"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("p.status_", Direction.Asc).add(
				"p.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select p.id,p.status_,c.plate_type,c.plate_no,p.register_date,p.assured,p.commerial_no");
		sql.append(",p.commerial_company,p.commerial_start_date,p.commerial_end_date");
		sql.append(" ,p.ownrisk,p.greenslip,p.liability_no,p.amount,c.id carId,p.op_type");
		sql.append(" ,p.greenslip_no,p.greenslip_company,p.greenslip_start_date,p.greenslip_end_date,p.stopdate,p.file_date");
		sql.append(" from BS_CAR_POLICY p");
		sql.append(" left join BS_CAR c on c.id=p.car_id");
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
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("plate", map.get("plate_type").toString() + "."
						+ map.get("plate_no").toString());
				map.put("register_date", rs[i++]);
				map.put("assured", rs[i++]);
				map.put("commerial_no", rs[i++]);
				map.put("commerial_company", rs[i++]);
				map.put("commerial_start_date", rs[i++]);
				map.put("commerial_end_date", rs[i++]);
				map.put("ownrisk", rs[i++]);
				map.put("greenslip", rs[i++]);
				map.put("liability_no", rs[i++]);
				map.put("amount", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("op_type", rs[i++]);
				map.put("greenslip_no", rs[i++]);
				map.put("greenslip_company", rs[i++]);
				map.put("greenslip_start_date", rs[i++]);
				map.put("greenslip_end_date", rs[i++]);
				map.put("stopdate", rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("p.id", "id"));
		columns.add(new TextColumn4MapKey("p.status_", "status_",
				getText("policy.status"), 60).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses1())));
		columns.add(new TextColumn4MapKey("p.op_type", "op_type",
				getText("policy.labour.optype"), 60).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getEntityOpTypes())));
		if (carId == null) {
			columns.add(new TextColumn4MapKey("p.plate_no", "plate",
					getText("policy.carId"), 80)
					.setValueFormater(new LinkFormater4Id(this.getContextPath()
							+ "/bc-business/car/edit?id={0}", "car") {
						@SuppressWarnings("unchecked")
						@Override
						public String getIdValue(Object context, Object value) {
							return StringUtils
									.toString(((Map<String, Object>) context)
											.get("carId"));
						}

						@Override
						public String getTaskbarTitle(Object context,
								Object value) {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = (Map<String, Object>) context;
							return getText("car") + " - " + map.get("plate");

						}
					}));
		}
		columns.add(new TextColumn4MapKey("p.assured", "assured",
				getText("policy.assured"), 180));
		columns.add(new TextColumn4MapKey("p.register_date", "register_date",
				getText("policy.registerDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("p.liability_no", "liability_no",
				getText("policy.liabilityNo"), 180).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("p.commerial_no", "commerial_no",
				getText("policy.commerialNo"), 180).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("p.commerial_company",
				"commerial_company", getText("policy.commerialCompany"), 100)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("p.commerial_start_date",
				"commerial_start_date", getText("policy.commerialDeadline"),
				180).setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
			@Override
			public Date getToDate(Object context, Object value) {
				@SuppressWarnings("rawtypes")
				Map contract = (Map) context;
				return (Date) contract.get("commerial_end_date");
			}
		}));
		columns.add(new TextColumn4MapKey("p.ownrisk", "ownrisk",
				getText("policy.ownrisk"), 80).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("p.greenslip", "greenslip",
				getText("policy.greenslip"), 120).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("p.greenslip_no", "greenslip_no",
				getText("policy.greenslipNo"), 180).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("p.greenslip_company",
				"greenslip_company", getText("policy.greenslipCompany"), 100)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("p.greenslip_start_date",
				"greenslip_start_date", getText("policy.greenslipDeadline"),
				180).setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
			@Override
			public Date getToDate(Object context, Object value) {
				@SuppressWarnings("rawtypes")
				Map contract = (Map) context;
				return (Date) contract.get("greenslip_end_date");
			}
		}));
		columns.add(new TextColumn4MapKey("p.spotdate", "spotdate",
				getText("policy.stopDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("p.amount", "amount",
				getText("policy.amount"), 80).setSortable(true)
				.setUseTitleFromLabel(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_type", "c.plate_no", "p.commerial_no",
				"p.liability_no", "p.commerial_company" };
	}

	@Override
	protected String getFormActionName() {
		return "policy";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(550).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("p.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("p.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("c.id", carId);
		}
		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition, carIdCondition);

	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (this.status == null || this.status.length() == 0) {
			json.put("status", status);
		}

		// carId条件
		if (carId != null) {
			json.put("carId", carId);
		}
		return json.isEmpty() ? null : json;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return super.getHtmlPageToolbar()
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getBSStatuses1(), "status", 0,
								getText("title.click2changeSearchStatus")));
	}

	/**
	 * 布尔值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getBooleanValue() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Policy.BOOLEAN_YES), getText("policy.yes"));
		statuses.put(String.valueOf(Policy.BOOLEAN_NO), getText("policy.no"));
		return statuses;
	}

	/**
	 * 获取Policy的操作类型列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityOpTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Policy.OPTYPE_CREATE),
				getText("policy.optype.create"));
		types.put(String.valueOf(Policy.OPTYPE_EDIT),
				getText("policy.optype.edit"));
		types.put(String.valueOf(Policy.OPTYPE_RENEWAL),
				getText("policy.optype.renewal"));
		types.put(String.valueOf(Policy.OPTYPE_SURRENDERS),
				getText("policy.optype.surrenders"));
		return types;
	}

}
