/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

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
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.web.struts2.LinkFormater4ChargerInfo;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 投诉视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseAdvicesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
	public String type;
	public Long carManId;
	public Long carId;

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		if (CaseBase.TYPE_COMPLAIN == Integer.valueOf(this.type)) { // 客管投诉管理员和超级管理员
			return !context.hasAnyRole(getText("key.role.bs.advice.keguan"),
					getText("key.role.bc.admin"));
		} else { // 公司投诉管理员和超级管理员
			return !context.hasAnyRole(getText("key.role.bs.advice.gongsi"),
					getText("key.role.bc.admin"));
		}
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("b.status_", Direction.Asc).add(
				"b.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,b.status_,b.type_,a.advice_type,b.subject,b.motorcade_name,b.car_plate,b.driver_name");
		sql.append(",b.closer_name,b.close_date,a.advisor_name,a.advisor_phone,b.happen_date,a.path_from,a.path_to,a.detail,a.charger");
		sql.append(",b.from_,b.source,b.driver_cert,a.receive_code,a.receive_date ");
		sql.append(",b.company,c.bs_type,c.code");
		sql.append(",man.origin");
		sql.append(",b.motorcade_id,b.car_id,b.driver_id");
		sql.append(",bia.id batch_company_id,bia.name batch_company");
		// 最新参与流程信息
		sql.append(",getnewprocessnameandtodotasknames4midmtyle(a.id,'");
		sql.append(Case4Advice.class.getSimpleName());
		sql.append("') as processInfo");
		sql.append(" from BS_CASE_ADVICE a");
		sql.append(" inner join BS_CASE_BASE b on b.id=a.id");
		sql.append(" left join BS_CAR c on b.car_id = c.id");
		sql.append(" left join BS_CARMAN man on b.driver_id=man.id");
		sql.append(" left join bs_motorcade m on m.id=b.motorcade_id");
		sql.append(" left join bc_identity_actor bia on bia.id=m.unit_id");

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
				map.put("type_", rs[i++]);
				map.put("advice_type", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("car_plate", rs[i++]);
				map.put("driver_name", rs[i++]);
				map.put("closer_name", rs[i++]);
				map.put("close_date", rs[i++]);
				map.put("advisor_name", rs[i++]);
				map.put("advisor_phone", rs[i++]);
				map.put("happen_date", rs[i++]);
				map.put("path_from", rs[i++]);
				map.put("path_to", rs[i++]);
				map.put("detail", rs[i++]);
				map.put("charger", rs[i++]);
				map.put("from_", rs[i++]);
				map.put("source", rs[i++]);
				map.put("driver_cert", rs[i++]);
				map.put("receive_code", rs[i++]);
				map.put("receive_date", rs[i++]);
				map.put("company", rs[i++]);
				map.put("bs_type", rs[i++]);
				map.put("code", rs[i++]);
				map.put("origin", rs[i++]);
				map.put("motorcade_id", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("driverId", rs[i++]);
				map.put("batch_company_id", rs[i++]);
				map.put("batch_company", rs[i++]);
				map.put("processInfo", rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		// if (type.length() > 0
		// && Integer.valueOf(type) == Case4Advice.TYPE_COMPLAIN) { // 客管投诉
		// columns.add(new TextColumn4MapKey("a.receive_code", "receive_code",
		// getText("runcase.receiveCode"), 100).setSortable(true)
		// .setUseTitleFromLabel(true));
		// } else {
		// columns.add(new TextColumn4MapKey("a.receive_code", "receive_code",
		// getText("runcase.company.receiveCode"), 100).setSortable(
		// true).setUseTitleFromLabel(true));
		// }
		columns.add(new TextColumn4MapKey("a.receive_code", "receive_code",
				getText("runcase.receiveCode"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.status_", "status_",
				getText("runcase.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses3())));
		columns.add(new TextColumn4MapKey("b.company", "company",
				getText("runcase.company"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("bia.name", "batch_company",
				getText("runcase.batch.company"), 70).setSortable(true));
		columns.add(new TextColumn4MapKey("b.motorcade_name", "motorcade_name",
				getText("runcase.motorcadeName"), 70)
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
		if (carId == null) {
			columns.add(new TextColumn4MapKey("b.car_plate", "car_plate",
					getText("runcase.carPlate"), 80)
					.setValueFormater(new LinkFormater4Id(this.getContextPath()
							+ "/bc-business/car/edit?id={0}", "car") {
						@SuppressWarnings("unchecked")
						@Override
						public String getIdValue(Object context, Object value) {
							return StringUtils
									.toString(((Map<String, Object>) context)
											.get("carId"));
						}
					}));
		}
		if (carManId == null) {
			columns.add(new TextColumn4MapKey("b.driver_name", "driver_name",
					getText("runcase.driverName"), 70).setSortable(true)
					.setValueFormater(
							new LinkFormater4Id(this.getContextPath()
									+ "/bc-business/carMan/edit?id={0}",
									"drivers") {
								@SuppressWarnings("unchecked")
								@Override
								public String getIdValue(Object context,
										Object value) {
									return StringUtils
											.toString(((Map<String, Object>) context)
													.get("driverId"));
								}
							}));

		}
		columns.add(new TextColumn4MapKey("b.driver_cert", "driver_cert",
				getText("runcase.FWZGCert"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("runcase.accident.carCode"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.subject", "subject",
				getText("runcase.subject2"), 180).setSortable(true));
		// if (!isReadonly()
		// && (Integer.valueOf(type) == CaseBase.TYPE_COMPLAIN || Integer
		// .valueOf(type) == CaseBase.TYPE_COMPANY_COMPLAIN)) {
		// 最新参与流程信息
		columns.add(new TextColumn4MapKey("", "processInfo",
				getText("runcase.processInfo"), 350)
				.setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-workflow/workspace/open?id={0}",
								"workspace") {

							@Override
							public String getIdValue(Object context,
									Object value) {
								@SuppressWarnings("unchecked")
								String processInfo = StringUtils
										.toString(((Map<String, Object>) context)
												.get("processInfo"));
								if (processInfo == null
										|| processInfo.length() == 0)
									return "";

								// 获取流程id
								return processInfo.split(";")[1];
							}

							@Override
							public String getTaskbarTitle(Object context,
									Object value) {
								return "工作空间";
							}

							@Override
							public String getLinkText(Object context,
									Object value) {
								@SuppressWarnings("unchecked")
								String processInfo = StringUtils
										.toString(((Map<String, Object>) context)
												.get("processInfo"));
								if (processInfo == null
										|| processInfo.length() == 0)
									return "";

								String title = "";
								String[] processInfos = processInfo.split(";");
								if (processInfos.length > 2) {
									for (int i = 2; i < processInfos.length; i++) {
										if (i + 1 == processInfos.length) {
											title += processInfos[i];
										} else {
											title += processInfos[i] + ",";
										}
									}
									title += "--";
								}
								return title += "[" + processInfos[0] + "]";
							}

							@Override
							public String getWinId(Object context, Object value) {
								@SuppressWarnings("unchecked")
								String processInfo = StringUtils
										.toString(((Map<String, Object>) context)
												.get("processInfo"));
								if (processInfo == null
										|| processInfo.length() == 0)
									return "";

								// 获取流程id
								return this.moduleKey + "::"
										+ processInfo.split(";")[1];
							}
						}));
		// }

		if (this.type != null
				&& this.type.equals(String
						.valueOf(CaseBase.TYPE_COMPANY_COMPLAIN))) {
			columns.add(new TextColumn4MapKey("b.happen_date", "happen_date",
					getText("runcase.receive.happenDate"), 125).setSortable(
					true).setValueFormater(
					new CalendarFormater("yyyy-MM-dd HH:mm")));
		} else {
			columns.add(new TextColumn4MapKey("b.happen_date", "happen_date",
					getText("runcase.happenDate"), 125).setSortable(true)
					.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		}

		columns.add(new TextColumn4MapKey("a.path_from", "path_from",
				getText("runcase.address"), 200).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> advice = (Map<String, Object>) context;
						if (advice.get("path_from").toString().length() == 0
								|| advice.get("path_to").toString().length() == 0) {
							return "";
						}
						return "从" + advice.get("path_from") + "到"
								+ advice.get("path_to");
					}
				}));
		columns.add(new TextColumn4MapKey("a.charger", "charger",
				getText("runcase.chargers"), 170).setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4ChargerInfo(this.getContextPath())));
		columns.add(new TextColumn4MapKey("c.bs_type", "bs_type",
				getText("runcase.businessType"), 80));
		columns.add(new TextColumn4MapKey("man.origin", "origin",
				getText("runcase.origin"), 150));
		
		columns.add(new TextColumn4MapKey("a.advisor_name", "advisor_name",
				getText("runcase.advisorName"), 70));
		columns.add(new TextColumn4MapKey("a.advisor_phone", "advisor_phone",
				getText("runcase.advisorPhone"), 100));
		columns.add(new TextColumn4MapKey("a.detail", "detail",
				getText("runcase.detail"), 150).setUseTitleFromLabel(true));

		columns.add(new HiddenColumn4MapKey("type", "type_"));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {

		if (Integer.valueOf(type) == CaseBase.TYPE_COMPLAIN) {
			return new String[] {
					"b.subject",
					"b.car_plate",
					"a.receive_code",
					"b.driver_name",
					"b.driver_cert",
					"c.code",
					"getnewprocessnameandtodotasknames4midmtyle(a.id,'"
							+ Case4Advice.class.getSimpleName() + "')" };
		} else {
			return new String[] { "b.subject", "b.car_plate", "a.receive_code",
					"b.driver_name", "b.driver_cert", "c.code" };
		}
	}

	@Override
	protected String getFormActionName() {
		return "caseAdvice";
	}

	@Override
	protected String getHtmlPageTitle() {
		if (Integer.valueOf(type) == CaseBase.TYPE_COMPLAIN) {
			return this.getText("caseAdvice1.title");
		} else {
			return this.getText("caseAdvice2.title");
		}
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['car_plate']+'\t 的投诉信息'";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.status, "b.status_");

		// carManId条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("b.driver_id", carManId);
		}
		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("b.car_id", carId);
		}

		// 投诉分类type(2:客管投诉,6:公司投诉)
		Condition typeCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.type, "b.type_");

		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition,
				carManIdCondition, carIdCondition, typeCondition);
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (this.status != null || this.status.length() != 0) {
			json.put("status", status);
		}
		// carManId条件
		if (carManId != null) {
			json.put("carManId", carManId);
		}
		// carId条件
		if (carId != null) {
			json.put("carId", carId);
		}
		// type条件
		if (type != null) {
			json.put("type", type);
		}
		return json.isEmpty() ? null : json;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {

		Toolbar tb = new Toolbar();
		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(getDefaultOpenToolbarButton());
		} else {
			// 新建按钮
			tb.addButton(getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(getDefaultEditToolbarButton());
			// 删除
			tb.addButton(getDefaultDeleteToolbarButton());
			// 发起(客管)投诉流程
			// if (CaseBase.TYPE_COMPLAIN == Integer.valueOf(this.type)) {
			tb.addButton(new ToolbarButton().setIcon("ui-icon-play")
					.setText(getText("runcase.startFlow"))
					.setClick("bs.caseAdviceView.startFlow"));
			// }

		}
		// 公司、客管投诉添加“处理中”按钮
		if (CaseBase.TYPE_COMPLAIN == Integer.valueOf(this.type)
				|| CaseBase.TYPE_COMPANY_COMPLAIN == Integer.valueOf(this.type)) {
			tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
					this.getBSStatuses3(), "status", 0,
					getText("title.click2changeSearchStatus")));
		} else {
			tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
					this.getBSStatuses2(), "status", 0,
					getText("title.click2changeSearchStatus")));
		}

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;

	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc-business/caseAdvice/view.js";
	}

	/**
	 * 状态值转换列表：在案|处理中|结案|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses3() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("bs.status.active"));
		statuses.put(String.valueOf(CaseBase.STATUS_HANDLING),
				getText("runcase.select.status.handling"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("bs.status.closed"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	@Override
	protected String getGridDblRowMethod() {
		// 强制为只读表单
		return "bc.page.open";
	}

	@Override
	protected Condition getGridSearchCondition4OneField(String field,
			String value) {
		if (field.indexOf("car_plate") != -1) {
			return new LikeCondition(field, value != null ? value.toUpperCase()
					: value);
		} else {
			return super.getGridSearchCondition4OneField(field, value);
		}
	}

	/**
	 * 获取类型(表扬,投诉)分类值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getType() {
		Map<String, String> type = new HashMap<String, String>();
		type = new HashMap<String, String>();
		type.put(String.valueOf(Case4Advice.ADVICE_TYPE_COMPLAIN),
				getText("runcase.select.complain"));
		type.put(String.valueOf(Case4Advice.ADVICE_TYPE_SUGGEST),
				getText("runcase.select.suggest"));

		return type;
	}

	/**
	 * 获取Entity的来源转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getSourceStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(CaseBase.SOURCE_SYS),
				getText("runcase.select.source.sys"));
		statuses.put(String.valueOf(CaseBase.SOURCE_SYNC),
				getText("runcase.select.source.sync.auto"));
		statuses.put(String.valueOf(CaseBase.SOURCE_GENERATION),
				getText("runcase.select.source.sync.auto"));
		return statuses;
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
	public void setActorService(
			@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
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
