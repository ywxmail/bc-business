/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
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
import cn.bc.business.contract.service.ContractChargerService;
import cn.bc.business.contract.service.ContractLabourService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.Page;
import cn.bc.core.RichEntityImpl;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.CalendarRangeFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 经济合同Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ContractChargerAction extends FileEntityAction<Long, Contract4Charger> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long 		serialVersionUID 			= 1L;
	private ContractChargerService 	contractChargerService;
	private ContractLabourService   contractLabourService;
	private AttachService 			attachService;
	private OptionService			optionService;
	public	Long					carId; 
	public	Long					carManId;
	private	Long 					oldCarId;
	public 	AttachWidget 			attachsUI;
	
	public 	Map<String,String>		statusesValue;
	public	Map<String,String>		chargerInfoMap;
	
	public  List<OptionItem>		signTypeList;							// 可选签约类型
	public  List<OptionItem>		businessTypeList;						// 可选营运性质列表
	public	String					assignChargerIds;
	public  String					assignChargerNames;
	public  String []				chargerNameAry;
	public  Map<String,Object>	 	carInfoMap;
//	public	Long 					carManId;
//	public  String 					certCode;
//	public 	ContractService 		contractService;

//	@Autowired
//	public void setContractService(ContractService contractService) {
//		this.contractService = contractService;
//	}
	
	@Autowired
	public void setContractChargerService(ContractChargerService contractChargerService) {
		this.contractChargerService = contractChargerService;
		this.setCrudService(contractChargerService);
	}

	@Autowired
	public void setContractLabourService(ContractLabourService contractLabourService) {
		this.contractLabourService = contractLabourService;
	}
	
	
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
	
	public Long getOldCarId() {
		return oldCarId;
	}

	public void setOldCarId(Long oldCarId) {
		this.oldCarId = oldCarId;
	}

	@Override
	public boolean isReadonly() {
		// 经济合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4charger"),
				getText("key.role.bc.admin"));
	}
	
	@SuppressWarnings("static-access")
	public String create() throws Exception {
		String r = super.create();

		if(carId != null){
			//根据carId查找车辆的车牌号码
			carInfoMap = this.contractChargerService.findCarByCarId(carId);
			this.getE().setExt_str1(isNullObject(carInfoMap.get("plate_type")+"."+carInfoMap.get("plate_no")));
		}
		
		if(carManId != null){
			//根据carManId查找车辆的车牌号码
			carInfoMap = this.contractChargerService.findCarByCarManId(carManId);
			this.getE().setExt_str1(isNullObject(carInfoMap.get("plate_type")+"."+carInfoMap.get("plate_no")));
			carId = Long.valueOf(isNullObject(carInfoMap.get("id")));
		}
		
		
		
		this.getE().setCode(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		this.getE().setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		this.getE().setType(Contract.TYPE_CHARGER);
		this.getE().setStatus(RichEntityImpl.STATUS_ENABLED);
		statusesValue		=	this.getEntityStatuses();
		
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
		
		//根据contractId查找car信息
//		carInfoMap = this.contractService.findCarInfoByContractId(this.getId());
//		carId = Long.valueOf(carInfoMap.get("id")+"");
//		this.getE().setExt_str1(carInfoMap.get("plate_type")+" "+carInfoMap.get("plate_no"));
		
		
		
		Contract4Charger e = this.getE();
		//根据contractId查找所属的carId
		carId = this.contractChargerService.findCarIdByContractId(e.getId());
		oldCarId = carId;
		//根据contractId查找所属的carManId列表
		List<String> chargerIdList = this.contractChargerService.findChargerIdByContractId(e.getId());
		if((chargerIdList != null && chargerIdList.size() > 0) && (e.getExt_str2() != null && e.getExt_str2().length() > 0)){
			chargerNameAry = this.getE().getExt_str2().split(",");
			chargerInfoMap = new HashMap<String, String>();
			for(int i=0; i<chargerIdList.size(); i++){
				chargerInfoMap.put(chargerIdList.get(i), chargerNameAry[i]);
			}
		}

		return "form";
	}
	
	@Override
	public String save() throws Exception{
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		Contract4Charger e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		
		e.setExt_str2(assignChargerNames);
		this.getCrudService().save(e);

		//保存证件与车辆的关联表信息
		if(oldCarId != null){
			if(oldCarId != carId && !oldCarId.equals(carId)){
				this.contractChargerService.carNContract4Save(carId,getE().getId());
			}
		}else{
			this.contractChargerService.carNContract4Save(carId,getE().getId());
		}
		
		//保存证件与责任人的关联表信息
		this.contractChargerService.carMansNContract4Save(assignChargerIds, getE().getId());
		//更新车辆的chager列显示责任人姓名
		this.contractChargerService.updateCar4dirverName(assignChargerNames,carId);
		//更新司机的chager列显示责任人姓名
		this.contractChargerService.updateCarMan4dirverName(assignChargerNames,carId);
		
		return "saveSuccess";
		
	}
	
	// 删除
	@Override
	public String delete() throws Exception {
		if (this.getId() != null) {// 删除一条
			//单个删除中间表car_contract表
			this.contractChargerService.deleteCarNContract(this.getId());
			//单个删除中间表carman_contract表
			this.contractLabourService.deleteCarManNContract(this.getId());
			//单个删除本表
			this.getCrudService().delete(this.getId());
		} else {// 删除一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] contractIds = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				//批量删除中间表car_contract表
				this.contractLabourService.deleteCarManNContract(contractIds);
				//批量删除中间表carman_contract表
				this.contractChargerService.deleteCarNContract(contractIds);
				//批量删除本表
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.getCrudService().delete(ids);
			} else {
				throw new CoreException("must set property id or ids");
			}
		}
		return "deleteSuccess";
	}
	
	/**
	 * 根据请求的条件查找非分页信息对象
	 * 
	 * @return
	 */
	@Override
	protected List<Map<String, Object>> findList() {
		return this.contractChargerService.list4car(this.getCondition(),carId);
	}
	
	/**
	 * 根据请求的条件查找分页信息对象
	 * 
	 * @return
	 */
	protected Page<Map<String,Object>> findPage() {
		return this.contractChargerService.page4car(
					this.getCondition(),this.getPage().getPageNo(), 
					this.getPage().getPageSize());
	}
	
	@Override
	protected List<Column> buildGridColumns() {
		//List<Column> columns = super.buildGridColumns();
		List<Column> columns = new ArrayList<Column>();
		columns.add(new TextColumn("['id']", "ID",20));
		columns.add(new TextColumn("['type']", getText("contract.type"),80)
				.setSortable(true).setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(getEntityTypes())));
		columns.add(new TextColumn("['ext_str1']", getText("contract.car"),80));
		columns.add(new TextColumn("['ext_str2']", getText("contract.charger.charger"),140));
		columns.add(new TextColumn("['transactorName']", getText("contract.transactor"),60));
		columns.add(new TextColumn("['signDate']", getText("contract.signDate"),90)
				.setSortable(true).setValueFormater(
						new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("['startDate']", getText("contract.deadline"))
				.setValueFormater(
						new CalendarRangeFormater("yyyy-MM-dd") {
							@SuppressWarnings("rawtypes")
							@Override
							public Calendar getToDate(Object context,
									Object value) {
								Map contract = (Map) context;
								return (Calendar) contract.get("endDate");
							}
						}));
		columns.add(new TextColumn("['code']", getText("contract.code"),120)
				.setUseTitleFromLabel(true));

		return columns;
	}
	
	
	
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

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("['code']");
	}


	@SuppressWarnings("static-access")
	private AttachWidget buildAttachsUI(boolean isNew) {
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
		return super.buildListPageOption().setWidth(850).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "contract.code", "contract.wordNo" , "contract.ext_str1", "contract.ext_str2"};
	}
	
	
	// 表单可选项的加载
	public void initSelects(){
		// 加载可选签约类型
		this.signTypeList		=	this.optionService.findOptionItemByGroupKey(OptionConstants.CONTRACT_SIGNTYPE);
		// 加载可选营运性质列表
		this.businessTypeList	=	this.optionService.findOptionItemByGroupKey(OptionConstants.CAR_BUSINESS_NATURE);

	}
	
	//复写搜索URL方法
	protected String getEntityConfigName() {
		return "contractCharger";
	}
	
//	// 视图特殊条件
//	@Override
//	protected Condition getSpecalCondition() {
//		if (carId != null) {
//			return new EqualsCondition("carId", carId);
//		} else {
//			return null;
//		}
//	}
//	
//
/*	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carId != null)
			page.setAttr("data-extras", new Json().put("carId", carId)
					.toString());
		return page;
	}*/
	
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
}