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
		return this.find4Option(new Integer[] { BCConstants.STATUS_ENABLED},1);
	}
	
	/**
	 * 获取当前可用的退票单下拉列表信息
	 * 
	 * @return 返回结果中的元素Map格式为：：id -- Invoice4Buy的id, name --
	 *         Invoice4Buy的code(startNo-endNo),如:XXXX(10001~20000)
	 */
	public List<Map<String, String>> findRefundEnabled4Option() {
		return this.find4Option(new Integer[] { BCConstants.STATUS_ENABLED},2);
	}

	//sell_type 销售类型，1-购买单下拉 2-退票单下拉,其它=全部
	public List<Map<String, String>> find4Option(Integer[] statuses,int sell_type) {
		String hql = "select b.id,b.code,b.start_no,b.end_no";
				hql += ",b.company,to_char(b.buy_date,'YYYY-MM-DD') as buyDate";
				hql += ",case b.unit_ when "+Invoice4Buy.UNIT_BEN+" then '"+Invoice4Buy.UNIT_STR_BEN;
				hql += "' when "+Invoice4Buy.UNIT_JUAN+" then '"+Invoice4Buy.UNIT_STR_JUAN;
				hql += "' else '其它' end";
				hql += ",case b.type_ when "+Invoice4Buy.TYPE_PRINT+" then '"+Invoice4Buy.TYPE_STR_PRINT;
				hql += "' when "+Invoice4Buy.TYPE_TORE+" then '"+Invoice4Buy.TYPE_STR_TORE;
				hql += "' else '其它' end";
				hql +=",get_balancecount_invoice_buyid(b.id)";
				hql +=",b.patch_no";
				hql += " from BS_INVOICE_BUY b";
		if (statuses != null && statuses.length > 0) {
			if (statuses.length == 1) {
				hql += " where b.status_ = ?";
			} else {
				hql += " where b.status_ in (";
				for (int i = 0; i < statuses.length; i++) {
					hql += (i == 0 ? "?" : ",?");
				}
				hql += ")";
			}
		}
		//可选采购单
		if(sell_type==1){
			hql += " and get_balancecount_invoice_buyid(b.id)>0";
		//可选退票单
		}else if(sell_type==2){
			hql += " and get_balancecount_invoice_buyid(b.id)<b.count_";
		}

		hql += " order by b.buy_date DESC";
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
						String buyDate = rs[i++].toString();
						String unit = rs[i++].toString();
						String type = rs[i++].toString();
						String balanceCount = rs[i++].toString();	
						Object obj_patch_no = rs[i++];
						oi.put("key", key);
						if(obj_patch_no!=null&&obj_patch_no.toString().length()>0){
							oi.put("value",code +"["+obj_patch_no.toString()+"]"+ "(" + startNo + "~" + endNo
									+ ")"+company+","+ buyDate+","+ type+",库存数量"+balanceCount+unit);
						}else
							oi.put("value",code + "(" + startNo + "~" + endNo
								+ ")"+company+","+ buyDate+","+ type+",库存数量"+balanceCount+unit);
						return oi;
					}
			});
	}
	
	public List<Map<String, String>> findOneInvoice4Buy(Long id){
		String hql = "select ib.id,ib.code,ib.start_no,ib.end_no";
		hql += ",ib.company,to_char(ib.buy_date,'YYYY-MM-DD'),ib.patch_no";
		hql += ",case ib.type_ when "+Invoice4Buy.TYPE_PRINT+" then '"+Invoice4Buy.TYPE_STR_PRINT;
		hql += "' when "+Invoice4Buy.TYPE_TORE+" then '"+Invoice4Buy.TYPE_STR_TORE;
		hql += "' else '其它' end";
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
						String buyDate = rs[i++].toString();	
						Object obj_patch_no = rs[i++];
						String type = rs[i++].toString();
						oi.put("key", key);
						if(obj_patch_no!=null&&obj_patch_no.toString().length()>0){
							oi.put("value",code +"["+obj_patch_no.toString()+"]"+ "(" + startNo + "~" + endNo
									+ ")"+company+","+ buyDate+","+ type);
						}else
							oi.put("value",code + "(" + startNo + "~" + endNo
								+ ")"+company+","+ buyDate+","+ type);
						
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
		sbSql.append(" from bs_invoice_sell_detail d");
		sbSql.append(" where d.status_=0 and d.buy_id=?");
		sbSql.append(" order by d.start_no");
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
		String sql = "select b.count_,(select sum(count_) from bs_invoice_sell_detail where buy_id=b.id and status_=";
		sql += BCConstants.STATUS_ENABLED+" and type_=1) as sell_count";
		sql += ",(select sum(count_) from bs_invoice_sell_detail where buy_id=b.id and status_=";
		sql += BCConstants.STATUS_ENABLED+" and type_=2) as refund_count";
		sql += " from bs_invoice_buy b";
		sql += " where b.id=?";
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), sql,
				new Object[] { id }, new RowMapper<String>() {
					public String mapRow(Object[] rs, int rowNum) {
						int i=0;
						//采购
						Object obj_buy_count=rs[i++];
						//销售
						Object obj_sell_count=rs[i++];
						//退票
						Object obj_refund_count=rs[i++];
						
						//采购
						int int_buy_count=Integer.valueOf(obj_buy_count.toString());
						//销售
						int int_sell_count=0;
						//退票
						int int_refund_count=0;
						
						//销售
						if(obj_sell_count!=null&&obj_sell_count.toString().length()>0){
							int_sell_count=Integer.valueOf(obj_sell_count.toString());
						}
						//退票
						if(obj_refund_count!=null&&obj_refund_count.toString().length()>0){
							int_refund_count=Integer.valueOf(obj_refund_count.toString());
						}
						
						//菜够单剩余数量等于 采购数量-销售数量+退票数量
						return String.valueOf(int_buy_count-int_sell_count+int_refund_count);
					}
				});
	}
}