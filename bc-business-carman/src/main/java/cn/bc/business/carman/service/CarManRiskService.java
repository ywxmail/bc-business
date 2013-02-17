/**
 * 
 */
package cn.bc.business.carman.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.core.service.CrudService;

/**
 * 司机人意险Service
 * 
 * @author dragon
 */
public interface CarManRiskService extends CrudService<CarManRisk> {
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

	/**
	 * 获取已有的保司名称列表
	 * 
	 * @return
	 */
	List<String> findRiskCompanies();
	
	/**
	 * 删除人意险中的司机信息
	 * 
	 * @param info
	 */
	void doDeleteCarMan(Map<Long,List<Long>> info);
}