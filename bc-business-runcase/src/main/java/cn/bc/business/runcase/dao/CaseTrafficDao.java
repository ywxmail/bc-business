/**
 * 
 */
package cn.bc.business.runcase.dao;

import java.util.Calendar;
import java.util.Map;

import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.core.dao.CrudDao;

/**
 * 营运事件交通违章Dao
 * 
 * @author dragon
 */
public interface CaseTrafficDao extends CrudDao<Case4InfractTraffic> {

	/**
	 * 根据司机ID和违法时间查找司机在该违法周期内所有的违法信息
	 * 
	 * @param carManId
	 * @param startDate初领驾驶证周期的开始日
	 * @param endDate初领驾驶证周期的结束日
	 * @return
	 */
	String getCaseTrafficInfoByCarManId(Long carManId, Calendar startDate,
			Calendar endDate);

	/**
	 * 驾驶员交通违法流程结束后更新交通违法的状态和司机
	 * 
	 * @param id
	 *            交通违法信息ID
	 * @param attributes
	 *            更新的信息
	 */
	void updateCaseTrafficInfo4Flow(Long id, Map<String, Object> attributes);
}