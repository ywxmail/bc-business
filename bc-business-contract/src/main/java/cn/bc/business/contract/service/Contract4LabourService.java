/**
 * 
 */
package cn.bc.business.contract.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;
import cn.bc.template.service.AddAttachFromTemplateService;

/**
 * 司机劳动合同Service
 * 
 * @author wis
 * 
 */
public interface Contract4LabourService extends CrudService<Contract4Labour>,
		AddAttachFromTemplateService {
	/**
	 * 保存劳动合同并处理车辆和司机的关联关系
	 * 
	 * @param contract4Labour
	 *            要保存的合同信息
	 * @param carId
	 *            要关联的车辆id
	 * @param driverId
	 *            要关联的司机id
	 * @return
	 */
	Contract4Labour save(Contract4Labour contract4Labour, Long carId,
			Long driverId);

	/**
	 * 续签处理：新纪录、主版本号加1
	 * 
	 * @parma contractId 原合同id
	 * @parma newStartDate 续签的开始日期
	 * @parma newEndDate 续签的结束日期
	 * @return 续签后的合同信息
	 */
	Contract4Labour doRenew(Long contractId, Calendar newStartDate,
			Calendar newEndDate);

	/**
	 * 离职处理：记录不变、次版本号不变
	 * 
	 * @param contractId
	 *            原合同id
	 * @param resignDate
	 *            指定的离职日期，为空则使用当前时间
	 * @param resignDate
	 *            指定的停保日期，为空则使用当前时间
	 */
	void doResign(Long contractId, Calendar resignDate, Calendar stopDate);

	/**
	 * 转车处理：新纪录、主版本号加1
	 * 
	 * @param contractId
	 *            原合同id
	 * @param newCarId
	 *            指定新车的id
	 * @param newCarPlate
	 *            指定新车的车牌
	 * @return 转车后的合同信息
	 */
	Contract4Labour doChangeCar(Long contractId, Long newCarId,
			String newCarPlate);

	/**
	 * 查找劳动合同列表
	 * 
	 * @parma condition
	 * @parma carId
	 * @return
	 */
	List<Map<String, Object>> list4carMan(Condition condition, Long carManId);

	/**
	 * 查找劳动合同分页
	 * 
	 * @parma condition
	 * @parma carId
	 * @return
	 */
	Page<Map<String, Object>> page4carMan(Condition condition, int pageNo,
			int pageSize);

	/**
	 * 根据carManId查找cert信息
	 * 
	 * @parma carManId
	 * @return
	 */
	Map<String, Object> findCertByCarManId(Long carManId);

	/**
	 * 根据合同ID查找车辆ID
	 * 
	 * @param contractId
	 * @return
	 */
	public Long findCarIdByContractId(Long contractId);

	/**
	 * 根据合同ID查找司机ID
	 * 
	 * @param contractId
	 * @return
	 */
	public Long findCarManIdByContractId(Long contractId);

	/**
	 * 根据车辆Id查找车辆
	 * 
	 * @param carId
	 * @return
	 */
	Map<String, Object> findCarManByCarId(Long carId);

	/**
	 * 根据司机ID相应的车
	 * 
	 * @param carManId
	 * @return
	 */
	List<Map<String, Object>> selectRelateCarByCarManId(Long carManId);

	/**
	 * 根据司机ID查找司机
	 * 
	 * @param carManId
	 * @return
	 */
	Map<String, Object> findCarManByCarManId(Long carManId);

	/**
	 * 根据车辆ID查找车辆
	 * 
	 * @param carId
	 * @return
	 */
	Map<String, Object> findCarByCarId(Long carId);

	/**
	 * 根据车辆ID查找关联的司机
	 * 
	 * @param carId
	 * @return
	 */
	List<Map<String, Object>> selectRelateCarManByCarId(Long carId);

	/**
	 * 判断指定的司机是否已经存在劳动合同
	 * 
	 * @param driverId
	 * @return
	 */
	boolean isExistContractByDriverId(Long driverId);

	/**
	 * 判断劳动社保编号唯一
	 * 
	 * @param excludeId
	 * @param code
	 * @return
	 */
	Long checkInsurCodeIsExist(Long excludeId, String insurCode);

	/**
	 * 根据旧合同id复制出新合同
	 * 
	 * @param id
	 * @param opType
	 * @return
	 */
	Contract4Labour doCopyContract(Long id, int opType);

	/**
	 * 劳动合同操处理
	 * 
	 * @param carId
	 *            -- 车辆id
	 * @param e
	 *            -- 新的经济合同
	 * @param fromContractId
	 *            -- 旧经济合同id
	 * @param stopDate
	 *            合同实际结束日期
	 * @return
	 */
	Contract4Labour doOperate(Long carId, Contract4Labour e,
			Long fromContractId, String stopDate);
	/**
	 * 劳动合同入库
	 * 
	 * @param carId车辆Id
	 * @param driverId司机Id
	 * @param e
	 * @return
	 */
	String doWarehousing(Long carId, Long driverId, Contract4Labour e);

}