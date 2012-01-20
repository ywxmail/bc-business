package cn.bc.business.motorcade.dao;

import java.util.List;
import java.util.Map;

import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.core.dao.CrudDao;

public interface MotorcadeDao extends CrudDao<Motorcade> {
	/**
	 * 获取当前可用的车队
	 * 
	 * @return
	 */
	List<Motorcade> findActive();

	/**
	 * 获取当前可用的车队下拉列表信息
	 * 
	 * @return 返回结果中的元素Map格式为：id -- Motorcade的id,name -- Motorcade的name
	 */
	List<Map<String, String>> findEnabled4Option();

	/**
	 * 获取车队下拉列表信息
	 * 
	 * @param statuses 车队的状态：为空则获取所有状态
	 * 
	 * @return 返回结果中的元素Map格式为：id -- Motorcade的id,name -- Motorcade的name
	 */
	List<Map<String, String>> find4Option(Integer[] statuses);
}
