/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.invoice.domain.Invoice;
import cn.bc.business.invoice.service.InvoiceService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 票务Action
 * 
 * @author wis
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class InvoiceAction extends FileEntityAction<Long, Invoice> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private InvoiceService invoiceService;
	private OptionService optionService;

	@Autowired
	public void setInvoiceService(InvoiceService invoiceService) {
		this.setCrudService(invoiceService);
		this.invoiceService = invoiceService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 车辆管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.invoice"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(765)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	protected void afterCreate(Invoice entity) {
		super.afterCreate(entity);
		// 自动生成uid
		this.getE().setUid(this.getIdGeneratorService().next(Invoice.KEY_UID));
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

	}
	
}