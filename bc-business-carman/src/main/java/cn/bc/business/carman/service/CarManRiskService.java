/**
 * 
 */
package cn.bc.business.carman.service;

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
}