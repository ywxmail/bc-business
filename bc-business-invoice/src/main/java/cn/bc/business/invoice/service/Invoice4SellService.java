/**
 * 
 */
package cn.bc.business.invoice.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.core.service.CrudService;

/**
 * 票务销售Service
 * 
 * @author wis
 */
public interface Invoice4SellService extends CrudService<Invoice4Sell> {

	/**
	 * 根据发票代码查找相应的sell4Detail列表
	 * @param code
	 * @return
	 */
	List<Map<String, Object>> selectSellDetailByCode(String code);
	
	/**
	 * 根据发票代码查找相应的sellDetail列表(排除此sellId)
	 * @param code
	 * @param sellId
	 * @return
	 */
	List<Map<String, Object>> selectSellDetailByCode(String code,Long sellId);
	
}