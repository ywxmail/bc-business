/**
 * 
 */
package cn.bc.business.carman.dao;

import java.util.Calendar;
import java.util.Map;

import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.core.dao.CrudDao;

/**
 * 司机人意险Dao
 * 
 * @author dragon
 */
public interface CarManRiskDao extends CrudDao<CarManRisk> {
	/**
	 * 根据身份证号获取司机的相关信息
	 * 
	 * @param identity
	 * @return Keys:[id,name,identity]
	 */
	Map<String, Object> getCarManInfo(String identity);

	/**
	 * 通过保险单号查找保单
	 * 
	 * @param code
	 * @return
	 */
	CarManRisk loadByCode(String code);

	/**
	 * 通过保险公司、有效期限查找保单
	 * 
	 * @param company
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	CarManRisk loadByCompanyAndDate(String company, Calendar startDate,
			Calendar endDate);
}