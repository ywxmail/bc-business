/**
 * 
 */
package cn.bc.business.ownership.web.struts2;

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

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.LinkFormater4CarInfo;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.IsNotNullCondition;
import cn.bc.core.query.condition.impl.IsNullCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.query.condition.impl.QlCondition;
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
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.FooterButton;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

import com.google.gson.JsonObject;

/**
 * 车辆经营权视图Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OwnershipsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
	public Long carId;// 车辆Id
	private OptionService optionService;
	public List<Map<String, String>> logoutReasonList; // 注销原因列表
	public String mateValue = "weipipei";// 经营权是否车辆,默认未匹配

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 车辆经营权管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.ownership"));
	}

	public boolean isCheck4Advanced() {
		// 车辆经营权查看(高级)
		SystemContext context = (SystemContext) this.getContext();
		return context
				.hasAnyRole(getText("key.role.bs.ownership.check_advanced"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：车辆状态|创建日期
		return new OrderCondition("o.status_", Direction.Asc).add(
				"o.file_date", Direction.Desc).add("o.number_", Direction.Asc);

	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select o.id,o.status_,o.number_,o.nature,o.situation,o.source,o.owner_,o.ownership,o.whither");
		sql.append(",o.file_date,ac.actor_name author,o.modified_date,md.actor_name modifier");
		sql.append(",c.plate_type,c.plate_no,c.register_date,c.operate_date,getDisabledCarByOwnerNumber(o.number_) carInfo");
		sql.append(",c.company,bia.name as unit_name,c.bs_type nbs_type,c.factory_type,c.factory_model,c.origin_no");
		sql.append(",oc.plate_type oplate_type,oc.plate_no oplate_no,oc.register_date oregister_date,oc.factory_type ofactory_type");
		sql.append(",oc.factory_model ofactory_model,oc.bs_type,oc.logout_reason,oc.return_date,oc.verify_date");
		sql.append(",c.id newCarId,oc.id oldCarId,oc.return_date oreturn_date,m.name motorcadeName from bs_car_ownership o");
		sql.append(" left join bs_car c on (c.cert_no2=o.number_ and c.status_ = 0)");
		sql.append(" left join bs_car oc on (oc.cert_no2=o.number_ and oc.status_ =1)");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY md on md.id=o.modifier_id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY ac on ac.id=o.author_id");
		sql.append(" left join bs_motorcade m on m.id=c.motorcade_id");
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
				map.put("number_", rs[i++]);
				map.put("nature", rs[i++]);
				map.put("situation", rs[i++]);
				map.put("source", rs[i++]);
				map.put("owner_", rs[i++]);
				map.put("ownership", rs[i++]);
				map.put("whither", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("author", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("modifier", rs[i++]);
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				if (map.get("plate_type") == null
						&& map.get("plate_no") == null) {
					map.put("newPlate", null);
				} else {
					map.put("newPlate", map.get("plate_type").toString() + "."
							+ map.get("plate_no").toString());
				}
				map.put("register_date", rs[i++]);
				map.put("operate_date", rs[i++]);
				map.put("carInfo", rs[i++]);
				map.put("company", rs[i++]);
				map.put("unit_name", rs[i++]);
				map.put("nbs_type", rs[i++]);
				map.put("factory_type", rs[i++]);
				map.put("factory_model", rs[i++]);
				map.put("origin_no", rs[i++]);

				map.put("oplate_type", rs[i++]);
				map.put("oplate_no", rs[i++]);
				if (map.get("oplate_type") == null
						&& map.get("oplate_no") == null) {
					map.put("oldPlate", null);
				} else {
					map.put("oldPlate", map.get("oplate_type").toString() + "."
							+ map.get("oplate_no").toString());
				}
				map.put("oregister_date", rs[i++]);
				map.put("ofactory_type", rs[i++]);
				map.put("ofactory_model", rs[i++]);
				map.put("bs_type", rs[i++]);
				map.put("logout_reason", rs[i++]);
				map.put("return_date", rs[i++]);
				map.put("verify_date", rs[i++]);
				map.put("newCarId", rs[i++]);
				map.put("oldCarId", rs[i++]);
				map.put("oreturn_date", rs[i++]);
				map.put("motorcadeName", rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("o.id", "id"));
		columns.add(new TextColumn4MapKey("o.status_", "status_",
				getText("label.status"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(getBSStatuses1())));
		columns.add(new TextColumn4MapKey("o.number_", "number_",
				getText("ownership.number"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		if (!this.isReadonly() || this.isCheck4Advanced()) {
			columns.add(new TextColumn4MapKey("o.ownership", "ownership",
					getText("ownership.owner_ship"), 80).setSortable(true)
					.setUseTitleFromLabel(true));
			columns.add(new TextColumn4MapKey("o.nature", "nature",
					getText("ownership.nature"), 80).setSortable(true)
					.setUseTitleFromLabel(true));
			columns.add(new TextColumn4MapKey("o.situation", "situation",
					getText("ownership.situation"), 100).setSortable(true)
					.setUseTitleFromLabel(true));
		}
		columns.add(new TextColumn4MapKey("o.owner_", "owner_",
				getText("ownership.owner"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		// 更新天数
		columns.add(new TextColumn4MapKey("c.operate_date", "operate_date",
				getText("ownership.updateDays"), 80).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> car = (Map<String, Object>) context;
						// 新车投产日期
						Date operateDate = (Date) car.get("operate_date");

						// 旧车交车日期
						Date oreturnDate = (Date) car.get("oreturn_date");
						// 经营权创建时间
						Date createDate = (Date) car.get("file_date");

						// 如果新车的投产日期和旧车的交车日期不为空就返回更新天数
						if (operateDate != null && oreturnDate != null) {
							return String.valueOf((operateDate.getTime() - oreturnDate
									.getTime()) / (1000 * 60 * 60 * 24));
						} else if (operateDate != null && oreturnDate == null) {
							return String.valueOf((operateDate.getTime() - createDate
									.getTime()) / (1000 * 60 * 60 * 24));
						} else {
							return "";
						}
					}
				}));
		// 交车天数
		columns.add(new TextColumn4MapKey("c.operate_date", "operate_date",
				getText("ownership.returnCarDays"), 80).setUseTitleFromLabel(
				true).setValueFormater(new AbstractFormater<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public String format(Object context, Object value) {
				Map<String, Object> car = (Map<String, Object>) context;
				// 新车投产日期
				Date operateDate = (Date) car.get("operate_date");

				// 旧车交车日期
				Date oreturnDate = (Date) car.get("oreturn_date");
				// 经营权创建时间
				Date createDate = (Date) car.get("file_date");

				// 如果新车的投产日期和旧车的交车日期不为空就返回更新天数
				if (operateDate == null && oreturnDate != null) {

					return String.valueOf((Calendar.getInstance().getTime()
							.getTime() - oreturnDate.getTime())
							/ (1000 * 60 * 60 * 24));
				} else if (operateDate == null && oreturnDate == null) {
					return String.valueOf((Calendar.getInstance().getTime()
							.getTime() - createDate.getTime())
							/ (1000 * 60 * 60 * 24));
				} else {
					return "";
				}
			}
		}));
		// 公司
		columns.add(new TextColumn4MapKey("c.company", "company",
				getText("ownership.company"), 40).setSortable(true)
				.setUseTitleFromLabel(true));
		// 分公司
		columns.add(new TextColumn4MapKey("bia.name", "unit_name",
				getText("ownership.unitname"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.name", "motorcadeName",
				getText("ownership.motorcade"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		// 新车牌号码
		columns.add(new TextColumn4MapKey("c.plate_no", "newPlate",
				getText("ownership.plate"), 80)
				.setValueFormater(new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/car/edit?id={0}", "car") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("newCarId"));
					}

					@Override
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("ownership.plate") + " - "
								+ map.get("newPlate");
					}
				}));

		columns.add(new TextColumn4MapKey("c.bs_type", "nbs_type",
				getText("ownership.businessType"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车型
		columns.add(new TextColumn4MapKey("c.factory_type", "factory_type",
				getText("ownership.factory_type"), 70).setSortable(true)
				.setUseTitleFromLabel(true));

		// 登记日期
		columns.add(new TextColumn4MapKey("c.register_date", "register_date",
				getText("ownership.registerDate"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		// 投产日期
		columns.add(new TextColumn4MapKey("c.operate_date", "operate_date",
				getText("ownership.newOperate_date"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		// 旧车牌号码
		columns.add(new TextColumn4MapKey("oc.plate_no", "oldPlate",
				getText("ownership.oldPlate"), 90)
				.setValueFormater(new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/car/edit?id={0}", "car") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("oldCarId"));
					}

					@Override
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("ownership.oldPlate") + " - "
								+ map.get("oldPlate");

					}
				}));
		// 登记日期
		columns.add(new TextColumn4MapKey("oc.register_date", "oregister_date",
				getText("ownership.oldRegisterDate"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		// 车型
		columns.add(new TextColumn4MapKey("oc.factory_type", "ofactory_type",
				getText("ownership.oldFactory_type"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("oc.bs_type", "bs_type",
				getText("ownership.oldBs_type"), 90).setSortable(true)
				.setUseTitleFromLabel(true));
		// columns.add(new TextColumn4MapKey("oc.logout_reason",
		// "logout_reason",
		// getText("ownership.model"), 100).setSortable(true)
		// .setUseTitleFromLabel(true)
		// .setValueFormater(new KeyValueFormater(getModelValue())));
		columns.add(new TextColumn4MapKey("oc.return_date", "return_date",
				getText("ownership.return_date"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("oc.verify_date", "verify_date",
				getText("ownership.verify_date"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		columns.add(new TextColumn4MapKey("c.carinfo", "carInfo",
				getText("ownership.oldCar"), 220)
				.setValueFormater(new LinkFormater4CarInfo(this
						.getContextPath())));
		columns.add(new TextColumn4MapKey("o.source", "source",
				getText("ownership.source"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("o.whither", "whither",
				getText("ownership.whither"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("ac.actor_name", "author",
				getText("ownership.author"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("o.file_date", "file_date",
				getText("ownership.fileDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("md.actor_name", "modifier",
				getText("ownership.modifier"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("o.modified_date", "modified_date",
				getText("ownership.modifiedDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		return columns;
	}

	public Map<String, String> getModelValue() {

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CAR_LOGOUT_REASON });
		// 车辆注销原因
		logoutReasonList = optionItems.get(OptionConstants.CAR_LOGOUT_REASON);
		Map<String, String> model = new LinkedHashMap<String, String>();
		for (int i = 0; i < logoutReasonList.size(); i++) {
			model.put(logoutReasonList.get(i).get("key"),
					logoutReasonList.get(i).get("value"));
		}

		return model;

	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "o.number_", "o.nature", "o.situation",
				"o.owner_", "ac.actor_name", "md.actor_name" };
	}

	@Override
	protected String getFormActionName() {
		return "ownership";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['number_']";
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
			tb.addButton(this.getDefaultDeleteToolbarButton());

			// 批量修改
			// tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
			// .setText("批量修改")
			// .setClick("bc.business.piLiangXiuGai.create"));

			// 取消删除按钮
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		// 状态单选按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getBSStatuses1(),
				"status", 0, getText("title.click2changeSearchStatus")));

		// 是否匹配车辆的经营权单选按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getMateValue(),
				"mateValue", 0, getText("title.click2changeSearchMateValue")));

		return tb;
	}

	/**
	 * 经营权是否匹配车辆值转换
	 * 
	 * @return
	 */
	private Map<String, String> getMateValue() {
		Map<String, String> mateValue = new LinkedHashMap<String, String>();
		mateValue.put("weipipei", getText("ownership.weipipei"));
		mateValue.put("pipei", getText("ownership.pipei"));
		mateValue.put("", getText("bs.status.all"));
		return mateValue;
	}

	protected String getHtmlPageJs() {
		return this.getContextPath()
				+ "/bc-business/ownership/piliangXiuGai.js";

	}

	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition andCondition = new AndCondition();
		// 状态条件
		Condition statusCondition = null;
		Condition filterCondition = null;
		Condition mateCondition = null;

		// 状态
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("o.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("o.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}
		// 经营权是否匹配车辆条件
		if (mateValue.equals("weipipei")) {
			mateCondition = new IsNullCondition("c.id");
		} else if (mateValue.equals("pipei")) {
			mateCondition = new IsNotNullCondition("c.id");
		} else {
			mateCondition = null;
		}
		// 取报废日期最新的注销车辆
		filterCondition = andCondition
				.add(new QlCondition(
						"not exists(select 1 from bs_car_ownership o2 left join bs_car oc2 on (oc2.cert_no2=o2.number_ and oc2.status_ ="
								+ BCConstants.STATUS_DISABLED
								+ ") where oc.cert_no2=oc2.cert_no2"
								+ " and oc2.scrap_date > oc.scrap_date)"));
		return ConditionUtils.mix2AndCondition(statusCondition, mateCondition,
				filterCondition);
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}

	}

	@Override
	protected FooterButton getGridFooterImportButton() {
		// 获取默认的导入按钮设置
		FooterButton fb = this.getDefaultGridFooterImportButton();

		// 配置特殊参数
		JsonObject cfg = new JsonObject();
		cfg.addProperty("tplCode", "IMPORT_OWNERSHIP");// 模板编码
		cfg.addProperty("importAction", "bc-business/ownership/import");// 导入数据的action路径(使用相对路径)
		cfg.addProperty("headerRowIndex", 0);// 列标题所在行的索引号(0-based)
		fb.setAttr("data-cfg", cfg.toString());

		// 返回导入按钮
		return fb;
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
	public JSONArray natures;// 经营权性质
	public JSONArray situations;// 经营权情况
	public JSONArray owners;// 车辆产权

	@Override
	protected void initConditionsFrom() throws Exception {
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.OWNERSHIP_NATURE,
						OptionConstants.OWNERSHIP_OWNER,
						OptionConstants.OWNERSHIP_SITUATION,
						OptionConstants.CAR_LOGOUT_REASON });
		// 经营权性质
		natures = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.OWNERSHIP_NATURE));
		// 经营权情况
		situations = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.OWNERSHIP_SITUATION));
		// 车辆产权
		owners = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.OWNERSHIP_OWNER));
		// 可选分公司列表
		units = OptionItem.toLabelValues(this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT }, (Integer[]) null), "name",
				"id");

		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));
		// 车辆注销原因
		logoutReasonList = optionItems.get(OptionConstants.CAR_LOGOUT_REASON);
	}

	// ==高级搜索代码结束==

}
