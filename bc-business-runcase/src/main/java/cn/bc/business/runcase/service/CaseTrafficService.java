/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.List;

import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.core.service.CrudService;
import cn.bc.sync.domain.SyncBase;


/**
 * 营运事件交通违章Service
 * 
 * @author dragon
 */
public interface CaseTrafficService extends CrudService<Case4InfractTraffic> {

	
	/**
	 * 保存并更新Sycn对象的状态
	 * @param e
	 * @param sb
	 * @return
	 */
	Case4InfractTraffic save(Case4InfractTraffic e, SyncBase sb);

	/**
	 * 批量生成交通违章
	 * @param syncIds
	 * @return
	 */
	List<Case4InfractTraffic> doPatchSave(String syncIds);

}