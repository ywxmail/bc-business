/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.business.invoice.service.Invoice4BuyService;
import cn.bc.business.invoice.service.Invoice4SellService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 票据余额Action
 * 
 * @author wis
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4BalanceAction extends ActionSupport {
	protected static final Log logger = LogFactory
			.getLog(Invoice4BalanceAction.class);
	private static final long serialVersionUID = 1L;
	private Invoice4SellService invoice4SellService;
	private Invoice4BuyService invoice4BuyService;
	public PageOption pageOption;

	public List<Map<String, String>> typeList; // 发票类型列表（打印票、手撕票）
	public List<Map<String, String>> companyList; // 公司

	@Autowired
	public void setInvoice4BuyService(Invoice4SellService invoice4SellService) {
		this.invoice4SellService = invoice4SellService;
	}

	@Autowired
	public void setInvoice4BuyService(Invoice4BuyService invoice4BuyService) {
		this.invoice4BuyService = invoice4BuyService;
	}

	public Calendar startDate;
	public Calendar endDate;

	//public String startCount;// 期初数量
	//public String endCount;// 剩余数量
	//public String buyCount;// 收入数量
	//public String sellCount;// 发出数量

	@Override
	public String execute() throws Exception {
		// 设置页面
		this.pageOption = new PageOption()
				.setMinWidth(200)
				.setWidth(580)
				.setMinHeight(200)
				.setHeight(340)
				.addButton(
						new ButtonOption(getText("invoice.optype.select"),
								null, "bs.invoice4BalanceForm.select"));

		// 设置日期
		this.startDate = Calendar.getInstance();
		this.endDate = Calendar.getInstance();

		// 发票类型
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.TYPE_PRINT),
				getText("invoice.type.dayinpiao")));
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.TYPE_TORE),
				getText("invoice.type.shousipiao")));
		this.typeList = list;

		// 公司
		this.companyList = this.invoice4BuyService.findCompany4Option();
	return super.execute();
	}

	/**
	 * 生成OptiomItem key、value值
	 */
	private Map<String, String> getOptiomItems(String key, String value) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", key);
		map.put("value", value);
		return map;
	}

	public String json;// ajax返回的json信息
	public Integer type;// 发票类型
	public String company;// 公司

	public String select() {
		Calendar endDate=this.endDate;
		endDate.add(Calendar.DAY_OF_MONTH, 1);
		Json json = new Json();
		json.put("startCount", String.valueOf(this.invoice4SellService
				.countInvoiceBuyCountByBuyDate(this.type, this.startDate,
						this.company, false)
				- this.invoice4SellService.countInvoiceSellCountBySellDate(
						this.type, this.startDate, this.company, false)));
		json.put("buyCount", String.valueOf(this.invoice4SellService
				.countInvoiceBuyCountByBuyDate(this.type, this.startDate,
						endDate, this.company)));
		json.put("sellCount", String.valueOf(this.invoice4SellService
				.countInvoiceSellCountBySellDate(this.type, this.startDate,
						endDate, this.company)));
		json.put("endCount", String.valueOf(this.invoice4SellService
				.countInvoiceBuyCountByBuyDate(this.type, this.endDate,
						this.company, true)
				- this.invoice4SellService.countInvoiceSellCountBySellDate(
						this.type, endDate, this.company, true)));

		this.json = json.toString();
		return "json";
	}

}