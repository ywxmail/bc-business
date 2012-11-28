package cn.bc.business.ownership.dao;

import java.util.ArrayList;
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

	/**
	 * 查找出需要更新车辆经营权信息的车辆ID
	 * 
	 * @param carIds
	 *            批量处理的车辆ID
	 * @return
	 */
	ArrayList<Object> getUpdateCarIdsList(Long[] carIds);

	/**
	 * 根据经营权证号查找经营权
	 * 
	 * @param number
	 *            经营权证号
	 * @return
	 */
	Ownership getOwershipByNumber(String number);

	/**
	 * 更新经营权号已配车的车辆产权字段
	 * 
	 * @param owner
	 *            产权
	 * 
	 * @param number经营权号
	 */
	void updateCar4OwnerByNumber(String owner, String number);

}
