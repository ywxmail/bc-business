/**
 * 
 */
package cn.bc.business.carman.dao;

import java.util.List;

import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.cert.domain.Cert;
import cn.bc.core.dao.CrudDao;

/**
 * 司机责任人Dao
 * 
 * @author dragon
 */
public interface CarManDao extends CrudDao<CarMan> {
	/**
	 * 为司机责任人添加一个证件
	 * 
	 * @param carManId
	 * @param cert
	 * @return
	 */

	CarMan saveCert4CarMan(Long carManId, Cert cert);

	/**
	 * 为司机责任人添加一个证件关联
	 * 
	 * @param carManId
	 * @param certId
	 * @return
	 */
	CarMan saveCertRelationship(Long carManId, Long certId);

	/**
	 * 根据车辆ID查找返回状态为启用中相关司机信息
	 * 
	 * @parma id
	 * @return
	 */
	List<CarMan> findAllcarManBycarId(Long id);

	/**
	 * 判断服务资格证是否已经被占用
	 * 
	 * @param excludeId
	 *            要排除检测的id
	 * @param cert4FWZG
	 *            服务资格证
	 * @return 如果服务资格证被占用，返回占用此服务资格证的CarManId，否则返回null
	 */
	Long checkCert4FWZGIsExists(Long excludeId, String cert4FWZG);

	/**
	 * 获取司机的最新营运车辆
	 * 
	 * @param driverId
	 *            司机Id
	 * @return
	 */
	String getNewestCarInfo4Driver(Long driverId);

	/**
	 * 更新司机的电话号码
	 * 
	 * @param carManId
	 *            司机Id
	 * @param phone1
	 *            电话1
	 * @param phone2
	 *            电话2
	 */
	void updatePhoneBycarManId(Long carManId, String phone1, String phone2);

	/**
	 * 获取司机的信息
	 * 
	 * @param carManId
	 *            司机id
	 * @return
	 */
	String getCarManInfoByCarManId(Long carManId);
}