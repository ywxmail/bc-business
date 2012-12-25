/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.Calendar;
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
	 * 
	 * @param e
	 * @param sb
	 * @return
	 */
	Case4InfractTraffic save(Case4InfractTraffic e, SyncBase sb);

	/**
	 * 批量生成交通违章
	 * 
	 * @param syncIds
	 * @return
	 */
	List<Case4InfractTraffic> doPatchSave(String syncIds);

	/**
	 * 发起流程
	 * 
	 * @param key
	 *            流程key值
	 * @param ids
	 *            交通违法信息的ID
	 * @return
	 */
	String doStartFlow(String key, Long[] ids);

	/**
	 * 根据司机ID和违法时间查找司机在该违法周期内所有的违法信息
	 * 
	 * @param carManId
	 * @param happenDate违法时间
	 * @return
	 */
	String getCaseTrafficInfoByCarManId(Long carManId, Calendar happenDate);
}