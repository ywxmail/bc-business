/**
 * 
 */
package cn.bc.business.fee.template.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.BSConstants;
import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 费用管理接口Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectFeeTemplateAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆保单险种的状态，多个用逗号连接
	public String module;//所属模板

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：排序号
		return new OrderCondition("a.order_", Direction.Asc);
	}
	
	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,a.status_ as status,a.module_ as module,a.type_ as type,b.name as pname,a.order_ as order");
		sql.append(",a.name,a.price,a.count_ as count,a.pay_type,a.desc_ as desc");
		sql.append(" from bs_fee_template a");
		sql.append(" left join bs_fee_template b on b.id=a.pid ");
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
				map.put("module", rs[i++]);
				map.put("type", rs[i++]);
				map.put("pname", rs[i++]);
				map.put("order", rs[i++]);
				map.put("name", rs[i++]);
				map.put("price", rs[i++]);
				map.put("count", rs[i++]);
				map.put("pay_type", rs[i++]);
				map.put("desc", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}       

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		//类型
		columns.add(new TextColumn4MapKey("a.type_", "type",
				getText("feeTemplate.type"), 40)
				.setValueFormater(new KeyValueFormater(this.getTypes())));
		//所属模板
		columns.add(new TextColumn4MapKey("b.name", "pname",
				getText("feeTemplate.ptempalte"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.order_", "order",
				getText("feeTemplate.order"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("feeTemplate.name"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("a.price", "price",
				getText("feeTemplate.price"), 80).setSortable(true)
				.setValueFormater(new NubmerFormater("###,##0.00")));
		columns.add(new TextColumn4MapKey("a.count_", "count",
				getText("feeTemplate.count"), 80).setSortable(true));
		//收费方式
		columns.add(new TextColumn4MapKey("t.pay_type", "pay_type",
				getText("feeTemplate.payType"), 65).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getPayTypes())));	
		columns.add(new TextColumn4MapKey("a.desc_", "desc",
				getText("feeTemplate.desc")).setUseTitleFromLabel(true));
		return columns;
	}

	//收费方式值转换
	private Map<String, String> getPayTypes() {
		Map<String,String> paytypes=new HashMap<String, String>();
		paytypes.put(null, "");
		paytypes.put(String.valueOf(FeeTemplate.PAY_TYPE_MONTH), 
				getText("feeTemplate.payType.month"));
		paytypes.put(String.valueOf(FeeTemplate.PAY_TYPE_SEASON), 
				getText("feeTemplate.payType.season"));
		paytypes.put(String.valueOf(FeeTemplate.PAY_TYPE_YEAR), 
				getText("feeTemplate.payType.year"));
		paytypes.put(String.valueOf(FeeTemplate.PAY_TYPE_ALL), 
				getText("feeTemplate.payType.all"));
		return paytypes;
	}
	
	//类型值转换
	private Map<String, String> getTypes() {
		Map<String,String> types=new HashMap<String, String>();
		types.put(String.valueOf(FeeTemplate.TYPE_FEE), 
				getText("feeTemplate.type.fee"));
		types.put(String.valueOf(FeeTemplate.TYPE_TEMPLATE), 
				getText("feeTemplate.type.template"));
		return types;
	}
	
	@Override
	protected String[] getGridSearchFields() {
		return new String[]{"b.name","a.name"};
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("feeTemplate.title.selectFeeTemplate");
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(600).setHeight(480);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectFeeTemplate");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/feeTemplate/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if(module==null||module==""){
			return new AndCondition(new EqualsCondition("a.status_", BCConstants.STATUS_ENABLED));
		}else{
			return new AndCondition(new EqualsCondition("a.status_", BCConstants.STATUS_ENABLED)
			,new EqualsCondition("a.module_", module.trim()));
		}
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
		return "bs.feeTemplateSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}
	
	
	
}
