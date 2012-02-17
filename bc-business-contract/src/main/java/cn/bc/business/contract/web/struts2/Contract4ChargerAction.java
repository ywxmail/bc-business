/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.business.contract.service.Contract4ChargerService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 经济合同Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Contract4ChargerAction extends FileEntityAction<Long, Contract4Charger> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long 			serialVersionUID 			= 1L;
	private Contract4ChargerService 	contract4ChargerService;
//	private Contract4LabourService   	contract4LabourService;
	private AttachService 				attachService;
	private OptionService				optionService;
	private	Long						carId; 
	public	Long						driverId;								
	public 	AttachWidget 				attachsUI;
	
	public 	Map<String,String>			statusesValue;
	public	Map<String,String>			chargerInfoMap;							//责任人Map
	
	public  List<Map<String, String>>	signTypeList;							//可选签约类型
	public  List<Map<String, String>>	businessTypeList;						//可选营运性质列表
	public  List<Map<String, String>>	contractVersionNoList;					//可选合同版本号列表
	public  List<Map<String, String>>	paymentDates;					//// 可选缴费日列表
	public	String						assignChargerIds;						//多个责任人Id
	public  String						assignChargerNames;						//多个责任人name
	public  String []					chargerNameAry;							
	public  Map<String,Object>	 		carInfoMap;								//车辆Map
	public 	boolean 					isExistContract;						// 是否存在合同
	public 	Json 						json;
//	public	Long 					driverId;
//	public  String 					certCode;
//	public 	ContractService 		contractService;

//	@Autowired
//	public void setContractService(ContractService contractService) {
//		this.contractService = contractService;
//	}
	
	@Autowired
	public void setContract4ChargerService(Contract4ChargerService contract4ChargerService) {
		this.contract4ChargerService = contract4ChargerService;
		this.setCrudService(contract4ChargerService);
	}

