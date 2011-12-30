/**
 * 
 */
package cn.bc.business.cert.web.struts2;

import java.util.Calendar;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.cert.domain.Cert;
import cn.bc.business.cert.domain.Cert4DriverEducation;
import cn.bc.business.cert.service.CertJspxService;
import cn.bc.business.cert.service.CertService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.RichEntityImpl;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 驾驶培训证Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CertJspxAction extends FileEntityAction<Long, Cert4DriverEducation> {
	// private static Log logger = LogFactory.getLog(CertIdentityAction.class);
	private static final long 		    serialVersionUID		= 1L;                       
	public 	CertJspxService				certJspxService;    
	public 	CertService					certService;
	private AttachService 			    attachService;                                          
	public 	AttachWidget 			    attachsUI;                                              
	public 	Map<String,String>		    statusesValue;                                          
	public	Long					    carManId;                                               
	public  Map<String,Object>	 		carManMessMap;
	public  Map<String,Object>	 		carManMap;
	
	@Autowired
	public void setCertJspxService(CertJspxService certJspxService) {
		this.certJspxService = certJspxService;
		this.setCrudService(certJspxService);
	}
	
	
	@Autowired
	public void setCertService(CertService certService) {
		this.certService = certService;
	}


	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}
	
	public Long getCarManId() {
		return carManId;
	}

	public void setCarManId(Long carManId) {
		this.carManId = carManId;
	}
	

	@Override
	public boolean isReadonly() {
		// 司机证件管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.cert4driver"),
				getText("key.role.bc.admin"));
	}
	
	@SuppressWarnings("static-access")
	public String create() throws Exception {
		String r = super.create();

		if(carManId != null){
			carManMap = this.certService.findCarManByCarManId(carManId);
			this.getE().setName(isNullObject(carManMap.get("name")));
			this.getE().setCertCode(isNullObject(carManMap.get("cert_fwzg")));
		}
		
		this.getE().setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		// 自动生成自编号
		this.getE().setCertCode(
				this.getIdGeneratorService().nextSN4Month(Cert4DriverEducation.KEY_CODE));
		this.getE().setType(Cert.TYPE_JSPX);
		this.getE().setStatus(BCConstants.STATUS_ENABLED);
		statusesValue		=	this.getEntityStatuses();
		
		attachsUI = buildAttachsUI(true);
		return r;
	}
	
	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		
		this.formPageOption = 	buildFormPageOption();
		statusesValue		=	this.getEntityStatuses();
		
		//根据certId查找carMan信息
		carManMessMap = this.certService.findCarManMessByCertId(this.getId());
		carManId = Long.valueOf(carManMessMap.get("id")+"");
		this.getE().setName(carManMessMap.get("name")+"");
		
		// 构建附件控件
		attachsUI = buildAttachsUI(false);
		return "form";
	}
	

	@Override
	public String save() throws Exception{
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		Cert4DriverEducation e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		
		this.getCrudService().save(e);
		
		//保存证件与司机的关联表信息
		if(carManId != null){
			this.certService.carManNCert4Save(carManId,getE().getId());
		}
		
		return "saveSuccess";
	}

	
	private AttachWidget buildAttachsUI(boolean isNew) {
		// 构建附件控件
		String ptype = "certJspx.main";
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(isFlashUpload());
		attachsUI.addClazz("formAttachs");
		if (!isNew)
			attachsUI.addAttach(this.attachService.findByPtype(ptype, this
					.getE().getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
		if (this.isReadonly()) {
			attachsUI.setReadOnly(true);
		}
		return attachsUI;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = super.buildFormPageOption().setWidth(720).setMinWidth(250)
				.setMinHeight(160);
		if (!this.isReadonly()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return null;// new OrderCondition("fileDate", Direction.Desc);
	}

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

    public String isNullObject(Object obj){
    	if(null != obj){
    		return obj.toString();
    	}else{
    		return "";
    	}
    }

}