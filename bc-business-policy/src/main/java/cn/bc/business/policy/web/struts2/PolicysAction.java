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

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.policy.domain.Policy;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.formater.NubmerFormater;
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
	public Long main;// 主体： 0-当前版本,1-历史版本
	public Long policyId;// 合同ID

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
		sql.append("select p.id,p.status_");
		sql.append(",c.code,c.company,bia.name as unit_name,m.name");
		sql.append(",c.plate_type,c.plate_no,c.register_date,p.file_date,p.assured,p.commerial_no");
		sql.append(",p.commerial_company,p.commerial_start_date,p.commerial_end_date");
		sql.append(",p.ownrisk,p.greenslip,p.liability_no,c.id as carId,p.op_type");
		sql.append(",p.greenslip_no,p.greenslip_company,p.greenslip_start_date,p.greenslip_end_date,p.stop_date,p.main");
		sql.append(",m.id as motorcade_id,p.liability_amount,p.commerial_amount,p.greenslip_amount,c.bs_type as bstype");
		sql.append(",p.buy_plant");
		sql.append(" from bs_car_policy p");
		sql.append(" inner join bs_car c on c.id=p.car_id");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sql.append(" inner join bc_identity_actor bia on bia.id=m.unit_id");
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
				map.put("code", rs[i++]);// 自编号
				map.put("company", rs[i++]);// 公司
				map.put("unit_name", rs[i++]);// 分公司
				map.put("motorcade_name", rs[i++]);// 车队
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("registeDate", rs[i++]);
				map.put("plate", map.get("plate_type").toString() + "."
						+ map.get("plate_no").toString());
				map.put("file_date", rs[i++]);
				map.put("assured", rs[i++]);
				map.put("commerial_no", rs[i++]);
				map.put("commerial_company", rs[i++]);
				map.put("commerial_start_date", rs[i++]);
				map.put("commerial_end_date", rs[i++]);
				map.put("ownrisk", rs[i++]);
				map.put("greenslip", rs[i++]);
				map.put("liability_no", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("op_type", rs[i++]);
				map.put("greenslip_no", rs[i++]);
				map.put("greenslip_company", rs[i++]);
				map.put("greenslip_start_date", rs[i++]);
				map.put("greenslip_end_date", rs[i++]);
				map.put("stop_date", rs[i++]);
				map.put("main", rs[i++]);
				map.put("motorcade_id", rs[i++]);// 车队id
				map.put("liability_amount", rs[i++]);// 责任险合计
				map.put("commerial_amount", rs[i++]);// 商业险合计
				map.put("greenslip_amount", rs[i++]);// 强制险合计
				map.put("bstype", rs[i++]);
				map.put("buy_plant",rs[i++]);//承保险种
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("p.id", "id"));
		// 状态
		columns.add(new TextColumn4MapKey("p.status_", "status_",
				getText("policy.status"), 40)
				.setSortable(true)
				.setValueFormater(new EntityStatusFormater(getPolicyStatuses())));
		// 公司
		columns.add(new TextColumn4MapKey("c.company", "company",
				getText("car.company"), 40).setSortable(true)
				.setUseTitleFromLabel(true));
		// 分公司
		columns.add(new TextColumn4MapKey("unit_name", "unit_name",
				getText("car.unitname"), 65).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车队
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("car.motorcade"), 65)
				.setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/motorcade/edit?id={0}",
								"motorcade") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("motorcade_id"));
							}
						}));
		// 自编号
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("car.code"), 75).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车号
		if (carId == null) {// 车辆页签时不需显示车牌号码
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
		// 车辆登记日期
		columns.add(new TextColumn4MapKey("c.register_date", "registeDate",
				getText("policy.carRegisteDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		//车辆营运性质
		columns.add(new TextColumn4MapKey("c.bs_type", "bstype",
				getText("car.bstype"), 100).setSortable(true));
		//投保人
		columns.add(new TextColumn4MapKey("p.assured", "assured",
				getText("policy.assured"), 190));
		// 创建日期
		columns.add(new TextColumn4MapKey("p.file_date", "file_date",
				getText("policy.fileDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("p.liability_no", "liability_no",
				getText("policy.liabilityNo"), 190).setSortable(true)
				.setUseTitleFromLabel(true));
		if (!this.isReadonly()) {
			// 责任险合计
			columns.add(new TextColumn4MapKey("p.liability_amount",
					"liability_amount", getText("policy.liabilityAmount"), 105)
					.setSortable(true).setUseTitleFromLabel(true)
					.setValueFormater(new NubmerFormater("###,###.00")));
		}
		columns.add(new TextColumn4MapKey("p.commerial_no", "commerial_no",
				getText("policy.commerialNo"), 190).setUseTitleFromLabel(true));
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
		if (!this.isReadonly()) {
			// 商业险合计
			columns.add(new TextColumn4MapKey("p.commerial_amount",
					"commerial_amount", getText("policy.commerialAmount"), 105)
					.setSortable(true).setUseTitleFromLabel(true)
					.setValueFormater(new NubmerFormater("###,###.00")));
		}
		columns.add(new TextColumn4MapKey("p.ownrisk", "ownrisk",
				getText("policy.ownrisk"), 40).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("p.greenslip", "greenslip",
				getText("policy.greenslip"), 75).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("p.greenslip_no", "greenslip_no",
				getText("policy.greenslipNo"), 190).setUseTitleFromLabel(true));
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
		if (!this.isReadonly()) {
			// 强制险合计
			columns.add(new TextColumn4MapKey("p.greenslip_amount",
					"greenslip_amount", getText("policy.greenslipAmount"), 105)
					.setSortable(true).setUseTitleFromLabel(true)
					.setValueFormater(new NubmerFormater("###,###.00")));
		}
		// 操作类型
		columns.add(new TextColumn4MapKey("p.op_type", "op_type",
				getText("policy.labour.optype"),65).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getEntityOpTypes())));
		//承保险种
		columns.add(new TextColumn4MapKey("p.buy_plant", "buy_plant",
				getText("policy.insuranceType")).setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.company", "bia.name", "m.name",
				"c.plate_type", "c.plate_no", "p.commerial_no",
				"p.greenslip_no", "p.liability_no", "p.commerial_company",
				"c.code","p.buy_plant" };
	}

	@Override
	protected String getFormActionName() {
		return "policy";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
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
		// 历史版本条件
		Condition mainCondition = null;
		if (main != null) {
			mainCondition = new EqualsCondition("p.main", main);
			// 状态为注销
			statusCondition = new EqualsCondition("p.status_",
					Policy.STATUS_DISABLED);
		}
		// 历史版本查看历史版本时排除本身
		Condition policyIdCondition = null;
		if (policyId != null) {
			policyIdCondition = new NotEqualsCondition("p.id", policyId);
		}
		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition, carIdCondition,
				mainCondition, policyIdCondition);

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

	/**
	 * 状态值转换列表：在案|注销|停保|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getPolicyStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Policy.STATUS_ENABLED),
				getText("policy.status.enabled"));
		statuses.put(String.valueOf(Policy.STATUS_DISABLED),
				getText("policy.status.disabled"));
		/*
		 * statuses.put(String.valueOf(Policy.STATUS_SURRENDER),
		 * getText("policy.status.surrender"));
		 */
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
		} else {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
			// 删除按钮
			tb.addButton(this.getDefaultDeleteToolbarButton());
		}
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				getPolicyStatuses(), "status", 0,
				getText("title.click2changeSearchClasses")));
	}

	@Override
	protected String getGridDblRowMethod() {
		return "bc.page.open";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	private MotorcadeService motorcadeService;
	private ActorService actorService;

	@Autowired
	public void setActorService(
			@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	public JSONArray motorcades;// 车队的下拉列表信息
	public JSONArray units;// 分公司的下拉列表信息

	@Override
	protected void initConditionsFrom() throws Exception {
		// 可选分公司列表
		units = OptionItem.toLabelValues(this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT }, (Integer[]) null), "name",
				"id");

		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));
	}

	// ==高级搜索代码结束==
}