//	@Autowired
//	public void setContract4LabourService(Contract4LabourService contract4LabourService) {
//		this.contract4LabourService = contract4LabourService;
//	}
	
	
	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}
	
	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}
	
	@Override
	public boolean isReadonly() {
		// 经济合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4charger"),
				getText("key.role.bc.admin"));
	}
	
	@Override
	protected void afterCreate(Contract4Charger entity){
		
		super.afterCreate(entity);
		if(carId != null){
			//查找此车辆是否存在经济合同,若存在前台提示
//			isExistContract = this.contract4ChargerService.isExistContract(carId);
//			if(isExistContract == false){
				//根据carId查找车辆的车牌号码
				carInfoMap = this.contract4ChargerService.findCarByCarId(carId);
				entity.setExt_str1(isNullObject(carInfoMap.get("plate_type")+"."+carInfoMap.get("plate_no")));
				entity.setWordNo(isNullObject(carInfoMap.get("code")));//车辆自编号
				entity.setBusinessType(isNullObject(carInfoMap.get("bs_type")));//车辆自编号
//			}
		}
		
		if(driverId != null){
			//根据driverId查找车辆的车牌号码
			carInfoMap = this.contract4ChargerService.findCarByCarManId(driverId);
			if(carInfoMap != null && !carInfoMap.isEmpty()){
				entity.setExt_str1(isNullObject(carInfoMap.get("plate_type")+"."+carInfoMap.get("plate_no")));
				entity.setWordNo(isNullObject(carInfoMap.get("code")));
				entity.setBusinessType(isNullObject(carInfoMap.get("bs_type")));
				carId = Long.valueOf(isNullObject(carInfoMap.get("id")));
			}
		}
		
		
		entity.setPatchNo(this.getIdGeneratorService().next(Contract4Charger.KEY_UID));
		entity.setOpType(Contract.OPTYPE_CREATE);
		entity.setMain(Contract.MAIN_NOW);
		entity.setVerMajor(Contract.MAJOR_DEFALUT);
		entity.setVerMinor(Contract.MINOR_DEFALUT);
		entity.setUid(this.getIdGeneratorService().next(Contract4Charger.KEY_UID));
		SimpleDateFormat format4month = new SimpleDateFormat("yyyyMM");
		entity.setCode("CLHT"+format4month.format(new Date()));
		entity.setType(Contract.TYPE_CHARGER);
		entity.setStatus(Contract.STATUS_NORMAL);
		entity.setSignType(getText("contract4Charger.optype.create"));
		entity.setIncludeCost(true);
		
		// 构建附件控件
		attachsUI = buildAttachsUI(true, false);
	}
	
	@Override
	public String save() throws Exception{
		json = new Json();
		Contract4Charger e = this.getE();
		Long excludeId = null;
		// 保存之前检测自编号是否唯一:仅在新建时检测
		if (e.getId() == null) {
			excludeId = this.contract4ChargerService.checkCodeIsExist(
					e.getId(),e.getCode()); 
		}
		if(excludeId != null){
			json.put("success", false);
			json.put("msg", getText("contract4Labour.code.exist2"));
			return "json";
		}else{
			// 执行基类的保存
			this.beforeSave(e);
			
			//设置最后更新人的信息
			SystemContext context = this.getSystyemContext();
			e.setModifier(context.getUserHistory());
			e.setModifiedDate(Calendar.getInstance());
			//设置责任人姓名
			e.setExt_str2(setChargerName(assignChargerIds,assignChargerNames));
			
			this.contract4ChargerService.save(e,this.getCarId(),
					assignChargerIds,assignChargerNames);

			this.afterSave(e);
			
			json.put("id", e.getId());
			json.put("success", true);
			json.put("msg", getText("form.save.success"));
			return "json";
		}

//		//保存证件与车辆的关联表信息
//		this.contract4ChargerService.carNContract4Save(carId,getE().getId());
//		
//		//保存证件与责任人的关联表信息
//		this.contract4ChargerService.carMansNContract4Save(assignChargerIds, getE().getId());
//		//更新车辆的chager列显示责任人姓名
//		this.contract4ChargerService.updateCar4dirverName(assignChargerNames,carId);
//		//更新司机的chager列显示责任人姓名
//		this.contract4ChargerService.updateCarMan4dirverName(assignChargerNames,carId);
		
	}
	
	/**
	 * 设置责任人姓名
	 * @param assignChargerIds
	 * @param assignChargerNames
	 * @return
	 */
	private String setChargerName(String assignChargerIds,String assignChargerNames){
		String chargerName = "";
		if(assignChargerIds.length() > 0){
			String [] ids = assignChargerIds.split(",");
			String [] names = assignChargerNames.split(",");
			for(int i=0;i<ids.length;i++){ //设置责任人如:姓名1,id1;姓名2,id2;
				chargerName += names[i]+",";
				chargerName += ids[i]+";";
			}
		}
		return chargerName;
	}
	
	@Override
	protected void afterEdit(Contract4Charger e) {
		// == 合同维护处理
		setChargerList(e);
		// 构建附件控件
		attachsUI = buildAttachsUI(false, false);

		// 将次版本号加1
		if(e.getVerMinor() != null){
			e.setVerMinor(e.getVerMinor() + 1);
		}

		// 操作类型设置为维护
		e.setOpType(Contract.OPTYPE_MAINTENANCE);
	}
	
	@Override
	protected void afterOpen(Contract4Charger e) {
		// == 合同维护处理
		setChargerList(e);
		// 构建附件控件
		attachsUI = buildAttachsUI(false, true);
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		
		// 状态列表
		statusesValue		=	this.getContractStatuses();
		// 表单可选项的加载
		initSelects();
	}
	
	/** 设置责任人显示列表*/
	private void setChargerList(Contract4Charger e){
		//根据contractId查找所属的carId
		carId = this.contract4ChargerService.findCarIdByContractId(e.getId());
		//根据contractId查找所属的责任人ID列表
		List<String> chargerIdList = this.contract4ChargerService.findChargerIdByContractId(e.getId());
		//根据contractId查找所属的责任人姓名列表
		List<String> chargerNameList = this.contract4ChargerService.findChargerNameByContractId(e.getId());
		//组装责任人信息
		if((chargerIdList != null && chargerIdList.size() > 0) && (e.getExt_str2() != null && e.getExt_str2().length() > 0)){
			chargerInfoMap = new HashMap<String, String>();
			for(int i=0; i<chargerIdList.size();i++){
				chargerInfoMap.put(chargerIdList.get(i), chargerNameList.get(i));
			}
		}
	}
	
//	/** 设置责任人显示列表*/
//	private void setChargerList(Contract4Charger e){
//		//根据contractId查找所属的carId
//		carId = this.contract4ChargerService.findCarIdByContractId(e.getId());
//		//根据contractId查找所属的driverId列表
//		List<String> chargerIdList = this.contract4ChargerService.findChargerIdByContractId(e.getId());
//		if((chargerIdList != null && chargerIdList.size() > 0) && (e.getExt_str2() != null && e.getExt_str2().length() > 0)){
//			chargerNameAry = this.getE().getExt_str2().split(";");
//			chargerInfoMap = new HashMap<String, String>();
//			String names = "";
//			List<String> list = new ArrayList<String>();
//			for(int i=0; i<chargerNameAry.length;i++){
//				names = chargerNameAry[i];
//				list.add(names.split(",")[0]);
//				chargerInfoMap.put(chargerIdList.get(i), list.get(i));
//			}
//		}
//	}
	
