package cn.bc.business.mix.dao;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 信息中心综合查询
 * 
 * @author dragon
 * 
 */
public interface InfoCenterDao {
	/**
	 * 查询符合条件的车辆信息
	 * 
	 * @param unitId
	 *            车辆所属单位的id
	 * @param motorcadeId
	 *            车辆所属车队的id
	 * @return [{code: c1,name: n1}, {code: c2,name: n2},...]
	 */
	JSONArray findCars(Long unitId, Long motorcadeId);

	/**
	 * 查询符合条件的车辆信息
	 * 
	 * @param searchType
	 *            模糊查询的字段类型
	 * @param searchText
	 *            模糊查询的值
	 * @return [{code: c1,name: n1}, {code: c2,name: n2},...]
	 */
	JSONArray findCars(String searchType, String searchText);

	/**
	 * 获取车辆的综合详细信息
	 * 
	 * @param carId
	 *            车辆id
	 * @return
	 */
	JSONObject findCarDetail(Long carId) throws Exception;
}
