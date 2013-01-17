/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.Calendar;

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
}