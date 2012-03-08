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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.LinkFormater4Id;
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
	public Long buyerId;
	public Long carId;

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
		return new OrderCondition("s.status_", Direction.Asc).add(
				"s.sell_date", Direction.Desc);
	}

	@Override
	protected LikeCondition getGridSearchCondition4OneField(String field,
			String value) {
		if (field.indexOf("s.car_plate") != -1) {
			return new LikeCondition(field, value != null ? value.toUpperCase()
					: value);
		} else {
			return super.getGridSearchCondition4OneField(field, value);
		}
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select s.id as id,s.status_ as status_,s.sell_date as sell_date");
		sql.append(",d.start_no as start_no,d.end_no as end_no,s.company as company");
		sql.append(",m.id as motorcade_id,m.name as motorcade_name,s.car_id as car_id,s.car_plate as car_plate");
		sql.append(",s.buyer_id as buyer_id,s.buyer_name as buyer_name,b.type_ as type,b.unit_ as unit_");
		sql.append(",d.count_ as count_,d.price as price,s.desc_ as desc,b.code as code");
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
				map.put("start_no", rs[i++]); // 开始号
				map.put("end_no", rs[i++]); // 结束号
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
				map.put("desc", rs[i++]); // 备注
				map.put("code", rs[i++]); // 发票代码
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
		columns.add(new TextColumn4MapKey("s.status_", "status_",
				getText("invoice.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getStatus())));
		// 公司
		columns.add(new TextColumn4MapKey("s.company", "company",
				getText("invoice.company"), 60).setSortable(true));
		// 销售日期
		columns.add(new TextColumn4MapKey("s.sell_date", "sell_date",
				getText("invoice.sell.selldate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		// 车队
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("invoice.motorcade"), 70)
				.setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/motorcade/edit?id={0}",
								"motorcade") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("motorcade_id"));
							}
						}));
		// 车牌
		if (carId == null) {
			columns.add(new TextColumn4MapKey("s.car_plate", "car_plate",
					getText("invoice.carPlate"), 80)
					.setValueFormater(new LinkFormater4Id(this.getContextPath()
							+ "/bc-business/car/edit?id={0}", "car") {
						@SuppressWarnings("unchecked")
						@Override
						public String getIdValue(Object context, Object value) {
							return StringUtils
									.toString(((Map<String, Object>) context)
											.get("car_id"));
						}
					}));
		}
		// 购买人
		if (buyerId == null) {
			columns.add(new TextColumn4MapKey("s.buyer_name", "buyer_name",
					getText("invoice.buyer"), 60).setSortable(true)
					.setValueFormater(
							new LinkFormater4Id(this.getContextPath()
									+ "/bc-business/carMan/edit?id={0}",
									"drivers") {
								@SuppressWarnings("unchecked")
								@Override
								public String getIdValue(Object context,
										Object value) {
									return StringUtils
											.toString(((Map<String, Object>) context)
													.get("buyer_id"));
								}
							}));
		}

		// 发票类型
		columns.add(new TextColumn4MapKey("b.type_", "type_",
				getText("invoice.type"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getTypes())));
		// 发票单位
		columns.add(new TextColumn4MapKey("b.unit_", "unit_",
				getText("invoice.unit"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(getUnits())));
		// 发票代码
		/*
		 * columns.add(new TextColumn4MapKey("b.code", "code",
		 * getText("invoice.code"), 100).setSortable(true)
		 * .setUseTitleFromLabel(true));
		 */
		// 发票编码开始号
		columns.add(new TextColumn4MapKey("s.start_no", "start_no",
				getText("invoice.startNo"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		// 发票编码结束号
		columns.add(new TextColumn4MapKey("s.end_no", "end_no",
				getText("invoice.endNo"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		// 数量
		columns.add(new TextColumn4MapKey("s.count_", "count_",
				getText("invoice.count"), 60).setSortable(true));
		// 销售单价
		columns.add(new TextColumn4MapKey("d.price", "price",
				getText("invoice.sell.price"), 60).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		// 合计
		columns.add(new TextColumn4MapKey("s.price", "amount",
				getText("invoice.amount"), 60).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		// 备注
		columns.add(new TextColumn4MapKey("s.desc_", "desc",
				getText("invoice.desc")).setUseTitleFromLabel(true));
		return columns;
	}

	/**
	 * 发票状态值转换:正常|作废|全部
	 * 
	 */
	private Map<String, String> getStatus() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(String.valueOf(Invoice4Sell.STATUS_NORMAL),
				getText("invoice.status.normal"));
		map.put(String.valueOf(Invoice4Sell.STATUS_INVALID),
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
		return new String[] { "m.name", "car_plate", "s.buyer_name" };
	}

	@Override
	protected String getFormActionName() {
		return "invoice4Sell";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "'购买人' + ['buyer_name']";
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
		// carManId条件
		Condition carManIdCondition = null;
		if (buyerId != null) {
			carManIdCondition = new EqualsCondition("s.buyer_id", buyerId);
		}
		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("b.car_id", carId);
		}
		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition,
				carManIdCondition, carIdCondition);
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
			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());
			// 作废按钮
			tb.addButton(Toolbar
					.getDefaultDisabledToolbarButton(getText("invoice.status.invalid")));
		}
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				this.getStatus(), "status", 0,
				getText("title.click2changeSearchStatus")));
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	private MotorcadeService motorcadeService;
	private ActorService actorService;

	@Autowired
	public void setActorService(
			@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	public JSONArray motorcades;// 车队的下拉列表信息
	public JSONArray units;// 分公司的下拉列表信息

	@Override
	protected void initConditionsFrom() throws Exception {
		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));

		// 可选分公司列表
		units = OptionItem.toLabelValues(this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT }, (Integer[]) null), "name",
				"id");
	}
	// ==高级搜索代码结束==
}
