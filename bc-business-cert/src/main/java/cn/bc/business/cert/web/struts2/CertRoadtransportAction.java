/**
 * 
 */
package cn.bc.business.cert.web.struts2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.cert.domain.Cert;
import cn.bc.business.cert.domain.Cert4RoadTransport;
import cn.bc.business.cert.service.CertRoadtransportService;
import cn.bc.business.cert.service.CertService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.RichEntityImpl;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 道路运输证Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CertRoadtransportAction extends FileEntityAction<Long, Cert4RoadTransport> {
	// private static Log logger = LogFactory.getLog(CertIdentityAction.class);
	private static final long 		    serialVersionUID		= 1L;                       
	public 	CertRoadtransportService	certRoadtransportService;    
	public 	CertService					certService;
	private AttachService 			    attachService;                                          
	private String					    MANAGER_KEY				= "R_ADMIN";// 管理角色的编码      
	public 	boolean 	   			    isManager;                                              
	public 	AttachWidget 			    attachsUI;                                              
	public 	Map<String,String>		    statusesValue;                                          
	public	Long					    carId;                                               
	public  Map<String,Object>	 		carMessMap;
	public  Map<String,Object>	 		carMessInfoMap;
	
	@Autowired
	public void setCertRoadtransportService(CertRoadtransportService certRoadtransportService) {
		this.certRoadtransportService = certRoadtransportService;
		this.setCrudService(certRoadtransportService);
	}
	
	
	@Autowired
	public void setCertService(CertService certService) {
		this.certService = certService;
	}


	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}
	
	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(MANAGER_KEY);
	}
	
	@SuppressWarnings("static-access")
	public String create() throws Exception {
		String r = super.create();
		isManager = isManager();

		this.getE().setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		this.getE().setType(Cert.TYPE_ROADTRANSPORT);
		this.getE().setStatus(RichEntityImpl.STATUS_DISABLED);
		
		attachsUI = buildAttachsUI(true);
		return r;
	}
	
	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		
		this.formPageOption = 	buildFormPageOption();
		statusesValue		=	this.getEntityStatuses();
		
		//根据certId查找car信息
		carMessMap = this.certService.findCarMessByCertId(this.getId());
		carId = Long.valueOf(carMessMap.get("id")+"");
		this.getE().setPlate(carMessMap.get("plate_type")+" "+carMessMap.get("plate_no"));
		
		// 构建附件控件
		attachsUI = buildAttachsUI(false);
		return "form";
	}
	

	@Override
	public String save() throws Exception{
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		Cert4RoadTransport e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		this.getCrudService().save(e);
		
		//保存证件与司机的关联表信息
		this.certService.carNCert4Save(carId,getE().getId());
		
		return "saveSuccess";
	}
	
	/**
	 * 根据carId查找car详细信息
	 * @parma carId 
	 * @return
	 */
	public Json json;
	public String carInfo(){
		carMessInfoMap = this.certService.findCarByCarId(carId);
		json = new Json();
		
		if(carMessInfoMap != null && carMessInfoMap.size() > 0){
			json.put("factorytype", 	isNullObject(carMessInfoMap.get("factory_type"))+isNullObject(carMessInfoMap.get("factory_model")));
			json.put("registerdate", 	isNullObject(getDayStartString((Date)carMessInfoMap.get("register_date"))));
			json.put("scrapdate", 		isNullObject(getDayStartString((Date)carMessInfoMap.get("scrap_date"))));
			json.put("level", 			isNullObject(carMessInfoMap.get("level_")+""));
			json.put("vin", 			isNullObject(carMessInfoMap.get("vin")));
			json.put("engineno", 		isNullObject(carMessInfoMap.get("engine_no")));
			json.put("totalweight", 	isNullObject(carMessInfoMap.get("total_weight")+""));
			json.put("dimlen",			isNullObject(carMessInfoMap.get("dim_len")+""));
			json.put("dimwidth", 		isNullObject(carMessInfoMap.get("dim_width")+""));
			json.put("dimheight", 		isNullObject(carMessInfoMap.get("dim_height")+""));
			json.put("accessweight", 	isNullObject(carMessInfoMap.get("access_weight")+""));
			json.put("accesscount", 	isNullObject(carMessInfoMap.get("access_count")+""));
		}

		return "json";
	}

	
	@SuppressWarnings("static-access")
	private AttachWidget buildAttachsUI(boolean isNew) {
		isManager = isManager();
		// 构建附件控件
		String ptype = "certRoadtransport.main";
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

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}
	
    /**
     * 格式化日期
     * @return
     */
    public String getDayStartString(Date date){
    	if(null != date){
	    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    	StringBuffer str = new StringBuffer(df.format(date));
	        return str.toString();
    	}else{
    		return "";
    	}
    }
    
    public String isNullObject(Object obj){
    	if(null != obj){
    		return obj.toString();
    	}else{
    		return "";
    	}
    }

}