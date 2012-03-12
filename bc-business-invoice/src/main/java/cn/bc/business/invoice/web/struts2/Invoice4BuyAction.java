/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.business.invoice.service.Invoice4BuyService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 票务采购Action
 * 
 * @author wis
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4BuyAction extends FileEntityAction<Long, Invoice4Buy> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private Invoice4BuyService invoice4BuyService;
	private OptionService optionService;
	private ActorHistoryService  actorHistoryService;
	
	public List<Map<String, String>> companyList; // 所属公司列表（宝城、广发）
	public List<Map<String, String>> typeList; // 发票类型列表（打印票、手撕票）
	public List<Map<String, String>> unitList; // 发票单位列表（卷、本）
	
	public String buyer;//购买人

	@Autowired
	public void setInvoice4BuyService(Invoice4BuyService invoice4BuyService) {
		this.setCrudService(invoice4BuyService);
		this.invoice4BuyService = invoice4BuyService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}
	
	@Autowired
	public void setActorHistoryServicec(ActorHistoryService actorHistoryService){
		this.actorHistoryService= actorHistoryService;
	}

	@Override
	public boolean isReadonly() {
		// 票务采购管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.invoice"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(550)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	protected void afterCreate(Invoice4Buy entity) {
		super.afterCreate(entity);
		entity.setBuyDate(Calendar.getInstance());
		entity.setBuyPrice(7F);
		entity.setStatus(BCConstants.STATUS_ENABLED);
	}
	
	

	@Override
	protected void afterEdit(Invoice4Buy entity) {
		super.afterEdit(entity);
		if(entity.getBuyerId()!=null){
			buyer=this.actorHistoryService.load(entity.getBuyerId()).getName();
		}
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_COMPANY,
						OptionConstants.INVOICE_TYPE,
						OptionConstants.INVOICE_UNIT
						});

		// 所属公司列表
		this.companyList =optionItems.get(OptionConstants.CAR_COMPANY);
		OptionItem.insertIfNotExist(companyList, null, getE().getCompany());
		//发票类型
		this.typeList=optionItems.get(OptionConstants.INVOICE_TYPE);
		//发票单位
		this.unitList=optionItems.get(OptionConstants.INVOICE_UNIT);
	}
	
}