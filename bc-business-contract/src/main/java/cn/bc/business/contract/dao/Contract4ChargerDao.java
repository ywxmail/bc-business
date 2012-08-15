/**
 * 
 */
package cn.bc.business.contract.dao;

import java.util.List;
import java.util.Map;

import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.core.Page;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.condition.Condition;

/**
 * 责任人合同Dao
 * 
 * @author dragon
 */
public interface Contract4ChargerDao extends CrudDao<Contract4Charger> {

	/**
	 * 删除单个CarNContract
	 * 
	 * @parma contractId
	 * @return
	 */
	void deleteCarNContract(Long contractId);

	/**
	 * 删除批量CarNContract
	 * 
	 * @parma contractId
	 * @return
	 */
	void deleteCarNContract(Long[] contractIds);

	/**
	 * 保存合同与车辆的关联表信息
	 * 
	 * @parma carId
	 * @parma contractId
	 * @return
	 */
	void carNContract4Save(Long carId, Long contractId);

	/**
	 * 查找车辆合同列表
	 * 
	 * @parma condition
	 * @parma carId
	 * @return
	 */
	List<Map<String, Object>> list4car(Condition condition, Long carId);

	/**
	 * 查找车辆合同分页
	 * 
	 * @parma condition
	 * @parma carId
	 * @return
	 */
	Page<Map<String, Object>> page4car(Condition condition, int pageNo,
			int pageSize);

	/**
	 * 根据contractId查找car信息
	 * 
	 * @parma contractId
	 * @return
	 */
	Map<String, Object> findCarInfoByContractId(Long contractId);

	/**
	 * 根据contractId查找car信息
	 * 
	 * @parma contractId
	 * @return
	 */
	List<Map<String, String>> findCarByContractId(Long contractId);

	/**
	 * 根据contractId查找car信息
	 * 
	 * @parma contractId
	 * @return
	 */
	List<String> findChargerIdByContractId(Long contractId);

	/**
	 * 根据责任人ID和合同ID.保存到人员与合同中间表,不存在插入新纪录,存在删除.重新插入
	 * 
	 * @param assignChargerIds
	 * @param contractId
	 */
	void carMansNContract4Save(String assignChargerIds, Long contractId);

	/**
	 * 根据合同ID查找关联责任人
	 * 
	 * @param contractId
	 * @return
	 */
	public Long findCarIdByContractId(Long contractId);

	/**
	 * 更新车辆表的负责人信息
	 * 
	 * @param assignChargerNames
	 * @param carId
	 */
	void updateCar4dirverName(String assignChargerNames, Long carId);

	/**
	 * JDBC 更新司机表的负责人信息
	 * 
	 * @param assignChargerNames
	 * @param carId
	 */
	void updateCarMan4dirverName(String assignChargerNames, Long carId);

	/**
	 * JDBC 根据车辆ID查找车辆信息
	 * 
	 * @param carId
	 * @return
	 */
	Map<String, Object> findCarByCarId(Long carId);

	/**
	 * 根据司机ID查找车辆信息
	 * 
	 * @param carManId
	 * @return
	 */
	Map<String, Object> findCarByCarManId(Long carManId);

	/**
	 * 根据车辆Id查找与车辆相关未入库的迁移记录
	 * 
	 * @param carId
	 * @return
	 */
	List<Map<String, Object>> findDraftCarByDriverHistoryByCarId(Long carId);

	/**
	 * 更新司机表的负责人信息
	 * 
	 * @param assignChargerNames
	 * @param carId
	 */
	void updateCar4ChargerName(String assignChargerNames, Long carId);

	/**
	 * 更新司机表的负责人信息
	 * 
	 * @param assignChargerNames
	 * @param carId
	 */
	void updateCarMan4ChargerName(String assignChargerNames, Long carId);

	/** 判断指定的车辆是否已经存在经济合同 */
	boolean isExistContract(Long carId);

	/**
	 * 更新车辆表的负责人信息(调用存储过程)
	 * 
	 * @param carId
	 */
	void updateCar4ChargerName(Long carId);

	/**
	 * 更新司机表的负责人信息(调用存储过程)
	 * 
	 * @param carId
	 */
	void updateCarMan4ChargerName(Long carId);

	/**
	 * 根据合同ID查找关联责任人姓名
	 * 
	 * @param contractId
	 * @return
	 */
	List<String> findChargerNameByContractId(Long contractId);

	/**
	 * 判断经济合同自编号唯一
	 * 
	 * @param excludeId
	 * @param code
	 * @return
	 */
	Long checkCodeIsExist(Long excludeId, String code);

	/**
	 * 获取去责任人信息
	 * 
	 * @param contractId
	 *            合同id
	 * @return
	 */
	List<Map<String, String>> findChargerByContractId(Long contractId);

	/**
	 * 根据司机ID获取司机的状态和姓名
	 * 
	 * @param carManId司机id
	 */
	Map<String, Object> getCarManInfoByCarManId(Long carManId);

	/**
	 * 获取去正班司机信息
	 * 
	 * @param contractId
	 *            合同id
	 * @return
	 */
	List<Map<String, String>> findDriverByContractId(Long contractId);

	/**
	 * 更新车辆的合同性质
	 * 
	 * @param businessType
	 *            经济合同的合同性质值
	 * @param carId
	 *            车辆Id
	 */
	void updateCarWithbusinessType(String businessType, Long carId);

	/**
	 * 根据车辆Id获取在案的正副班司机数量排除迁移类型为交回未注销，注销未有去向， 公司到公司草稿状态迁移记录的司机
	 * 
	 * @param carId
	 *            车辆Id
	 * @return
	 */
	int getDriverAmount(Long carId);

	/**
	 * 将车辆状态更新为在案
	 * 
	 * @param draftCarId
	 *            车辆Id
	 */
	void doWarehous4Car(Long draftCarId);

	/**
	 * 将司机的状态更新为在案
	 * 
	 * @param draftCarManId
	 *            司机Id
	 */
	void doWarehous4CarMan(Long draftCarManId);

	/**
	 * JDBC 根据合同ID查找顶班司机(主挂司机的信息)
	 * 
	 * @param contractId
	 * @return
	 */
	Map<String, Object> findShiftworkInfoByContractId(Long contractId);

	/**
	 * 根据合同获取合同车辆在案的司机和责任人
	 * 
	 * @param contractId
	 * @return
	 */
	List<Map<String, String>> getNormalChargerAndDriverByContractId(
			Long contractId);

	/**
	 * 通过合同查找该合同中交车司机的信息
	 * 
	 * @param id
	 *            合同Id
	 * @return
	 */
	List<Map<String, Object>> findReturnCarDriverInfoByContractId(Long id);
}