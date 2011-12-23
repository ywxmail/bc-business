/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.runcase.domain.Case4InfractBusiness;
import cn.bc.core.service.CrudService;
import cn.bc.sync.domain.SyncBase;


/**
 * 营运事件营运违章Service
 * 
 * @author dragon
 */
public interface CaseBusinessService extends CrudService<Case4InfractBusiness> {

	/**
	 * 保存并更新Sycn对象的状态
	 * @param e
	 * @param sb
	 * @return
	 */
	Case4InfractBusiness save(Case4InfractBusiness e, SyncBase sb);

}