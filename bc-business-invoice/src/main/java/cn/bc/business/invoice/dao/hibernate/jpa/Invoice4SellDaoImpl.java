/**
 * 
 */
package cn.bc.business.invoice.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.invoice.dao.Invoice4SellDao;
import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

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
	public List<Map<String, Object>> selectSellDetailByCode(String code) {
		List<Map<String,Object>> list = null;
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sd.id,sd.buy_id,sd.sell_id,sd.sell_id,sd.start_no,sd.end_no,sd.count_,sd.price")
		   .append(" from bs_invoice_sell_detail sd")
		   .append(" inner join bs_invoice_buy ib on sd.buy_id = sd.buy_id")
		   .append(" where ib.code=?");
		
		args.add(code);
		
		//jdbc查询BS_INVOICE_DETAIL记录
		try {
			list = this.jdbcTemplate.queryForList(sql.toString(),args);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}
		
		return list;
	}

	/**
	 * 根据发票代码查找相应的sellDetail列表(排除此sellId)
	 * @param code
	 * @param sellId
	 * @return
	 */
	public List<Map<String, Object>> selectSellDetailByCode(String code,Long sellId) {
		List<Map<String,Object>> list = null;
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select sd.id,sd.buy_id,sd.sell_id,sd.sell_id,sd.start_no,sd.end_no,sd.count_,sd.price")
		   .append(" from bs_invoice_sell_detail sd")
		   .append(" inner join bs_invoice_buy ib on sd.buy_id = sd.buy_id")
		   .append(" where ib.code=? and sd.sell_id != ?");
		
		args.add(code);
		args.add(sellId);
		
		//jdbc查询BS_INVOICE_DETAIL记录
		try {
			list = this.jdbcTemplate.queryForList(sql.toString(),args);
		} catch (EmptyResultDataAccessException e) {
			e.getStackTrace();
		}
		
		return list;
	}

}