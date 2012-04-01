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
public class Invoice4SellStatssAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 票务的状态，多个用逗号连接

	private final static Log logger = LogFactory
			.getLog("cn.bc.business.invoice.Invoice4SellStatssAction");
	
	public String startDate;
	public String endDate;
	public Integer type;
	
	@Override
	public String list() throws Exception {
		Date startTime = new Date();
		// 根据请求的条件查找信息
		this.es = this.findList();

		// 构建页面的html
		this.html = buildHtmlPage();

		logger.info("list耗时：" + DateUtils.getWasteTime(startTime));
		// 返回全局的global-results：在cn/bc/web/struts2/struts.xml中定义的
		return "page";
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
			if(this.startDate!=null){
				sql.append(" and s.sell_date>=");
				sql.append("'");
				sql.append(this.startDate);
				sql.append("'");
			}
			if(this.endDate!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
				try {
					cal.setTime(sdf.parse(this.endDate));
					sql.append(" and s.sell_date<");
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
				getText("invoice4SellStats.amount")));
		return columns;
	}


	@Override
	protected String getFormActionName() {
		return "invoice4SellStats";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(550).setMinWidth(500)
				.setHeight(300).setMinHeight(200);
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace()+"/invoice4SellStats/select.js";
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
		Div conta=new Div();
		conta.addStyle("padding-left", "8px");
		//加入销售日期
		Text text=new Text(getText("invoice4SellStats.selldate"));
		conta.addChild(text);
		
		Div divS=new Div();
		divS.addClazz("bc-dateContainer");
		
		Ul ul=new Ul();
		ul.addClazz("inputIcons");
		Li lis=new Li();
		lis.addClazz("selectCalendar inputIcon ui-icon ui-icon-calendar");
		Li lic=new Li();
		lic.addClazz("clearSelect inputIcon ui-icon ui-icon-close");
		lic.setTitle("点击清除");
		ul.addChild(lis);
		ul.addChild(lic);
		
		Input inputS=new Input();
		inputS.setType("text");
		inputS.addClazz("bc-date ui-widget-content hasDatepicker");
		inputS.setAttr("data-validate", "date");
		inputS.addStyle("width", "9em");
		
		//加入startDate
		inputS.setId("i4SellStatsStartDateId");
		divS.addChild(inputS);
		divS.addChild(ul);
		conta.addChild(divS);
		
		text=new Text("~");
		conta.addChild(text);
		
		Div divE=new Div();
		divE.addClazz("bc-dateContainer");
		
		Input inputE=new Input();
		inputE.setType("text");
		inputE.addClazz("bc-date ui-widget-content hasDatepicker");
		inputE.setAttr("data-validate", "date");
		inputE.addStyle("width", "9em");
		
		//加入endDate
		inputE.setId("i4SellStatsEndDateId");
		divE.addChild(inputE);
		divE.addChild(ul);
		conta.addChild(divE);
		
		//加入查询按钮
		Div contad=new Div();
		contad.addStyle("padding-left", "8px");
		contad.addClazz("bc-dateContainer");
		Button button=new Button();
		button.setClick("bs.invoice4SellStatsWindow.onClick");
		Span span1=new Span();
		span1.addClazz("ui-button-icon-primary ui-icon ui-icon-search");
		Span span2=new Span();
		span2.addClazz("ui-button-text");
		text=new Text("查询");
		span2.addChild(text);
		button.addClazz("bc-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-primary");
		button.addChild(span1);
		button.addChild(span2);
		//button加入事件
		
		
		contad.addChild(button);
		conta.addChild(contad);
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
