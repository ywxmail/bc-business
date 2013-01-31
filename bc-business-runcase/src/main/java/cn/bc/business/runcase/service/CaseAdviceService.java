/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.Calendar;
import java.util.Map;

import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.core.service.CrudService;
import cn.bc.sync.domain.SyncBase;

/**
 * 营运事件投诉与建议Service
 * 
 * @author dragon
 */
public interface CaseAdviceService extends CrudService<Case4Advice> {

	/**
	 * 保存并更新Sycn对象的状态
	 * 
	 * @param e
	 * @param sb
	 * @return
	 */
	Case4Advice save(Case4Advice e, SyncBase sb);

	/**
	 * 核准操作
	 * 
	 * @param fromAdviceId
	 * @param handlerId
	 * @param handlerName
	 * @param handleDate
	 * @param handleOpinion
	 * @return
	 */
	Case4Advice doManage(Long fromAdviceId, Long handlerId, String handlerName,
			Calendar handleDate, String handleOpinion);

	/**
	 * 发起流程
	 * 
	 * @param key
	 *            流程key值
	 * @param ids
	 *            客管投诉信息的ID
	 * @return
	 */
	String doStartFlow(String key, Long[] ids);

	/**
	 * 根据司机ID和事发时间查找司机在事发日期向前推算一年内的安全服务信息
	 * 
	 * @param carManId司机id
	 * @param happenDate事发日期
	 * @return
	 */
	String getCaseTrafficInfoByCarManId(Long carManId, Calendar happenDate);

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