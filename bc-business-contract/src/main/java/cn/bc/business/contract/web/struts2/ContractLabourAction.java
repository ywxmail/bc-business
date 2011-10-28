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

import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Labour;
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
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.CalendarRangeFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
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
	private AttachService 				attachService;
	private	Long 						carManId;
	private	Long 						carId;
	private	Long 						oldCarManId;
	private	Long 						oldCarId;
	public  String 						certCode;
	public 	AttachWidget 				attachsUI;
	public 	Map<String,String>			statusesValue;
	public	Map<String,Object>			certInfoMap;
	public	Map<String,Object>			carInfoMap;
	public	Map<String,Object>			carManInfoMap;
	public	List<Map<String,Object>>	infoList;
	public 	boolean 					isMoreCar;
	public 	boolean 					isMoreCarMan;
	public 	boolean 					isNullCar;
	public 	boolean 					isNullCarMan;
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
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
	}
	

	@SuppressWarnings("static-access")
	public String create() throws Exception {
		String r = super.create();
		
		
		if(carId != null && carManId == null){
			//根据carId查找车辆以及司机id
			carInfoMap = this.contractLabourService.findCarByCarId(carId);
			infoList = this.contractLabourService.selectRelateCarManByCarId(carId);
			if(infoList.size() == 1){
				carManId = Long.valueOf(isNullObject(infoList.get(0).get("driver_id")));
				this.getE().setExt_str2(isNullObject(infoList.get(0).get("name")));
				this.getE().setCertNo(isNullObject(infoList.get(0).get("cert_fwzg")));
			}else if(infoList.size() > 1){
				isMoreCarMan = true;
			}else{
				isNullCarMan = true;
			}
			this.getE().setExt_str1(
				isNullObject(carInfoMap.get("plate_type"))+"."+
				isNullObject(carInfoMap.get("plate_no"))
			);
		}
		
		if(carManId != null && carId == null){
			//根据carManId查找司机以及车辆id
			carManInfoMap = this.contractLabourService.findCarManByCarManId(carManId);
			infoList = this.contractLabourService.selectRelateCarByCarManId(carManId);
			if(infoList.size() == 1){
				carId = Long.valueOf(isNullObject(infoList.get(0).get("car_id")));
				this.getE().setExt_str1(
					isNullObject(infoList.get(0).get("plate_type"))+"."+
					isNullObject(infoList.get(0).get("plate_no"))
				);
			}else if(infoList.size() > 1){
				isMoreCar = true;
			}else{
				isNullCar = true;
			}
			this.getE().setExt_str2(isNullObject(carManInfoMap.get("name")));
			this.getE().setCertNo(isNullObject(carManInfoMap.get("cert_fwzg")));
		}
		
		// 自动生成自编号
		this.getE().setUid(this.getIdGeneratorService().next(this.getE().KEY_UID));
		this.getE().setCode(this.getIdGeneratorService().nextSN4Month(Contract4Labour.KEY_CODE));
		this.getE().setType(Contract.TYPE_LABOUR);
		this.getE().setStatus(RichEntityImpl.STATUS_ENABLED);
		statusesValue		=	this.getEntityStatuses();
		
		attachsUI = buildAttachsUI(true);

		return r;
	}
	
	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		// 表单可选项的加载
		this.formPageOption = 	buildFormPageOption();
		statusesValue		=	this.getEntityStatuses();
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
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		Contract4Labour e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		
		this.getCrudService().save(e);
		
		//保存合同与车辆的关联表信息
		if(oldCarId != null){
			if(oldCarId != carId && !oldCarId.equals(carId)){
				this.contractLabourService.carNContract4Save(carId,getE().getId());
			}
		}else{
			this.contractLabourService.carNContract4Save(carId,getE().getId());
		}
		//保存合同与司机的关联表信息
		if(oldCarManId != null){
			if(oldCarManId != carManId && !oldCarManId.equals(carManId)){
				this.contractLabourService.carManNContract4Save(carManId,getE().getId());
			}
		}else{
			this.contractLabourService.carManNContract4Save(carManId,getE().getId());
		}
		
		
		return "saveSuccess";
		
	}
	
	// 删除
	@Override
	public String delete() throws Exception {
		if (this.getId() != null) {// 删除一条
			//单个删除中间表carman_contract表
			this.contractLabourService.deleteCarManNContract(this.getId());
			//单个删除中间表car_contract表
			this.contractChargerService.deleteCarNContract(this.getId());
			//单个删除本表
			this.getCrudService().delete(this.getId());
		} else {// 删除一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				//批量删除中间表car_contract表
				Long[] contractIds = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.contractLabourService.deleteCarManNContract(contractIds);
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
				.setMinHeight(160).setModal(false).setHeight(500);
		option.addButton(new ButtonOption(getText("label.save"), "save"));
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
	
	public Json json;
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