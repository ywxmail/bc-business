/**
 * 
 */
package cn.bc.business.carman.dao;

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
}