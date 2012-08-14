/**
 * 
 */
package cn.bc.business.contract.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;
import cn.bc.template.service.AddAttachFromTemplateService;

/**
 * 责任人合同Service
 * 
 * @author dragon
 */
/**
 * @author wis
 *
 */
/**
 * @author wis
 * 
 */
public interface Contract4ChargerService extends CrudService<Contract4Charger>,
		AddAttachFromTemplateService {

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
	 * @parma contractIds
	 * @return
	 */
	void deleteCarNContract(Long[] contractIds);

	/**
	 * 保存车辆与合同的关联信息 jdbc查询BS_CAR_CONTRACT表是否存在相应carId和contractId的记录
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
	 * 根据合同ID查找关联责任人
	 * 
	 * @param contractId
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
	 * 根据合同ID查找车辆ID
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
	 * 更新司机表的负责人信息
	 * 
	 * @param assignChargerNames
	 * @param carId
	 */
	void updateCarMan4dirverName(String assignChargerNames, Long carId);

	/**
	 * 根据车辆ID查找车辆信息
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
	 * 保存劳动合同并处理车辆和司机的关联关系
	 * 
	 * @param contract4Charger
	 *            要保存的合同信息
	 * @param carId
	 *            要关联的车辆id
	 * @param assignChargerNames
	 *            责任人ID列表
	 * @param assignChargerNames
	 *            责任人姓名列表
	 * @return
	 */
	Contract4Charger save(Contract4Charger e, Long carId,
			String assignChargerIds, String assignChargerNames);

	/**
	 * 判断指定的车辆是否已经存在经济合同
	 * 
	 * @param carId
	 * @return
	 */
	boolean isExistContract(Long carId);

	/**
	 * 续签处理：新纪录、主版本号加1
	 * 
	 * @parma contractId 原合同id
	 * @parma newStartDate 续签的开始日期
	 * @parma newEndDate 续签的结束日期
	 * @parma code 合同编号
	 * @return 续签后的合同信息
	 */
	Contract4Charger doRenew(Long fromContractId, Calendar newStartDate,
			Calendar newEndDate, String code);

	/**
	 * 根据合同ID查找关联责任人姓名
	 * 
	 * @param contractId
	 * @return
	 */
	List<String> findChargerNameByContractId(Long contractId);

	/**
	 * 过户处理：新纪录、主版本号加1
	 * 
	 * @parma carId 原车辆id
	 * @parma takebackOrigin 是否收回原件
	 * @parma assignChargerIds 多个新责任人id
	 * @parma assignChargerNames 多个新责任人名
	 * @parma contractId 原合同id
	 * @parma newStartDate 续签的开始日期
	 * @parma newEndDate 续签的结束日期
	 * @parma code 合同编号
	 * @return 过户后的合同信息
	 */
	Contract4Charger doChaneCharger(Long carId, Boolean takebackOrigin,
			String assignChargerIds, String assignChargerNames,
			Long fromContractId, Calendar newStartDate, Calendar newEndDate,
			String code);

	/**
	 * 重发包处理：新纪录、主版本号加1
	 * 
	 * @parma carId 原车辆id
	 * @parma takebackOrigin 是否收回原件
	 * @parma assignChargerIds 多个新责任人id
	 * @parma assignChargerNames 多个新责任人名
	 * @parma contractId 原合同id
	 * @parma newStartDate 续签的开始日期
	 * @parma newEndDate 续签的结束日期 * @parma code 合同编号
	 * @return 过户后的合同信息
	 */
	Contract4Charger doChaneCharger2(Long carId, Boolean takebackOrigin,
			String assignChargerIds, String assignChargerNamesStr,
			Long fromContractId, Calendar newStartDate, Calendar newEndDate,
			String code);

	/**
	 * 注销处理：记录不变、次版本号不变
	 * 
	 * @param contractId
	 * @param stopDate
	 *            合同实际结束日期
	 */
	void doLogout(Long contractId, Calendar stopDate);

	/**
	 * 判断经济合同自编号唯一
	 * 
	 * @param excludeId
	 * @param code
	 * @return
	 */
	Long checkCodeIsExist(Long excludeId, String code);

	/**
	 * 根据旧合同id复制出新合同
	 * 
	 * @param id
	 * @param opType
	 * @param signType
	 * @return
	 */
	Contract4Charger doCopyContract(Long id, int opType, String signType);

	/**
	 * 经济合同操处理
	 * 
	 * @param carId
	 *            -- 车辆id
	 * @param e
	 *            -- 新的经济合同
	 * @param assignChargerIds
	 *            -- 责任人id列表
	 * @param fromContractId
	 *            -- 旧经济合同id
	 * @param stopDate
	 *            合同实际结束日期
	 * @return
	 */
	Contract4Charger doOperate(Long carId, Contract4Charger e,
			String assignChargerIds, Long fromContractId, String stopDate);

	/**
	 * 入库
	 * 
	 * @param contract4Charger合同
	 * 
	 * @param contractCarId车辆ID
	 * @param carMansId司机Id
	 * @param draftCarId未入库的车辆Id
	 * @param draftCarManId未入库的司机Id
	 * @param draftcarByDriverHistoryId
	 *            未入库的迁移记录Id
	 * @return
	 */
	String doWarehousing(Long contractCarId, String carMansId,
			Contract4Charger contract4Charger, Long draftCarId,
			String draftCarManId, String draftcarByDriverHistoryId);

	/**
	 * 根据车辆Id获取在案的正副班司机数量
	 * 
	 * @param carId
	 *            车辆Id
	 * @return
	 */
	int getDriverAmount(Long carId);

	/**
	 * 检查车辆和司机的状态
	 * 
	 * @param carId
	 * @param carMansId
	 * @return
	 */
	String checkDriverOrCarStatus(Long carId, String carMansId);

	/**
	 * 根据合同获取合同车辆在案的司机和责任人
	 * 
	 * @param contractId
	 * @return
	 */
	List<Map<String, String>> getNormalChargerAndDriverByContractId(
			Long contractId);
}