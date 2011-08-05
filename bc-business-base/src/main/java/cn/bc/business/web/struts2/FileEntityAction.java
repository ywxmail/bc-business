/**
 * 
 */
package cn.bc.business.web.struts2;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.docs.service.AttachService;
import cn.bc.identity.domain.FileEntity;

/**
 * bc-business子系统FileEntity的CRUD通用Action
 * 
 * @author dragon
 * 
 */
public class FileEntityAction<K extends Serializable, E extends FileEntity<K>>
		extends cn.bc.identity.web.struts2.FileEntityAction<K, E> {
	private static final long serialVersionUID = 1L;
	private AttachService attachService;

	public AttachService getAttachService() {
		return attachService;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Override
	protected String getActionPathPrefix() {
		// 与配置文件对应：src/main/resources/cn/bc/business/web/struts2/struts.xml
		return EntityAction.PATH_PREFIX;
	}
}