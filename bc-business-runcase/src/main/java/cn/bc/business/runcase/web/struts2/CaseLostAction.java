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
import cn.bc.business.runcase.domain.Case4Lost;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.runcase.service.CaseLostService;
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
public class CaseLostAction extends FileEntityAction<Long, Case4Lost> {
	private static 	final long 				serialVersionUID 	= 1L;
	public  Long 							carId;
	public  Long 							carManId;
	public  boolean 						isMoreCar;
	public  boolean 						isMoreCarMan;
	public  boolean 						isNullCar;
	public  boolean 						isNullCarMan;
	private	boolean							multiple;
	@SuppressWarnings("unused")
	private CaseLostService					caseLostService;
	private MotorcadeService	 			motorcadeService;
	private OptionService					optionService;
	private CarManService 					carManService;
	private CarService 						carService;
	private String							sourceStr;

	public 	List<Map<String, String>> 		motorcadeList;			// 可选车队列表
	public  List<Map<String, String>> 		sitePostionList; 		// 可选遗失位置列表
	public  List<Map<String, String>> 		levelList; 				// 可选级别列表
	public  List<Map<String, String>> 		resultList; 			// 可选失物去向列表
	public  List<Map<String, String>> 		handleResultList; 		// 可选处理结果列表
	public  List<Map<String, String>> 		fromList; 				// 可选来源列表
	
	
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
	public void setCaseLostService(CaseLostService caseLostService) {
		this.caseLostService = caseLostService;
		this.setCrudService(caseLostService);
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
	
	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("status", Direction.Asc).add("fileDate", Direction.Desc);
	}
	
	//复写搜索URL方法
	protected String getEntityConfigName() {
		return "caseLost";
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		//报失管理员和超级管理员
		return !context.hasAnyRole(getText("key.role.bs.lost"),
				getText("key.role.bc.admin"));
	}
	
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(745).setMinWidth(250).setHeight(500)
				.setMinHeight(200);
	}

	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			//特殊处理结案按钮
			if(CaseBase.STATUS_ACTIVE == getE().getStatus() && !getE().isNew()){
				ButtonOption buttonOption = new ButtonOption(getText("label.closefile"),null,"bc.caseLostForm.closefile");
				buttonOption.put("id", "bcSaveDlgButton");
				pageOption.addButton(buttonOption);
			}
			if(CaseBase.STATUS_CLOSED != getE().getStatus()){
				// 添加默认的保存按钮
				pageOption.addButton(this.getDefaultSaveButtonOption());
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected void afterCreate(Case4Lost entity) {
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
				this.getE().setCompany(car.get(0).getCompany());
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
			this.getE().setCompany(car.getCompany());
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
		this.getE().setType  (CaseBase.TYPE_LOST);
		this.getE().setStatus(CaseBase.STATUS_ACTIVE);
		this.getE().setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		// 自动生成自编号
		this.getE().setCode(
				this.getIdGeneratorService().nextSN4Month(Case4Lost.KEY_CODE));
		this.getE().setOwnerSex(Case4Lost.SEX_MAN);
		this.getE().setDriverSex(Case4Lost.SEX_MAN);
		this.getE().setTakerSex(Case4Lost.SEX_MAN);
		//经办人初始化
		SystemContext context = this.getSystyemContext();
		this.getE().setTransactorId(context.getUserHistory().getId());
		this.getE().setTransactorName(context.getUserHistory().getName());
		this.getE().setSource(CaseBase.SOURCE_SYS);
		this.getE().setFrom("电话");

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
		Case4Lost e = this.getE();
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
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		if (this.getE().getMotorcadeId() != null)
			OptionItem.insertIfNotExist(this.motorcadeList, this.getE()
					.getMotorcadeId().toString(), this.getE()
					.getMotorcadeName());

		this.allList		=	this.optionService.findOptionItemByGroupKeys(new String[] {
									OptionConstants.LOST_SITE_POSTION,OptionConstants.LOST_LEVEL,
									OptionConstants.LOST_RESULT,OptionConstants.LOST_HANDLE_RESULT,
									OptionConstants.AD_SOURCE
								});
		
		// 可选遗失位置列表    
		this.sitePostionList	=	allList.get(OptionConstants.LOST_SITE_POSTION);
		// 可选级别列表
		this.levelList			=	allList.get(OptionConstants.LOST_LEVEL);
		// 可选失物去向列表 
		this.resultList			=	allList.get(OptionConstants.LOST_RESULT);
		// 可选处理结果   
		this.handleResultList	=	allList.get(OptionConstants.LOST_HANDLE_RESULT);
		// 可选来源列表	
		this.fromList			=	allList.get(OptionConstants.AD_SOURCE);
	
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
	 * 获取Entity的来源转换列表
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
				getText("runcase.select.source.sync.auto"));
		return statuses;
	}

}
