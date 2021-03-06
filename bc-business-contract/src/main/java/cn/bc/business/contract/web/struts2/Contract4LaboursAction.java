/**
 * 
 */
package cn.bc.business.contract.web.struts2;

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
import cn.bc.business.OptionConstants;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
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
import cn.bc.web.formater.DateRangeFormater;
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
 * 劳动合同视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Contract4LaboursAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(Contract.STATUS_NORMAL); // 合同的状态，多个用逗号连接
	public String mains = String.valueOf(Contract.MAIN_NOW); // 现实当前版本
	public int type = Contract.TYPE_LABOUR;

	public Long contractId;
	public String patchNo;
	public Long carId;
	public Long driverId;

	@Override
	public boolean isReadonly() {
		// 劳动合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
	}

	public boolean isEntering() {
		// 劳动合同草稿信息录入
		SystemContext context = (SystemContext) this.getContext();
		return context
				.hasAnyRole(getText("key.role.bs.contract4labour.entering"));
	}

	public boolean isCheck() {
		// 劳动合同草稿信息查询
		SystemContext context = (SystemContext) this.getContext();
		return context.hasAnyRole(getText("key.role.bs.contract4labour.check"));
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc-business/contract4Labour/view.js";
	}

	/**
	 * @param useDisabledReplaceDelete
	 *            控制是使用删除按钮还是禁用按钮
	 * @return
	 */
	@Override
	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
			if (this.isEntering()) {
				// 新建按钮
				tb.addButton(this.getDefaultCreateToolbarButton());

			}

		} else {

			if (contractId == null) {
				// 新建按钮
				tb.addButton(this.getDefaultCreateToolbarButton());
			}
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
			if (contractId == null) {

				if (useDisabledReplaceDelete) {
					// 禁用按钮
					tb.addButton(this.getDefaultDisabledToolbarButton());
				} else {
					// 删除按钮
					tb.addButton(this.getDefaultDeleteToolbarButton());
				}
				tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
						.setText("补录")
						.setClick("bs.contract4LabourView.clickOk"));

			}
		}
		// 状态单选按钮组
		// 如果有权限的用户可以看到草稿状态的车
		if (!isReadonly() || this.isEntering() || this.isCheck()) {
			tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
					this.getEntityStatuses2(), "status", 0,
					getText("title.click2changeSearchStatus")));

		} else {
			tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
					this.getEntityStatuses(), "status", 0,
					getText("title.click2changeSearchStatus")));
		}
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());
		return tb;
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|登记日期
		if (contractId == null) {// 当前版本
			return new OrderCondition("c.status_", Direction.Asc).add(
					"c.file_date", Direction.Desc);
		} else { // 历史版本
			return new OrderCondition("c.file_date", Direction.Desc);
		}
	}

	@Override
	protected Condition getGridSearchCondition4OneField(String field,
			String value) {
		if (field.indexOf("ext_str1") != -1) {// 车牌，忽略大小写
			return new LikeCondition(field, value != null ? value.toUpperCase()
					: value);
		} else {
			return super.getGridSearchCondition4OneField(field, value);
		}
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select cl.id,c.type_,c.status_,c.ext_str1,c.ext_str2,c.transactor_name,c.file_date,c.sign_date");
		sql.append(",c.start_date,c.end_date,c.code,cl.joinDate,cl.get_startDate,cl.get_endDate,cl.stopDate");
		sql.append(",cl.insurCode,cl.insurance_type,cl.cert_no,cl.leaveDate,c.author_id,c.ver_major,c.ver_minor,c.op_type");
		sql.append(",car.id carId");
		sql.append(",man.id manId");
		sql.append(",man.cert_fwzg certfwzg");
		sql.append(",car.company company");
		sql.append(",bia.id batch_company_id,bia.name batch_company");
		sql.append(",m.id motorcade_id,m.name motorcade_name");
		sql.append(",car.code car_code,c.stop_date");
		sql.append(",c.modified_date,md.actor_name modifier,c.file_date,ad.actor_name author");
		sql.append(",cl.domicile_place,cl.cultural_degree,cl.marital_status");
		sql.append(" from BS_CONTRACT_LABOUR cl");
		sql.append(" inner join BS_CONTRACT c on cl.id = c.id");
		sql.append(" left join BS_CAR_CONTRACT carc on c.id = carc.contract_id");
		sql.append(" left join BS_Car car on carc.car_id = car.id");
		sql.append(" left join BS_CARMAN_CONTRACT manc on c.id = manc.contract_id");
		sql.append(" left join BS_CARMAN man on manc.man_id = man.id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY iah on c.author_id = iah.id");
		sql.append(" left join bs_motorcade m on m.id=car.motorcade_id");
		sql.append(" left join bc_identity_actor bia on bia.id=m.unit_id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY md on md.id=c.modifier_id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY ad on ad.id=c.author_id");

		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("type_", rs[i++]);
				map.put("status_", rs[i++]);
				map.put("ext_str1", rs[i++]);
				map.put("ext_str2", rs[i++]);
				map.put("transactor_name", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("sign_date", rs[i++]);
				map.put("start_date", rs[i++]);
				map.put("end_date", rs[i++]);
				map.put("code", rs[i++]);
				map.put("joinDate", rs[i++]);
				map.put("getStartDate", rs[i++]);
				map.put("getEndDate", rs[i++]);
				map.put("stopDate", rs[i++]);
				map.put("insurCode", rs[i++]);
				map.put("insurance_type", rs[i++]);
				map.put("cert_no", rs[i++]);
				map.put("leaveDate", rs[i++]);
				map.put("author_id", rs[i++]);
				map.put("ver_major", rs[i++]);
				map.put("ver_minor", rs[i++]);
				map.put("op_type", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("manId", rs[i++]);
				map.put("certfwzg", rs[i++]);
				map.put("company", rs[i++]);
				map.put("batch_company_id", rs[i++]);
				map.put("batch_company", rs[i++]);
				map.put("motorcade_id", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("car_code", rs[i++]);
				map.put("stop_date", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("modifier", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("author", rs[i++]);
				map.put("domicile_place", rs[i++]);
				map.put("cultural_degree", rs[i++]);
				map.put("marital_status", rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("cl.id", "id"));
		columns.add(new TextColumn4MapKey("c.status_", "status_",
				getText("contract.status"), 35).setSortable(true)
				.setValueFormater(
						new EntityStatusFormater(getEntityStatuses2())));
		columns.add(new TextColumn4MapKey("c.file_date", "fileDate",
				getText("label.fileDate"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.ver_major", "ver_major",
				getText("contract4Labour.ver"), 40)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> ver = (Map<String, Object>) context;
						if (null == ver.get("ver_major")) {
							return "";
						} else if (null != ver.get("ver_major")
								&& null == ver.get("ver_minor")) {
							return ver.get("ver_major") + "." + "0";
						} else {
							return ver.get("ver_major") + "."
									+ ver.get("ver_minor");
						}
					}
				}));
		columns.add(new TextColumn4MapKey("car.company", "company",
				getText("contract.company"), 50).setSortable(true));
		columns.add(new TextColumn4MapKey("bia.name", "batch_company",
				getText("contract.batch.company"), 70).setSortable(true));
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("contract.motorcadeName"), 70)
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
		columns.add(new TextColumn4MapKey("c.ext_str1", "ext_str1",
				getText("contract.car"), 85).setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/car/edit?id={0}", "car") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("carId"));
							}
						}));
		columns.add(new TextColumn4MapKey("car.code", "car_code",
				getText("contract4Charger.wordNo"), 70));
		columns.add(new TextColumn4MapKey("c.ext_str2", "ext_str2",
				getText("contract4Labour.driver"), 55).setUseTitleFromLabel(
				true).setValueFormater(
				new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/carMan/edit?id={0}", "carMan") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("manId"));
					}
				}));
		columns.add(new TextColumn4MapKey("man.cert_certfwzg", "certfwzg",
				getText("contract4Labour.certNo"), 60)
				.setUseTitleFromLabel(true));
		// columns.add(new TextColumn4MapKey("c.sign_date", "sign_date",
		// getText("contract.signDate"), 90).setSortable(true)
		// .setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.start_date", "start_date",
				getText("contract.deadline"), 180)
				.setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
					@Override
					public Date getToDate(Object context, Object value) {
						@SuppressWarnings("rawtypes")
						Map contract = (Map) context;
						return (Date) contract.get("end_date");
					}
				}));
		columns.add(new TextColumn4MapKey("cl.insurCode", "insurCode",
				getText("contract4Labour.insurCode"), 80)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("cl.insurance_type",
				"insurance_type", getText("contract4Labour.insuranceType"), 190)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("cl.joinDate", "joinDate",
				getText("contract4Labour.joinDate"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("cl.get_startDate", "getStartDate",
				getText("contract4Labour.deadline4shebao"), 180)
				.setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
					@Override
					public Date getToDate(Object context, Object value) {
						@SuppressWarnings("rawtypes")
						Map contract = (Map) context;
						return (Date) contract.get("getEndDate");
					}
				}));
		columns.add(new TextColumn4MapKey("cl.stopDate", "stopDate",
				getText("contract4Labour.stopDate"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("cl.leaveDate", "leaveDate",
				getText("contract4Labour.leaveDate"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.stop_date", "stop_date",
				getText("contract.stopDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		columns.add(new TextColumn4MapKey("cl.domicile_place",
				"domicile_place", getText("contract4Labour.domicilePlace"), 150)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("cl.cultural_degree",
				"cultural_degree", getText("contract4Labour.culturalDegree"),
				60));
		columns.add(new TextColumn4MapKey("cl.marital_status",
				"marital_status", getText("contract4Labour.maritalStatus"), 60));

		columns.add(new TextColumn4MapKey("c.op_type", "op_type",
				getText("contract4Labour.op"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getEntityOpTypes())));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("contract.code"), 130).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("ad.actor_name", "author",
				getText("contract.author"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("c.file_date", "file_date",
				getText("contract.fileDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("md.actor_name", "modifier",
				getText("contract.modifier"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("c.modified_date", "modified_date",
				getText("contract.modifiedDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		return columns;
	}

	@Override
	protected String getGridDblRowMethod() {
		// 强制为只读表单
		return "bs.contract4LabourView.dblclick";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.code", "c.ext_str1", "c.ext_str2",
				"cl.insurance_type", "cl.cert_no", "cl.insurCode", "car.code",
				"bia.name", "car.company" };
	}

	@Override
	protected String getFormActionName() {
		return "contract4Labour";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(490).setMinHeight(300).setHelp("laodonghetong");
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['ext_str2']+'的劳动合同\t-\t v'+['ver_major']+'.'+['ver_minor']";
	}

	/**
	 * 状态值转换列表：正常|注销|离职|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Contract.STATUS_NORMAL),
				getText("contract.status.normal"));
		statuses.put(String.valueOf(Contract.STATUS_LOGOUT),
				getText("contract.status.logout"));
		statuses.put(String.valueOf(Contract.STATUS_RESGIN),
				getText("contract.status.resign"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	/**
	 * 状态值转换列表：正常|注销|离职|草稿|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityStatuses2() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Contract.STATUS_NORMAL),
				getText("contract.status.normal"));
		statuses.put(String.valueOf(Contract.STATUS_LOGOUT),
				getText("contract.status.logout"));
		statuses.put(String.valueOf(Contract.STATUS_RESGIN),
				getText("contract.status.resign"));
		statuses.put(String.valueOf(BCConstants.STATUS_DRAFT),
				getText("bc.status.draft"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	/**
	 * 获取Contract的操作类型列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityOpTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Contract.OPTYPE_CREATE),
				getText("contract4Labour.optype.create"));
		types.put(String.valueOf(Contract.OPTYPE_MAINTENANCE),
				getText("contract4Labour.optype.maintenance"));
		types.put(String.valueOf(Contract.OPTYPE_CHANGECAR),
				getText("contract4Labour.optype.changeCar"));
		types.put(String.valueOf(Contract.OPTYPE_RENEW),
				getText("contract4Labour.optype.renew"));
		types.put(String.valueOf(Contract.OPTYPE_RESIGN),
				getText("contract4Labour.optype.resign"));
		return types;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("c.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("c.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		Condition mainsCondition = null;
		Condition carCondition = null;
		Condition driverCondition = null;
		// Condition patchCondtion = null;
		// Condition typeCondtion = new EqualsCondition("c.type_", type);

		if (contractId == null) {
			// 查看最新合同列表
			statusCondition = ConditionUtils.toConditionByComma4IntegerValue(
					this.status, "c.status_");
			// if (this.status.length() <= 0) { // 显示全部状态的时候只显示最新版本的记录
			// mainsCondition = ConditionUtils.toConditionByComma4IntegerValue(
			// this.mains, "c.main");
			// }
		}
		if (carId != null) {
			carCondition = new EqualsCondition("carc.car_id", carId);
		}

		if (driverId != null) {
			driverCondition = new EqualsCondition("manc.man_id", driverId);
		}
		return ConditionUtils.mix2AndCondition(statusCondition, mainsCondition,
				driverCondition, carCondition);
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}

		if (contractId != null) {
			json.put("contractId", contractId);
		}

		if (patchNo != null) {
			json.put("patchNo", patchNo);
		}

		if (carId != null) {
			json.put("carId", carId);
		}

		if (driverId != null) {
			json.put("driverId", driverId);
		}

		json.put("type", type);
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
	public JSONArray houseTypes;// 营运性质列表

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
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CARMAN_HOUSETYPE });

		// 户口性质列表
		this.houseTypes = OptionItem.toLabelValues(
				optionItems.get(OptionConstants.CARMAN_HOUSETYPE), "value");
	}

	// ==高级搜索代码结束==
}