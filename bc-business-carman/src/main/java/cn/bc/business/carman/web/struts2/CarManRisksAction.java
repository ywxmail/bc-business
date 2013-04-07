/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
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

import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.business.carman.service.CarManRiskService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.LinkFormater4CarInfo;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.GreaterThanOrEqualsCondition;
import cn.bc.core.query.condition.impl.IsNullCondition;
import cn.bc.core.query.condition.impl.LessThanCondition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.formater.SexFormater;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.FooterButton;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

import com.google.gson.JsonObject;

/**
 * 司机人意险视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarManRisksAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	/** 购买状态：有效 */
	public static final int VIEW_STATUS_ENABLED = 0;
	/** 购买状态：已过期 */
	public static final int VIEW_STATUS_OVERDUE = 1;
	/** 购买状态：全部 */
	public static final int VIEW_STATUS_ALL = 9;
	public int viewStatus = VIEW_STATUS_ENABLED;// 购买状态

	private CarManRiskService carManRiskService;

	@Autowired
	public void setCarManRiskService(CarManRiskService carManRiskService) {
		this.carManRiskService = carManRiskService;
	}

	@Override
	public boolean isReadonly() {
		// 司机人意险管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.carManRisk"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建日期|保险编号|司机创建日期
		return new OrderCondition("r.file_date", Direction.Desc).add("r.code", Direction.Asc).add("m.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer select = new StringBuffer();
		select.append("m.id as mid,m.status_ as mstatus,m.sex,m.name as mname");
		select.append(",m.cert_fwzg,m.cert_identity,m.file_date as man_file_date,m.carinfo");
		select.append(",m.move_type,m.move_date,m.shiftwork_end_date");
		select.append(",r.id as rid,r.code,r.company as rcompany,r.holder,r.buy_type");
		select.append(",r.start_date,r.end_date,r.file_date as risk_file_date");
		select.append(",c.id car_id,c.plate_type car_plate_type,c.plate_no car_plate_no,c.company car_company");
		select.append(",c.manage_no,bia.name unit_name,mo.name motorcade_name,c.code car_code");
		select.append(",c.register_date");
		sqlObject.setSelect(select.toString());

		StringBuffer from = new StringBuffer();
		from.append("bs_carman_risk_insurant ri");
		from.append(" inner join bs_carman_risk r on r.id=ri.risk_id");
		from.append(" inner join bs_carman m on m.id=ri.man_id");
		from.append(" left join bs_car c on m.main_car_id=c.id");
		from.append(" left join bs_motorcade mo on c.motorcade_id=mo.id");
		from.append(" left join bc_identity_actor bia on bia.id=mo.unit_id");
		sqlObject.setFrom(from.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("man_id", rs[i++]);
				map.put("man_status", rs[i++]);
				map.put("man_sex", rs[i++]);
				map.put("man_name", rs[i++]);
				map.put("man_fwzg", rs[i++]);
				map.put("man_identity", rs[i++]);
				map.put("man_file_date", rs[i++]);
				map.put("man_carinfo", rs[i++]);
				map.put("man_move_type", rs[i++]);
				map.put("man_move_date", rs[i++]);
				map.put("man_shiftwork_end_date", rs[i++]);
				map.put("id", rs[i++]);
				map.put("risk_code", rs[i++]);
				map.put("risk_company", rs[i++]);
				map.put("risk_holder", rs[i++]);
				map.put("risk_buyType", rs[i++]);
				map.put("risk_start_date", rs[i++]);
				map.put("risk_end_date", rs[i++]);
				map.put("risk_file_date", rs[i++]);
				map.put("car_id", rs[i++]);
				map.put("car_plate_type", rs[i++]);
				map.put("car_plate_no", rs[i++]);
				map.put("car_company", rs[i++]);
				map.put("car_manage_no", rs[i++]);
				map.put("unit_name", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("car_code", rs[i++]);
				map.put("car_register_date", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("r.id", "id"));
		columns.add(new TextColumn4MapKey("r.start_date", "risk_start_date",
				getText("carManRisk.status"), 55)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						Date now = Calendar.getInstance().getTime();
						Date endDate = (Date) map.get("risk_end_date");
						if (endDate == null) {
							return "长期";
						} else if (now.before(endDate)) {
							return "有效";
						} else if (now.after(endDate)) {
							return "已过期";
						}
						return null;
					}
				}));
		columns.add(new TextColumn4MapKey("c.company", "car_company",
				getText("carMan.company"), 40).setSortable(true));
		columns.add(new TextColumn4MapKey("bia.name", "unit_name",
				getText("carMan.unit_name"), 70).setSortable(true));
		columns.add(new TextColumn4MapKey("mo.name", "motorcade_name",
				getText("carMan.motorcade"), 70).setSortable(true));
		// 司机
		columns.add(new TextColumn4MapKey("m.name", "man_name",
				getText("carManRisk.manName"), 55).setSortable(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/carMan/edit?id={0}", "carMan") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("man_id"));
							}
						}));
		columns.add(new TextColumn4MapKey("m.status_", "man_status",
				getText("carManRisk.manStatus"), 55).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses3())));
		// 车辆
		columns.add(new TextColumn4MapKey("m.carinfo", "man_carinfo",
				getText("carMan.operationCar"), 110)
				.setValueFormater(new LinkFormater4CarInfo(this
						.getContextPath())));
		columns.add(new TextColumn4MapKey("c.code", "car_code",
				getText("carMan.carCode"), 70).setSortable(true));
		columns.add(new TextColumn4MapKey("c.manage_no", "car_manage_no",
				getText("carMan.carManageNo"), 70).setSortable(true));
		columns.add(new TextColumn4MapKey("c.register_date", "car_register_date",
				getText("car.registerDate"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.cert_identity", "man_identity",
				getText("carMan.cert4Indentity"), 150));
		columns.add(new TextColumn4MapKey("r.start_date", "risk_start_date",
				getText("carManRisk.startDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("r.end_date", "risk_end_date",
				getText("carManRisk.endDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("r.company", "risk_company",
				getText("carManRisk.company"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("r.buyType", "risk_buyType",
				getText("carManRisk.buyType"), 95).setSortable(true)
				.setValueFormater(new KeyValueFormater(getBuyTypes())));
		columns.add(new TextColumn4MapKey("r.code", "risk_code",
				getText("carManRisk.code"), 135).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.cert_fwzg", "man_fwzg",
				getText("carMan.cert4FWZG"), 65));
		columns.add(new TextColumn4MapKey("m.file_date", "man_file_date",
				getText("carManRisk.manFileDate"), 85).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.sex", "man_sex",
				getText("carMan.sex"), 30).setSortable(true).setValueFormater(
				new SexFormater()));
		columns.add(new TextColumn4MapKey("r.holder", "risk_holder",
				getText("carManRisk.holder"), 180).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.move_type", "man_move_type",
				getText("carMan.move_type"), 140)
				.setValueFormater(new KeyValueFormater(getMoveType())));
		columns.add(new TextColumn4MapKey("m.move_date", "man_move_date",
				getText("carMan.moveDate"), 180)
				.setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
					@Override
					public Date getToDate(Object context, Object value) {
						@SuppressWarnings("rawtypes")
						Map contract = (Map) context;
						return (Date) contract.get("man_shiftwork_end_date");
					}
				}.setUseEmptySymbol(true)));
		columns.add(new TextColumn4MapKey("r.file_date", "risk_file_date",
				getText("carManRisk.fileDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		
		columns.add(new HiddenColumn4MapKey("man_id", "man_id"));
		columns.add(new HiddenColumn4MapKey("man_name", "man_name"));
		
		return columns;
	}

	private Map<String, String> getBuyTypes() {
		Map<String, String> type = new LinkedHashMap<String, String>();
		type.put(String.valueOf(CarManRisk.BUY_TYPE_COMPANY),
				getText("label.yes"));
		type.put(String.valueOf(CarManRisk.BUY_TYPE_SELF), getText("label.no"));
		type.put(String.valueOf(CarManRisk.BUY_TYPE_NONE), "");
		return type;
	}
	
	/**
	 * 获取迁移类型值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getMoveType() {
		Map<String, String> type = new LinkedHashMap<String, String>();
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_CLDCL),
				getText("carByDriverHistory.moveType.cheliangdaocheliang"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_GSDGSYZX),
				getText("carByDriverHistory.moveType.gongsidaogongsiyizhuxiao"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_ZXWYQX),
				getText("carByDriverHistory.moveType.zhuxiaoweiyouquxiang"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_YWGSQH),
				getText("carByDriverHistory.moveType.youwaigongsiqianhui"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_JHWZX),
				getText("carByDriverHistory.moveType.jiaohuiweizhuxiao"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_XRZ),
				getText("carByDriverHistory.moveType.xinruzhi"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_ZCD),
				getText("carByDriverHistory.moveType.cheduidaochedui"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_DINGBAN),
				getText("carByDriverHistory.moveType.dingban"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_JHZC),
				getText("carByDriverHistory.moveType.jiaohuizhuanche"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_WJZZX),
				getText("carByDriverHistory.moveType.weijiaozhengzhuxiao"));
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_NULL), "(无)");
		return type;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "m.name", "m.cert_identity", "m.cert_fwzg",
				"r.company", "r.code" };
	}

	@Override
	protected Condition getGridSearchCondition4OneField(String field,
			String value) {
		if ("m.carinfo".equals(field)) {
			return new LikeCondition(field, value != null ? value.toUpperCase()
					: value);
		} else {
			return super.getGridSearchCondition4OneField(field, value);
		}
	}

	@Override
	protected String getFormActionName() {
		return "carManRisk";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "'司机人意险保险单'+['risk_code']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 保单状态
		Condition viewStatusCondition = null;
		if (this.viewStatus == VIEW_STATUS_ENABLED) {// 有效
			Date now = Calendar.getInstance().getTime();
			viewStatusCondition = new OrCondition().setAddBracket(true)
					.add(new GreaterThanOrEqualsCondition("r.end_date", now))
					.add(new IsNullCondition("r.end_date"));
		} else if (this.viewStatus == VIEW_STATUS_OVERDUE) {// 已过期
			Date now = Calendar.getInstance().getTime();
			viewStatusCondition = new LessThanCondition("r.end_date", now);
		}
		return viewStatusCondition;
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		json.put("viewStatus", viewStatus);
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

			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());

			// 删除按钮
			tb.addButton(new ToolbarButton()
					.setIcon("ui-icon-cancel")
					.setText(getText("label.delete"))
					.setClick("bc.carManRishView.delete_"));
		}

		// 状态按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				this.getViewStatuses(), "viewStatus", 0,
				getText("title.click2changeSearchStatus")));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());
		return tb;
	}

	private Map<String, String> getViewStatuses() {
		Map<String, String> vs = new LinkedHashMap<String, String>();
		vs.put(String.valueOf(VIEW_STATUS_ENABLED),
				getText("carManRisk.viewStatus.active"));// 有效
		vs.put(String.valueOf(VIEW_STATUS_OVERDUE),
				getText("carManRisk.viewStatus.overdue"));// 已过期
		vs.put(String.valueOf(VIEW_STATUS_ALL),
				getText("carManRisk.viewStatus.all"));// 全部
		return vs;
	}

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
	public JSONArray riskCompanies;// 保司列表

	@Override
	protected void initConditionsFrom() throws Exception {
		// 可选分公司列表
		units = OptionItem.toLabelValues(this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT }, (Integer[]) null), "name",
				"id");

		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));

		// 可选保司列表
		riskCompanies = new JSONArray(
				this.carManRiskService.findRiskCompanies());
	}

	@Override
	protected FooterButton getGridFooterImportButton() {
		// 管理员才能导入
		if (this.isReadonly())
			return null;

		// 获取默认的导入按钮设置
		FooterButton fb = this.getDefaultGridFooterImportButton();

		// 配置特殊参数
		JsonObject cfg = new JsonObject();
		cfg.addProperty("tplCode", "IMPORT_CARMAN_RISK");// 模板编码
		cfg.addProperty("importAction", "bc-business/carManRisk/import");// 导入数据的action路径(使用相对路径)
		cfg.addProperty("headerRowIndex", 1);// 列标题所在行的索引号(0-based)
		cfg.addProperty("ptype", "Import" + CarManRisk.KEY_UID);
		cfg.addProperty("puid", CarManRisk.KEY_UID);
		fb.setAttr("data-cfg", cfg.toString());

		// 返回导入按钮
		return fb;
	}
	
	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc-business/carManRisk/view.js";
	}
}
