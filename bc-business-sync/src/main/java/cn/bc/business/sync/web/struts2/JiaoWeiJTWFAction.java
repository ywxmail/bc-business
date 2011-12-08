/**
 * 
 */
package cn.bc.business.sync.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.business.sync.service.JiaoWeiJTWFService;
import cn.bc.business.web.struts2.EntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 交委交通违章Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class JiaoWeiJTWFAction extends EntityAction<Long, JiaoWeiJTWF> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static 	final long 				serialVersionUID 	= 1L;

	@SuppressWarnings("unused")
	private JiaoWeiJTWFService				jiaoWeiJTWFService;

	@Autowired
	public void setJiaoWeiJTWFService(JiaoWeiJTWFService jiaoWeiJTWFService) {
		this.jiaoWeiJTWFService = jiaoWeiJTWFService;
		this.setCrudService(jiaoWeiJTWFService);
	}
	
	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractTraffic"),
				getText("key.role.bc.admin"));
	}


	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = super.buildFormPageOption().setWidth(820).setMinWidth(250).setHeight(500)
				.setMinHeight(200);
		return option;
	}

	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		this.formPageOption = this.buildFormPageOption();
		
		return "form";
	}
	
//	//复写搜索URL方法
//	protected String getEntityConfigName() {
//		return "jiaoWeiJTWF";
//	}


	
}
