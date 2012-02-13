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

import cn.bc.BCConstants;
import cn.bc.business.BSConstants;
import cn.bc.business.insuranceType.domain.InsuranceType;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择车辆保单险种Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectInsuranceTypeAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆保单险种的状态，多个用逗号连接

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建日期
		return new OrderCondition("i.order_", Direction.Asc);
	}
	
	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select i.id,i.name,i.desc_,i.status_,i.type_,i.order_,i.coverage,n.name as pname");
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
				map.put("name", rs[i++]);
				map.put("desc_", rs[i++]);
				map.put("status_", rs[i++]);
				map.put("type_", rs[i++]);
				map.put("order_", rs[i++]);
				map.put("coverage", rs[i++]);
				map.put("pname", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}       

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("i.id", "id"));
		columns.add(new TextColumn4MapKey("i.type_", "type_",
				getText("insuranceType.type"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(this.getTypes())));
		// 所属模板名称
		columns.add(new TextColumn4MapKey("n.name", "pname",
				getText("insuranceType.pname"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("i.order_", "order_",
				getText("insuranceType.orderNo"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("i.name", "name",
				getText("insuranceType.name"), 120)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("i.coverage", "coverage",
				getText("insuranceType.coverage"), 80)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("i.desc_", "desc_",
				getText("insuranceType.description")).setUseTitleFromLabel(true));
		return columns;
	}

	// 类型键值转换
	private Map<String, String> getTypes() {
		Map<String, String> mtypes = new HashMap<String, String>();
		mtypes.put(String.valueOf(InsuranceType.TYPE_PLANT),
				getText("insuranceType.type.plant"));
		mtypes.put(String.valueOf(InsuranceType.TYPE_TEMPLATE),
				getText("insuranceType.type.template"));
		return mtypes;
	}
	
	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "i.name" };
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("insuranceType.title.selectInsuranceType");
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(600).setHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectInsuranceType");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/insuranceType/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		return new AndCondition(new EqualsCondition("i.status_", BCConstants.STATUS_ENABLED));
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

	@Override
	protected String getClickOkMethod() {
		return "bs.insuranceTypeSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}
	
	
	
}
