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
import cn.bc.core.Entity;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
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
	public String status = String.valueOf(Entity.STATUS_ENABLED)+","+String.valueOf(Entity.STATUS_DISABLED); // 交通违章的状态，多个用逗号连接
	public String type = String.valueOf(Contract.TYPE_CHARGER);

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
		sql.append("select cl.id,c.type_,c.ext_str1,c.ext_str2,c.transactor_name,c.sign_date,c.start_date,c.end_date,c.code");
		sql.append(" from BS_CONTRACT_LABOUR cl inner join BS_CONTRACT c on cl.id = c.id");
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
				map.put("ext_str1", rs[i++]);
				map.put("ext_str2", rs[i++]);
				map.put("transactor_name", rs[i++]);
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
		columns.add(new IdColumn4MapKey("cl.id","id"));
		columns.add(new TextColumn4MapKey("c.type_", "type_",
				getText("contract.type"), 80).setSortable(true).setValueFormater(
				new EntityStatusFormater(getEntityTypes()))); 
		columns.add(new TextColumn4MapKey("c.ext_str1", "ext_str1",
				getText("contract.car"), 80).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.motorcade_name", "ext_str2",
				getText("contract.labour.driver"), 80).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.transactor_name", "transactor_name",
				getText("contract.transactor"), 60).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.sign_date", "sign_date",
				getText("contract.signDate"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.start_date", "start_date", getText("contract.deadline"))
				.setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
					@Override
					public Date getToDate(Object context, Object value) {
						@SuppressWarnings("rawtypes")
						Map contract = (Map) context;
						return (Date) contract.get("end_date");
					}
				}));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("contract.code"),120));
		
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.code", "c.ext_str1","c.ext_str2", "c.word_no" };
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
	protected Json getGridExtrasData() {
		if(this.status == null || this.status.length() == 0){
			return null;
		}else{
			Json json = new Json();
			if (this.status != null || this.status.length() > 0) {
				json.put("status", status);
			}
			return json;
		}
	}
	

}
