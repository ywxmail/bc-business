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
public class ContractLaboursAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(Contract.STATUS_NORMAL); // 合同的状态，多个用逗号连接
	public String mains = String.valueOf(Contract.MAIN_NOW); //现实当前版本
	public String type = String.valueOf(Contract.TYPE_CHARGER);
	
	public Long contractId;

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

			if(contractId == null){
				// 新建按钮
				tb.addButton(Toolbar
						.getDefaultCreateToolbarButton(getText("label.create")));
			}
			// 查看按钮
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.read")));
			if(contractId == null){
				
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
		if(contractId == null){
			tb.addButton(Toolbar
					.getDefaultSearchToolbarButton(getText("title.click2search")));
		}
		return tb;
	}
	
	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|登记日期
		return new OrderCondition("c.status_", Direction.Asc).add(
				"c.file_date", Direction.Desc);
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
		columns.add(new IdColumn4MapKey("cl.id","id"));
		columns.add(new TextColumn4MapKey("c.status_", "status_",
				getText("contract.status"), 30).setSortable(true).setValueFormater(
				new EntityStatusFormater(getEntityStatuses())));
		columns.add(new TextColumn4MapKey("c.type_", "type_",
				getText("contract.type"), 60).setSortable(true).setValueFormater(
				new EntityStatusFormater(getEntityTypes()))); 
		columns.add(new TextColumn4MapKey("c.ver_major", "ver_major",
				getText("contract.labour.ver"),40).setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> ver = (Map<String, Object>) context;
						if(null == ver.get("ver_major")){
							return "";
						}else if(null != ver.get("ver_major") && null == ver.get("ver_minor")){
							return ver.get("ver_major") + "." + "0";
						}else{
							return ver.get("ver_major") + "."
									+ ver.get("ver_minor");
						}
					}
				}));
		columns.add(new TextColumn4MapKey("c.op_type", "op_type",
				getText("contract.labour.optype"),50).setSortable(true).setValueFormater(
				new EntityStatusFormater(getEntityOpTypes())));
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
				getText("contract.labour.driver"), 80).setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/carMan/edit?id={0}", "carMan") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("manId"));
							}
						}));
		columns.add(new TextColumn4MapKey("cl.cert_no", "cert_no",
					getText("contract.labour.certNo"),60));
		columns.add(new TextColumn4MapKey("iah.name", "name",
				getText("contract.author"), 80).setUseTitleFromLabel(true));
//		columns.add(new TextColumn4MapKey("c.sign_date", "sign_date",
//				getText("contract.signDate"), 90).setSortable(true)
//				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.start_date", "start_date", getText("contract.deadline"))
				.setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
					@Override
					public Date getToDate(Object context, Object value) {
						@SuppressWarnings("rawtypes")
						Map contract = (Map) context;
						return (Date) contract.get("end_date");
					}
				}));
		columns.add(new TextColumn4MapKey("cl.insurance_type", "insurance_type",
				getText("contract.labour.insuranceType")));
		columns.add(new TextColumn4MapKey("cl.joinDate", "joinDate",
				getText("contract.labour.joinDate"), 80)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("cl.insurCode", "insurCode",
				getText("contract.labour.insurCode"),80));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("contract.code"),60));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.code", "c.ext_str1","c.ext_str2", "cl.insurance_type" };
	}

	@Override
	protected String getFormActionName() {
		return "contractLabour";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(550).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['code']";
	}

	/**
	 * 获取Contract的合同类型列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Contract.TYPE_LABOUR),
				getText("contract.select.labour"));
		types.put(String.valueOf(Contract.TYPE_CHARGER),
				getText("contract.select.charger"));
		return types;
	}
	
	
	/**
	 * 状态值转换列表：正常|失效|离职|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Contract.STATUS_NORMAL),
				getText("contract.normal"));
		statuses.put(String.valueOf(Contract.STATUS_FAILURE),
				getText("contract.failure"));
		statuses.put(String.valueOf(Contract.STATUS_RESGIN),
				getText("contract.resign"));
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
				getText("contract.labour.optype.create"));
		types.put(String.valueOf(Contract.OPTYPE_EDIT),
				getText("contract.labour.optype.edit"));
		types.put(String.valueOf(Contract.OPTYPE_TRANSFER),
				getText("contract.labour.optype.transfer"));
		types.put(String.valueOf(Contract.OPTYPE_RENEW),
				getText("contract.labour.optype.renew"));
		types.put(String.valueOf(Contract.OPTYPE_RESIGN),
				getText("contract.labour.optype.resign"));
		return types;
	}
	
//	@Override
//	protected Condition getGridSpecalCondition() {
//
//		if (type != null && type.length() > 0) {
//			String[] ss = type.split(",");
//			if (ss.length == 1) {
//				return new EqualsCondition("c.type_", new Integer(ss[0]));
//			} else {
//				return new InCondition("c.types_",
//						StringUtils.stringArray2IntegerArray(ss));
//			}
//		}else{
//			return null;
//		}
//
//	}
	
//	@Override
//	protected Json getGridExtrasData() {
//		if(this.status == null || this.status.length() == 0){
//			return null;
//		}else{
//			Json json = new Json();
//			if (this.status != null || this.status.length() > 0) {
//				json.put("status", status);
//			}
//			return json;
//		}
//	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		//状态条件
		
		//状态条件
		Condition statusCondition = null;
		Condition mainsCondition = null;
		if (contractId == null) {
			statusCondition = ConditionUtils.toConditionByComma4IntegerValue(this.status,
					"c.status_");
			mainsCondition = ConditionUtils.toConditionByComma4IntegerValue(this.mains,
					"c.main");
		}
		
		// contractId条件
//		Condition or = null;
//		if (contractId != null) {
//			Condition contractIdCondition = new EqualsCondition("cl.id", contractId);
//			Condition pIdCondition = new EqualsCondition("c.pid", contractId);
//			or = (Condition) ConditionUtils.mix2OrCondition(contractIdCondition,pIdCondition);
//			
//		}
		Condition idCondition = null;
		if (contractId != null) {
			idCondition = new EqualsCondition("c.id", contractId);
		}
		return ConditionUtils.mix2AndCondition(statusCondition,mainsCondition,idCondition);
	}
	
	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}
		
		if (contractId != null) {
			json.put("id", contractId);
		}
		
		
	}
	
	@Override
	protected Toolbar getHtmlPageToolbar() {
		if(contractId == null){
			return super.getHtmlPageToolbar()
					.addButton(
							Toolbar.getDefaultToolbarRadioGroup(
									this.getEntityStatuses(), "status", 0,
									getText("title.click2changeSearchStatus")));
		}else{
			return super.getHtmlPageToolbar();
		}
	}
	

}
