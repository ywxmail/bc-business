/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.business.invoice.service.Invoice4BuyService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 票务采购视图Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4BuysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 票务的状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 票务管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		//配置权限：发票管理员、发票采购管理员
		return !context.hasAnyRole(getText("key.role.bs.invoice"),getText("key.role.bs.invoice4buy"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态
		return new OrderCondition("b.status_", Direction.Asc).add("b.buy_date",
				Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select b.id as id,b.status_ as status_,b.buy_date as buy_date,");
		sql.append("b.code as code,b.start_no as start_no,b.end_no as end_no");
		sql.append(",b.company as company,b.type_ as type_,b.count_ as count_,b.buy_price as buy_price");
		sql.append(",b.desc_ as desc,b.unit_ as unit_,b.sell_price as sell_price,a.actor_name as buyerName");
		sql.append(",getbalancecountbyinvoicebuyid(b.id) as balance_count");
		sql.append(",getbalancenumberbyinvoicebuyid(b.id,b.start_no,b.end_no) as balance_number");
		sql.append(" from bs_invoice_buy b");
		sql.append(" left join bc_identity_actor_history a on a.id=b.buyer_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status_", rs[i++]); // 状态
				map.put("buy_date", rs[i++]); // 采购日期
				map.put("code", rs[i++]); // 发票代码
				map.put("start_no", rs[i++]); // 发票编码开始号
				map.put("end_no", rs[i++]); // 发票编码结束号
				map.put("company", rs[i++]); // 公司
				map.put("type_", rs[i++]); // 发票类型
				map.put("count_", rs[i++]); // 数量
				map.put("buy_price", rs[i++]); // 采购单价
				// 合计
				if (map.get("count_") != null && map.get("buy_price") != null) {
					map.put("amount",
							Float.parseFloat(map.get("count_").toString())
									* Float.parseFloat(map.get("buy_price")
											.toString()));
				} else {
					map.put("amount", null);
				}
				map.put("desc", rs[i++]); // 备注
				map.put("unit_", rs[i++]); // 单位
				map.put("sell_price", rs[i++]); // 销售单价
				map.put("buyerName", rs[i++]); // 购买人
				map.put("balance_count", rs[i++]); // 剩余数量
				map.put("balance_number", rs[i++]); // 剩余号码段
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("b.id", "id"));
		// 状态
		columns.add(new TextColumn4MapKey("b.status_", "status_",
				getText("invoice.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(this.getStatus())));
		// 公司
		columns.add(new TextColumn4MapKey("b.company", "company",
				getText("invoice.company"), 60).setSortable(true));
		// 购买人
		columns.add(new TextColumn4MapKey("a.actor_name", "buyerName",
				getText("invoice4Buy.buyer"), 80).setSortable(true));
		// 采购日期
		columns.add(new TextColumn4MapKey("b.buy_date", "buy_date",
				getText("invoice4Buy.buydate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		// 发票类型
		columns.add(new TextColumn4MapKey("b.type_", "type_",
				getText("invoice.type"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getTypes())));
		// 发票单位
		columns.add(new TextColumn4MapKey("b.unit_", "unit_",
				getText("invoice.unit"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(getUnits())));
		// 发票代码
		columns.add(new TextColumn4MapKey("b.code", "code",
				getText("invoice.code"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		// 发票编码开始号
		columns.add(new TextColumn4MapKey("b.start_no", "start_no",
				getText("invoice.startNo"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		// 发票编码结束号
		columns.add(new TextColumn4MapKey("b.end_no", "end_no",
				getText("invoice.endNo"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		// 数量
		columns.add(new TextColumn4MapKey("b.count_", "count_",
				getText("invoice.count"), 60).setSortable(true));
		// 剩余数量
		columns.add(new TextColumn4MapKey("", "balance_count",
				getText("invoice4Buy.balanceCount"), 60).setSortable(true));
		// 采购单价
		columns.add(new TextColumn4MapKey("b.buy_price", "buy_price",
				getText("invoice4Buy.buyPrice"), 60).setSortable(true)
				.setValueFormater(new NubmerFormater("###,##0.00")));
		// 销售单价
		columns.add(new TextColumn4MapKey("b.sell_price", "sell_price",
				getText("invoice4Buy.sellPrice"), 60).setSortable(true)
				.setValueFormater(new NubmerFormater("###,##0.00")));
		// 合计
		columns.add(new TextColumn4MapKey("b.buy_price", "amount",
				getText("invoice.amount"), 150).setSortable(true).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,##0.00")));
		// 剩余号码段
		columns.add(new TextColumn4MapKey("", "balance_number",
				getText("invoice4Buy.balanceNumber"), 200).setUseTitleFromLabel(true));
		// 备注
		columns.add(new TextColumn4MapKey("b.desc_", "desc",
				getText("invoice.desc")).setUseTitleFromLabel(true));
		return columns;
	}

	/**
	 * 发票状态值转换:正常|作废|全部
	 * 
	 */
	private Map<String, String> getStatus() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(String.valueOf(Invoice4Buy.STATUS_NORMAL),
				getText("invoice.status.normal"));
		map.put(String.valueOf(Invoice4Buy.STATUS_INVALID),
				getText("invoice.status.invalid"));
		map.put("", getText("invoice.status.all"));
		return map;
	}

	/**
	 * 发票类型值转换:手撕票|打印票
	 * 
	 */
	private Map<String, String> getTypes() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(String.valueOf(Invoice4Buy.TYPE_PRINT),
				getText("invoice.type.dayinpiao"));
		map.put(String.valueOf(Invoice4Buy.TYPE_TORE),
				getText("invoice.type.shousipiao"));
		return map;
	}

	/**
	 * 发票单位值转换:卷|本
	 * 
	 */
	private Map<String, String> getUnits() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(String.valueOf(Invoice4Buy.UNIT_JUAN),
				getText("invoice.unit.juan"));
		map.put(String.valueOf(Invoice4Buy.UNIT_BEN),
				getText("invoice.unit.ben"));
		return map;
	}
	
	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.code",	"a.actor_name" ,"b.start_no" ,"b.end_no"};
	}

	@Override
	protected String getFormActionName() {
		return "invoice4Buy";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "'发票采购单 '";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
		} else {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
			// 作废按钮
			/*tb.addButton(Toolbar
					.getDefaultDisabledToolbarButton(getText("invoice.status.invalid")));*/
		}
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				this.getStatus(), "status", 0,
				getText("title.click2changeSearchStatus")));
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("b.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("b.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}
		
		// 合并条件
		return statusCondition;
	}
	
	@Override
	protected String getGridDblRowMethod() {
		return "bc.page.open";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
	
	private Invoice4BuyService invoice4BuyService;
	
	@Autowired
	public void setInvoice4BuyService(Invoice4BuyService invoice4BuyService) {
		this.invoice4BuyService = invoice4BuyService;
	}

	public JSONArray codes;
	public JSONArray companies;// 公司的下拉列

	@Override
	protected void initConditionsFrom() throws Exception {
			codes=OptionItem.toLabelValues(this.invoice4BuyService.findEnabled4Option());
			
			//公司
			companies=OptionItem.toLabelValues(this.invoice4BuyService.findCompany4Option());
	}
	// ==高级搜索代码结束==
}
