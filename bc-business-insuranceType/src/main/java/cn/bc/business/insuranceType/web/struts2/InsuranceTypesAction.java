/**
 * 
 */
package cn.bc.business.insuranceType.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.insuranceType.domain.InsuranceType;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 车辆保单险种视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class InsuranceTypesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status; // 车辆保单险种的状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 车辆保单险种管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.insuranceType"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|类型|创建日期
		return new OrderCondition("i.status_", Direction.Asc).add("i.type_", Direction.Desc).add(
				"i.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select i.id as id,i.status_ as status,i.type_ as type,i.name as name ");
		sql.append(" ,i.coverage as coverage,n.name as pname,i.desc_ as desc,i.file_date");
		sql.append(" from bs_insurance_type i");
		sql.append(" left join bs_insurance_type n on i.pid=n.id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]);
				map.put("type", rs[i++]);
				map.put("name", rs[i++]);
				map.put("coverage", rs[i++]);
				map.put("pname", rs[i++]);
				map.put("desc", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("i.id", "id"));
		columns.add(new TextColumn4MapKey("i.status_", "status",
				getText("label.status"), 60)
				.setSortable(true)
				.setValueFormater(new EntityStatusFormater(getEntityStatuses())));
		//类型
		columns.add(new TextColumn4MapKey("i.type_", "type",
				getText("insuranceType.type"), 40)
				.setSortable(true)
				.setValueFormater(new EntityStatusFormater(this.getTypes())));
		columns.add(new TextColumn4MapKey("i.name", "name",
				getText("insuranceType.name"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("i.coverage", "coverage",
				getText("insuranceType.coverage"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		//所属模板名称
		columns.add(new TextColumn4MapKey("n.name","pname",
				getText("insuranceType.pname"),120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("i.desc_", "desc",
				getText("insuranceType.description"),100).setSortable(true));
		return columns;
	}
	
	//类型键值转换
	private Map<String,String> getTypes(){
		Map<String,String> mtypes=new HashMap<String, String>();
		mtypes.put(String.valueOf(InsuranceType.TYPE_PLANT), getText("insuranceType.type.plant"));
		mtypes.put(String.valueOf(InsuranceType.TYPE_TEMPLATE), getText("insuranceType.type.template"));
		return mtypes;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "i.name","n.name" };
	}

	@Override
	protected String getFormActionName() {
		return "insuranceType";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(620).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		return ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"i.status_");
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
	protected Toolbar getHtmlPageToolbar() {
		return getHtmlPageToolbar(true);
	}

}
