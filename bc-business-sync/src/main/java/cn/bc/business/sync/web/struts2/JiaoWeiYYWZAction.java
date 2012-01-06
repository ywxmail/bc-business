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

import cn.bc.business.sync.domain.JiaoWeiYYWZ;
import cn.bc.business.sync.service.JiaoWeiYYWZService;
import cn.bc.business.web.struts2.EntityAction;
import cn.bc.sync.domain.SyncBase;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 交委营运违章Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class JiaoWeiYYWZAction extends EntityAction<Long, JiaoWeiYYWZ> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static 	final long 				serialVersionUID 	= 1L;
	public	Map<String,String>				statusesValue;
	@SuppressWarnings("unused")
	private JiaoWeiYYWZService				jiaoWeiYYWZService;

	@Autowired
	public void setJiaoWeiYYWZService(JiaoWeiYYWZService jiaoWeiYYWZService) {
		this.jiaoWeiYYWZService = jiaoWeiYYWZService;
		this.setCrudService(jiaoWeiYYWZService);
	}
	
	@Override
	public boolean isReadonly() {
		return true;
	}


	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		PageOption option = super.buildFormPageOption(editable).setWidth(700).setMinWidth(250).setHeight(400)
				.setMinHeight(200);
		return option;
	}

	@Override
	protected void initForm(boolean editable) {
		super.initForm(editable);
		// 状态列表
		statusesValue = this.getSyncStatuses();
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
//		return "jiaoWeiYYWZ";
//	}


	
}
