/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.util.DateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.Div;
import cn.bc.web.ui.html.Input;
import cn.bc.web.ui.html.Li;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Text;
import cn.bc.web.ui.html.Ul;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 销售统计视图Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4SellStatsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 票务的状态，多个用逗号连接

	private final static Log logger = LogFactory
			.getLog("cn.bc.business.invoice.Invoice4SellStatsAction");
	
	public String startDate;
	public String endDate;
	public Integer type;
	
	@Override
	public String list() throws Exception {
		Date startTime = new Date();
		// 根据请求的条件查找信息
		this.es = this.createStatsTr(this.findList());

		// 构建页面的html
		this.html = buildHtmlPage();

		logger.info("list耗时：" + DateUtils.getWasteTime(startTime));
		// 返回全局的global-results：在cn/bc/web/struts2/struts.xml中定义的
		return "page";
	}

	@Override
	public String data() throws Exception {
		Date startTime = new Date();
		// 根据请求的条件查找信息
		this.es = this.createStatsTr(this.findList());

		// 构建页面的html
		this.html = getGridData(this.getGridColumns());

		logger.info("list耗时：" + DateUtils.getWasteTime(startTime));
		// 返回全局的global-results：在cn/bc/web/struts2/struts.xml中定义的
		return "page";
	}

	//生成合计行
	private List<Map<String,Object>> createStatsTr(List<Map<String,Object>> list){
		if(list.size()==0){
			return list;
		}else{
			List<Map<String,Object>> newList=new ArrayList<Map<String,Object>>();
			int count=0;
			float price=0;
			for(Map<String,Object> map:list){
				count+=Integer.parseInt(map.get("sum_count").toString());
				price+=Float.parseFloat(map.get("sum_price").toString());
				newList.add(map);
			}
			Map<String,Object> newMap=new HashMap<String, Object>();
			newMap.put("actor_name",getText("invoice.amount"));
			newMap.put("sum_count",count);
			newMap.put("sum_price",price);
			newList.add(newMap);
			return newList;
		}
	}
	


	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select s.cashier_id as id,ah.actor_name,sum(d.count_) as sum_count,sum(d.count_*d.price) as sum_price");
		sql.append(" from bs_invoice_sell s");
		sql.append(" inner join bs_invoice_sell_detail d on d.sell_id=s.id");
		sql.append(" inner join bc_identity_actor_history ah on ah.id=s.cashier_id");
		sql.append(" where s.status_=0");
		
		//根据传入参数拼装sql的where条件
		Calendar cal = Calendar.getInstance();
		CalendarFormater calf=new CalendarFormater();
		if(this.type!=null){
			if(this.startDate!=null&&!this.startDate.equals("")){
				sql.append(" and s.sell_date>=");
				sql.append("'");
				sql.append(this.startDate);
				sql.append("'");
			}
			if(this.endDate!=null&&!this.endDate.equals("")){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
				try {
					cal.setTime(sdf.parse(this.endDate));
					sql.append(" and s.sell_date<");
					//加一天
			        cal.add(Calendar.DAY_OF_MONTH, 1);
			        sql.append("'");
			        sql.append(calf.format(cal));
			        sql.append("'");
				} catch (ParseException e) {
					
				}
			}
		}else{
			sql.append(" and s.sell_date>=");
			sql.append("'");
			sql.append(calf.format(cal));
			sql.append("'");
			sql.append(" and s.sell_date<");
	        cal.add(Calendar.DAY_OF_MONTH, 1);
	        sql.append("'");
	        sql.append(calf.format(cal));
	        sql.append("'");
		}
		
		sql.append(" group by s.cashier_id,ah.actor_name");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("actor_name", rs[i++]); // 状态
				map.put("sum_count", rs[i++]); // 采购日期
				map.put("sum_price", rs[i++]); // 发票代码
				// 合计

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("s.cashier_id", "id"));
		columns.add(new TextColumn4MapKey("", "actor_name",
				getText("invoice4Sell.cashier"),100));
		// 数量
		columns.add(new TextColumn4MapKey("", "sum_count",
				getText("invoice4SellStats.count"), 120));
		columns.add(new TextColumn4MapKey("", "sum_price",
				getText("invoice4SellStats.amount"))
				.setValueFormater(new NubmerFormater("###,###.00")));
		return columns;
	}


	@Override
	protected String getFormActionName() {
		return "invoice4SellStat";
	}
	
	@Override
	protected String getHtmlPageTitle() {
		return getText("invoice4SellStats.title");
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(550).setMinWidth(500)
				.setHeight(300).setMinHeight(200);
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace()+"/invoice/sellStats/invoice4SellStats.js";
	}

	@Override
	protected String getHtmlPageInitMethod() {
		return "bs.invoice4SellStatsWindow.init";
	}

	@Override
	protected String getGridRowLabelExpression() {
		return null;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		/* 
		 * 日期input选择和清除小按钮
		 * <ul class="inputIcons">
		 * 	<li class="selectCalendar inputIcon ui-icon ui-icon-calendar"></li>
		 * 	<li class="clearSelect inputIcon ui-icon ui-icon-close" title="点击清除"></li>
		 * </ul>
		 * 
		 */
		Ul ul=new Ul();
		ul.addClazz("inputIcons");
		Li lis=new Li();
		lis.addClazz("selectCalendar inputIcon ui-icon ui-icon-calendar");
		Li lic=new Li();
		lic.addClazz("clearSelect inputIcon ui-icon ui-icon-close");
		lic.setTitle("点击清除");
		ul.addChild(lis);
		ul.addChild(lic);
		
		//选择销售日期
		Text sellDate=new Text(getText("invoice4SellStats.selldate"));
		
		/*
		 * <div class="bc-dateContainer">
		 *	<input type="text" class="bc-date ui-widget-content " 
		 * 		data-validate="date" style="width:9em" id="i4SellStatsStartDateId">
		 * </div>
		 */
		Div divS=new Div();
		divS.addClazz("bc-dateContainer");
		Input inputS=new Input();
		inputS.setType("text");
		inputS.addClazz("bc-date ui-widget-content");
		inputS.setAttr("data-validate", "date");
		inputS.addStyle("width", "9em");
		inputS.setId("i4SellStatsStartDateId");
		divS.addChild(inputS);
		//加入日期选择和清除小按钮
		divS.addChild(ul);
		
		/*	
		 *  <div class="bc-dateContainer">
		 *	<input type="text" class="bc-date ui-widget-content " 
		 *		data-validate="date" style="width:9em" id="i4SellStatsEndDateId">
		 *	</div>
		 */
		Div divE=new Div();
		divE.addClazz("bc-dateContainer");
		Input inputE=new Input();
		inputE.setType("text");
		inputE.addClazz("bc-date ui-widget-content");
		inputE.setAttr("data-validate", "date");
		inputE.addStyle("width", "9em");
		inputE.setId("i4SellStatsEndDateId");
		divE.addChild(inputE);
		//加入日期选择和清除小按钮
		divE.addChild(ul);
		
		//声明查询按钮
		Button button=new Button();
		//button加入事件
		button.setClick("bs.invoice4SellStatsWindow.onClick");
		Span span1=new Span();
		span1.addClazz("ui-button-icon-primary ui-icon ui-icon-search");
		Span span2=new Span();
		span2.addClazz("ui-button-text");
		Text btntext=new Text("查询");
		span2.addChild(btntext);
		button.addClazz("bc-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-primary");
		button.addChild(span1);
		button.addChild(span2);
		
		//声明容器div
		Div conta=new Div();
		conta.addStyle("padding-left", "0.2em");
		Text text=new Text("~");
		
		Div div=new Div();
		div.addStyle("display", "inline-block");
		div.addStyle("width", "8px");
		
		//容器div添加内容
		conta.addChild(button);
		conta.addChild(div);
		conta.addChild(sellDate);
		conta.addChild(divS);
		conta.addChild(text);
		conta.addChild(divE);
		
		tb.addChild(conta);
		return tb;
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		return null;
	}

	@Override
	protected String[] getGridSearchFields() {
		return null;
	}

	@Override
	protected String getGridDblRowMethod() {
		return null;
	}

}
