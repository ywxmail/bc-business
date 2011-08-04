/**
 * 
 */
package cn.bc.business.web.struts2;

import java.io.Serializable;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.Entity;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.domain.FileEntity;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;

/**
 * bc-business子系统的CRUD通用Action
 * 
 * @author dragon
 * 
 */
public class CrudAction<K extends Serializable, E extends Entity<K>>
		extends cn.bc.web.struts2.CrudAction<K, E> {
	private static final long serialVersionUID = 1L;
	protected IdGeneratorService idGeneratorService;
	protected AttachService attachService;

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Override
	protected String getActionPathPrefix() {
		// 与配置文件对应：src/main/resources/cn/bc/business/web/struts2/struts.xml
		return "/bc-business";
	}

	@Override
	public String save() throws Exception {
		SystemContext context = (SystemContext) this.getContext();
		E e = this.getE();
		if (e instanceof FileEntity) {
			//设置最后更新人的信息
			FileEntity<K> fe = (FileEntity<K>)e;
			fe.setModifier(context.getUserHistory());
			fe.setModifiedDate(Calendar.getInstance());
		}

		return super.save();
	}
}