/**
 * 
 */
package cn.bc.business.fee.dao;

import java.util.Calendar;

import cn.bc.business.fee.domain.Fee;
import cn.bc.core.dao.CrudDao;

/**
 * 承包费Dao
 * 
 * @author wis
 */
public interface FeeDao extends CrudDao<Fee> {

	/**
	 * 根据本期的carId和feeDate查找前期的fee对象
	 * @param carId
	 * @param feeDate
	 * @return
	 */
	Fee findb4FeeByCarIdANDFeeDate(Long carId, Calendar feeDate);

	/**
	 * 根据本期的carId和feeYear-1和feeMonth-1查找前期的fee对象
	 * @param carId
	 * @param b4Year
	 * @param b4Month
	 * @return
	 */
	Fee findb4FeeByCarIdANDYearAndMonth(Long carId, int b4Year, int b4Month);

	/**
	 * 检测此车辆是否存在本年本月的承包费用
	 * @param feeId
	 * @param carId
	 * @param feeYear
	 * @param feeMonth
	 * @return
	 */
	Long checkFeeIsExist(Long feeId, Long carId, Integer feeYear,
			Integer feeMonth);

}