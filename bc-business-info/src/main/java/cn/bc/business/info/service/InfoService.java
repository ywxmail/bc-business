/**
 * 
 */
package cn.bc.business.info.service;

import cn.bc.business.info.domain.Info;
import cn.bc.core.service.CrudService;

/**
 * 信息管理Service
 * 
 * @author dragon
 */
public interface InfoService extends CrudService<Info> {
	/**
	 * 发布指定的信息
	 * 
	 * @param id
	 */
	void doIssue(Long id);

	/**
	 * 禁用指定的信息
	 * 
	 * @param id
	 */
	void doDisabled(Long id);
}