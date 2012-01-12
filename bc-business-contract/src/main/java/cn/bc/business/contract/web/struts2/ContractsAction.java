/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 合同视图Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ContractsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(Contract.STATUS_NORMAL); // 合同的状态，多个用逗号连接
	public String mains = String.valueOf(Contract.MAIN_NOW); // 现实当前版本
	public Long carId;
	public Long driverId;

	@Override
	public boolean isReadonly() {
		// 合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4charger"),
				getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|登记日期
		return new OrderCondition("c.file_date", Direction.Desc).add(
				"c.status_", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.type_,c.ver_major,c.ver_minor,c.ext_str1,c.ext_str2,c.sign_date,c.start_date,c.end_date,c.code");
		sql.append(" from BS_CONTRACT c");
		if(carId != null){
			sql.append(" inner join BS_CAR_CONTRACT carc on c.id = carc.contract_id");
		}
		if(driverId != null){
			sql.append(" inner join BS_CARMAN_CONTRACT manc on c.id = manc.contract_id");
		}
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
				map.put("ver_major", rs[i++]);
				map.put("ver_minor", rs[i++]);
				map.put("ext_str1", rs[i++]);
				map.put("ext_str2", rs[i++]);
				map.put("sign_date", rs[i++]);
				map.put("start_date", rs[i++]);
				map.put("end_date", rs[i++]);
				map.put("code", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("c.id", "id"));
		columns.add(new TextColumn4MapKey("c.type_", "type_",
				getText("contract.type"), 50)
				.setSortable(true)
				.setValueFormater(new EntityStatusFormater(getEntityTypes())));
		columns.add(new TextColumn4MapKey("c.ver_major", "ver_major",
				getText("contract4Labour.ver"), 50)
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
		if (driverId != null) {
			columns.add(new TextColumn4MapKey("c.ext_str1", "ext_str1",
					getText("contract.car"), 120).setUseTitleFromLabel(true));
		}
		if (carId != null) {
		columns.add(new TextColumn4MapKey("c.ext_str2", "ext_str2",
				getText("contract.driver.charger"), 55).setUseTitleFromLabel(
				true));
		}
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
		columns.add(new TextColumn4MapKey("c.sign_date", "sign_date",
				getText("contract.signDate"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("contract.code"),60).setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.code", "c.ext_str1", "c.ext_str2"};
	}

	@Override
	protected String getFormActionName() {
		return "contract";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
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

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition carCondition = null;
		Condition carManCondition = null;
		Condition mainsCondition = null;
		
		if (carId != null) {
			carCondition = new EqualsCondition("carc.car_id",
					carId);
		}
		
		if (driverId != null) {
			carManCondition = new EqualsCondition("manc.man_id",
					driverId);
		}
		
		// 查看最新合同列表
		mainsCondition = new EqualsCondition("c.main",
				Contract.MAIN_NOW);
		return ConditionUtils.mix2AndCondition(mainsCondition,carCondition
				,carManCondition);
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}

		if (carId != null) {
			json.put("carId", carId);
		}

		if (driverId != null) {
			json.put("driverId", driverId);
		}
	}

	@Override
	protected String getGridRowLabelExpression() { //条件表达式例子:a<b?a:c?c:d
		return "['type_'] == 1 ? ['ext_str2'] + '的劳动合同' : ['type_'] == 2 ? ['ext_str1'] + '的经济合同' : '未定义'";
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
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.read"))
					.setClick("bc.contractList.edit"));
		} else {

			// 新建按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.create"))
					.setClick("bc.contractList.create"));
			// 编辑按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.edit"))
					.setClick("bc.contractList.edit"));

			
			if (useDisabledReplaceDelete) {
				// 禁用按钮
				tb.addButton(Toolbar
						.getDefaultDisabledToolbarButton(getText("label.disabled")));
			} else {
				// 删除按钮
				tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
						.setText(getText("label.delete"))
						.setClick("bc.contractList.del"));
			}
		}

		// 搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));
		return tb;
	}
	
	@Override
	/** 获取表格双击行的js处理函数名 */
	protected String getGridDblRowMethod() {
		return "bc.contractList.edit";
	}

	
	@Override
	/** 页面需要另外加载的js、css文件，逗号连接多个文件 */
	protected String getHtmlPageJs() {
		return getContextPath() + "/bc-business/contract/list.js";
	}
	
}