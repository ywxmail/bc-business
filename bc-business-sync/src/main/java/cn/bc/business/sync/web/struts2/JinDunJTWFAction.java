/**
 * 
 */
package cn.bc.business.sync.web.struts2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.business.sync.service.JinDunJTWFService;
import cn.bc.business.web.struts2.EntityAction;
import cn.bc.sync.domain.SyncBase;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 金盾交通违章Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class JinDunJTWFAction extends EntityAction<Long, JinDunJTWF> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static 	final long 				serialVersionUID 	= 1L;
	public 	Map<String,String> 				statusesValue;
	
	@SuppressWarnings("unused")
	private JinDunJTWFService				jinDunJTWFService;

	@Autowired
	public void setJinDunJTWFService(JinDunJTWFService jinDunJTWFService) {
		this.jinDunJTWFService = jinDunJTWFService;
		this.setCrudService(jinDunJTWFService);
	}
	

	@Override
	public boolean isReadonly() {
		return true;
	}


	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = super.buildFormPageOption().setWidth(615).setMinWidth(250).setHeight(290)
				.setMinHeight(200);
		return option;
	}

	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		this.formPageOption = this.buildFormPageOption();
		statusesValue = this.getSyncStatuses();
		return "form";
	}
	
	protected Map<String, String> getSyncStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(SyncBase.STATUS_NEW),
				getText("bs.sync.status.new"));
		statuses.put(String.valueOf(SyncBase.STATUS_DONE),
				getText("bs.sync.status.done"));
		statuses.put(String.valueOf(SyncBase.STATUS_GEN),
				getText("bs.sync.status.gen"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}
//	//复写搜索URL方法
//	protected String getEntityConfigName() {
//		return "jinDunJTWF";
//	}


	
}
