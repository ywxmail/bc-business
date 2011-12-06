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
import cn.bc.business.cert.domain.Cert4VehiceLicense;
import cn.bc.business.cert.service.CertService;
import cn.bc.business.cert.service.CertVehicelicenseService;
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
 * 机动车行驶证Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CertVehicelicenseAction extends FileEntityAction<Long, Cert4VehiceLicense> {
	// private static Log logger = LogFactory.getLog(CertIdentityAction.class);
	private static final long 		    serialVersionUID		= 1L;                       
	public 	CertVehicelicenseService	certVehicelicenseService;    
	public 	CertService					certService;
	private AttachService 			    attachService;                                          
	public 	AttachWidget 			    attachsUI;                                              
	public 	Map<String,String>		    statusesValue;                                          
	public	Long					    carId;                                               
	public  Map<String,Object>	 		carMessMap;
	public  Map<String,Object>	 		carMessInfoMap;
	
	@Autowired
	public void setCertVehicelicenseService(CertVehicelicenseService certVehicelicenseService) {
		this.certVehicelicenseService = certVehicelicenseService;
		this.setCrudService(certVehicelicenseService);
	}
	
	
	@Autowired
	public void setCertService(CertService certService) {
		this.certService = certService;
	}
	
	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}


	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}
	
	@Override
	public boolean isReadonly() {
		// 车辆证件管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.cert4car"),
				getText("key.role.bc.admin"));
	}

	@SuppressWarnings("static-access")
	public String create() throws Exception {
		String r = super.create();
		Cert4VehiceLicense e = this.getE();
		
		//根据carId查找car信息
		if(carId != null){
			carMessMap = this.certService.findCarByCarId(carId);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			Calendar date2 = Calendar.getInstance();
			
			if(getDayStartString(carMessMap.get("register_date")).length() > 0){
				date = sdf.parse(carMessMap.get("register_date")+"");
				date2.setTime(date);
				e.setRegisterDate(date2);
			}
			
			if(getDayStartString(carMessMap.get("scrap_date")).length() > 0){
				date = sdf.parse(carMessMap.get("scrap_date")+"");
				date2.setTime(date);
				e.setScrapDate(date2);
			}
			String factory = "";
			if(carMessMap.get("factory_type") != null && carMessMap.get("factory_type").toString().length() > 0){
				factory = carMessMap.get("factory_type")+"."+carMessMap.get("factory_model");
			}
			e.setPlate(isNullObject(carMessMap.get("plate_type")+""+carMessMap.get("plate_no")));
			e.setFactory(factory);
			e.setVin(isNullObject(carMessMap.get("vin")));
			e.setEngineNo(isNullObject(carMessMap.get("engine_no")));
			e.setTotalWeight(Integer.valueOf(isNullObject(carMessMap.get("total_weight"))));
			e.setDimLen(Integer.valueOf(isNullObject(carMessMap.get("dim_len"))));
			e.setDimWidth(Integer.valueOf(isNullObject(carMessMap.get("dim_width"))));
			e.setDimHeight(Integer.valueOf(isNullObject(carMessMap.get("dim_height"))));
			e.setAccessWeight(Integer.valueOf(isNullObject(carMessMap.get("access_weight"))));
		}
		
		e.setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		// 自动生成自编号
		this.getE().setCertCode(
				this.getIdGeneratorService().nextSN4Month(Cert4VehiceLicense.KEY_CODE));
		e.setType(Cert.TYPE_VEHICELICENSE);
		e.setStatus(RichEntityImpl.STATUS_ENABLED);
		statusesValue		=	this.getEntityStatuses();
		
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
		this.getE().setPlate(carMessMap.get("plate_type")+"."+carMessMap.get("plate_no"));
		
		// 构建附件控件
		attachsUI = buildAttachsUI(false);
		return "form";
	}
	

	@Override
	public String save() throws Exception{
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		Cert4VehiceLicense e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		e.setCertCode(e.getArchiveNo());
		this.getCrudService().save(e);
		
		//保存证件与车辆的关联表信息
		if(carId != null){
			this.certService.carNCert4Save(carId,getE().getId());
		}
		
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

	
	private AttachWidget buildAttachsUI(boolean isNew) {
		// 构建附件控件
		String ptype = "certVehicelicense.main";
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
		PageOption option = super.buildFormPageOption().setWidth(750).setMinWidth(250).setHeight(450)
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

	
    /**
     * 格式化日期
     * @return
     */
    public String getDayStartString(Object object){
    	if(null != object){
	    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    	StringBuffer str = new StringBuffer(df.format(object));
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
    
//	@Override
//	protected HtmlPage buildHtml4Paging() {
//		HtmlPage page = super.buildHtml4Paging();
//		if (carId != null)
//			page.setAttr("data-extras", new Json().put("carId", carId)
//					.toString());
//		return page;
//	}

}