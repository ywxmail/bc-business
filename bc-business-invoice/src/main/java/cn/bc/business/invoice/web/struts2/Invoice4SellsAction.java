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
import cn.bc.business.BSConstants;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.business.invoice.service.Invoice4SellService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
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
import cn.bc.web.ui.json.Json;

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
	public Long buyId;//采购单ID
	public String type;//类型
	public String readType;//查询类型，1-发票销售，2-发票退票，3，发票查询,默认null为发票销售
	
//	private Invoice4BuyService invoice4BuyService;
//	
//	@Autowired
//	public void setInvoice4BuyService(Invoice4BuyService invoice4BuyService) {
//		this.invoice4BuyService = invoice4BuyService;
//	}

	@Override
	public boolean isReadonly() {
		// 票务管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.invoice"),getText("key.role.bs.invoice4sell"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态
		return new OrderCondition("s.status_", Direction.Asc).add(
				"s.sell_date", Direction.Desc).add("s.file_date",Direction.Desc);
	}

	@Override
	protected LikeCondition getGridSearchCondition4OneField(String field,
			String value) {
		if (field.indexOf("car_plate") != -1) {
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
		sql.append(",d.count_ as count_,d.price as price,s.desc_ as desc,b.code as code,a.actor_name as cashier");
		sql.append(",bia.name as unit_name,cm.cert_fwzg as fwzg");
		sql.append(",au.actor_name as author_name,s.file_date as file_date");
		sql.append(",am.actor_name as modified_name,s.modified_date as modified_date,s.type_ as sellType");
		sql.append(" from bs_invoice_sell_detail d");
		sql.append(" inner join bs_invoice_buy b on b.id=d.buy_id");
		sql.append(" inner join bs_invoice_sell s on s.id=d.sell_id");
		sql.append(" inner join bc_identity_actor_history a on a.id=s.cashier_id");
		sql.append(" inner join bs_motorcade m on m.id=s.motorcade_id");
		sql.append(" inner join bc_identity_actor bia on bia.id=m.unit_id");
		sql.append(" left join bs_carman cm on cm.id=s.buyer_id");
		sql.append(" inner join bc_identity_actor_history au on au.id=s.author_id");
		sql.append(" left join bc_identity_actor_history am on am.id=s.modifier_id");
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
				map.put("car_plate", rs[i++]); // 车辆
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
				map.put("cashier", rs[i++]); // 销售员
				map.put("unit_name", rs[i++]); // 分公司
				map.put("fwzg", rs[i++]); // 购买人服务资格证
				map.put("author_name", rs[i++]); // 创建人
				map.put("file_date", rs[i++]); // 创建时间
				map.put("modified_name", rs[i++]); // 最后修改人
				map.put("modified_date", rs[i++]); // 最后修改时间
				map.put("sellType", rs[i++]); // 销售类型
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
		//查看状态为查询时
		if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_READ)){
			// 类型
			columns.add(new TextColumn4MapKey("s.type_", "sellType",
					getText("invoice4Sell.type"), 40).setSortable(true)
					.setValueFormater(new EntityStatusFormater(getSellTypes())));
			// 销售/退票日期
			columns.add(new TextColumn4MapKey("s.sell_date", "sell_date",
					getText("invoice.select.date"), 95).setSortable(true).setUseTitleFromLabel(true)
					.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
			// 销售/受理员
			columns.add(new TextColumn4MapKey("a.actor_name", "cashier",
					getText("invoice.select.seller"),90).setUseTitleFromLabel(true));
		//类型为退票时
		}else if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_REFUND)){
			// 退票日期
			columns.add(new TextColumn4MapKey("s.sell_date", "sell_date",
					getText("invoice4Refund.refundDate"), 95).setSortable(true).setUseTitleFromLabel(true)
					.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
			// 退票员
			columns.add(new TextColumn4MapKey("a.actor_name", "cashier",
					getText("invoice4Refund.receiver"), 60).setUseTitleFromLabel(true));
		}else{
			// 销售日期
			columns.add(new TextColumn4MapKey("s.sell_date", "sell_date",
					getText("invoice4Sell.selldate"), 95).setSortable(true).setUseTitleFromLabel(true)
					.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
			// 销售员
			columns.add(new TextColumn4MapKey("a.actor_name", "cashier",
					getText("invoice4Sell.cashier"), 60).setUseTitleFromLabel(true));
		}
		
		// 公司
		columns.add(new TextColumn4MapKey("s.company", "company",
				getText("invoice.company"), 45).setSortable(true));
		// 分公司
		columns.add(new TextColumn4MapKey("bia.name", "unit_name",
				getText("invoice.unitCompany"), 70).setSortable(true));
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
					getText("invoice.carPlate"), 80).setUseTitleFromLabel(true)
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
			//查看状态为查询时
			if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_READ)){
				columns.add(new TextColumn4MapKey("s.buyer_name", "buyer_name",
						getText("invoice.select.carman"), 90).setSortable(true)
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
			//类型为退票时
			}else if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_REFUND)){
				columns.add(new TextColumn4MapKey("s.buyer_name", "buyer_name",
						getText("invoice4Refund.carman"), 60).setSortable(true)
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
			}else{
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
			columns.add(new TextColumn4MapKey("cm.cert_fwzg", "fwzg",
					getText("invoice4Sell.fwzg"), 80));
		}
		// 数量
		columns.add(new TextColumn4MapKey("d.count_", "count_",
				getText("invoice.count"), 65).setSortable(true));
		// 发票代码
		columns.add(new TextColumn4MapKey("b.code", "code",
				getText("invoice.code"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		// 发票编码开始号
		columns.add(new TextColumn4MapKey("d.start_no", "start_no",
				getText("invoice.startNo"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		// 发票编码结束号
		columns.add(new TextColumn4MapKey("d.end_no", "end_no",
				getText("invoice.endNo"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		// 销售单价
		columns.add(new TextColumn4MapKey("d.price", "price",
				getText("invoice4Sell.price"), 60).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		// 合计
		columns.add(new TextColumn4MapKey("d.price", "amount",
				getText("invoice4Sell.amount"), 100).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.00")));
		if(buyId == null){
			// 发票类型
			columns.add(new TextColumn4MapKey("b.type_", "type_",
					getText("invoice4Sell.type"), 55).setSortable(true)
					.setValueFormater(new KeyValueFormater(getTypes())));
			// 发票单位
			columns.add(new TextColumn4MapKey("b.unit_", "unit_",
					getText("invoice4Sell.unit"), 40).setSortable(true)
					.setValueFormater(new KeyValueFormater(getUnits())));
		}
		columns.add(new TextColumn4MapKey("au.actor_name", "author_name",
				getText("invoice.author"), 60));
		columns.add(new TextColumn4MapKey("s.file_date", "file_date",
				getText("invoice.fileDate"), 160)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
		columns.add(new TextColumn4MapKey("am.actor_name", "modified_name",
				getText("invoice.modifier"), 80));
		columns.add(new TextColumn4MapKey("s.modified_date", "modified_date",
				getText("invoice.modifiedDate"), 160)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
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
	
	/**
	 * 发票销售类型值转换:已销|退票
	 * 
	 */
	private Map<String, String> getSellTypes() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(String.valueOf(Invoice4Sell.TYPE_SELL),
				getText("invoice4Sell.type.sell"));
		map.put(String.valueOf(Invoice4Sell.TYPE_REFUND),
				getText("invoice4Sell.type.refund"));
		return map;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "m.name", "car_plate", "s.buyer_name"
				,"a.actor_name","d.start_no","d.end_no","au.actor_name","am.actor_name" };
	}

	@Override
	protected String getFormActionName() {
		//查看状态为退票时
		if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_REFUND)){
			return "invoice4Refund";
		}else{
			return "invoice4Sell";
		}
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		//查看状态为查询时
		if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_READ)){
			return "['car_plate']+'的发票单'";
		//查看状态为退票时
		}else if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_REFUND)){
			return "['car_plate']+'的发票退票单'";
		}else{
			return "['car_plate']+'的发票销售单'";
		}
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		AndCondition ac = new AndCondition();
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				ac.add(new EqualsCondition("s.status_", new Integer(
						ss[0])));
			} else {
				ac.add(new InCondition("s.status_",
						StringUtils.stringArray2IntegerArray(ss)));
			}
		}
		if (buyerId != null) 
			ac.add(new EqualsCondition("s.buyer_id", buyerId));
		
		// carId条件
		if (carId != null) 
			ac.add(new EqualsCondition("s.car_id", carId));
		
		// buyId采购单条件
		if(buyId!=null)
			ac.add(new EqualsCondition("d.buy_id", buyId));	
		//销售
		if(readType==null||readType.equals(Invoice4Sell.READ_TYPE_SELL))
			ac.add(new EqualsCondition("s.type_", Invoice4Sell.TYPE_SELL));
		
		//退票
		if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_REFUND))
			ac.add(new EqualsCondition("s.type_", Invoice4Sell.TYPE_REFUND));
			
		// 合并条件
		return ac;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		//带权限且在查看状态为销售和退票时
		if (!this.isReadonly()
				&&(readType==null
					||readType.equals(Invoice4Sell.READ_TYPE_SELL)
						||readType.equals(Invoice4Sell.READ_TYPE_REFUND))) {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());
		}
		// 查看按钮
		tb.addButton(this.getDefaultOpenToolbarButton());
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				this.getStatus(), "status", 0,
				getText("title.click2changeSearchStatus")));
	}

	@Override
	protected String getGridDblRowMethod() {
		return "bc.page.open";
	}
	
	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (status != null && status.length() > 0) 
			json.put("status", status);
		
		//类型
		if(readType != null && readType.length() > 0)
			json.put("readType", readType);
		
		// carManId条件
		if (buyerId != null) 
			json.put("buyerId", buyerId);
		
		// carId条件
		if (carId != null) 
			json.put("carId", carId);
		
		if(buyId !=null)
			json.put("buyId", buyId);
		
		return json.isEmpty() ? null : json;
	}
	
	//视图标题名称控制
	@Override
	protected String getHtmlPageTitle() {
		//查看状态为查询时
		if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_READ)){
			return getText("invoice.select");
		//查看状态为退票时
		}else if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_REFUND)){
			return getText("invoice4Refund.title");
		}else{
			return getText("invoice4Sell.title");
		}
	}
	
	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	private MotorcadeService motorcadeService;
	private ActorService actorService;
	private Invoice4SellService invoice4SellService;
	
	@Autowired
	public void setActorService(
			@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}
	
	@Autowired
	public void setInvoice4SellService(Invoice4SellService invoice4SellService) {
		this.invoice4SellService = invoice4SellService;
	}

	public JSONArray motorcades;// 车队的下拉列表信息
	public JSONArray units;// 分公司的下拉列表信息
	public JSONArray companies;// 公司的下拉列

	@Override
	protected void initConditionsFrom() throws Exception {
		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));

		// 可选分公司列表
		units = OptionItem.toLabelValues(this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT }, (Integer[]) null), "name",
				"id");
		//公司
		companies=OptionItem.toLabelValues(this.invoice4SellService.findCompany4Option());
		
	}

	@Override
	public String getAdvanceSearchConditionsJspPath() {
		return  BSConstants.NAMESPACE + "/invoice/sell";
	}
	// ==高级搜索代码结束==
}
