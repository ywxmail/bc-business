/**
 * 
 */
package cn.bc.business.car.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.bc.business.car.domain.Car;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;

/**
 * 车辆Service
 * 
 * @author dragon
 */
public interface CarService extends CrudService<Car> {

	/**
	 * 查找汽车列表
	 * 
	 * @parma condition
	 * @return
	 */
	List<Map<String, Object>> list(Condition condition);

	/**
	 * 查找汽车分页
	 * 
	 * @parma condition
	 * @parma Page
	 * @return
	 */
	Page<? extends Object> page(Condition condition, int pageNo, int pageSize);

	/** 根据司机ID查找返回状态为启用中相关辆信息 */
	List<Car> selectAllCarByCarManId(Long id);

	/**
	 * 根据车牌号查找车辆id
	 * 
	 * @parma carPlateNo
	 * @return Long
	 */
	Long findcarIdByCarPlateNo(String carPlateNo);

	/**
	 * 通过自编号生成原车号
	 * 
	 * @param code
	 * @return
	 */
	Car findcarOriginNoByCode(String code);

	/**
	 * 
	 * 通过经营权号生成原车号
	 * 
	 * @param ownership
	 *            经营权号
	 * @param fileDate
	 *            创建时间
	 * @return
	 */
	Car findcarOriginNoByOwnership(String ownership, Calendar fileDate);

	/**
	 * 通过车牌号查找此车辆所属的分公司与车队
	 * 
	 * @parma carPlateNo
	 * @return Map<String, Object>
	 */
	Map<String, Object> findcarInfoByCarPlateNo2(String carPlateNo);

	/**
	 * 判断车辆自编号唯一
	 * 
	 * @param excludeId
	 *            要排除检测的id
	 * @param code
	 *            要检测的自编号
	 * @return 如果编号被占用，返回占用此编号的车辆的id，否则返回null
	 */
	Long checkCodeIsExists(Long excludeId, String code);

	/**
	 * 判断车牌号码是否已经被占用
	 * 
	 * @param excludeId
	 *            要排除检测的id
	 * @param plateType
	 *            车牌类型，如粤A
	 * @param plateNo
	 *            车牌号码，如JM123
	 * @return 如果车牌被占用，返回占用此车牌的车辆id，否则返回null
	 */
	Long checkPlateIsExists(Long excludeId, String plateType, String plateNo);
}