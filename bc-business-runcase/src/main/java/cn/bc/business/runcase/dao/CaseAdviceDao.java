/**
 * 
 */
package cn.bc.business.runcase.dao;

import java.util.Calendar;
import java.util.Map;

import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.core.dao.CrudDao;

/**
 * 营运事件投诉与建议Dao
 * 
 * @author dragon
 */
public interface CaseAdviceDao extends CrudDao<Case4Advice> {

	/**
	 * 在指定时期内获取司机的安全服务信息
	 * 
	 * @param carManId司机id
	 * @param startDate开始日期
	 * @param endDate结束日期
	 * @author zxr
	 * @return
	 */
	String getCaseTrafficInfoByCarManId(Long carManId, Calendar startDate,
			Calendar endDate);

	/**
	 * 驾驶员客管投诉流程结束后更新客管投诉的状态和司机
	 * 
	 * @param id
	 *            客管投诉信息ID
	 * @param attributes
	 *            更新的信息
	 */
	void updateCaseAdviceInfo4Flow(Long id, Map<String, Object> attributes);

}