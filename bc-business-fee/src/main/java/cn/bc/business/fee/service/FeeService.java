/**
 * 
 */
package cn.bc.business.fee.service;

import java.util.Calendar;

import cn.bc.business.fee.domain.Fee;
import cn.bc.core.service.CrudService;

/**
 * 承包费Service
 * 
 * @author wis
 */
public interface FeeService extends CrudService<Fee> {

	/**
	 * 根据本期的carId和feeDate查找前期的fee对象
	 * @param carId
	 * @param feeDate
	 * @return
	 */
	Fee findb4FeeByCarIdANDFeeDate(Long carId, Calendar feeDate);

	/**
	 * 根据本期的carId和feeYear和feeMonth查找前期的fee对象
	 * @param carId
	 * @param feeYear
	 * @param feeMonth
	 * @return
	 */
	Fee findb4FeeByCarIdANDYearAndMonth(Long carId, Integer feeYear,
			Integer feeMonth);

	/**
	 * 检测此车辆是否存在本年本月的承包费用
	 * @param feeId
	 * @param carId
	 * @param feeYear
	 * @param feeMonth
	 * @return
	 */
	Long checkFeeIsExist(Long feeId, Long carId, Integer feeYear, Integer feeMonth);

	/**
	 * 批量保存承包费车辆
	 * @param carIds
	 * @param carPlates
	 * @param feeYear
	 * @param feeMonth
	 */
	void saveBatchInit(Long[] carIds,String[] carPlates, Integer feeYear, Integer feeMonth);

}