//	private void dealCharger4Save() {
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
//	}
	
	/** 判断指定的车辆是否已经存在经济合同 */
	public String isExistContract() {
		json = new Json();
		json.put("isExistContract",
				this.contract4ChargerService.isExistContract(carId));
		return "json";
	}
	
	// ========判断经济合同自编号唯一代码开始========
	private Long excludeId;
	private String code;
	
	public Long getExcludeId() {
		return excludeId;
	}

	public void setExcludeId(Long excludeId) {
		this.excludeId = excludeId;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String checkCodeIsExist() {
		json = new Json();
		Long excludeId = this.contract4ChargerService.checkCodeIsExist(
				this.excludeId,this.code); 
		if(excludeId != null){
			json.put("id", excludeId);
			json.put("isExist", "true"); //存在重复自编号
			json.put("msg", getText("contract4Labour.code.exist"));
		}else{
			json.put("isExist", "false");
		}
		return "json";
	}
	// ========判断经济合同自编号唯一代码结束========

	private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = Contract4Charger.KEY_UID;
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

		// 只读控制
		attachsUI.setReadOnly(forceReadonly ? true : this.isReadonly());
		return attachsUI;
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(725).setMinWidth(250)
				.setMinHeight(160).setHeight(405);
		//option.addButton(new ButtonOption(getText("label.save"), "save"));
//		if (!this.isReadonly()) {
//			option.addButton(new ButtonOption(getText("label.save"), null, "bc.contractChargerForm.save"));
//		}
//		return option;
	}
	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		// 特殊处理的部分
		if (!this.isReadonly()) {// 有权限
			if (editable) {// 编辑状态显示保存按钮
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.contract4ChargerForm.save"));
			} else {// 只读状态显示操作按钮
				if(this.getE().getMain() == Contract.MAIN_NOW && this.getE().getStatus() != Contract.STATUS_LOGOUT){
					pageOption.addButton(new ButtonOption(
							getText("contract4Labour.optype.maintenance"), null,
							"bc.contract4ChargerForm.doMaintenance"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Labour.optype.renew"), null,
							"bc.contract4ChargerForm.doRenew"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Charger.optype.changeCharger"), null,
							"bc.contract4ChargerForm.doChangeCharger"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Charger.optype.changeCharger2"), null,
							"bc.contract4ChargerForm.doChangeCharger2"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Charger.logout"), null,
							"bc.contract4ChargerForm.doLogout"));
				}
			}
		}
	}


	
	// 表单可选项的加载
	public void initSelects(){
		
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CONTRACT_SIGNTYPE,
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.CONTRACT_VERSION_NO,
						OptionConstants.MOTORCADE_PAYMENT_DATE
			});
		
		// 加载可选签约类型
		this.signTypeList		=	 optionItems.get(OptionConstants.CONTRACT_SIGNTYPE);
		if(!this.getE().isNew() && this.getE().getSignType().equals(getText("contract4Labour.optype.renew"))){
			OptionItem.insertIfNotExist(signTypeList, getText("contract4Labour.optype.renew"), getText("contract4Labour.optype.renew"));
		}
		// 加载可选营运性质列表
		this.businessTypeList	=	 optionItems.get(OptionConstants.CAR_BUSINESS_NATURE);
		// 加载可选合同版本号列表
		this.contractVersionNoList = optionItems.get(OptionConstants.CONTRACT_VERSION_NO);
		// 加载缴费日列表
		this.paymentDates = optionItems.get(OptionConstants.MOTORCADE_PAYMENT_DATE);

	}
	
	
	/**
	 * 获取Contract的合同类型列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Contract.TYPE_LABOUR),
				getText("contract.select.labour"));
		types.put(String.valueOf(Contract.TYPE_CHARGER),
				getText("contract.select.charger"));
		return types;
	}

    public String isNullObject(Object obj){
    	if(null != obj){
    		return obj.toString();
    	}else{
    		return "";
    	}
    }
    
	/**
	 * 获取合同的状态列表
	 * 
	 * @return
	 */
	private Map<String, String> getContractStatuses() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Contract.STATUS_NORMAL),
				getText("contract.status.normal"));
		types.put(String.valueOf(Contract.STATUS_LOGOUT),
				getText("contract.status.logout"));
		types.put(String.valueOf(Contract.STATUS_RESGIN),
				getText("contract.status.resign"));
		return types;
	}
    
}