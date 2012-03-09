package cn.bc.business.invoice.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.invoice.dao.Invoice4BuyDao;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.core.service.DefaultCrudService;

/**
 * 票务采购Service实现类
 * 
 * @author wis
 */
public class Invoice4BuyServiceImpl extends DefaultCrudService<Invoice4Buy> implements
		Invoice4BuyService {
	private Invoice4BuyDao invoice4BuyDao;
	
	@Autowired
	public void setInvoice4BuyDao(Invoice4BuyDao invoice4BuyDao) {
		this.invoice4BuyDao = invoice4BuyDao;
		this.setCrudDao(invoice4BuyDao);
	}

	public List<Map<String, String>> findEnabled4Option() {
		return this.invoice4BuyDao.findEnabled4Option();
	}

	public List<Invoice4Buy> selectInvoice4BuyByCode(String code) {
		return this.invoice4BuyDao.selectInvoice4BuyByCode(code);
	}
	
}