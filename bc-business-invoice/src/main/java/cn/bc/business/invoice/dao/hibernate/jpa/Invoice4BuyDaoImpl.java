/**
 * 
 */
package cn.bc.business.invoice.dao.hibernate.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.BCConstants;
import cn.bc.business.invoice.dao.Invoice4BuyDao;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

/**
 * 票务采购Dao的hibernate jpa实现
 * 
 * @author wis
 */
public class Invoice4BuyDaoImpl extends HibernateCrudJpaDao<Invoice4Buy> implements Invoice4BuyDao {

	private static Log logger = LogFactory.getLog(Invoice4BuyDaoImpl.class);
	
	/**
	 * 获取当前可用的购买单下拉列表信息
	 * 
	 * @return 返回结果中的元素Map格式为：：id -- Invoice4Buy的id,
	 * name -- Invoice4Buy的code(startNo-endNo),如:XXXX(10001~20000)
	 */
	public List<Map<String, String>> findEnabled4Option() {
		return this.find4Option(new Integer[] { BCConstants.STATUS_ENABLED });
	}
	
	public List<Map<String, String>> find4Option(Integer[] statuses) {
		String hql = "select ib.id,ib.code,ib.start_no,ib.end_no from BS_INVOICE_BUY ib";
		if (statuses != null && statuses.length > 0) {
			if (statuses.length == 1) {
				hql += " where ib.status_ = ?";
			} else {
				hql += " where ib.status_ in (";
				for (int i = 0; i < statuses.length; i++) {
					hql += (i == 0 ? "?" : ",?");
				}
				hql += ")";
			}
		}
		hql += " order by ib.buy_date DESC";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
		}
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,
				statuses, new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> oi = new HashMap<String, String>();
						int i = 0;
						oi.put("key", rs[i++].toString());
						oi.put("value", rs[i++]+"("+
						rs[i++]+"~"+rs[i++]+")");
						return oi;
					}
				});
	}

	/**
	 * 通过发票代码查找对应可用的采购单列表
	 * @param code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Invoice4Buy> selectInvoice4BuyByCode(String code) {
		return this.getJpaTemplate().find(
				"from Invoice4Buy where code=? and status=? order by startNo", 
				new Object[] {code,BCConstants.STATUS_ENABLED});
	}

	/**
	 * 通过发票代码查找对应可用的采购单列表(不包含自己本身)
	 * @param code
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Invoice4Buy> selectInvoice4BuyByCode(String code, Long id) {
		return this.getJpaTemplate().find(
				"from Invoice4Buy where code=? and id != ? and status=? order by startNo",
				new Object[] { code,id,BCConstants.STATUS_ENABLED});
	}

}