/**
 * 
 */
package cn.bc.business.fee.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.contract.domain.Contract;
import cn.bc.business.fee.dao.FeeDao;
import cn.bc.business.fee.domain.Fee;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 承包费Dao的hibernate jpa实现
 * 
 * @author wis
 */
public class FeeDaoImpl extends HibernateCrudJpaDao<Fee> implements
		FeeDao {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * 根据本期的carId和feeDate查找前期的fee对象
	 * @param carId
	 * @param feeDate
	 * @return
	 */
	public Fee findb4FeeByCarIdANDFeeDate(Long carId, Calendar feeDate) {
		@SuppressWarnings("unchecked")
		List<Fee> fees = this.getJpaTemplate()
				.find("from Fee fee where fee.carId=? and fee.feeDate < ? order by fee.feeDate desc limit 1",
						new Object[] { carId, feeDate });
		if(fees.isEmpty()){
			return null;
		}else{
			return fees.get(0);
		}
	}

	/**
	 * 根据本期的carId和feeYear-1和feeMonth-1查找前期的fee对象
	 * @param carId
	 * @param b4Year
	 * @param b4Month
	 * @return
	 */
	public Fee findb4FeeByCarIdANDYearAndMonth(Long carId, int b4Year,
			int b4Month) {
		@SuppressWarnings("unchecked")
		List<Fee> fees = this
				.getJpaTemplate()
				.find("from Fee fee where fee.carId=? and fee.feeYear=? and fee.feeMonth=?",
						new Object[] { carId, b4Year, b4Month });
		if (fees.isEmpty()) {
			return null;
		} else {
			return fees.get(0);
		}
	}

	/**
	 * 检测此车辆是否存在本年本月的承包费用
	 * @param feeId
	 * @param carId
	 * @param feeYear
	 * @param feeMonth
	 * @return
	 */
	public Long checkFeeIsExist(Long feeId, Long carId, Integer feeYear,
			Integer feeMonth) {
		String sql = "select f.id as id from BS_FEE f"+ 
				" where f.status_=? and f.car_id=? and f.fee_year=? and f.fee_month=?";
		Object[] args;
		if (feeId != null) {
			sql += " and f.id!=?";
			args = new Object[] { new Integer(Contract.STATUS_NORMAL), carId,
					feeYear, feeMonth, feeId};
		} else {
			args = new Object[] { new Integer(Contract.STATUS_NORMAL), carId, 
					feeYear, feeMonth };
		}
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql,
				args);
		if (list != null && !list.isEmpty())
			return new Long(list.get(0).get("id").toString());
		else
			return null;
	}
	
	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;
		for (Serializable id : ids) {
			this.delete(id);
		}
	}

	@Override
	public void delete(Serializable pk) {
		Long feeId = (Long) pk;
		// 删除承包费的明细信息
		//this.deleteFeeDatail(feeId);
		// 删除承包费自身
		Fee fee = this.load(feeId);
		this.getJpaTemplate().remove(fee);

	}
	
//	/**
//	 * 删除承包费明细
//	 * 
//	 * @parma feeId
//	 * @return
//	 */
//	public void deleteFeeDatail(Long feeId) {
//		if (feeId != null) {
//			this.executeUpdate("delete FeeDetail where fee.id=?",
//					new Object[] { feeId });
//		}
//	}
	
}