/**
 * 
 */
package cn.bc.business.invoice.service;

import java.util.Calendar;
import java.util.List;

import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.business.invoice.domain.Invoice4SellDetail;
import cn.bc.core.service.CrudService;

/**
 * 票务销售Service
 * 
 * @author wis
 */
public interface Invoice4SellService extends CrudService<Invoice4Sell> {

	/**
	 * 根据采购ID查找相应的sell4Detail列表
	 * @param code
	 * @return
	 */
	List<Invoice4SellDetail> selectSellDetailByCode(Long buyId);
	
	/**
	 * 根据发票代码查找相应的sellDetail列表(排除此sellId)
	 * @param code
	 * @param sellId
	 * @return
	 */
	List<Invoice4SellDetail> selectSellDetailByCode(Long buyId,Long sellId);
	
	/**
	 * 指定采购日期 总采购数 (company为null即查宝城和广发所有记录)
	 * @param type
	 * @param buyDate
	 * @param company
	 * @return
	 */
	int countInvoiceBuyCountByBuyDate(Integer type,Calendar buyDate,String company);
	
	/**
	 * 指定采购日期范围 总采购数(company为null即查宝城和广发所有记录)
	 * @param type
	 * @param buyDateFrom
	 * @param buyDateTo
	 * @param company
	 * @return
	 */
	int countInvoiceBuyCountByBuyDate(Integer type,Calendar buyDateFrom, Calendar buyDateTo, String company);
	
	/**
	 * 指定销售日期 总销售数(company为null即查宝城和广发所有记录)
	 * @param type
	 * @param SellDate
	 * @param company
	 * @return
	 */
	int countInvoiceSellCountBySellDate(Integer type,Calendar SellDate, String company);
	
	/**
	 * 指定销售日期范围  总销售数(company为null即查宝城和广发所有记录)
	 * @param type
	 * @param SellDateFrom
	 * @param SellDateTo
	 * @param company
	 * @return
	 */
	int countInvoiceSellCountBySellDate(Integer type,Calendar SellDateFrom, Calendar SellDateTo, String company);
	
}