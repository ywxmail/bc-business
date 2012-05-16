/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.runcase.domain.Case4Praise;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.runcase.service.CasePraiseService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

/**
 * 表扬Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CasePraiseAction extends FileEntityAction<Long, Case4Praise> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static 	final long 				serialVersionUID 	= 1L;
	public  Long 							carId;
	public  Long 							carManId;
	public  boolean 						isMoreCar;
	public  boolean 						isMoreCarMan;
	public  boolean 						isNullCar;
	public  boolean 						isNullCarMan;
	private	boolean							multiple;
   //public  String							isClosed;	
	@SuppressWarnings("unused")
	private CasePraiseService				casePraiseService;
	private MotorcadeService	 			motorcadeService;
	private OptionService					optionService;
	private CarManService 					carManService;
	private CarService 						carService;
	private String							sourceStr;

	//public  List<Map<String, String>>			dutyList;						// 可选责任列表
	//public  List<Map<String, String>> 		degreeList; 					// 可选程度列表
	//public  List<Map<String, String>> 		certList; 						// 可选没收证件列表
	public 	List<Map<String, String>> 		motorcadeList;					// 可选车队列表
	public  List<Map<String, String>> 		sourceList; 					// 可选投诉建议来源
	public  List<Map<String, String>> 		typeList; 						// 可选表扬类型
	
	
	public 	Map<String,String> 				statusesValue;
	public	Map<String,String>				sourcesValue;
	private Map<String, List<Map<String, String>>> 			allList;

	
	
	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
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
	
	public String getSourceStr() {
		return sourceStr;
	}

	public void setSourceStr(String sourceStr) {
		this.sourceStr = sourceStr;
	}

	@Autowired
	public void setCasePraiseService(CasePraiseService casePraiseService) {
		this.casePraiseService = casePraiseService;
		this.setCrudService(casePraiseService);
	}
	
//	@Autowired
//	public void setCaseBaseService(CaseBaseService caseBaseService) {
//		this.caseBaseService = caseBaseService;
//	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}
	
	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}
	
	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}
	
	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("status", Direction.Asc).add("fileDate", Direction.Desc);
	}
	
	//复写搜索URL方法
	protected String getEntityConfigName() {
		return "casePraise";
	}

	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.praise"),
				getText("key.role.bc.admin"));
	}
	
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(825).setMinWidth(250).setHeight(480)
				.setMinHeight(200);
	}

	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		
		if (this.useFormPrint()) {
			// 添加打印按钮
			pageOption.addButton(this.getDefaultPrintButtonOption());
		}

		if (editable && !readonly) {
			//特殊处理结案按钮
			if(Case4Praise.STATUS_ACTIVE == getE().getStatus() && !getE().isNew()){
				ButtonOption buttonOption = new ButtonOption(getText("label.closefile"),null,"bc.casePraiseForm.closefile");
				buttonOption.put("id", "bcSaveDlgButton");
				pageOption.addButton(buttonOption);
			}
			if(Case4Praise.STATUS_CLOSED != getE().getStatus()){
				// 添加默认的保存按钮
				pageOption.addButton(this.getDefaultSaveButtonOption());
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected void afterCreate(Case4Praise entity) {
		super.afterCreate(entity);
		
		if (carManId != null) {
			CarMan driver = this.carManService.load(carManId);
			List<Car> car = this.carService.selectAllCarByCarManId(carManId);
			if (car.size() == 1) {
				this.getE().setCarId(car.get(0).getId());
				this.getE().setCarPlate(
						car.get(0).getPlateType() + "."
								+ car.get(0).getPlateNo());
				this.getE().setMotorcadeId(car.get(0).getMotorcade().getId());
				this.getE().setMotorcadeName(car.get(0).getMotorcade().getName());
			} else if (car.size() > 1) {
				isMoreCar = true;
			} else {
				isNullCar = true;
			}
			this.getE().setDriverId(carManId);
			this.getE().setDriverName(driver.getName());
			this.getE().setDriverCert(driver.getCert4FWZG());
		}
		if (carId != null) {
			Car car = this.carService.load(carId);
			this.getE()
					.setCarPlate(car.getPlateType() + "." + car.getPlateNo());
			this.getE().setCarId(carId);
			this.getE().setMotorcadeId(car.getMotorcade().getId());
			this.getE().setMotorcadeName(car.getMotorcade().getName());
			List<CarMan> carMan = this.carManService
					.selectAllCarManByCarId(carId);
			if (carMan.size() == 1) {
				this.getE().setDriverName(carMan.get(0).getName());
				this.getE().setDriverId(carMan.get(0).getId());
				this.getE().setDriverCert(carMan.get(0).getCert4FWZG());
			} else if (carMan.size() > 1) {
				isMoreCarMan = true;
			} else {
				isNullCarMan = true;
			}
		}
		
		// 初始化信息
		this.getE().setType  (CaseBase.TYPE_COMPLAIN);
		this.getE().setStatus(CaseBase.STATUS_ACTIVE);
		this.getE().setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		// 自动生成自编号
		this.getE().setCode(
				this.getIdGeneratorService().nextSN4Month(Case4Praise.KEY_CODE));
		
		sourceStr = getSourceStatuses().get(this.getE().getSource()+"");

	}
	
	
	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		
		sourceStr = getSourceStatuses().get(this.getE().getSource()+"");
		// 表单可选项的加载
		this.formPageOption = 	buildFormPageOption(true);
		// 初始化表单的其他配置
		this.initForm(true);
		return "form";
	}
	
	@Override
	public String save() throws Exception{
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		Case4Praise e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		
		//设置结案信息
		if(e.getStatus() == 1){
			e.setStatus(CaseBase.STATUS_CLOSED);
			e.setCloserId(context.getUserHistory().getId());
			e.setCloserName(context.getUserHistory().getName());
			e.setCloseDate(Calendar.getInstance(Locale.CHINA));
		}
		
		this.getCrudService().save(e);
		
		return "saveSuccess";
	}
	
	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		statusesValue		=	this.getCaseStatuses();
		sourcesValue		=	this.getSourceStatuses();
		
		// 表单可选项的加载
		initSelects();
		
	}
/*
 *  业务变更注释
	public Json json;
	public String closefile(){
		SystemContext context = this.getSystyemContext();
		
		this.getE().setStatus(CaseBase.STATUS_CLOSED);
		this.getE().setCloserId(context.getUserHistory().getId());
		this.getE().setCloserName(context.getUserHistory().getName());
		this.getE().setCloseDate(Calendar.getInstance(Locale.CHINA));
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");   
		String closeDateStr = df.format(this.getE().getCloseDate().getTime());
		
		json = new Json();
		json.put("status", this.getE().getStatus());
		json.put("closeDate", closeDateStr);
		json.put("closeId",   this.getE().getCloserId());
		json.put("closeName", this.getE().getCloserName());
		return "json";
	}
	
	public String getCarNManInfo(){
		if(carId != null){	//从页面ajax请求carId参数不为空时通过carManId查找关联司机信息
			this.caseBaseService.findCarManNameNCertCodeByCarId(carId);
		}
		if(carManId != null){ //从页面ajax请求carManId参数不为空时通过carId查找关联车辆信息
			this.caseBaseService.findCarPlateNCertCodeByCarManId(carManId);
		}
		return "json";
	}
	
*/
	
	public String selectCarMansInfo() {
		List<CarMan> drivers = this.carManService.selectAllCarManByCarId(carId);
		JsonArray jsons = new JsonArray();
		Json o;
		for(CarMan driver : drivers){
			o = new Json();
			o.put("name", driver.getName());
			o.put("id", driver.getId());
			o.put("cert4FWZG", driver.getCert4FWZG());
			//o.put("region", driver.getRegion());
			//o.put("drivingStatus", driver.getDrivingStatus());
			jsons.add(o);
		}
		json = jsons.toString();
		return "json";
	}
	
	public String selectSubject(){
		return "showdialog";
	}
	
	
	// 表单可选项的加载
	public void initSelects(){
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		if (this.getE().getMotorcadeId() != null)
			OptionItem.insertIfNotExist(this.motorcadeList, this.getE()
					.getMotorcadeId().toString(), this.getE()
					.getMotorcadeName());

		// 加载可选责任列表
		this.allList		=	this.optionService.findOptionItemByGroupKeys(new String[] {
									OptionConstants.PR_TYPE,OptionConstants.AD_SOURCE,
								});
		// 可选责任列表
		//this.dutyList			=	allList.get(OptionConstants.IT_DUTY);	
		// 可选程度列表
		//this.degreeList			=	allList.get(OptionConstants.IT_DEGREE);
		// 可选没收证件列表
		//this.certList			=	allList.get(OptionConstants.BS_CERT);
		// 可选投诉建议来源
		this.sourceList			=	allList.get(OptionConstants.AD_SOURCE);
		// 可选表扬类型
		this.typeList			=	allList.get(OptionConstants.PR_TYPE);
	
	}
	
	/**
	 * 获取Entity的状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getCaseStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(CaseBase.STATUS_ACTIVE),
				getText("runcase.select.status.active"));
		statuses.put(String.valueOf(CaseBase.STATUS_CLOSED),
				getText("runcase.select.status.closed"));
		return statuses;
	}
	
	/**
	 * 获取Entity的来源值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getSourceStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(CaseBase.SOURCE_SYS),
				getText("runcase.select.source.sys"));
		statuses.put(String.valueOf(CaseBase.SOURCE_SYNC),
				getText("runcase.select.source.sync.auto"));
		statuses.put(String.valueOf(CaseBase.SOURCE_GENERATION),
				getText("runcase.select.source.sync.gen"));
		return statuses;
	}
	
}
