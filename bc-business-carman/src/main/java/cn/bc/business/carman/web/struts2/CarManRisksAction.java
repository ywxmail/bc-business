/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.business.carman.service.CarManRiskService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
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
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.FooterButton;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
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
	public String status = String.valueOf(CarManRisk.STATUS_ENABLED); // 状态，多个用逗号连接
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
		// 默认排序方向：状态|创建日期
		return new OrderCondition("m.status_", Direction.Asc).add(
				"m.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select m.id as mid,m.status_ as mstatus,m.sex,m.name as mname");
		sql.append(",m.cert_fwzg,m.cert_identity,m.file_date as man_file_date");
		sql.append(",r.id as rid,r.code,r.company as rcompany,r.holder,r.buy_type");
		sql.append(",r.start_date,r.end_date,r.file_date as risk_file_date");
		sql.append(",c.company car_company,bia.name unit_name,mo.name motorcade_name");
		sql.append(" from bs_carman_risk_insurant ri");
		sql.append(" inner join bs_carman_risk r on r.id=ri.risk_id");
		sql.append(" inner join bs_carman m on m.id=ri.man_id");
		sql.append(" left join bs_car c on m.main_car_id=c.id");
		sql.append(" left join bs_motorcade mo on c.motorcade_id=mo.id");
		sql.append(" left join bc_identity_actor bia on bia.id=mo.unit_id");
		sqlObject.setSql(sql.toString());

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
				map.put("id", rs[i++]);
				map.put("risk_code", rs[i++]);
				map.put("risk_company", rs[i++]);
				map.put("risk_holder", rs[i++]);
				map.put("risk_buyType", rs[i++]);
				map.put("risk_start_date", rs[i++]);
				map.put("risk_end_date", rs[i++]);
				map.put("risk_file_date", rs[i++]);
				map.put("car_company", rs[i++]);
				map.put("unit_name", rs[i++]);
				map.put("motorcade_name", rs[i++]);
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
				getText("carManRisk.status"), 40)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						Date now = Calendar.getInstance().getTime();
						Date endDate = (Date) map.get("risk_end_date");
						if(endDate == null){
							return "长期";
						}else if(now.before(endDate)){
							return "正常";
						}else if(now.after(endDate)){
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
		columns.add(new TextColumn4MapKey("m.name", "man_name",
				getText("carManRisk.manName"), 50).setSortable(true));
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
				getText("carManRisk.holder"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("r.file_date", "risk_file_date",
				getText("carManRisk.fileDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));

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
		return super.getHtmlPageOption().setWidth(600).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['drvierName']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("m.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("m.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		} else {
			return null;
		}
		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition);

	}

	@Override
	protected Json getGridExtrasData() {
		if (this.status == null || this.status.length() == 0) {
			return null;
		} else {
			Json json = new Json();
			json.put("status", status);
			return json;
		}
	}

	/**
	 * 获取司机分类值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getType() {
		Map<String, String> type = new LinkedHashMap<String, String>();
		type.put(String.valueOf(CarMan.TYPE_DRIVER),
				getText("carMan.type.driver"));
		type.put(String.valueOf(CarMan.TYPE_CHARGER),
				getText("carMan.type.charger"));
		type.put(String.valueOf(CarMan.TYPE_DRIVER_AND_CHARGER),
				getText("carMan.type.driverAndCharger"));
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
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		tb.addButton(Toolbar.getDefaultEmptyToolbarButton());

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
			// 新建按钮
			// tb.addButton(this.getDefaultCreateToolbarButton());
		} else {
			// 新建按钮
			// tb.addButton(this.getDefaultCreateToolbarButton());

			// 编辑按钮
			// tb.addButton(this.getDefaultEditToolbarButton());

			// 删除按钮
			// tb.addButton(this.getDefaultDeleteToolbarButton());
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());
		return tb;
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

	public JSONArray moveTypes;// 迁移类型
	public JSONArray motorcades;// 车队的下拉列表信息
	public JSONArray units;// 分公司的下拉列表信息
	public JSONArray riskCompanies;// 保司列表

	@Override
	protected void initConditionsFrom() throws Exception {
		// 可选迁移类型列表
		moveTypes = new JSONArray();
		Map<String, String> mt = getMoveType();
		if (mt != null) {
			JSONObject json;
			Iterator<String> iterator = mt.keySet().iterator();
			String key;
			while (iterator.hasNext()) {
				key = iterator.next();
				json = new JSONObject();
				json.put("label", mt.get(key));
				json.put("value", key);
				moveTypes.put(json);
			}
		}

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
}
