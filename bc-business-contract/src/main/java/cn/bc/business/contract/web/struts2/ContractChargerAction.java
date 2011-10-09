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
import cn.bc.web.ui.html.page.HtmlPage;
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
public class ContractChargerAction extends FileEntityAction<Long, Contract4Charger> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long 		serialVersionUID 			= 1L;
	public 	ContractChargerService 	contractChargerService;
	private AttachService 			attachService;
	private OptionService			optionService;
	private String					MANAGER_KEY 				= "R_ADMIN";// 管理角色的编码
	public 	boolean 	   			isManager;
	public	Long					carId; 
	public 	AttachWidget 			attachsUI;
	
	public 	Map<String,String>		statusesValue;
	
	public  List<OptionItem>		signTypeList;							// 可选签约类型
	public  List<OptionItem>		businessTypeList;						// 可选营运性质列表
//	public	Long 					carManId;
//	public  String 					certCode;
//	public	String					assignChargerIds;
//	public  Map<String,Object>	 	carInfoMap;
//	public 	ContractService 		contractService;

	@Autowired
	public void setContractChargerService(ContractChargerService contractChargerService) {
		this.contractChargerService = contractChargerService;
		this.setCrudService(contractChargerService);
	}
	
//	@Autowired
//	public void setContractService(ContractService contractService) {
//		this.contractService = contractService;
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
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(MANAGER_KEY);
	}
	
	@SuppressWarnings("static-access")
	public String create() throws Exception {
		String r = super.create();
		isManager = isReadonly();

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
		
		//根据contractId查找car信息
//		carInfoMap = this.contractService.findCarInfoByContractId(this.getId());
//		carId = Long.valueOf(carInfoMap.get("id")+"");
//		this.getE().setExt_str1(carInfoMap.get("plate_type")+" "+carInfoMap.get("plate_no"));
		// 构建附件控件
		attachsUI = buildAttachsUI(false);
		return "form";
	}
	
	@Override
	public String save() throws Exception{
		// 处理保存责任人
//		dealCharger4Save();
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		Contract4Charger e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		this.getCrudService().save(e);
		
		//保存证件与车辆的关联表信息
		if(carId != null){
			this.contractChargerService.carNContract4Save(carId,getE().getId());
		}
		
		return "saveSuccess";
		
	}
	
	// 删除
	@Override
	public String delete() throws Exception {
		if (this.getId() != null) {// 删除一条
			//单个删除中间表car_contract表
			this.contractChargerService.deleteCarNContract(this.getId());
			//单个删除本表
			this.getCrudService().delete(this.getId());
		} else {// 删除一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				//批量删除中间表car_contract表
				Long[] contractIds = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
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
		columns.add(new TextColumn("['code']", getText("contract.code"),150)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("['type']", getText("contract.type"),120)
				.setSortable(true).setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(getEntityTypes())));
		columns.add(new TextColumn("['signDate']", getText("contract.signDate"))
				.setSortable(true).setValueFormater(
						new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("['startDate']", getText("contract.deadline"),200)
				.setSortable(true).setValueFormater(
						new CalendarRangeFormater("yyyy-MM-dd") {
							@SuppressWarnings("rawtypes")
							@Override
							public Calendar getToDate(Object context,
									Object value) {
								Map contract = (Map) context;
								return (Calendar) contract.get("endDate");
							}
						}));
		columns.add(new TextColumn("['transactorName']", getText("contract.transactor")));

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
		isManager = isReadonly();
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
		return new String[] { "contract.code", "contract.wordNo", "car.plateType" , "car.plateNo" };
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
	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carId != null)
			page.setAttr("data-extras", new Json().put("carId", carId)
					.toString());
		return page;
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

}