/**
 * 
 */
package cn.bc.business.invoice.dao.hibernate.jpa;

import java.text.SimpleDateFormat;
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
public class Invoice4BuyDaoImpl extends HibernateCrudJpaDao<Invoice4Buy>
		implements Invoice4BuyDao {

	private static Log logger = LogFactory.getLog(Invoice4BuyDaoImpl.class);

	/**
	 * 获取当前可用的购买单下拉列表信息
	 * 
	 * @return 返回结果中的元素Map格式为：：id -- Invoice4Buy的id, name --
	 *         Invoice4Buy的code(startNo-endNo),如:XXXX(10001~20000)
	 */
	public List<Map<String, String>> findEnabled4Option() {
		return this.find4Option(new Integer[] { BCConstants.STATUS_ENABLED });
	}

	public List<Map<String, String>> find4Option(Integer[] statuses) {
		String hql = "select ib.id,ib.code,ib.start_no,ib.end_no";
				hql += ",ib.company,ib.buy_date,ib.type_,ib.unit_";
				hql += ",getbalancecountbyinvoicebuyid(ib.id)";
				hql += " from BS_INVOICE_BUY ib";
		
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
						String key=rs[i++].toString();
						String code = rs[i++].toString();
						String startNo = rs[i++].toString();
						String endNo = rs[i++].toString();
						String company = rs[i++].toString();
						Object buy_date = rs[i++];
						String unit = rs[i++].toString();	
						String type = rs[i++].toString();
						
						//剩余数量
						String count_ = rs[i++].toString();
						//剩余数量大于0时 才return
						if(Integer.parseInt(count_)>0){
							String typeStr=null;
							String unitStr=null;
							
							oi.put("key", key);
							// 打印票
							if (Integer.parseInt(type) == Invoice4Buy.TYPE_PRINT) {
								typeStr = Invoice4Buy.TYPE_STR_PRINT;
								// 手撕票
							} else if (Integer.parseInt(type) == Invoice4Buy.TYPE_TORE) {
								typeStr = Invoice4Buy.TYPE_STR_TORE;
							}
							if(Integer.parseInt(unit) == Invoice4Buy.UNIT_JUAN){
								unitStr = Invoice4Buy.UNIT_STR_JUAN;
							}else if(Integer.parseInt(unit) == Invoice4Buy.UNIT_BEN){
								unitStr = Invoice4Buy.UNIT_STR_BEN;
							}
							SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
								String buyDateStr=dateformat.format(buy_date);
							
							String value = code + "(" + startNo + "~" + endNo
									+ ")"+company+","+ buyDateStr+","+ typeStr+",剩余数量："+count_+unitStr;
							
							oi.put("value",value);
							return oi;
						}else{
							return null;
						}
					}
				});
	}
	
	public List<Map<String, String>> findOneInvoice4Buy(Long id){
		String hql = "select ib.id,ib.code,ib.start_no,ib.end_no";
		hql += ",ib.company,ib.buy_date,ib.type_";
		hql += " from BS_INVOICE_BUY ib";
		hql += " where ib.id=?";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
		}
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,
				new Object[]{id}, new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> oi = new HashMap<String, String>();
						int i = 0;
						String key=rs[i++].toString();
						String code = rs[i++].toString();
						String startNo = rs[i++].toString();
						String endNo = rs[i++].toString();
						String company = rs[i++].toString();
						Object buy_date = rs[i++];	
						String type = rs[i++].toString();
					
						String typeStr=null;
						oi.put("key", key);
						// 打印票
						if (Integer.parseInt(type) == Invoice4Buy.TYPE_PRINT) {
							typeStr = Invoice4Buy.TYPE_STR_PRINT;
							// 手撕票
						} else if (Integer.parseInt(type) == Invoice4Buy.TYPE_TORE) {
							typeStr = Invoice4Buy.TYPE_STR_TORE;
						}
						SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
							String buyDateStr=dateformat.format(buy_date);
						
						String value = code + "(" + startNo + "~" + endNo
								+ ")"+company+","+ buyDateStr+","+ typeStr;
						
						oi.put("value",value);
						return oi;
					}
				});
	}

	/**
	 * 通过发票代码查找对应可用的采购单列表
	 * 
	 * @param code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Invoice4Buy> selectInvoice4BuyByCode(String code) {
		return this.getJpaTemplate().find(
				"from Invoice4Buy where code=? and status=? order by startNo",
				new Object[] { code, BCConstants.STATUS_ENABLED });
	}

	/**
	 * 通过发票代码查找对应可用的采购单列表(不包含自己本身)
	 * 
	 * @param code
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Invoice4Buy> selectInvoice4BuyByCode(String code, Long id) {
		return this
				.getJpaTemplate()
				.find("from Invoice4Buy where code=? and id != ? and status=? order by startNo",
						new Object[] { code, id, BCConstants.STATUS_ENABLED });
	}

	/**
	 * 获取采购单中公司列表
	 * 
	 * @return
	 */
	public List<Map<String, String>> findCompany4Option() {
		String sql = "select company,count(*) from bs_invoice_buy  GROUP BY company";
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), sql,
				null, new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> oi = new HashMap<String, String>();
						int i = 0;
						oi.put("value", rs[i++].toString());
						return oi;
					}
				});
	}

	/**
	 * 通过采购单ID，获取指定的销售明细开始结束号
	 * 
	 * @return
	 */
	public List<Map<String, String>> findSellDetail(Long id) {
		StringBuffer sbSql = new StringBuffer("select d.start_no,d.end_no");
		sbSql.append(" from bs_invoice_buy b");
		sbSql.append(" left join bs_invoice_sell_detail d on d.buy_id=b.id");
		sbSql.append(" left join bs_invoice_sell s on s.id=d.sell_id and s.status_=0");
		sbSql.append(" where b.id=?");
		String sql = sbSql.toString();
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), sql,
				new Object[] { id }, new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> oi = null;
						int i = 0;
						Object a = rs[i++];
						Object b = rs[i++];
						if (a != null && !a.equals("") && b != null
								&& !b.equals("")) {
							oi = new HashMap<String, String>();
							oi.put("startNo", a.toString());
							oi.put("endNo", b.toString());
						}
						return oi;
					}
				});
	}

	public List<String> findBalanceNumberByInvoice4BuyId(Long id) {
		String sql = "select getbalancenumberbyinvoicebuyid(b.id,b.count_,b.start_no,b.end_no),1";
		sql += " from bs_invoice_buy b";
		sql += " where b.id=?";
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), sql,
				new Object[] { id }, new RowMapper<String>() {
					public String mapRow(Object[] rs, int rowNum) {
						return rs[0].toString();
					}
				});
	}
	
	public List<String> findBalanceCountByInvoice4BuyId(Long id){
		String sql = "select getbalancecountbyinvoicebuyid(b.id),1";
		sql += " from bs_invoice_buy b";
		sql += " where b.id=?";
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), sql,
				new Object[] { id }, new RowMapper<String>() {
					public String mapRow(Object[] rs, int rowNum) {
						return rs[0].toString();
					}
				});
	}
}