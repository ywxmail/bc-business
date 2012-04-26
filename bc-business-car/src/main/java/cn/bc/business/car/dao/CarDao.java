/**
 * 
 */
package cn.bc.business.car.dao;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.bc.business.car.domain.Car;
import cn.bc.core.Page;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.condition.Condition;

/**
 * 车辆Dao
 * 
 * @author dragon
 */
public interface CarDao extends CrudDao<Car> {

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
	Page<Map<String, Object>> page(Condition condition, int pageNo, int pageSize);

	/** 通过carManId找车辆 */
	List<Car> findAllcarBycarManId(Long id);

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
	 *            车辆信息的创建时间
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
	 * 判断车牌是否已经被占用
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

	/**
	 * 根据车辆Id获取司机信息 [姓名1,班次1,ID1;姓名2,班次2,ID2;...]
	 * 
	 * @param carId
	 *            车辆Id
	 */
	String getDriverInfoByCarId(Long carId);

	/**
	 * 根据车辆Id获取责任人信息 [姓名1,ID1;姓名2,ID2;...]
	 * 
	 * @param carId
	 *            车辆ID
	 * @return
	 */
	String getChargerInfoByCarId(Long carId);

	/**
	 * 根据状态查找车辆列表
	 * @param status
	 * @return
	 */
	List<Car> selectCarByStatus(Integer status);
}