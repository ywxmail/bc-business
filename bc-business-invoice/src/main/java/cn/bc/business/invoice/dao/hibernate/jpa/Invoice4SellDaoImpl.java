/**
 * 
 */
package cn.bc.business.invoice.dao.hibernate.jpa;

import java.util.List;

import cn.bc.BCConstants;
import cn.bc.business.invoice.dao.Invoice4SellDao;
import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.business.invoice.domain.Invoice4SellDetail;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 票务销售Dao的hibernate jpa实现
 * 
 * @author wis
 */
public class Invoice4SellDaoImpl extends HibernateCrudJpaDao<Invoice4Sell> implements Invoice4SellDao {
	
	/**
	 * 根据发票代码查找相应的sell4Detail列表
	 * @param code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Invoice4SellDetail> selectSellDetailByCode(Long buyId) {
		return this.getJpaTemplate().find(
				"from Invoice4SellDetail where buyId=? and invoice4Sell.status=? order by startNo", 
				new Object[] {buyId,BCConstants.STATUS_ENABLED});
		
	}

	/**
	 * 根据发票代码查找相应的sellDetail列表(排除此sellId)
	 * @param code
	 * @param sellId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Invoice4SellDetail> selectSellDetailByCode(Long buyId,Long sellId) {
		return this.getJpaTemplate().find(
				"from Invoice4SellDetail where buyId=? and invoice4Sell.id!=? and invoice4Sell.status=? order by startNo", 
				new Object[] {buyId,sellId,BCConstants.STATUS_ENABLED});
	}

}