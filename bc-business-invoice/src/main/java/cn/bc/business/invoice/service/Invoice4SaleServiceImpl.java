package cn.bc.business.invoice.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.invoice.dao.Invoice4SaleDao;
import cn.bc.business.invoice.domain.Invoice4Sale;
import cn.bc.core.service.DefaultCrudService;

/**
 * 票务销售Service实现类
 * 
 * @author wis
 */
public class Invoice4SaleServiceImpl extends DefaultCrudService<Invoice4Sale> implements
		Invoice4SaleService {
	@SuppressWarnings("unused")
	private Invoice4SaleDao invoice4SaleDao;
	
	@Autowired
	public void setInvoice4SaleDao(Invoice4SaleDao invoice4SaleDao) {
		this.invoice4SaleDao = invoice4SaleDao;
		this.setCrudDao(invoice4SaleDao);
	}

}