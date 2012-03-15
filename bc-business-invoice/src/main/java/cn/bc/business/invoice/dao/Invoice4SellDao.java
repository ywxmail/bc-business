/**
 * 
 */
package cn.bc.business.invoice.dao;

import java.util.List;

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
	 * 根据发票代码查找相应的sell4Detail列表
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
	List<Invoice4SellDetail> selectSellDetailByCode(Long buyId, Long sellId);

}