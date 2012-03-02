/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.runcase.domain.Case4InfractBusiness;
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
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 营运违章视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseBusinesssAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(CaseBase.STATUS_ACTIVE); // 营运违章的状态，多个用逗号连接
	public Long carManId;
	public Long carId;
	
	@Override
	public boolean isReadonly() {
		// 营运违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractBusiness"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：登记日期|状态
		return new OrderCondition("b.file_date", Direction.Desc).add(
				"b.status_", Direction.Asc).add("b.happen_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select cit.id,cit.charger,cit.category,b.status_,b.subject,b.motorcade_name,b.car_plate,b.driver_name,b.closer_name,b.happen_date");
		sql.append(",b.close_date,b.address,b.from_,b.source,b.driver_cert,b.case_no,b.motorcade_id,b.driver_id,b.car_id");
		sql.append(",b.company,c.bs_type");
		sql.append(",man.origin");
		sql.append(" from bs_case_infract_business cit");
		sql.append(" inner join BS_CASE_BASE b on cit.id=b.id");
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
				map.put("charger", rs[i++]);
				map.put("category", rs[i++]);
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
				map.put("motorcade_id", rs[i++]);
				map.put("driverId", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("company", rs[i++]);
				map.put("bs_type", rs[i++]);
				map.put("origin", rs[i++]);
				
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("cit.id","id"));
		columns.add(new TextColumn4MapKey("b.case_no", "case_no",
				getText("runcase.caseNo2"),110));
		columns.add(new TextColumn4MapKey("b.status_", "status_",
				getText("runcase.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses2())));
		columns.add(new TextColumn4MapKey("b.company", "company",
				getText("runcase.company"), 60).setSortable(true));
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
		columns.add(new TextColumn4MapKey("b.subject", "subject",
				getText("runcase.subject"), 180).setUseTitleFromLabel(true)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("b.happen_date", "happen_date",
				getText("runcase.happenDate2"), 125).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("b.address", "address",
				getText("runcase.address2"), 200).setUseTitleFromLabel(true)
		);
		columns.add(new TextColumn4MapKey("cit.category", "category",
				getText("runcase.category"), 70).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getCategory())));
		columns.add(new TextColumn4MapKey("cit.charger", "charger",
				getText("runcase.chargers"), 170).setUseTitleFromLabel(true)
				.setValueFormater(new LinkFormater4ChargerInfo(this
						.getContextPath()))
		);
		columns.add(new TextColumn4MapKey("c.bs_type", "bs_type",
				getText("runcase.businessType"), 80));
		columns.add(new TextColumn4MapKey("man.origin", "origin",
				getText("runcase.origin"), 150));
		
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.case_no", "b.car_plate","b.driver_name","b.driver_cert",
						"b.closer_name", "b.subject" };
	}

	@Override
	protected String getFormActionName() {
		return "caseBusiness";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(450).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['car_plate']+'\t 的营运违章信息'";
	}

	
	@Override
	protected Condition getGridSpecalCondition() {
		//状态条件
		Condition statusCondition = ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"b.status_");
		
		// carManId条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("b.driver_id", carManId);
		}
		
		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition =new EqualsCondition("b.car_id", carId);
		}
		
		return ConditionUtils.mix2AndCondition(statusCondition,carManIdCondition,carIdCondition);
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
				getText("runcase.select.source.sync.auto"));
		return statuses;
	}
	
	/**
	 * 获取Entity的违章类别转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getCategory() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(Case4InfractBusiness.CATEGORY_BUSINESS),
				getText("runcase.category.business"));
		statuses.put(String.valueOf(Case4InfractBusiness.CATEGORY_STATION),
				getText("runcase.category.station"));
		statuses.put(String.valueOf(Case4InfractBusiness.CATEGORY_SERVICE),
				getText("runcase.category.service"));
		return statuses;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return super.getHtmlPageToolbar()
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getBSStatuses2(), "status", 0,
								getText("title.click2changeSearchStatus")));
	}
	
	@Override
	protected LikeCondition getGridSearchCondition4OneField(String field,
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
						.findOptionItemByGroupKeys(new String[] {
							OptionConstants.CAR_BUSINESS_NATURE
						});
		
		// 营运性质列表
		this.businessTypes = OptionItem.toLabelValues(
				optionItems.get(OptionConstants.CAR_BUSINESS_NATURE), "value");
	}

	// ==高级搜索代码结束==

}
