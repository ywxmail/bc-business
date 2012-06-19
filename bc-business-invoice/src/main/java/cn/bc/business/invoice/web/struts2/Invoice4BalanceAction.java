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
import cn.bc.web.formater.NubmerFormater;
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
		Calendar endDate = this.endDate;
		endDate.add(Calendar.DAY_OF_MONTH, 1);
		// 指定日期前采购总数；
		int sbuy = invoice4SellService.countInvoiceBuyCountByBuyDate(type,
				startDate, company, false);
		int ebuy = invoice4SellService.countInvoiceBuyCountByBuyDate(type,
				endDate, company, true);
		// 指定日期范围采购总数
		int buy4r = invoice4SellService.countInvoiceBuyCountByBuyDate(type,
				startDate, endDate, company);

		// 指定日期前销售总数；
		int ssell = invoice4SellService.countInvoiceSellCountBySellDate(type,
				startDate, company, false);
		int esell = invoice4SellService.countInvoiceSellCountBySellDate(type,
				endDate, company, true);
		// 指定日期范围销售总数；
		int sell4r = invoice4SellService.countInvoiceSellCountBySellDate(type,
				startDate, endDate, company);

		// 指定日期前退票总数；
		int srefund = invoice4SellService.countInvoiceRefundCountBySellDate(
				type, startDate, company, false);
		int erefund = invoice4SellService.countInvoiceRefundCountBySellDate(
				type, endDate, company, true);
		// 指定日期范围退票总数；
		int refund4r = invoice4SellService.countInvoiceRefundCountBySellDate(
				type, startDate, endDate, company);
		NubmerFormater nf = new NubmerFormater("###,##0");
		Json json = new Json();
		json.put("startCount", nf.format(sbuy - ssell + srefund));
		json.put("buyCount", nf.format(buy4r + refund4r));
		json.put("sellCount", nf.format(sell4r));
		json.put("endCount", nf.format(ebuy - esell + erefund));

		this.json = json.toString();
		return "json";
	}

}