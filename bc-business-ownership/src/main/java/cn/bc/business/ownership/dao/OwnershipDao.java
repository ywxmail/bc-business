package cn.bc.business.ownership.dao;

import java.util.Map;

import cn.bc.business.ownership.domain.Ownership;
import cn.bc.core.dao.CrudDao;

/**
 * @author zxr 车辆经营权dao
 */
public interface OwnershipDao extends CrudDao<Ownership> {

	/**
	 * 根据车辆ID获取车辆经营权对象
	 * 
	 * @param carId
	 * @return
	 */
	Ownership getEntityByCarid(Long carId);

	/**
	 * 批量修改车辆经营权方法
	 * 
	 * @param ownershipInfo
	 *            修改的经营权记录
	 * @param carIds
	 *            车辆id
	 */
	void updateOwnershipByCarId(Map<String, Object> ownershipInfo, Long[] carIds);

}
