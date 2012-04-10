package cn.bc.business.ownership.service;

import java.util.Map;

import cn.bc.business.ownership.domain.Ownership;
import cn.bc.core.service.CrudService;

/**
 * @author zxr 车辆经营权serviec
 * 
 */
public interface OwnershipService extends CrudService<Ownership> {
	/**
	 * 根据车辆ID获取车辆经营权对象
	 * 
	 * @param carId
	 * @return
	 */
	Ownership getEByCarId(Long carId);

	/**
	 * @param ownershipInfo
	 *            修改以营权信息
	 * @param carIds
	 *            车辆的Id
	 */
	void saveBatchTaxis(Map<String, Object> ownershipInfo, Long[] carIds);

}
