/**
 * 
 */
package cn.bc.business.cert.web.struts2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.cert.domain.Cert;
import cn.bc.business.cert.service.CertService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 证件Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CertAction extends FileEntityAction<Long, Cert> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	public CertService certService;

	@Autowired
	public void setCertService( CertService certService) {
		this.certService = certService;
		this.setCrudService(certService);
	}


	public AttachWidget attachsUI;

	
	// 设置页面的尺寸
	@Override
	public PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(850).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}
	
	
	/**
	 * 获取车辆证件类型列表
	 * 
	 * @return
	 */
	public Map<String, String> getEntityTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Cert.TYPE_IDENTITY),
				getText("cert.select.identity"));
		types.put(String.valueOf(Cert.TYPE_DRIVING),
				getText("cert.select.driving"));
		types.put(String.valueOf(Cert.TYPE_CYZG),
				getText("cert.select.cyzg"));
		types.put(String.valueOf(Cert.TYPE_FWZG),
				getText("cert.select.fwzg"));
		types.put(String.valueOf(Cert.TYPE_JSPX),
				getText("cert.select.jspx"));
		types.put(String.valueOf(Cert.TYPE_VEHICELICENSE),
				getText("cert.select.vehicelicense"));
		types.put(String.valueOf(Cert.TYPE_ROADTRANSPORT),
				getText("cert.select.roadtransport"));
	
		return types;
	}
}
