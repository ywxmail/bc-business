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

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.runcase.domain.Case4Lost;
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
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 投诉视图Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseLostsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
	public String type;
	public Long carManId;
	public Long carId;
	
	private Map<String, String> levels;
	private Map<String, String> sitePostions;
	private Map<String, String> results;
	private Map<String, String> handleResults;
	
	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		//报失管理员和超级管理员
		return !context.hasAnyRole(getText("key.role.bs.lost"),
				getText("key.role.bc.admin"));
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
		sql.append("select l.id,b.status_,b.happen_date,l.items,l.money,l.level,l.owner_name,l.owner_sex,l.owner_tel,b.driver_name,b.driver_cert");
		sql.append(",b.car_plate,c.code carCode,b.company,bia.name batch_company,b.motorcade_name,l.result_,l.handle_result,l.receive_date,l.site_postion,l.is_took");
		sql.append(",l.taker_name,l.took_date,l.taker_identity,b.code,iah.actor_name author,b.file_date,iah2.actor_name modifier,b.modified_date,b.closer_name,b.close_date");
		sql.append(",b.from_,b.source");
		sql.append(",b.motorcade_id,b.car_id,b.driver_id,b.desc_");
		sql.append(" from BS_CASE_LOST l");
		sql.append(" inner join BS_CASE_BASE b on b.id=l.id");
		sql.append(" left join BS_CAR c on b.car_id = c.id");
		sql.append(" left join BS_CARMAN man on b.driver_id=man.id");
		sql.append(" left join bs_motorcade m on m.id=b.motorcade_id");
		sql.append(" left join bc_identity_actor bia on bia.id=m.unit_id");
		sql.append(" left join bc_identity_actor_history iah on b.author_id = iah.id");
		sql.append(" left join bc_identity_actor_history iah2 on b.modifier_id = iah2.id");

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
				map.put("happen_date", rs[i++]);
				map.put("items", rs[i++]);
				map.put("money", rs[i++]);
				map.put("level", rs[i++]);
				map.put("owner_name", rs[i++]);
				map.put("owner_sex", rs[i++]);
				map.put("owner_tel", rs[i++]);
				map.put("driver_name", rs[i++]);
				map.put("driver_cert", rs[i++]);
				map.put("car_plate", rs[i++]);
				map.put("carCode", rs[i++]);
				map.put("company", rs[i++]);
				map.put("batch_company", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("result_", rs[i++]);
				map.put("handle_result", rs[i++]);
				map.put("receive_date", rs[i++]);
				map.put("site_postion", rs[i++]);
				map.put("is_took", rs[i++]);
				map.put("taker_name", rs[i++]);
				map.put("took_date", rs[i++]);
				map.put("taker_identity", rs[i++]);
				map.put("code", rs[i++]);
				map.put("author", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("modifier", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("closer_name", rs[i++]);
				map.put("close_date", rs[i++]);
				map.put("from_", rs[i++]);
				map.put("source", rs[i++]);
				map.put("motorcade_id", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("driverId", rs[i++]);
				map.put("desc_", rs[i++]);
				
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		// 初始化optionItem列表
		Map<String,List<Map<String, String>>> map = this.optionService.findOptionItemByGroupKeys(new String[] {
				OptionConstants.LOST_SITE_POSTION,OptionConstants.LOST_LEVEL,
				OptionConstants.LOST_RESULT,OptionConstants.LOST_HANDLE_RESULT,
			});
		this.sitePostions = this.getItems(map.get(OptionConstants.LOST_SITE_POSTION));
		this.levels = this.getItems(map.get(OptionConstants.LOST_LEVEL));
		this.results = this.getItems(map.get(OptionConstants.LOST_RESULT));
		this.handleResults = this.getItems(map.get(OptionConstants.LOST_HANDLE_RESULT));
		
		// 设置视图列
		List<Column> columns = new ArrayList<Column>();
		
		columns.add(new IdColumn4MapKey("l.id", "id"));
		columns.add(new TextColumn4MapKey("b.status_", "status_",
				getText("runcase.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses2())));
		// 受理号
		columns.add(new TextColumn4MapKey("b.code", "code",
				getText("runcase.receiveCode"), 120).setUseTitleFromLabel(true));
		// 事发时间
		columns.add(new TextColumn4MapKey("b.happen_date", "happen_date",
				getText("runcase.happenDate"), 125).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		// 报失物品
		columns.add(new TextColumn4MapKey("l.items", "items",
				getText("runcase.lost.items"), 140).setUseTitleFromLabel(true));
		// 价值估算
		columns.add(new TextColumn4MapKey("l.money", "money",
				getText("runcase.lost.money"), 60).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.##")));
		// 级别
		columns.add(new TextColumn4MapKey("l.level", "level",
				getText("runcase.lost.level"), 100).
				setValueFormater(new KeyValueFormater(levels)));
		// 报失人
		columns.add(new TextColumn4MapKey("l.owner_name", "owner_name",
				getText("runcase.lost.ownerName"), 80));
		// 报失人性别
		columns.add(new TextColumn4MapKey("l.owner_sex", "owner_sex",
				getText("runcase.lost.ownerSex"), 80).
				setValueFormater(new KeyValueFormater(getSexs())));
		// 联系电话
		columns.add(new TextColumn4MapKey("l.owner_tel", "owner_tel",
				getText("runcase.lost.ownerTel"), 110).setUseTitleFromLabel(true));

		
		if (carManId == null) {
		// 司机
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
		// 司机资格证
		columns.add(new TextColumn4MapKey("b.driver_cert", "driver_cert",
				getText("runcase.FWZGCert"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		if (carId == null) {
		//车辆
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
		// 车辆自编号
		columns.add(new TextColumn4MapKey("c.code", "carCode",
				getText("runcase.accident.carCode"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		// 公司
		columns.add(new TextColumn4MapKey("b.company", "company",
				getText("runcase.company"), 60).setSortable(true));
		// 分公司
		columns.add(new TextColumn4MapKey("bia.name", "batch_company",
				getText("runcase.batch.company"), 70).setSortable(true));
		// 车队
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
		
		//失物去向
		columns.add(new TextColumn4MapKey("l.result_", "result_",
				getText("runcase.lost.result"), 100).
				setValueFormater(new KeyValueFormater(results)));
		//处理结果
		columns.add(new TextColumn4MapKey("l.handle_result", "handle_result",
				getText("runcase.lost.handleResult"), 150).
				setValueFormater(new KeyValueFormater(handleResults)));
		//接报失时间
		columns.add(new TextColumn4MapKey("l.receive_date", "receive_date",
				getText("runcase.lost.receiveDate"), 125).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		//遗失位置
		columns.add(new TextColumn4MapKey("l.site_postion", "site_postion",
				getText("runcase.lost.sitePostion"), 100).
				setValueFormater(new KeyValueFormater(sitePostions)));
		// 领取
		columns.add(new TextColumn4MapKey("l.is_took", "is_took",
				getText("runcase.lost.took"), 40).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		// 领取人
		columns.add(new TextColumn4MapKey("l.taker_name", "taker_name",
				getText("runcase.lost.takerName"), 60));
		// 领取人时间
		columns.add(new TextColumn4MapKey("l.took_date", "took_date",
				getText("runcase.lost.tookDate"), 125).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		// 领取人证件
		columns.add(new TextColumn4MapKey("l.taker_identity", "taker_identity",
				getText("runcase.lost.takerIdentity"), 100));
		// 创建人
		columns.add(new TextColumn4MapKey("iah.actor_name", "author",
				getText("runcase.lost.author"), 80)
				.setUseTitleFromLabel(true));
		// 创建时间
		columns.add(new TextColumn4MapKey("b.file_date", "file_date",
				getText("runcase.lost.fileDate"), 125).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		// 最后修改人
		columns.add(new TextColumn4MapKey("iah2.actor_name", "modifier",
				getText("runcase.lost.modifier"), 80)
				.setUseTitleFromLabel(true));
		// 最后修改时间
		columns.add(new TextColumn4MapKey("b.modified_date", "modified_date",
				getText("runcase.lost.modifiedDate"),125).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		//备注
		columns.add(new TextColumn4MapKey("b.desc_", "desc_",
				getText("runcase.description"))
				.setUseTitleFromLabel(true));
		
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.subject", "b.car_plate",
				 "b.driver_name", "b.driver_cert","c.code","b.code","l.items" };
	}

	@Override
	protected String getFormActionName() {
		return "caseLost";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}
	
	@Override
	protected String getGridRowLabelExpression() {
		return "['car_plate']+'\t 的报失信息'";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
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
			carIdCondition = new EqualsCondition("b.car_id", carId);
		}
		
		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition,carManIdCondition,
				carIdCondition);
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
		return super.getHtmlPageToolbar().addButton(
				Toolbar.getDefaultToolbarRadioGroup(
						this.getBSStatuses2(), "status", 0,
						getText("title.click2changeSearchStatus")));
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
	 * 获取Entity的级别转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getItems(List<Map<String, String>> list) {
		Map<String, String> levels = new HashMap<String, String>();
		for(Map<String, String> obj : list){
			levels.put(obj.get("key"), obj.get("value"));
		}
		return levels;
	}
	
	/**
	 * 获取Entity的性别转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getSexs() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(Case4Lost.SEX_NONE),
				"未定义");
		statuses.put(String.valueOf(Case4Lost.SEX_MAN),
				"男");
		statuses.put(String.valueOf(Case4Lost.SEX_WOMAN),
				"女");
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
	public JSONArray handleResult;// 处理结果列表
	public JSONArray result;// 处理结果列表

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
							OptionConstants.LOST_HANDLE_RESULT,
							OptionConstants.LOST_RESULT
						});
		
		// 处理结果列表
		this.handleResult = OptionItem.toLabelValues(
				optionItems.get(OptionConstants.LOST_HANDLE_RESULT), "value","key");
		
		// 失物去向列表
		this.result = OptionItem.toLabelValues(
				optionItems.get(OptionConstants.LOST_RESULT), "value","key");


	}

	// ==高级搜索代码结束==

}
