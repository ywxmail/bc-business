/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.invoice.service.Invoice4BuyService;
import cn.bc.business.invoice.service.Invoice4SellService;

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

	

	public List<Map<String, String>> typeList; // 发票类型列表（打印票、手撕票）

	@Autowired
	public void setInvoice4BuyService(Invoice4SellService invoice4SellService) {
		this.invoice4SellService = invoice4SellService;
	}

	@Autowired
	public void setInvoice4BuyService(Invoice4BuyService invoice4BuyService) {
		this.invoice4BuyService = invoice4BuyService;
	}

	
}