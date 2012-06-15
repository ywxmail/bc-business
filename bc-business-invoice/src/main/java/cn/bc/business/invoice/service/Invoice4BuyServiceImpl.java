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

	public List<Invoice4Buy> selectInvoice4BuyByCode(String code, Long id) {
		return this.invoice4BuyDao.selectInvoice4BuyByCode(code,id);
	}
	
	public List<Map<String, String>> findCompany4Option(){
		return invoice4BuyDao.findCompany4Option();
	}

	public List<Map<String, String>> findSellDetail(Long id) {
		return invoice4BuyDao.findSellDetail(id);
	}

	public List<String> findBalanceNumberByInvoice4BuyId(Long id) {
		return invoice4BuyDao.findBalanceNumberByInvoice4BuyId(id);
	}

	public List<String> findBalanceCountByInvoice4BuyId(Long id) {
		return invoice4BuyDao.findBalanceCountByInvoice4BuyId(id);
	}

	public List<Map<String, String>> findOneInvoice4Buy(Long id) {
		return invoice4BuyDao.findOneInvoice4Buy(id);
	}

	public List<Map<String, String>> findRefundEnabled4Option() {
		return invoice4BuyDao.findRefundEnabled4Option();
	}
}