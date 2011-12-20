/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.business.contract.service.ContractChargerService;
import cn.bc.business.contract.service.ContractLabourService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.Page;
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
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;
import cn.bc.web.ui.json.Json;

/**
 * 司机劳动合同Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ContractLabourAction extends FileEntityAction<Long, Contract4Labour> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long 			serialVersionUID 			= 1L;
	private ContractLabourService 		contractLabourService;
	private ContractChargerService		contractChargerService;
	private OptionService 				optionService;
	private AttachService 				attachService;
	private	Long 						carManId;
	private	Long 						carId;
	private	Long 						oldCarManId;							//记录旧有的carManId
	private	Long 						oldCarId;								//记录旧有的carId
	public  String 						certCode;								//证件编号
	public 	AttachWidget 				attachsUI;
	public 	Map<String,String>			statusesValue;
	public	Map<String,Object>			certInfoMap;							//证件信息map
	public	Map<String,Object>			carInfoMap;								//车辆信息map
	public	Map<String,Object>			carManInfoMap;							//司机信息map
	public	List<Map<String,Object>>	infoList;								
	
	public  List<Map<String, String>> 	businessTypeList; 						//可选营运性质列表
	public  List<Map<String, String>> 	insurancetypeList; 						//可选营运性质列表
	public  List<Map<String, String>> 	houseTypeList; 							//可选户口类型列表
	public  List<Map<String, String>> 	buyUnitList; 							//可选购买单位列表
	
	public 	boolean 					isMoreCar;								//是否存在多辆车
	public 	boolean 					isMoreCarMan;							//是否存在多个司机
	public 	boolean 					isNullCar;								//此司机没有车
	public 	boolean 					isNullCarMan;							//此车没有司机
	public 	Json 						json;
//	public	CertService				certService;
	

	@Autowired
	public void setContractLabourService(ContractLabourService contractLabourService) {
		this.contractLabourService = contractLabourService;
		this.setCrudService(contractLabourService);
	}
	
	@Autowired
	public void setContractChargerService(
			ContractChargerService contractChargerService) {
		this.contractChargerService = contractChargerService;
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

	public Long getCarManId() {
		return carManId;
	}

	public void setCarManId(Long carManId) {
		this.carManId = carManId;
	}
	
	public Long getOldCarManId() {
		return oldCarManId;
	}

	public void setOldCarManId(Long oldCarManId) {
		this.oldCarManId = oldCarManId;
	}

	public Long getOldCarId() {
		return oldCarId;
	}

	public void setOldCarId(Long oldCarId) {
		this.oldCarId = oldCarId;
	}
	
	@Override
	public boolean isReadonly() {
		// 劳动合同管理员或系统管理员
//		SystemContext context = (SystemContext) this.getContext();
//		return !context.hasAnyRole(getText("key.role.bs.contract4labour"),
//				getText("key.role.bc.admin"));
		if(!this.getE().isNew()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isRole() {
		// 劳动合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
	}
	
	public String create() throws Exception {
		String r = super.create();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		Calendar date2 = Calendar.getInstance();
		
		if(carId != null && carManId == null){
			//根据carId查找车辆以及司机id
			carInfoMap = this.contractLabourService.findCarByCarId(carId);
			infoList = this.contractLabourService.selectRelateCarManByCarId(carId);
			
			if(infoList.size() == 1){
				//填写司机信息
				carManId = Long.valueOf(isNullObject(infoList.get(0).get("driver_id")));
				this.getE().setExt_str2(isNullObject(infoList.get(0).get("name")));
				this.getE().setSex(Integer.valueOf(infoList.get(0).get("sex")+""));
				this.getE().setCertNo(isNullObject(infoList.get(0).get("cert_fwzg")));
				this.getE().setCertIdentity(isNullObject(infoList.get(0).get("cert_identity")));
				this.getE().setOrigin(isNullObject(infoList.get(0).get("origin")));
				this.getE().setHouseType(isNullObject(infoList.get(0).get("house_type")));
				
				if(this.getE().getAge() == null && getDateToString(infoList.get(0).get("birthdate")).length() > 0){
					date = sdf.parse(infoList.get(0).get("birthdate")+"");
					date2.setTime(date);
					this.getE().setBirthDate(date2);
				}
				
				this.getE().setAge(Integer.valueOf(getBirthDateToString(infoList.get(0).get("birthdate"))));
			}else if(infoList.size() > 1){
				isMoreCarMan = true;
			}else{
				isNullCarMan = true;
			}
			//填写车辆信息
			this.getE().setExt_str1(
				isNullObject(carInfoMap.get("plate_type"))+"."+
				isNullObject(carInfoMap.get("plate_no"))
			);
			if(getDateToString(carInfoMap.get("register_date")).length() > 0){
				date = sdf.parse(carInfoMap.get("register_date")+"");
				date2.setTime(date);
				this.getE().setRegisterDate(date2);
			}
			this.getE().setBsType(isNullObject(carInfoMap.get("bs_type")));
		}
		
		
		if(carManId != null && carId == null){
			//根据carManId查找司机以及车辆id
			carManInfoMap = this.contractLabourService.findCarManByCarManId(carManId);
			infoList = this.contractLabourService.selectRelateCarByCarManId(carManId);
			if(infoList.size() == 1){
				carId = Long.valueOf(isNullObject(infoList.get(0).get("car_id")));
				//填写车辆信息
				this.getE().setExt_str1(
					isNullObject(infoList.get(0).get("plate_type"))+"."+
					isNullObject(infoList.get(0).get("plate_no"))
				);
				if(this.getE().getAge() == null && getDateToString(infoList.get(0).get("register_date")).length() > 0){
					date = sdf.parse(infoList.get(0).get("register_date")+"");
					date2.setTime(date);
					this.getE().setRegisterDate(date2);
				}
				this.getE().setBsType(isNullObject(infoList.get(0).get("bs_type")));
			}else if(infoList.size() > 1){
				isMoreCar = true;
			}else{
				isNullCar = true;
			}
			//填写司机信息
			this.getE().setExt_str2(isNullObject(carManInfoMap.get("name")));
			this.getE().setSex(Integer.valueOf(carManInfoMap.get("sex")+""));
			this.getE().setCertNo(isNullObject(carManInfoMap.get("cert_fwzg")));
			this.getE().setCertIdentity(isNullObject(carManInfoMap.get("cert_identity")));
			this.getE().setOrigin(isNullObject(carManInfoMap.get("origin")));
			this.getE().setHouseType(isNullObject(carManInfoMap.get("house_type")));
			
			if(getDateToString(carManInfoMap.get("birthdate")).length() > 0){
				date = sdf.parse(carManInfoMap.get("birthdate")+"");
				date2.setTime(date);
				this.getE().setBirthDate(date2);
			}
			
			this.getE().setAge(Integer.valueOf(getBirthDateToString(carManInfoMap.get("birthdate"))));
		}
		
		// 自动生成UID
		this.getE().setUid(this.getIdGeneratorService().next(Contract4Labour.KEY_UID));
		// 自动生成合同编号
		this.getE().setCode(this.getIdGeneratorService().nextSN4Month(Contract4Labour.KEY_CODE));
		// 自动生成批号
		this.getE().setPatchNo(this.getIdGeneratorService().next(Contract4Labour.KEY_UID));
		this.getE().setType(Contract.TYPE_LABOUR);
		this.getE().setOpType(Contract.OPTYPE_CREATE);
		this.getE().setMain(Contract.MAIN_NOW);
		this.getE().setVerMajor(Contract.MAJOR_DEFALUT);
		this.getE().setVerMinor(Contract.MINOR_DEFALUT);
		this.getE().setInsuranceType(getText("contract.wujin"));
		this.getE().setBuyUnit(getText("contract.baocheng"));
		this.getE().setStatus(Contract.STATUS_NORMAL);
		
		statusesValue  =	this.getEntityStatuses();
		
		// 表单可选项的加载
		initSelects();
		// 构建附件控件
		attachsUI = buildAttachsUI(true);

		return r;
	}
	
	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		
		//计算年龄
//		String age = getBirthDateToString(this.getE().getBirthDate().getTime());
//		if(age.length() > 0){
//			this.getE().setAge(Integer.valueOf(age));
//		}
		// 表单可选项的加载
		this.formPageOption = 	buildFormPageOption();
		statusesValue		=	this.getEntityStatuses();
		initSelects();
		
		// 构建附件控件
		attachsUI = buildAttachsUI(false);
		//根据contractId查找所属的carId
		carId = this.contractLabourService.findCarIdByContractId(this.getE().getId());
		oldCarId = carId;
		//根据contractId查找所属的carManId
		carManId = this.contractLabourService.findCarManIdByContractId(this.getE().getId());
		oldCarManId = carManId;
		
		return "form";
	}
	
	@Override
	public String save() throws Exception{
		if(this.getE().getOpType() != Contract.OPTYPE_CREATE && this.getE().getOpType() != Contract.OPTYPE_RESIGN){
			saveSwitch(true);
		}else{
			saveSwitch(false);
			if(this.getE().getOpType() == Contract.OPTYPE_RESIGN){	//提示离职成功信息
				json = new Json();
				json.put("id",  this.getE().getId()+"");
				json.put("msg", getText("contract.labour.resign.success"));
				return "json";
			}
		}
		return "saveSuccess";
	}
	
	public void saveSwitch(boolean flag){
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		Contract4Labour e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		if(e.getOpType() > 0 && e.getOpType() == Contract.OPTYPE_RESIGN){ //操作状态离职的话,把状态设为离职
			e.setStatus(Contract.STATUS_RESGIN);
		}
		
		if(e.getUid().length() <= 0){ //如果UID为空重新设置
			e.setUid(this.getIdGeneratorService().next(Contract4Labour.KEY_UID));
			e.setAuthor(context.getUserHistory());
		}
		
		//保存新的记录
		this.getCrudService().save(e);
		
		//保存合同与车辆的关联表信息
		if(oldCarId != null){ //避免重复插入做相应处理
			if((null != e.getPid() && e.getPid() != e.getId() && !e.getPid().equals(e.getId())) || 
				(oldCarId != carId && !oldCarId.equals(carId))){
				this.contractLabourService.carNContract4Save(carId,getE().getId());
			}
		}else{
			this.contractLabourService.carNContract4Save(carId,getE().getId());
		}
		//保存合同与司机的关联表信息
		if(oldCarManId != null){ //避免重复插入做相应处理
			if((null != e.getPid() && e.getPid() != e.getId() && !e.getPid().equals(e.getId())) || 
				(oldCarManId != carManId && !oldCarManId.equals(carManId))){
				this.contractLabourService.carManNContract4Save(carManId,getE().getId());
			}
		}else{
			this.contractLabourService.carManNContract4Save(carManId,getE().getId());
		}
		
		if(flag){
			// flag为true即(维护,转车,续约) 要做相应处理
			if(e.getPid() != null){
				Contract4Labour oldE = contractLabourService.load(e.getPid());
				if(oldE != null){
//					if(e.getOpType() != Contract.OPTYPE_RENEW){	//如果是续约操作类型,把旧的合同状态改为注销并保存
//						oldE.setStatus(Contract.STATUS_FAILURE);
//					}
					oldE.setStatus(Contract.STATUS_FAILURE); //把旧的合同状态改为注销并保存
					oldE.setMain(Contract.MAIN_HISTORY); //设定为历史标识
					//保存旧的记录
					this.getCrudService().save(oldE);
				}
			}
		}

	}
	
	// 删除
	@Override
	public String delete() throws Exception {
		if (this.getId() != null) {// 删除一条
			deleteAll(this.getId());
		}else{// 删除一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				//批量删除中间表car_contract表
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				deleteAll(ids);
			}else{
				throw new CoreException("must set property id or ids");
			}
		}
		return "deleteSuccess";
	}
	
	/**
	 * 单条删除所有历史版本合同
	 * 
	 * @return
	 */
	public void deleteAll(Long id){
		Contract c = this.contractLabourService.load(id);
		//单个删除工伤表industrial_injury表
		this.contractLabourService.deleteInjury(id);
		//单个删除中间表carman_contract表
		this.contractLabourService.deleteCarManNContract(id);
		//单个删除中间表car_contract表
		this.contractChargerService.deleteCarNContract(id);
		//单个删除本表
		this.getCrudService().delete(id);
		if(c != null && c.getPid() != null){ //如有父级ID,递归删除父级记录
			deleteAll(c.getPid());
		}
	}
	
	/**
	 * 批量删除所有历史版本合同
	 * 
	 * @return
	 */
	public void deleteAll(Long[] ids){
		List<Contract> objList = new ArrayList<Contract>();
		for(Long id : ids){//通过id查找对应的对象并且存放再objList
			Contract c = this.contractLabourService.load(id);
			objList.add(c);
		}
		//批量删除工伤表industrial_injury表
		this.contractLabourService.deleteInjury(ids);
		//批量删除中间表carman_contract表
		this.contractLabourService.deleteCarManNContract(ids);
		//批量删除中间表car_contract表
		this.contractChargerService.deleteCarNContract(ids);
		//批量删除本表
		this.getCrudService().delete(ids);
		List<Long> idList = new ArrayList<Long>();
		for(Contract c : objList){//遍历objList里面的对象父级ID是否为空,不为空的放进idList
			if(c != null && c.getPid() != null){
				idList.add(c.getPid());
			}
		}
		if(idList != null && idList.size() > 0){ //idList不为空执行递归批量删除方法
			deleteAll(idList.toArray(new Long [0]));
		}
	}
	
	/**
	 * 根据请求的条件查找非分页信息对象
	 * 
	 * @return
	 */
	@Override
	protected List<Map<String, Object>> findList() {
		return this.contractLabourService.list4carMan(this.getCondition(),carManId);
	}
	
	/**
	 * 根据请求的条件查找分页信息对象
	 * 
	 * @return
	 */
	protected Page<Map<String,Object>> findPage() {
		return this.contractLabourService.page4carMan(
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
		columns.add(new TextColumn("['ext_str2']", getText("contract.labour.driver"),80));
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
	
	private AttachWidget buildAttachsUI(boolean isNew) {
		// 构建附件控件
		String ptype = "contractLabour.main";
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
		PageOption option =	super.buildFormPageOption().setWidth(725).setHeight(460);
		if (!this.isRole()) {
			ButtonOption buttonOption = new ButtonOption(getText("label.save"), "save");
			buttonOption.put("id", "bcSaveBtn");
			option.addButton(buttonOption);
			if(!this.getE().isNew() && this.getE().getMain() == Contract.MAIN_NOW){
				ToolbarMenuButton toolbarMenuButton = new ToolbarMenuButton(getText("contract.labour.op"));
				toolbarMenuButton.setId("bcOpBtn");
				toolbarMenuButton.addMenuItem(getText("contract.labour.optype.edit"),Contract.OPTYPE_EDIT+"")
								.addMenuItem(getText("contract.labour.optype.transfer"),Contract.OPTYPE_TRANSFER+"")
								.addMenuItem(getText("contract.labour.optype.renew"),Contract.OPTYPE_RENEW+"")
								.addMenuItem(getText("contract.labour.optype.resign"),Contract.OPTYPE_RESIGN+"")
								.setChange("bc.contractLabourForm.selectMenuButtonItem");
				option.addButton(
						toolbarMenuButton
				);
			}
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("['code']");
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
	
	public String certInfo(){
//		Cert cert = this.certService.findCertByCarManId(carManId);
//		json = new Json();
//		if(cert != null && cert.getCertCode().length() > 0){
//			certCode = cert.getCertCode();
//			json.put("certCode", certCode);
//		}
		certInfoMap	= this.contractLabourService.findCertByCarManId(carManId);
		json = new Json();
		if(certInfoMap != null && certInfoMap.size() > 0){
			certCode = certInfoMap.get("cert_code")+"";
			json.put("cert_code", certCode);
		}
		return "json";
	}
	
	public String carManInfo(){
		
		json = new Json();
		infoList = this.contractLabourService.selectRelateCarManByCarId(carId);
		if(infoList.size() == 1){
			json.put("id",isNullObject(infoList.get(0).get("driver_id")));
			json.put("name",isNullObject(infoList.get(0).get("name")));
			json.put("certNo",isNullObject(infoList.get(0).get("cert_fwzg")));
			json.put("isMore","false");
		}
		if(infoList.size() > 1){
			json.put("isMore","true");
		}
		return "json";
	}
	
	/** 根据车辆ID查找关联的司机否存在劳动合同 */
	public String isExistContract(){
		json = new Json();
		List<Map<String,Object>> list = this.contractLabourService.findCarManIsExistContract(carManId);
		if(list.size() > 0){
			json.put("isExistContract", "true");
		}else{
			json.put("isExistContract", "false");
		}
		return "json";
	}
	
	//复写搜索URL方法
	protected String getEntityConfigName() {
		return "contractLabour";
	}
	
//	@Override
//	protected HtmlPage buildHtml4Paging() {
//		HtmlPage page = super.buildHtml4Paging();
//		if (carManId != null)
//			page.setAttr("data-extras", new Json().put("carManId", carManId)
//					.toString());
//		return page;
//	}
	
	// 表单可选项的加载
	public void initSelects(){
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.LB_INSURANCETYPE,
						OptionConstants.CARMAN_HOUSETYPE,
						OptionConstants.LB_BUYUNIT
				});
		
		// 加载可选营运性质列表
		this.businessTypeList  = optionItems.get(OptionConstants.CAR_BUSINESS_NATURE);
		// 加载可选社保险种列表
		this.insurancetypeList = optionItems.get(OptionConstants.LB_INSURANCETYPE); 
		OptionItem.insertIfNotExist(this.insurancetypeList, this.getE().getInsuranceType(), null);
		// 加载可选户口类型列表
		this.houseTypeList = optionItems.get(OptionConstants.CARMAN_HOUSETYPE);
		// 加载可选购买单位列表
		this.buyUnitList = optionItems.get(OptionConstants.LB_BUYUNIT); 
		OptionItem.insertIfNotExist(this.buyUnitList, this.getE().getBuyUnit(), null);
		
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
	
	/**
	 * 获取Contract的状态列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityStatuses() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Contract.STATUS_NORMAL),
				getText("contract.normal"));
		types.put(String.valueOf(Contract.STATUS_FAILURE),
				getText("contract.resign"));
		types.put(String.valueOf(Contract.STATUS_RESGIN),
				getText("contract.resign"));
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
     * 格式化日期
     * @return
     */
    public String getDateToString(Object object){
    	if(null != object){
	    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    	StringBuffer str = new StringBuffer(df.format(object));
	        return str.toString();
    	}else{
    		return "";
    	}
    }
    
    /**
     * 计算当前岁数
     * @return
     */
    public String getBirthDateToString(Object object){
    	String birthDay = getDateToString(object);
		if(birthDay.length() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			Date myDate = null;
			try {
				myDate = sdf.parse(birthDay);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long day = (date.getTime() - myDate.getTime())/(24*60*60*1000) + 1;
			birthDay = new DecimalFormat("#,00").format(day/365f);
			birthDay = birthDay.split(",")[0];
//			//得到当前的年份
//			String cYear = sdf.format(new Date()).substring(0,4);
//			//得到生日年份
//			String birthYear = birthDay.substring(0,4);
//			//计算当前年龄
//			int age = Integer.parseInt(cYear) - Integer.parseInt(birthYear);
//			birthDay = age+"";
		}
    	return birthDay;
    }

}