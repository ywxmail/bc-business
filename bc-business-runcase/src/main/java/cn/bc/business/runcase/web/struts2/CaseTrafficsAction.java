/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 交通违章视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseTrafficsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(CaseBase.STATUS_ACTIVE)+","+String.valueOf(CaseBase.STATUS_CLOSED); // 交通违章的状态，多个用逗号连接
	public String type = String.valueOf(CaseBase.TYPE_INFRACT_TRAFFIC);

	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractTraffic"),
				getText("key.role.bc.admin"));
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
		sql.append("select cit.id,c.status_,c.subject,c.motorcade_name,c.car_plate,c.driver_name,c.closer_name,c.happen_date");
		sql.append(",c.close_date,c.address,c.from_,c.driver_cert,c.case_no");
		sql.append(" from bs_case_infract_traffic cit inner join BS_CASE_BASE c on cit.id=c.id");
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
				map.put("subject", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("car_plate", rs[i++]);
				map.put("driver_name", rs[i++]);
				map.put("closer_name", rs[i++]);
				map.put("happen_date", rs[i++]);
				map.put("close_date", rs[i++]);
				map.put("address", rs[i++]);
				map.put("from_", rs[i++]);
				map.put("driver_cert", rs[i++]);
				map.put("case_no", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("cit.id","id"));
		columns.add(new TextColumn4MapKey("c.status_", "status_",
				getText("runcase.status"), 50).setSortable(true).setValueFormater(
				new EntityStatusFormater(getCaseStatuses()))); 
		columns.add(new TextColumn4MapKey("c.subject", "subject",
				getText("runcase.subject"), 120).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.motorcade_name", "motorcade_name",
				getText("runcase.motorcadeName"), 80).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.car_plate", "car_plate",
				getText("runcase.carPlate"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.driver_name", "driver_name",
				getText("runcase.driverName"), 70).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.closer_name", "closer_name",
				getText("runcase.closerName"), 70).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.happen_date", "happen_date",
				getText("runcase.happenDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.close_date", "close_date",
				getText("runcase.closeDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.address", "address",
				getText("runcase.address"), 120));
		columns.add(new TextColumn4MapKey("c.from_", "from_",
				getText("runcase.source"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("c.driver_cert", "driver_cert",
				getText("runcase.driverCert"), 80));
		columns.add(new TextColumn4MapKey("c.case_no", "case_no",
				getText("runcase.caseNo1")));
		
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.case_no", "c.car_plate","c.driver_name", "c.motorcade_name", 
						"c.closer_name", "c.subject" };
	}

	@Override
	protected String getFormActionName() {
		return "caseTraffic";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(550).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['case_no']";
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
	

	@Override
	protected Json getGridExtrasData() {
		if((this.status == null || this.status.length() == 0) && 
			(this.type == null || this.type.length() == 0)){
			return null;
		}else{
			Json json = new Json();
			if (this.status != null || this.status.length() > 0) {
				json.put("status", status);
			}
			if (this.type != null || this.type.length() > 0){
				json.put("type", type);
			} 
			return json;
		}
	}
	
	/**
	 * 获取Entity的状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getCaseStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(CaseBase.STATUS_ACTIVE),
				getText("runcase.select.status.active"));
		statuses.put(String.valueOf(CaseBase.STATUS_CLOSED),
				getText("runcase.select.status.closed"));
		return statuses;
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
				getText("runcase.select.source.sync"));
		statuses.put(String.valueOf(CaseBase.SOURCE_FROM_DRIVER),
				getText("runcase.select.source.fromdriver"));
		return statuses;
	}

}
