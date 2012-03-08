package cn.bc.business.invoice.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.invoice.dao.Invoice4SellDao;
import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.core.service.DefaultCrudService;

/**
 * 票务销售Service实现类
 * 
 * @author wis
 */
public class Invoice4SellServiceImpl extends DefaultCrudService<Invoice4Sell> implements
		Invoice4SellService {
	@SuppressWarnings("unused")
	private Invoice4SellDao invoice4SellDao;
	
	@Autowired
	public void setInvoice4SellDao(Invoice4SellDao invoice4SellDao) {
		this.invoice4SellDao = invoice4SellDao;
		this.setCrudDao(invoice4SellDao);
	}

}