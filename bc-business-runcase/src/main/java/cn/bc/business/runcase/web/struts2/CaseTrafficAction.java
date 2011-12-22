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
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.runcase.service.CaseBaseService;
import cn.bc.business.runcase.service.CaseTrafficService;
import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.business.sync.service.JiaoWeiJTWFService;
import cn.bc.business.sync.service.JinDunJTWFService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.sync.domain.SyncBase;
import cn.bc.sync.service.SyncBaseService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

/**
 * 交通违章Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseTrafficAction extends FileEntityAction<Long, Case4InfractTraffic> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static 	final long 				serialVersionUID 	= 1L;
	private Long 							carId;
	private Long 							carManId;
	private	Long							syncId;			//同步ID
	public  boolean 						isMoreCar;		//是否存在多个车
	public  boolean 						isMoreCarMan;	//是否存在多个司机
	public  boolean 						isNullCar;		//此司机现在没有驾驶任何车辆
	public  boolean 						isNullCarMan;	//此车辆现在没有任何司机驾驶
	//public  boolean							isSync;			//是否同步
	//public  boolean							isExist;		//判断生成的当前记录是否存在,存在发出提醒
	//public  String							isClosed;	
	@SuppressWarnings("unused")
	private CaseTrafficService				caseTrafficService;
	private CaseBaseService					caseBaseService;
	private MotorcadeService	 			motorcadeService;
	private OptionService					optionService;
	private CarManService 					carManService;
	private CarService 						carService;
	private SyncBaseService 				syncBaseService;				//平台同步基类Serivce
	private JiaoWeiJTWFService 				jiaoWeiJTWFService;				//交委Service
	private JinDunJTWFService 				jinDunJTWFService;				//金盾Service

	public 	List<Map<String, String>> 		motorcadeList;					// 可选车队列表
	public  List<Map<String, String>>		dutyList;						// 可选责任列表
	public  List<Map<String, String>>		properitesList;					// 可选性质列表
	
	
	public 	Map<String,String> 				statusesValue;
	public	Map<String,String>				sourcesValue;
	private Map<String, List<Map<String, String>>> 			allList;

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

	public Long getSyncId() {
		return syncId;
	}

	public void setSyncId(Long syncId) {
		this.syncId = syncId;
	}

	@Autowired
	public void setCaseTrafficService(CaseTrafficService caseTrafficService) {
		this.caseTrafficService = caseTrafficService;
		this.setCrudService(caseTrafficService);
	}
	
	@Autowired
	public void setCaseBaseService(CaseBaseService caseBaseService) {
		this.caseBaseService = caseBaseService;
	}

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
	
	@Autowired
	public void setSyncBaseService(SyncBaseService syncBaseService) {
		this.syncBaseService = syncBaseService;
	}
	
	@Autowired
	public void setJiaoWeiJTWFService(JiaoWeiJTWFService jiaoWeiJTWFService) {
		this.jiaoWeiJTWFService = jiaoWeiJTWFService;
	}

	@Autowired
	public void setJinDunJTWFService(JinDunJTWFService jinDunJTWFService) {
		this.jinDunJTWFService = jinDunJTWFService;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("status", Direction.Asc).add("fileDate", Direction.Desc);
	}
	
	//复写搜索URL方法
	protected String getEntityConfigName() {
		return "caseTraffic";
	}

	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractTraffic"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = super.buildFormPageOption().setWidth(820).setMinWidth(250).setHeight(500)
				.setMinHeight(200);
		
		if (!isReadonly()) {
			//特殊处理结案按钮
			if(Case4InfractTraffic.STATUS_ACTIVE == getE().getStatus() && !getE().isNew()){
				ButtonOption buttonOption = new ButtonOption(getText("label.closefile"),null,"bc.caseTrafficForm.closefile");
				buttonOption.put("id", "bcSaveDlgButton");
				option.addButton(buttonOption);
			}
			option.addButton(new ButtonOption(getText("label.save"), null, "bc.caseTrafficForm.save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("caseNo");
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	//搜索条件
	@Override
	protected String[] getSearchFields() {
		return new String[] { "caseNo", "carPlate" ,"driverName", "driverCert", "motorcadeName","closerName","subject" };
	}
	
	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status",getText("runcase.status"),		50)
				.setSortable(true).setValueFormater(new EntityStatusFormater(getCaseStatuses())));
		columns.add(new TextColumn("subject", getText("runcase.subject"),	120));
		columns.add(new TextColumn("motorcadeName", getText("runcase.motorcadeName"),		80)
				.setSortable(true));
		columns.add(new TextColumn("carPlate", getText("runcase.carPlate"),		100)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("driverName", getText("runcase.driverName"),70)
					.setSortable(true));
		columns.add(new TextColumn("closerName", getText("runcase.closerName"),70)
				.setSortable(true));
		columns.add(new TextColumn("happenDate", getText("runcase.happenDate"),	120)
				.setSortable(true).setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("closeDate", getText("runcase.closeDate"),	120)
				.setSortable(true).setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("address", getText("runcase.address"),120));
		columns.add(new TextColumn("from", getText("runcase.ifsource"),		70).setSortable(true));
		columns.add(new TextColumn("driverCert", getText("runcase.driverCert"),	80));
		columns.add(new TextColumn("caseNo",	getText("runcase.caseNo1")));
		return columns;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public String create() throws Exception {
		String r = super.create();
		
		if(syncId != null){	//判断同步id是否为空
			SyncBase syncBase = this.syncBaseService.load(syncId);
			if(syncBase.getSyncType().equals(JinDunJTWF.KEY_TYPE)){	//判断是否金盾网同步
				JinDunJTWF jinDunJTWF = this.jinDunJTWFService.load(syncId);
				findCarId(jinDunJTWF.getCarPlateNo());
				this.getE().setCaseNo(jinDunJTWF.getSyncCode());
				this.getE().setAddress(jinDunJTWF.getAddress());
				this.getE().setHappenDate(jinDunJTWF.getHappenDate());
				this.getE().setJeom(jinDunJTWF.getJeom());
				this.getE().setFrom(getText("runcase.jindun")+jinDunJTWF.getSource());
			}else{	//交委同步
				JiaoWeiJTWF jiaoWeiJTWF = this.jiaoWeiJTWFService.load(syncId);
				findCarId(jiaoWeiJTWF.getCarPlateNo());
				this.getE().setCaseNo(jiaoWeiJTWF.getSyncCode());
				this.getE().setSubject(jiaoWeiJTWF.getContent());
				this.getE().setJeom(jiaoWeiJTWF.getJeom());
				this.getE().setHappenDate(jiaoWeiJTWF.getHappenDate());
				this.getE().setFrom(getText("runcase.jiaowei"));
			}
			//设置来源
			this.getE().setSource(CaseBase.SOURCE_GENERATION);
			//设置syncId
			this.getE().setSyncId(syncId);
			//判断生成的当前记录是否存在,存在发出提醒
			//isExist = this.syncBaseService.hadGenerate("BS_CASE_BASE", syncId);
		}
		
		if (carManId != null) {
			CarMan driver = this.carManService.load(carManId);
			List<Car> car = this.carService.selectAllCarByCarManId(carManId);
			if (car.size() == 1) {
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
			List<CarMan> carMan = this.carManService.selectAllCarManByCarId(carId);
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
		this.getE().setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		// 自动生成自编号
		this.getE().setCode(
				this.getIdGeneratorService().nextSN4Month(Case4InfractTraffic.KEY_CODE));
		this.getE().setType  (CaseBase.TYPE_INFRACT_TRAFFIC);
		this.getE().setStatus(CaseBase.STATUS_ACTIVE);
		// 来源
		if(syncId == null){ //不是同步过来的信息设为自建
			this.getE().setSource(CaseBase.SOURCE_SYS);
		}
		statusesValue		=	this.getCaseStatuses();
		sourcesValue		=	this.getSourceStatuses();
		// 表单可选项的加载
		initSelects();
		
		return r; 
	}

	/** 根据车牌号查找carId*/
	public void findCarId(String carPlateNo) {
		if(carPlateNo.length() > 0){ //判断车牌号是否为空
			Long tempCarId = this.carService.findcarInfoByCarPlateNo(carPlateNo);
			this.carId = tempCarId;
		}
	}
	
	@Override
	public String edit() throws Exception {
		if(syncId != null){//根据syncId查找已存在CaseBase的记录
			CaseBase cb = this.caseBaseService.findCaseBaseBysyncId(syncId);
			this.setE(this.getCrudService().load(cb.getId()));
		}else{
			this.setE(this.getCrudService().load(this.getId()));
		}
		// 表单可选项的加载
		this.formPageOption = 	buildFormPageOption();
		statusesValue		=	this.getCaseStatuses();
		sourcesValue		=	this.getSourceStatuses();
		initSelects();
		return "form";
	}
	
	@Override
	public String save() throws Exception{
		
		if(syncId != null){	//处理相应的来源信息的状态
			SyncBase sb = this.syncBaseService.load(syncId);
			sb.setStatus(SyncBase.STATUS_GEN);
			this.syncBaseService.save(sb);
		}
		SystemContext context = this.getSystyemContext();
		//设置最后更新人的信息
		Case4InfractTraffic e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		
		//设置结案信息
		if(e.getStatus() == 1){
			e.setStatus(CaseBase.STATUS_CLOSED);
			e.setCloserId(context.getUser().getId());
			e.setCloserName(context.getUser().getName());
			e.setCloseDate(Calendar.getInstance(Locale.CHINA));
		}
		
		this.getCrudService().save(e);
		
		return "saveSuccess";
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
	
	public String json;
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
	
	
	// 表单可选项的加载
	public void initSelects(){
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.find4Option();
		if (this.getE().getMotorcadeId() != null)
			OptionItem.insertIfNotExist(this.motorcadeList, this.getE()
					.getMotorcadeId().toString(), this.getE()
					.getMotorcadeName());

		// 加载可选责任列表
		this.allList		=	this.optionService.findOptionItemByGroupKeys(new String[] {
									OptionConstants.IT_DUTY,OptionConstants.IT_PROPERITES
								});
		// 可选责任列表
		this.dutyList			=	allList.get(OptionConstants.IT_DUTY);	
		// 可选性质列表
		this.properitesList		=	allList.get(OptionConstants.IT_PROPERITES);						
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
	
	
	// 视图特殊条件
	@Override
	protected Condition getSpecalCondition() {
		if (carId != null) {
			return new EqualsCondition("carId", carId);
		}
		if (carManId != null) {
			return new EqualsCondition("driverId", carManId);
		}else {
			return null;
		}
	}
	

	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carId != null)
			page.setAttr("data-extras", new Json().put("carId", carId)
					.toString());
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		return page;
	}
	
}
