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
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.business.runcase.domain.CaseBase;
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
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 交通违章视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseTrafficsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(CaseBase.STATUS_ACTIVE); // 交通违章的状态，多个用逗号连接
	public Long carManId;
	public Long carId;

	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractTraffic"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：登记日期|状态
		return new OrderCondition("b.file_date", Direction.Desc).add(
				"b.status_", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select cit.id,b.status_,b.subject,b.motorcade_name,b.car_plate,b.driver_name,b.closer_name,b.happen_date");
		sql.append(",b.close_date,b.address,b.from_,b.source,b.driver_cert,b.case_no,b.driver_id,b.car_id,b.company,c.code");
		sql.append(",bia.id batch_company_id,bia.name batch_company,cit.infract_code,cit.penalty");
		//最新参与流程信息
		sql.append(",getnewprocessnameandtodotasknames4midmtyle(cit.id,'");
		sql.append(Case4InfractTraffic.class.getSimpleName());
		sql.append("') as processInfo");
		sql.append(" from bs_case_infract_traffic cit inner join BS_CASE_BASE b on cit.id=b.id");
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
				map.put("subject", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("car_plate", rs[i++]);
				map.put("driver_name", rs[i++]);
				map.put("closer_name", rs[i++]);
				map.put("happen_date", rs[i++]);
				map.put("close_date", rs[i++]);
				map.put("address", rs[i++]);
				map.put("from_", rs[i++]);
				map.put("source", rs[i++]);
				map.put("driver_cert", rs[i++]);
				map.put("case_no", rs[i++]);
				map.put("driver_id", rs[i++]);
				map.put("car_id", rs[i++]);
				map.put("company", rs[i++]);
				map.put("code", rs[i++]);
				map.put("batch_company_id", rs[i++]);
				map.put("batch_company", rs[i++]);
				map.put("infract_code", rs[i++]);
				map.put("penalty", rs[i++]);
				map.put("processInfo",rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("cit.id", "id"));
		columns.add(new TextColumn4MapKey("b.status_", "status_",
				getText("runcase.status"), 50).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getCaseStatuses())));
		columns.add(new TextColumn4MapKey("b.source", "source",
				getText("runcase.source"), 40).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) { // 系统来源source:
																			// 自建,接口(对应旧数据),生成(区分金盾网,交委接口)
						// 从上下文取出元素Map //事件来源from: 由用户自己填写,或者由金盾网提供
						// 格式: source字段+from字段,如果from字段没有数据则只显示source
						@SuppressWarnings("unchecked")
						// 如: 生成 - 金盾网/电子警察
						Map<String, Object> obj = (Map<String, Object>) context;
						if (null != obj.get("from_")
								&& obj.get("from_").toString().length() > 0) {
							return getSourceStatuses().get(
									obj.get("source") + "")
									+ " - " + obj.get("from_");
						} else if (null != obj.get("source")
								&& obj.get("source").toString().length() > 0) {
							return getSourceStatuses().get(
									obj.get("source") + "");
						} else {
							return "";
						}
					}
				}));
		columns.add(new TextColumn4MapKey("b.happen_date", "happen_date",
				getText("runcase.happenDate"), 130).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("b.company", "company",
				getText("runcase.company2"), 40).setSortable(true));
		columns.add(new TextColumn4MapKey("bia.name", "batch_company",
				getText("runcase.batch.company"), 70).setSortable(true));
		columns.add(new TextColumn4MapKey("b.motorcade_name", "motorcade_name",
				getText("runcase.motorcadeName"), 70)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.car_plate", "car_plate",
				getText("runcase.carPlate"), 80).setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/car/edit?id={0}", "car") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("car_id"));
							}
						}));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("runcase.accident.carCode"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.driver_name", "driver_name",
				getText("runcase.driverName"), 70).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.driver_cert", "driver_cert",
				getText("runcase.FWZGCert"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.subject", "subject",
				getText("runcase.subject"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.address", "address",
				getText("runcase.address"), 120));
		columns.add(new TextColumn4MapKey("b.case_no", "case_no",
				getText("runcase.caseNo1"), 150).setUseTitleFromLabel(true));
		if(!isReadonly()){
			//最新参与流程信息
			columns.add(new TextColumn4MapKey("", "processInfo",
					getText("runcase.processInfo"), 350).setSortable(true)
					.setUseTitleFromLabel(true)
					.setValueFormater(new LinkFormater4Id(this.getContextPath()
							+ "/bc-workflow/workspace/open?id={0}", "workspace") {
						
						@Override
						public String getIdValue(Object context, Object value) {
							@SuppressWarnings("unchecked")
							String processInfo=StringUtils.toString(((Map<String, Object>) context).get("processInfo"));
							if(processInfo==null||processInfo.length()==0)
								return "";
							
							//获取流程id
							return processInfo.split(";")[1];
						}

						@Override
						public String getTaskbarTitle(Object context,
								Object value) {
							return "工作空间";
						}

						@Override
						public String getLinkText(Object context, Object value) {
							@SuppressWarnings("unchecked")
							String processInfo=StringUtils.toString(((Map<String, Object>) context).get("processInfo"));
							if(processInfo==null||processInfo.length()==0)
								return "";
							
							String title="";
							String[] processInfos=processInfo.split(";");
							if(processInfos.length>2){
								for(int i=2;i<processInfos.length;i++){
									if(i+1==processInfos.length){
										title+=processInfos[i];
									}else{
										title+=processInfos[i]+",";
									}
								}
								title+="--";
							}
							return title+="["+processInfos[0]+"]";
						}
						
						@Override
						public String getWinId(Object context,
								Object value) {
							@SuppressWarnings("unchecked")
							String processInfo=StringUtils.toString(((Map<String, Object>) context).get("processInfo"));
							if(processInfo==null||processInfo.length()==0)
								return "";
							
							//获取流程id
							return this.moduleKey+"::"+processInfo.split(";")[1];
						}
					}));
		}
		
		columns.add(new TextColumn4MapKey("b.closer_name", "closer_name",
				getText("runcase.closerName"), 70).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("cit.infract_code", "infract_code",
				getText("runcase.infractCode"), 70).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("cit.penalty", "penalty",
				getText("runcase.penalty"), 120).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.##")));
		columns.add(new TextColumn4MapKey("b.close_date", "close_date",
				getText("runcase.closeDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.case_no", "b.car_plate", "b.driver_name",
				"b.motorcade_name", "b.closer_name", "b.subject",
				"b.driver_cert", "c.code"
				,"getnewprocessnameandtodotasknames4midmtyle(cit.id,'"+Case4InfractTraffic.class.getSimpleName()+"')"};
	}

	@Override
	protected String getFormActionName() {
		return "caseTraffic";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(480).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['car_plate']";
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

		return ConditionUtils.mix2AndCondition(statusCondition,
				carManIdCondition, carIdCondition);
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}

		if (carManId != null) {
			json.put("carManId", carManId);
		}
		// carId条件
		if (carId != null) {
			json.put("carId", carId);
		}
	}

	/**
	 * 获取Entity的状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getCaseStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(CaseBase.STATUS_ACTIVE),
				getText("runcase.select.status.active"));
		statuses.put(String.valueOf(CaseBase.STATUS_CLOSED),
				getText("runcase.select.status.closed"));
		statuses.put(String.valueOf(CaseBase.STATUS_HANDLING),
				getText("runcase.select.status.handling"));
		return statuses;
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
				getText("runcase.select.source.sync.gen"));
		return statuses;
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
			// 发起流程
			tb.addButton(new ToolbarButton().setIcon("ui-icon-play")
					.setText(getText("runcase.startFlow"))
					.setClick("bs.caseTrafficView.startFlow"));
			// 出租车协会查询
			/*
			 * tb.addButton(new ToolbarButton().setIcon("ui-icon-check")
			 * .setText(getText("tempDriver.gztaxixhDriverInfo"))
			 * .setClick("bs.tempDriverView.gztaxixhDriverInfo"));
			 */

		}

		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getBSStatuses2(),
				"status", 0, getText("title.click2changeSearchStatus")));

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	/**
	 * 状态值转换列表：在案|处理中|结案|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses2() {
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
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc-business/caseTraffic/view.js";
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
