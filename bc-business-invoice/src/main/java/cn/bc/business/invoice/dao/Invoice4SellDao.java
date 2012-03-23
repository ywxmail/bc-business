/**
 * 
 */
package cn.bc.business.invoice.dao;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.business.invoice.domain.Invoice4SellDetail;
import cn.bc.core.dao.CrudDao;

/**
 * 票务销售Dao
 * 
 * @author wis
 */
public interface Invoice4SellDao extends CrudDao<Invoice4Sell> {

	
	/**
	 * 根据采购单id查找相应的销售明细信息
	 * @param 
	 * @return
	 */
	List<Map<String, String>> selectListSellDetailByCode(Long buyId);
	
	
	/**
	 * 根据采购单id查找相应的sell4Detail列表
	 * @param 
	 * @return
	 */
	List<Invoice4SellDetail> selectSellDetailByCode(Long buyId);

	/**
	 * 根据采购单id查找相应的sellDetail列表(排除此sellId)
	 * @param 
	 * @param sellId
	 * @return
	 */
	List<Invoice4SellDetail> selectSellDetailByCode(Long buyId, Long sellId);
	
	
	/**
	 * 指定采购日期 总采购数 (company为null即查宝城和广发所有记录)
	 * @param type
	 * @param buyDate
	 * @param company
	 * @param falg 是否包含本田
	 * @return
	 */
	int countInvoiceBuyCountByBuyDate(Integer type,Calendar buyDate,String company,boolean flag);
	
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
	 * @param falg 是否包含本田
	 * @return
	 */
	int countInvoiceSellCountBySellDate(Integer type,Calendar SellDate, String company,boolean flag);
	
	/**
	 * 指定销售日期范围  总销售数(company为null即查宝城和广发所有记录)
	 * @param type
	 * @param SellDateFrom
	 * @param SellDateTo
	 * @param company
	 * @return
	 */
	int countInvoiceSellCountBySellDate(Integer type,Calendar SellDateFrom, Calendar SellDateTo, String company);
	
	/**
	 * 获取销售单中公司列表
	 * 
	 * @return
	 */
	public List<Map<String, String>> findCompany4Option();
}