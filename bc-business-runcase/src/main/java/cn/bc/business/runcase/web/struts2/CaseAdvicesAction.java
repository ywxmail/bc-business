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

import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.Entity;
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
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 投诉视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseAdvicesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(Entity.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
	public Long carManId;
	public Long carId;

	@Override
	public boolean isReadonly() {
		// 司机管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.driver"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("b.status_", Direction.Asc).add(
				"b.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id, b.status_,a.advice_type,b.subject,b.motorcade_name,b.car_plate,b.driver_name");
		sql.append(",b.closer_name,b.close_date,a.advisor_name,b.happen_date,b.address");
		sql.append(",b.from_,b.source,b.driver_cert,a.receive_code,b.case_no,a.receive_date ");
		sql.append(" from BS_CASE_ADVICE a");
		sql.append(" inner join BS_CASE_BASE b on b.id=a.id");
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
				map.put("advice_type", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("car_plate", rs[i++]);
				map.put("driver_name", rs[i++]);
				map.put("closer_name", rs[i++]);
				map.put("close_date", rs[i++]);
				map.put("advisor_name", rs[i++]);
				map.put("happen_date", rs[i++]);
				map.put("address", rs[i++]);
				map.put("from_", rs[i++]);
				map.put("source", rs[i++]);
				map.put("driver_cert", rs[i++]);
				map.put("receive_code", rs[i++]);
				map.put("case_no", rs[i++]);
				map.put("receive_date", rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("c.status_", "status_",
				getText("runcase.status"), 50).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses2())));
		columns.add(new TextColumn4MapKey("a.advice_type", "advice_type",
				getText("runcase.adviceType"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(getType())));
		columns.add(new TextColumn4MapKey("a.receive_date", "receive_date",
				getText("runcase.receiveDate3"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("b.subject", "subject",
				getText("runcase.subject"), 120).setSortable(true));
		columns.add(new TextColumn4MapKey("b.motorcade_name", "motorcade_name",
				getText("runcase.motorcadeName"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("b.car_plate", "car_plate",
				getText("runcase.carPlate"), 100));
		columns.add(new TextColumn4MapKey("b.driver_name", "driver_name",
				getText("runcase.driverName"), 70).setSortable(true));
		columns.add(new TextColumn4MapKey("b.closer_name", "closer_name",
				getText("runcase.closerName"), 70));
		columns.add(new TextColumn4MapKey("b.close_date", "close_date",
				getText("runcase.closeDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("a.advisor_name", "advisor_name",
				getText("runcase.advisorName"), 70).setSortable(true));
		columns.add(new TextColumn4MapKey("b.happen_date", "happen_date",
				getText("runcase.happenDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("b.address", "address",
				getText("runcase.address"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.driver_cert", "driver_cert",
				getText("runcase.driverCert"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.receive_code", "receive_code",
				getText("runcase.receiveCode"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.case_no", "case_no",
				getText("runcase.caseNo2"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.source", "source",
				getText("runcase.ifsource"), 60).setSortable(true).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						// 从上下文取出元素Map
						@SuppressWarnings("unchecked")
						Map<String, Object> obj = (Map<String, Object>) context;
						if(null != obj.get("from_") && obj.get("from_").toString().length() > 0){
							return getSourceStatuses().get(obj.get("source")+"") + " - " + obj.get("from_");
						}else if(null != obj.get("source") && obj.get("source").toString().length() > 0){
							return getSourceStatuses().get(obj.get("source")+"");
						}else{
							return "";
						}
					}
				}));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.case_no", "b.car_plate",
				"b.closer_name", "b.driver_name", "b.driver_cert"};
	}

	@Override
	protected String getFormActionName() {
		return "caseAdvice";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['car_plate']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"b.status_");
		
		// carManId条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("b.driver_id", carManId);
		}
		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("b.car_id", carId);
		}
		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition,carManIdCondition,carIdCondition);
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (this.status != null || this.status.length() != 0) {
			json.put("status", status);
		}
		// carManId条件
		if (carManId != null) {
			json.put("carManId", carManId);
		}
		// carId条件
		if (carId != null) {
			json.put("carId", carId);
		}
		return json.isEmpty() ? null : json;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return super.getHtmlPageToolbar()
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getBSStatuses2(), "status", 0,
								getText("title.click2changeSearchStatus")));
	}

	/**
	 * 获取类型(表扬,投诉)分类值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getType() {
		Map<String, String> type = new HashMap<String, String>();
		type = new HashMap<String, String>();
		type.put(String.valueOf(Case4Advice.ADVICE_TYPE_COMPLAIN),
				getText("runcase.select.complain"));
		type.put(String.valueOf(Case4Advice.ADVICE_TYPE_SUGGEST),
				getText("runcase.select.suggest"));

		return type;
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
				getText("runcase.select.source.sync.auto"));
		statuses.put(String.valueOf(CaseBase.SOURCE_GENERATION),
				getText("runcase.select.source.sync.auto"));
		return statuses;
	}
}
