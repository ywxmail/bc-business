/**
 * 
 */
package cn.bc.business.info.service;

import java.util.HashMap;
import java.util.Map;

import cn.bc.business.info.domain.Info;
import cn.bc.core.service.DefaultCrudService;

/**
 * 信息管理Service实现
 * 
 * @author dragon
 */
public class InfoServiceImpl extends DefaultCrudService<Info> implements
		InfoService {

	public void doIssue(Long id) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("status", Info.STATUS_ISSUED);
		this.update(id, attributes);
	}

	public void doDisabled(Long id) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("status", Info.STATUS_DISABLED);
		this.update(id, attributes);
	}
}