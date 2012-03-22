/**
 * 
 */
package cn.bc.business.invoice.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.BCConstants;
import cn.bc.business.invoice.dao.Invoice4SellDao;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.business.invoice.domain.Invoice4SellDetail;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

/**
 * 票务销售Dao的hibernate jpa实现
 * 
 * @author wis
 */
public class Invoice4SellDaoImpl extends HibernateCrudJpaDao<Invoice4Sell> implements Invoice4SellDao {
	
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
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
	
	/**
	 * 指定采购日期 总采购数 (company为null即查宝城和广发所有记录)
	 * @param type
	 * @param buyDate
	 * @param company
	 * @param falg 是否包括本天
	 * @return
	 */
	public int countInvoiceBuyCountByBuyDate(Integer type, Calendar buyDate,String company,boolean flag) {
		int count = 0;
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(b.count_) buyCount from bs_invoice_buy b");
		sql.append(" where b.status_=0 and b.type_=?");
		
		if(flag){
			sql.append(" and b.buy_date<=?");
		}else{
			sql.append(" and b.buy_date<?");
		}
		
		args.add(Invoice4Buy.TYPE_PRINT);
		args.add(buyDate);
		
		if(company != null && company.length() >0){
			sql.append(" and b.company=?");
			args.add(company);
		}
		
		
		//jdbc 查询
		count = this.jdbcTemplate.queryForInt(sql.toString(), args.toArray());
		
		return count;
	}

	/**
	 * 指定采购日期范围 总采购数(company为null即查宝城和广发所有记录)
	 * @param type
	 * @param buyDateFrom
	 * @param buyDateTo
	 * @param company
	 * @return
	 */
	public int countInvoiceBuyCountByBuyDate(Integer type,
			Calendar buyDateFrom, Calendar buyDateTo,String company) {
		int count = 0;
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(b.count_) buyCount from bs_invoice_buy b")
		   .append(" where b.status_=0 and b.type_=? and b.buy_date>=? and b.buy_date<=?");
		
		args.add(Invoice4Buy.TYPE_PRINT);
		args.add(buyDateFrom);
		args.add(buyDateTo);
		
		if(company != null && company.length() >0){
			sql.append(" and b.company=?");
			args.add(company);
		}
		
		//jdbc 查询
		count = this.jdbcTemplate.queryForInt(sql.toString(),  args.toArray());
		
		return count;
	}

	/**
	 * 指定销售日期 总销售数(company为null即查宝城和广发所有记录)
	 * @param type
	 * @param SellDate
	 * @param company
	 * @return
	 */
	public int countInvoiceSellCountBySellDate(Integer type, Calendar SellDate,String company,boolean flag) {
		int count = 0;
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(d.count_) sellCount from bs_invoice_sell_detail d")
		   .append(" inner join bs_invoice_sell s on s.id=d.sell_id")
		   .append(" inner join bs_invoice_buy b on b.id=d.buy_id")
		   .append(" where s.status_=0 and b.type_=?");
		
		if(flag){
			sql.append(" and s.sell_date<=?");
		}else{
			sql.append(" and s.sell_date<?");
		}
		
		args.add(Invoice4Buy.TYPE_PRINT);
		args.add(SellDate);
		
		if(company != null && company.length() >0){
			sql.append(" and b.company=?");
			args.add(company);
		}
		
		//jdbc 查询
		count = this.jdbcTemplate.queryForInt(sql.toString(),  args.toArray());
		
		return count;
	}

	/**
	 * 指定销售日期范围  总销售数(company为null即查宝城和广发所有记录)
	 * @param type
	 * @param SellDateFrom
	 * @param SellDateTo
	 * @param company
	 * @return
	 */
	public int countInvoiceSellCountBySellDate(Integer type,
			Calendar SellDateFrom, Calendar SellDateTo,String company) {
		int count = 0;
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(d.count_) sellCount from bs_invoice_sell_detail d")
		   .append(" inner join bs_invoice_sell s on s.id=d.sell_id")
		   .append(" inner join bs_invoice_buy b on b.id=d.buy_id")
	       .append(" where s.status_=0 and b.type_=? and s.sell_date>=? and s.sell_date<=?");
		
		args.add(Invoice4Buy.TYPE_PRINT);
		args.add(SellDateFrom);
		args.add(SellDateTo);
		
		if(company != null && company.length() >0){
			sql.append(" and b.company=?");
			args.add(company);
		}
		
		//jdbc 查询
		count = this.jdbcTemplate.queryForInt(sql.toString(),  args.toArray());
		
		return count;
	}

	/**
	 * 获取销售单中公司列表
	 * 
	 * @return
	 */
	public List<Map<String, String>> findCompany4Option(){
		String sql="select company,count(*) from bs_invoice_sell  GROUP BY company";
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), sql, null,new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(Object[] rs, int rowNum) {
				Map<String, String> oi = new HashMap<String, String>();
				int i = 0;
				oi.put("value", rs[i++].toString());
				return oi;
			}
		});
	}
}