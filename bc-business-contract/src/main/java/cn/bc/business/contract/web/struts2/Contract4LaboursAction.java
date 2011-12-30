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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.contract.domain.Contract;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
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
	public String type = String.valueOf(Contract.TYPE_CHARGER);

	public Long contractId;
	public String patchNo;

	@Override
	public boolean isReadonly() {
		// 劳动合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
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
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.read")));
		} else {

			if (contractId == null) {
				// 新建按钮
				tb.addButton(Toolbar
						.getDefaultCreateToolbarButton(getText("label.create")));
			}
			// 查看按钮
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.read")));
			if (contractId == null) {

				if (useDisabledReplaceDelete) {
					// 禁用按钮
					tb.addButton(Toolbar
							.getDefaultDisabledToolbarButton(getText("label.disabled")));
				} else {
					// 删除按钮
					tb.addButton(Toolbar
							.getDefaultDeleteToolbarButton(getText("label.delete")));
				}
			}
		}

		// 搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));
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
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select cl.id,c.type_,c.status_,c.ext_str1,c.ext_str2,c.transactor_name,c.sign_date,c.start_date,c.end_date,c.code,cl.joinDate,cl.insurCode,cl.insurance_type,cl.cert_no,c.author_id,c.ver_major,c.ver_minor,c.op_type,iah.actor_name");
		sql.append(",car.id carId");
		sql.append(",man.id manId");
		sql.append(" from BS_CONTRACT_LABOUR cl");
		sql.append(" left join BS_CONTRACT c on cl.id = c.id");
		sql.append(" left join BS_CAR_CONTRACT carc on c.id = carc.contract_id");
		sql.append(" left join BS_Car car on carc.car_id = car.id");
		sql.append(" left join BS_CARMAN_CONTRACT manc on c.id = manc.contract_id");
		sql.append(" left join BS_CARMAN man on manc.man_id = man.id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY iah on c.author_id = iah.id");
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
				map.put("sign_date", rs[i++]);
				map.put("start_date", rs[i++]);
				map.put("end_date", rs[i++]);
				map.put("code", rs[i++]);
				map.put("joinDate", rs[i++]);
				map.put("insurCode", rs[i++]);
				map.put("insurance_type", rs[i++]);
				map.put("cert_no", rs[i++]);
				map.put("author_id", rs[i++]);
				map.put("ver_major", rs[i++]);
				map.put("ver_minor", rs[i++]);
				map.put("op_type", rs[i++]);
				map.put("name", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("manId", rs[i++]);
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
				getText("contract.status"), 35)
				.setSortable(true)
				.setValueFormater(new EntityStatusFormater(getEntityStatuses())));
		columns.add(new TextColumn4MapKey("c.ver_major", "ver_major",
				getText("contract4Labour.ver"), 35)
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
		columns.add(new TextColumn4MapKey("c.ext_str1", "ext_str1",
				getText("contract.car"), 80).setUseTitleFromLabel(true)
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
		columns.add(new TextColumn4MapKey("cl.cert_no", "cert_no",
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
		columns.add(new TextColumn4MapKey("c.op_type", "op_type",
				getText("contract4Labour.op"), 60).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getEntityOpTypes())));
		columns.add(new TextColumn4MapKey("iah.name", "name",
				getText("contract.author"), 55).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("contract.code")).setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String getGridDblRowMethod() {
		// 强制为只读表单
		return "bc.page.open";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.code", "c.ext_str1", "c.ext_str2",
				"cl.insurance_type" };
	}

	@Override
	protected String getFormActionName() {
		return "contract4Labour";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(490).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['ext_str2']+'的劳动合同'";
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
		statuses.put(String.valueOf(Contract.STATUS_FAILURE),
				getText("contract.status.failure"));
		statuses.put(String.valueOf(Contract.STATUS_RESGIN),
				getText("contract.status.resign"));
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
				getText("contract4Labour.optype.transfer"));
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
		Condition mainsCondition = null;
		Condition patchCondtion = null;
		if (contractId == null) {
			// 查看最新合同列表
			statusCondition = ConditionUtils.toConditionByComma4IntegerValue(
					this.status, "c.status_");
			if (this.status.length() <= 0) { // 显示全部状态的时候只显示最新版本的记录
				mainsCondition = ConditionUtils
						.toConditionByComma4IntegerValue(this.mains, "c.main");
			}
		} else {
			// 查看历史版本
			patchCondtion = new EqualsCondition("c.patch_no", patchNo);
			mainsCondition = new EqualsCondition("c.main",
					Contract.MAIN_HISTORY);
		}
		return ConditionUtils.mix2AndCondition(statusCondition, mainsCondition,
				patchCondtion);
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
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		if (contractId == null) {
			return super.getHtmlPageToolbar().addButton(
					Toolbar.getDefaultToolbarRadioGroup(
							this.getEntityStatuses(), "status", 0,
							getText("title.click2changeSearchStatus")));
		} else {
			return super.getHtmlPageToolbar();
		}
	}
}