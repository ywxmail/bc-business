/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
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
 * 票务销售视图Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4SellsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 票务的状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 票务管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.invoice"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态
		return new OrderCondition("d.status_", Direction.Asc).add("s.sell_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select s.id as id,s.status_ as status,s.sell_date as selldate");
		sql.append(",d.start_no as start_no,d.end_no as end_no,s.company as company");
		sql.append(",m.id as motorcade_id,m.name as motorcade_name,s.car_id as car_id,s.car_plate as car_plate");
		sql.append(",s.buyer_id as buyer_id,s.buyer_name as buyer_name,b.type_ as type,b.unit_ as unit_");
		sql.append(",d.count_ as count,d.price as price");
		sql.append(" from bs_invoice_sell_detail d");
		sql.append(" inner join bs_invoice_buy b on b.id=d.buy_id");
		sql.append(" inner join bs_invoice_sell s on s.id=d.sell_id");
		sql.append(" inner join bs_motorcade m on m.id=s.motorcade_id");
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
				map.put("sell_date", rs[i++]); // 销售日期
				map.put("start_no", rs[i++]); // 销售日期
				map.put("end_no", rs[i++]); // 销售日期
				map.put("company", rs[i++]); // 公司
				map.put("motorcade_id", rs[i++]); // 车队id
				map.put("motorcade_name", rs[i++]); // 车队名称
				map.put("car_id", rs[i++]); // 车id
				map.put("car_plate", rs[i++]); // 车票
				map.put("buyer_id", rs[i++]); // 购买人id
				map.put("buyer_name", rs[i++]); // 购买人名称
				map.put("type_", rs[i++]); // 发票类型
				map.put("unit_", rs[i++]); // 单位
				map.put("count_", rs[i++]); // 销售数量
				map.put("price", rs[i++]); // 销售单价
				// 合计
				if (map.get("count_") != null && map.get("price") != null) {
					map.put("amount",
							Float.parseFloat(map.get("count_").toString())
									* Float.parseFloat(map.get("price")
											.toString()));
				} else {
					map.put("amount", null);
				}
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
				.setValueFormater(new EntityStatusFormater(getBSStatuses1())));
		// 公司
		columns.add(new TextColumn4MapKey("b.company", "company",
				getText("invoice.company"), 60).setSortable(true));
		// 采购日期
		columns.add(new TextColumn4MapKey("b.buy_date", "buy_date",
				getText("invoice.buy.buydate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		// 发票类型
		columns.add(new TextColumn4MapKey("b.type_", "type_",
				getText("invoice.type"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getTypes())));
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
				getText("invoice.endNo"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		// 数量
		columns.add(new TextColumn4MapKey("b.count_", "count_",
				getText("invoice.count"), 60).setSortable(true));
		// 采购单价
		columns.add(new TextColumn4MapKey("b.buy_price", "buy_price",
				getText("invoice.buy.price"), 60).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		// 合计
		columns.add(new TextColumn4MapKey("b.buy_price", "amount",
				getText("invoice.amount"), 60).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		return columns;
	}

	/**
	 * 发票类型值转换:手撕票|打印票
	 * 
	 */
	private Map<String, String> getTypes() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", getText("invoice.type.dayinpiao"));
		map.put("2", getText("invoice.type.shousipiao"));
		return map;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[]{"b.code"};
	}
	
	@Override
	protected String getFormActionName() {
		return "";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "'采购单 ' + ['id']";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return super.getHtmlPageToolbar()
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getBSStatuses1(), "status", 0,
								getText("title.click2changeSearchStatus")));
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	@Override
	protected void initConditionsFrom() throws Exception {
	
	}

	// ==高级搜索代码结束==

}
