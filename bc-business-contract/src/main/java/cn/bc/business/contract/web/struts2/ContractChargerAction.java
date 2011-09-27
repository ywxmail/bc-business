/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.business.contract.service.ContractChargerService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.RichEntityImpl;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 责任人合同Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ContractChargerAction extends FileEntityAction<Long, Contract4Charger> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long 		serialVersionUID 			= 1L;
	public 	ContractChargerService 	contractChargerService;
	private AttachService 			attachService;
	private OptionService			optionService;
	private String					MANAGER_KEY 				= "R_ADMIN";// 管理角色的编码
	public 	boolean 	   			isManager;
	public	Long 					carManId;
	public  String 					certCode;
	public 	AttachWidget 			attachsUI;
	public	String					assignChargerIds;
	public 	Map<String,String>		statusesValue;
	
	public  List<OptionItem>		signTypeList;							// 可选签约类型
	public  List<OptionItem>		businessTypeList;						// 可选营运性质列表

	@Autowired
	public void setContractChargerService(ContractChargerService contractChargerService) {
		this.contractChargerService = contractChargerService;
		this.setCrudService(contractChargerService);
	}
	
	
	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}
	
	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}


	@SuppressWarnings("static-access")
	public String create() throws Exception {
		String r = super.create();
		isManager = isManager();

		this.getE().setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		this.getE().setType(Contract.TYPE_CHARGER);
		this.getE().setStatus(RichEntityImpl.STATUS_DISABLED);
		
		attachsUI = buildAttachsUI(true);
		// 表单可选项的加载
		initSelects();
		return r;
	}
	
	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		// 表单可选项的加载
		initSelects();
		this.formPageOption = 	buildFormPageOption();
		statusesValue		=	this.getEntityStatuses();
		
		// 构建附件控件
		attachsUI = buildAttachsUI(false);
		return "form";
	}
	
	@Override
	public String save() throws Exception{
		// 处理保存责任人
		dealCharger4Save();
		
		return super.save();
	}
	
	
	
	private void dealCharger4Save() {
//		Set<CarMan> chargers = null;
//		if(this.assignChargerIds != null && this.assignChargerIds.length() > 0){
//			chargers = new HashSet<CarMan>();
//			String[] chargerIds = this.assignChargerIds.split(",");
//			CarMan carMan;
//			for(String cid : chargerIds){
//				carMan = new CarMan();
//				carMan.setId(new Long(cid));
//				chargers.add(carMan);
//			}
//		}
//		if(this.getE().getChargers() != null){
//			this.getE().getChargers().clear();
//			this.getE().getChargers().addAll(chargers);
//		}else{
//			this.getE().setChargers(chargers);
//		}
		
	}


	@SuppressWarnings("static-access")
	private AttachWidget buildAttachsUI(boolean isNew) {
		isManager = isManager();
		// 构建附件控件
		String ptype = "contractLabour.main";
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
		//option.addButton(new ButtonOption(getText("label.save"), "save"));
		option.addButton(new ButtonOption(getText("label.save"), null, "bc.contractChargerForm.save"));
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

	@Override
	protected String[] getSearchFields() {
		return new String[] { "code", "wordNo" };
	}
	
	
	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}
	
	// 表单可选项的加载
	public void initSelects(){
		// 加载可选签约类型
		this.signTypeList		=	this.optionService.findOptionItemByGroupKey(OptionConstants.CONTRACT_SIGNTYPE);
		// 加载可选营运性质列表
		this.businessTypeList	=	this.optionService.findOptionItemByGroupKey(OptionConstants.CAR_BUSINESS_NATURE);

	}

}