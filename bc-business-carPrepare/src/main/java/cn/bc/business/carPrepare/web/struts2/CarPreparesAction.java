/**
 * 
 */
package cn.bc.business.carPrepare.web.struts2;

import java.util.ArrayList;
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
import cn.bc.business.OptionConstants;
import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.business.motorcade.service.MotorcadeService;
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
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 出车准备 Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarPreparesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆保单的状态，多个用逗号连接
	public Long carId;
	public Long main;// 主体： 0-当前版本,1-历史版本
	public Long policyId;// 合同ID

	@Override
	public boolean isReadonly() {
		// 出车准备管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.carPrepare"),
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
		sql.append("select p.id,p.status_,p.code,p.plan_date,p.c1_plate_type,p.c1_plate_no,p.c1_company,m1.name c1Motorcade,p.c1_register_date");
		sql.append(",getUpdateTheProgress(p.id) updateTheProgress");
		sql.append(",p.c1_contract_end_date,p.c1_greenslip_end_date,p.c1_commerial_end_date,p.c1_bs_type,p.c1_scrapto,p.c2_indicator");
		sql.append(",p.c2_plate_type,p.c2_plate_no,p.c2_company,bia.name c2Brach,m2.name c2Motorcade,p.c1_motorcade,p.c2_motorcade");
		sql.append(" from bs_car_prepare p");
		sql.append(" left join bs_motorcade m1 on m1.id=p.c1_motorcade");
		sql.append(" left join bs_motorcade m2 on m2.id=p.c2_motorcade");
		sql.append(" left join bc_identity_actor bia on bia.id=p.c2_branch");
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
				map.put("code", rs[i++]);
				map.put("plan_date", rs[i++]);
				map.put("c1_plate_type", rs[i++]);
				map.put("c1_plate_no", rs[i++]);
				map.put("c1Plate",
						(map.get("c1_plate_no").toString().length() != 0 ? map
								.get("c1_plate_type").toString()
								+ "."
								+ map.get("c1_plate_no").toString() : ""));
				map.put("c1_company", rs[i++]);
				map.put("c1Motorcade", rs[i++]);
				map.put("c1_register_date", rs[i++]);
				map.put("updateTheProgress", rs[i++]);
				map.put("c1_contract_end_date", rs[i++]);
				map.put("c1_greenslip_end_date", rs[i++]);
				map.put("c1_commerial_end_date", rs[i++]);
				map.put("c1_bs_type", rs[i++]);
				map.put("c1_scrapto", rs[i++]);
				map.put("c2_indicator", rs[i++]);
				map.put("c2_plate_type", rs[i++]);
				map.put("c2_plate_no", rs[i++]);
				map.put("c2Plate",
						(map.get("c2_plate_no") != null ? map.get(
								"c2_plate_type").toString()
								+ "." + map.get("c2_plate_no").toString() : ""));
				map.put("c2_company", rs[i++]);
				map.put("c2Brach", rs[i++]);
				map.put("c2Motorcade", rs[i++]);
				map.put("c1_motorcade", rs[i++]);
				map.put("c2_motorcade", rs[i++]);
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
				getText("carPrepare.status"), 40).setSortable(true)
				.setValueFormater(
						new EntityStatusFormater(this.getCarPrepareStatuses())));
		columns.add(new TextColumn4MapKey("p.code", "code",
				getText("carPrepare.code"), 75).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("p.plan_date", "plan_date",
				getText("carPrepare.planDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("p.code", "updateTheProgress",
				getText("carPrepare.updateTheProgress"), 115).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车号
		columns.add(new TextColumn4MapKey("p.c1_plate_no", "c1Plate",
				getText("carPrepare.C1Plate"), 80)
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
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("car") + " - " + map.get("plate");

					}
				}));
		// 公司
		columns.add(new TextColumn4MapKey("p.c1_company", "c1_company",
				getText("carPrepare.C1Company"), 40).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车队
		columns.add(new TextColumn4MapKey("m1.name", "c1Motorcade",
				getText("carPrepare.C1Motorcade"), 65)
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
												.get("c1_motorcade"));
							}
						}));
		// 车辆登记日期
		columns.add(new TextColumn4MapKey("p.c1_register_date",
				"c1_register_date", getText("carPrepare.C1RegisterDate"), 100)
				.setSortable(true).setValueFormater(
						new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("p.c1_contract_end_date",
				"c1_contract_end_date",
				getText("carPrepare.C1ContractEndDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("p.c1_greenslip_end_date",
				"c1_greenslip_end_date",
				getText("carPrepare.C1GreenslipEndDate"), 100)
				.setSortable(true).setValueFormater(
						new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("p.c1_commerial_end_date",
				"c1_commerial_end_date",
				getText("carPrepare.C1CommeriaEndDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("p.c1_bs_type", "c1_bs_type",
				getText("carPrepare.C1BsType"), 75).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("p.c1_scrapto", "c1_scrapto",
				getText("carPrepare.C1Scrapto"), 75).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("p.c2_indicator", "c2_indicator",
				getText("carPrepare.C2Indicator"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车号
		columns.add(new TextColumn4MapKey("p.c2_plate_no", "c2Plate",
				getText("carPrepare.C2Plate"), 80)
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
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("car") + " - " + map.get("plate");

					}
				}));
		// 公司
		columns.add(new TextColumn4MapKey("p.c2_company", "c2_company",
				getText("carPrepare.C2Company"), 40).setSortable(true)
				.setUseTitleFromLabel(true));
		// 分公司
		columns.add(new TextColumn4MapKey("p.c2_branch", "c2Brach",
				getText("carPrepare.C2Branch"), 65).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车队
		columns.add(new TextColumn4MapKey("m2.name", "c2Motorcade",
				getText("carPrepare.C1Motorcade"), 65)
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
												.get("c2_motorcade"));
							}
						}));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "p.c1_plate_no", "p.c2_plate_no", "p.code" };
	}

	@Override
	protected Condition getGridSearchCondition4OneField(String field,
			String value) {
		if (field.endsWith(".c1_plate_no") || field.endsWith(".c2_plate_no")) {// 车牌号
			return buildDefaultLikeCondition(field,
					value != null ? value.toUpperCase() : value);
		} else {
			return super.getGridSearchCondition4OneField(field, value);
		}
	}

	@Override
	protected String getFormActionName() {
		return "carPrepare";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['c1Plate']";
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

	/**
	 * 状态值转换列表：待更新|更新中|已完成|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getCarPrepareStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(CarPrepare.STATUS_STAYUPDATED),
				getText("carPrepare.status.stayUpdate"));
		statuses.put(String.valueOf(CarPrepare.STATUS_INTHEUPDATE),
				getText("carPrepare.status.intheUpdate"));
		statuses.put(String.valueOf(CarPrepare.STATUS_COMPLETED),
				getText("carPrepare.status.completed"));
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
			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());
			// 删除按钮
			tb.addButton(this.getDefaultDeleteToolbarButton());
			// 生成年度计划
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText("生成更新计划")
					.setClick("bs.carPrepareView.createPlanDateDialog"));

		}
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				getCarPrepareStatuses(), "status", 0,
				getText("title.click2changeSearchClasses")));
	}

	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc-business/carPrepare/view.js";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	private MotorcadeService motorcadeService;
	private ActorService actorService;
	private OptionService optionService;

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

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
	public JSONArray businessTypes;// 营运性质列表

	@Override
	protected void initConditionsFrom() throws Exception {
		// 可选分公司列表
		units = OptionItem.toLabelValues(this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT }, (Integer[]) null), "name",
				"id");

		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CAR_BUSINESS_NATURE });
		// 营运性质列表
		this.businessTypes = OptionItem.toLabelValues(
				optionItems.get(OptionConstants.CAR_BUSINESS_NATURE), "value");

	}

	// ==高级搜索代码结束==
}
