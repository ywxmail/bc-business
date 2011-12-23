/**
 * 
 */
package cn.bc.business.runcase.service;

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
	 * @param e
	 * @param sb
	 * @return
	 */
	Case4Advice save(Case4Advice e, SyncBase sb);

}