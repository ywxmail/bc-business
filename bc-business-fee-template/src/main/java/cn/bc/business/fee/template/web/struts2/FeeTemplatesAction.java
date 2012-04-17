/**
 * 
 */
package cn.bc.business.fee.template.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 
 * 
 * @author
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FeeTemplatesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	
	
	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin")
				,getText("key.role.bs.fee.template"));
	}
	
	@Override
	protected OrderCondition getGridOrderCondition() {
		//先按状态，然后排序号
		return new OrderCondition("a.status_").add("a.order_", Direction.Asc);
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
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[]{"a.module_","b.name","a.name"};
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("a.status_", "status",
				getText("feeTemplate.status"), 40)
				.setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getStatuses())));
		//所属模块
		columns.add(new TextColumn4MapKey("a.module_", "a.module",
				getText("feeTemplate.module"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
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
				getText("feeTemplate.payType"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getPayTypes())));	
		columns.add(new TextColumn4MapKey("a.desc_", "desc",
				getText("feeTemplate.desc")).setUseTitleFromLabel(true));
		return columns;
	}
	
	//收费方式值转换
	private Map<String, String> getPayTypes() {
		Map<String,String> paytypes=new HashMap<String, String>();
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

	//状态键值转换
	private Map<String,String> getStatuses(){
		Map<String,String> statuses=new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED)
				, getText("feeTemplate.status.normal"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED)
				, getText("feeTemplate.status.disabled"));
		statuses.put(""
				, getText("feeTemplate.status.all"));
		return statuses;
	}

	@Override
	protected String getFormActionName() {
		return "feeTemplate";
	}
	
	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}
	
	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (!this.isReadonly()) {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());

			// 禁用按钮
			tb.addButton(this.getDefaultDisabledToolbarButton());
		}else{
			//查看
			tb.addButton(this.getDefaultOpenToolbarButton());
		}

		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				this.getStatuses(), "a.status_", 0, getText("feeTemplate.status.tips")));
		
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	//高级搜索
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
}