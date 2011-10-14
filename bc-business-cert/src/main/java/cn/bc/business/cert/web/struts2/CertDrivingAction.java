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

import cn.bc.business.cert.domain.Cert;
import cn.bc.business.cert.domain.Cert4Driving;
import cn.bc.business.cert.service.CertDrivingService;
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
 * 机动车驾驶证Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CertDrivingAction extends FileEntityAction<Long, Cert4Driving> {
	// private static Log logger = LogFactory.getLog(CertIdentityAction.class);
	private static final long 		    serialVersionUID		= 1L;                       
	public 	CertDrivingService			certDrivingService;    
	public 	CertService					certService;
	private AttachService 			    attachService;                                          
	private String					    MANAGER_KEY				= "R_ADMIN";// 管理角色的编码      
	public 	boolean 	   			    isManager;                                              
	public 	AttachWidget 			    attachsUI;                                              
	public 	Map<String,String>		    statusesValue;                                          
	public	Long					    carManId;                                               
	public  Map<String,Object>	 		carManMessMap;
	
	@Autowired
	public void setCertDrivingService(CertDrivingService certDrivingService) {
		this.certDrivingService = certDrivingService;
		this.setCrudService(certDrivingService);
	}
	
	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(MANAGER_KEY);
	}
	
	@Autowired
	public void setCertService(CertService certService) {
		this.certService = certService;
	}


	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}
	

	@SuppressWarnings("static-access")
	public String create() throws Exception {
		String r = super.create();
		isManager = isReadonly();

		this.getE().setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		this.getE().setType(Cert.TYPE_DRIVING);
		this.getE().setStatus(RichEntityImpl.STATUS_ENABLED);
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
		Cert4Driving e = this.getE();
		
		//计算有效期
//		Date date = e.getStartDate().getTime();
//		Calendar resultDate = Calendar.getInstance();
//		date = DateUtils.addYears(date, Integer.valueOf(e.getValidFor()));
//		resultDate.setTime(date);
//		e.setEndDate(resultDate);
		
		if(null != e && (e.getStartDate() != null && e.getValidFor() != null)){
			Calendar result = Calendar.getInstance();
			result.setTime(e.getStartDate().getTime());
			result.add(Calendar.YEAR, Integer.valueOf(e.getValidFor()));
			
			e.setEndDate(result);
		}
		
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		
		this.getCrudService().save(e);
		
		//保存证件与司机的关联表信息
		if(carManId != null){
			this.certService.carManNCert4Save(carManId,getE().getId());
		}
		
		return "saveSuccess";
	}
	
	
	@SuppressWarnings("static-access")
	private AttachWidget buildAttachsUI(boolean isNew) {
		isManager = isReadonly();
		// 构建附件控件
		String ptype = "certIdentity.main";
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(this.isFlashUpload());
		attachsUI.addClazz("formAttachs");
		if (!isNew)
			attachsUI.addAttach(this.attachService.findByPtype(ptype, this
					.getE().getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
		attachsUI.setReadOnly(!this.getE().isNew());
		return attachsUI;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(750).setMinWidth(250)
				.setMinHeight(160).setModal(false);
		option.addButton(new ButtonOption(getText("label.save"), "save"));
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


}