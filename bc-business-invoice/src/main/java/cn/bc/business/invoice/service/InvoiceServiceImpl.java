/**
 * 
 */
package cn.bc.business.invoice.service;

import cn.bc.business.invoice.dao.InvoiceDao;
import cn.bc.business.invoice.domain.Invoice;
import cn.bc.core.service.DefaultCrudService;

/**
 * 票务Service的实现
 * 
 * @author wis
 */
public class InvoiceServiceImpl extends DefaultCrudService<Invoice> implements
		InvoiceService {
	@SuppressWarnings("unused")
	private InvoiceDao invoiceDao;

	public void setnvoiceDao(InvoiceDao invoiceDao) {
		this.invoiceDao = invoiceDao;
		this.setCrudDao(invoiceDao);
	}

}