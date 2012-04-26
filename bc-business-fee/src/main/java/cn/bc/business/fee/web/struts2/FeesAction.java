/**
 * 
 */
package cn.bc.business.fee.web.struts2;

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
import cn.bc.business.fee.domain.FeeDetail;
import cn.bc.business.motorcade.service.MotorcadeService;
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
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 承包费视图Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FeesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 承包费的状态，多个用逗号连接
	public Long carManId;
	public Long carId;

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：收费日期|车牌号
		return new OrderCondition("f.fee_month", Direction.Desc).add("f.car_plate",Direction.Desc);
	}

	@Override
	public boolean isReadonly() {
		// 承包费管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.fee.manage"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id,f.status_,f.fee_year,f.fee_month,f.fee_date");
		sql.append(",c.company,unit.name unit_name,f.motorcade_id,f.motorcade_name");
		sql.append(",f.car_id,f.car_plate,c.code,f.payer_id,f.payer_name");
		sql.append(",f.s_subtotal,f.s_upkeep,f.a_subtotal,f.a_upkeep,f.s_total,f.a_total,f.sa_total1,f.r_total,f.o_total,f.desc_,f.collection_way,f.sa_total2");
		sql.append(",getfeedetailbyfeeid(f.id,"+FeeDetail.TYPE_REAL+") r_fee_detail");
		sql.append(",getfeedetailbyfeeid(f.id,"+FeeDetail.TYPE_OWE+") o_fee_detail");
		sql.append(",getb4feedetailbyfeeid(f.car_id,f.fee_year,f.fee_month) b4_o_fee_detail");
		sql.append(" from bs_fee f ");
		sql.append(" left join bs_car c on c.id=f.car_id");
		sql.append(" left join bs_carman d on d.id=f.payer_id");
		sql.append(" left join bs_motorcade m on m.id=f.motorcade_id");
		sql.append(" left join bc_identity_actor unit on unit.id=m.unit_id");
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
				map.put("fee_year", rs[i++]);
				map.put("fee_month", rs[i++]);
				map.put("fee_date", rs[i++]);
				map.put("company", rs[i++]);
				map.put("unit_name", rs[i++]);
				map.put("motorcade_id", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("car_id", rs[i++]);
				map.put("car_plate", rs[i++]);
				map.put("code", rs[i++]);
				map.put("payer_id", rs[i++]);
				map.put("payer_name", rs[i++]);
				map.put("s_subtotal", rs[i++]);
				map.put("s_upkeep", rs[i++]);
				map.put("a_subtotal", rs[i++]);
				map.put("a_upkeep", rs[i++]);
				
				// 本期应收合计
				map.put("s_total", rs[i++]);
				// 本期调整合计
				map.put("a_total", rs[i++]);
				// 本期应收款合计
				map.put("sa_total1",rs[i++]);
				// 本期实收合计
				map.put("r_total", rs[i++]);
				// 本期欠费合计
				map.put("o_total", rs[i++]);
				map.put("desc_", rs[i++]);
				//收款日期
				map.put("collection_way", rs[i++]);
				// 前期加本期实收
				map.put("sa_total2", rs[i++]);
				// 本期实收明细
				map.put("r_fee_detail", rs[i++]);
				// 本期欠费明细
				map.put("o_fee_detail", rs[i++]);
				// 前期欠费明细
				map.put("b4_o_fee_detail", rs[i++]);
				
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("f.id", "id"));
		columns.add(new TextColumn4MapKey("f.status_", "status_",
				getText("fee.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getFeeStatuses())));
		//年份
		columns.add(new TextColumn4MapKey("f.fee_year", "fee_year",
				getText("fee.feeYear"), 50).setSortable(true));
		//月份
		columns.add(new TextColumn4MapKey("f.fee_month", "fee_month",
				getText("fee.feeMonth"), 50).setSortable(true));
		//收款日期
		columns.add(new TextColumn4MapKey("f.fee_date", "fee_date",
				getText("fee.feeDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		// 公司
		columns.add(new TextColumn4MapKey("c.company", "company",
				getText("fee.company"), 40).setSortable(true)
				.setUseTitleFromLabel(true));
		// 分公司
		columns.add(new TextColumn4MapKey("unit.name", "unit_name",
				getText("fee.unitname"), 65).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车队
		columns.add(new TextColumn4MapKey("f.motorcade_name", "motorcade_name",
				getText("fee.motorcade"), 65)
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
		//车牌
		columns.add(new TextColumn4MapKey("f.car_plate", "car_plate",
				getText("fee.plate"), 80).setUseTitleFromLabel(true)
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
		//车辆自编号
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("fee.carCode"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		//缴费人
		columns.add(new TextColumn4MapKey("f.payer_name", "payer_name",
				getText("fee.payerName"), 80)
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
											.get("payer_id"));
						}
				}));
		
		//本期应收合计
		columns.add(new TextColumn4MapKey("f.s_total", "s_total",
				getText("fee.benqiyingshou"), 80).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		//本期调整合计
		columns.add(new TextColumn4MapKey("f.a_total", "a_total",
				getText("fee.benqitiaozheng"), 80).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		//本期应收款合计
		columns.add(new TextColumn4MapKey("f.sa_total1", "sa_total1",
				getText("fee.benqiyingshouheji"), 95).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		
		//前期加本期应收合计
		columns.add(new TextColumn4MapKey("f.sa_total2", "sa_total2",
				getText("fee.qianqibenqiyingshouheji"), 110).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		
		//本期实收
		columns.add(new TextColumn4MapKey("f.r_total", "r_total",
				getText("fee.benqishishou"), 80).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		//本期实收明细
		columns.add(new TextColumn4MapKey("f.r_total", "r_fee_detail",
				getText("fee.benqishishou.detail"), 120).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		//本期欠费
		columns.add(new TextColumn4MapKey("f.o_total", "o_total",
				getText("fee.benqiqianfei"), 80).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		//本期欠费明细
		columns.add(new TextColumn4MapKey("f.o_total", "o_fee_detail",
				getText("fee.benqiqianfei.detail"), 120).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		//前期欠费明细
		columns.add(new TextColumn4MapKey("f.o_total", "b4_o_fee_detail",
				getText("fee.qianqiqianfei.detail"), 120).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		//备注
		columns.add(new TextColumn4MapKey("f.desc_", "desc_",
				getText("fee.description"), 80)
				.setUseTitleFromLabel(true));
		//收款方式
		columns.add(new TextColumn4MapKey("f.collection_way", "collection_way",
				getText("fee.collectionWay"), 70).setSortable(true)
				.setUseTitleFromLabel(true));

		
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_type", "c.plate_no", "c.code",
				 "f.payer_name", "c.company","unit.name", "f.motorcade_name" };
	}

	@Override
	protected String getFormActionName() {
		return "fee";
	}
	
	@Override
	/** 页面需要另外加载的js、css文件，逗号连接多个文件 */
	protected String getHtmlPageJs() {
		return getContextPath() + "/bc-business/fee/form.js";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(780).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['car_plate']+'\t '+['fee_year']+'年'+['fee_month']+'月'+'\t 的承包费信息'";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// carManId条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("f.driver_id", carManId);
		}

		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("f.car_id", carId);
		}

		// 合并条件
		return ConditionUtils.mix2AndCondition(carManIdCondition, carIdCondition);
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		if (carManId != null) {
			json.put("carManId", carManId);
		}
		// carId条件
		if (carId != null) {
			json.put("carId", carId);
		}
		return json.isEmpty() ? null : json;
	}

	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.read")));
		} else {
			// 新建按钮
			tb.addButton(Toolbar
					.getDefaultCreateToolbarButton(getText("label.create")));

			// 编辑按钮
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.edit")));
			
			// 删除按钮
			tb.addButton(Toolbar.getDefaultDeleteToolbarButton(getText("label.delete")));
			
			// "批量初始化"按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("fee.batchInti"))
					.setClick("bs.feeForm.batchInti"));
		}
		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	/**
	 * 状态值转换列表：在案|注销|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getFeeStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("fee.status.active"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("fee.status.logout"));
		statuses.put("", getText("fee.status.all"));
		return statuses;
	}
	
	@Override
	protected LikeCondition getGridSearchCondition4OneField(String field,
			String value) {
		if (field.indexOf("car_plate") != -1) {// 车牌，忽略大小写
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
	public JSONArray feeMonths;// 营运性质列表

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
					OptionConstants.FEE_MONTH
				});
		// 列表
		this.feeMonths = OptionItem.toLabelValues(
				optionItems.get(OptionConstants.FEE_MONTH), "value");
	}

	// ==高级搜索代码结束==
	

}
