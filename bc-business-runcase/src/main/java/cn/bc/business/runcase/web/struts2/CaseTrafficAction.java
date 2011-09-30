/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.runcase.service.CaseTrafficService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

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
	private static 	final long 		serialVersionUID 	= 1L;
	private String 					MANAGER_KEY 		= "R_ADMIN";// 管理角色的编码
	public 	boolean 				isManager;
	public  Long 					carId;
	
	@SuppressWarnings("unused")
	private CaseTrafficService		caseTrafficService;
	private MotorcadeService	 	motorcadeService;
	private OptionService			optionService;

	public 	List<Motorcade> 		motorcadeList;					// 可选车队列表
	public  List<OptionItem>		dutyList;						// 可选责任列表
	public  List<OptionItem>		properitesList;					// 可选性质列表
	
	
	public 	Map<String,String> 		statusesValue;
	public	Map<String,String>		sourcesValue;


	@Autowired
	public void setCaseTrafficService(CaseTrafficService caseTrafficService) {
		this.caseTrafficService = caseTrafficService;
		this.setCrudService(caseTrafficService);
	}
	
	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}
	
	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}
	
	//复写搜索URL方法
	protected String getEntityConfigName() {
		return "caseTraffic";
	}
	
	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(MANAGER_KEY);
	}


	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(840).setMinWidth(250)
				.setMinHeight(200).setModal(false);
		
		if (!isReadonly()) {
			//特殊处理结案按钮
			if(Case4InfractTraffic.STATUS_ACTIVE == getE().getStatus()){
				ButtonOption buttonOption = new ButtonOption(getText("label.closefile"),null,"bc.caseTrafficForm.closefile");
				buttonOption.put("id", "bcSaveDlgButton");
				option.addButton(buttonOption);
			}
			option.addButton(new ButtonOption(getText("label.save"), "save"));
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

	@Override
	protected Toolbar buildToolbar() {
		isManager = isReadonly();
		Toolbar tb = new Toolbar();

		if (!isManager) {
			// 新建按钮
			tb.addButton(getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(getDefaultEditToolbarButton());

			// 删除按钮
			tb.addButton(getDefaultDeleteToolbarButton());
		} else {// 普通用户
			// 查看按钮
			tb.addButton(getDefaultOpenToolbarButton());
		}

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	//搜索条件
	@Override
	protected String[] getSearchFields() {
		return new String[] { "caseNo", "carPlate" ,"driverName", "driverCert", "motorcadeName" };
	}
	
	
	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isReadonly();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status",getText("runcase.status"),		50)
				.setSortable(true).setValueFormater(new EntityStatusFormater(getCaseStatuses())));
		columns.add(new TextColumn("caseNo",	getText("runcase.caseNo1"))
				.setSortable(true));
		columns.add(new TextColumn("source", getText("runcase.ifsource"),		80)
				.setSortable(true).setValueFormater(new EntityStatusFormater(getSourceStatuses())));
		columns.add(new TextColumn("motorcadeName", getText("runcase.motorcadeName"),		80)
				.setSortable(true));
		columns.add(new TextColumn("carPlate", getText("runcase.carPlate"),		100)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("driverName", getText("runcase.driverName"),70)
				.setSortable(true));
		columns.add(new TextColumn("happenDate", getText("runcase.happenDate"),	150)
				.setSortable(true).setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn("subject", getText("runcase.subject"),	120));
		columns.add(new TextColumn("address", getText("runcase.address"),120)
				.setSortable(true));
		columns.add(new TextColumn("driverCert", getText("runcase.driverCert"),			80)
				.setSortable(true));
		return columns;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public String create() throws Exception {
		String r = super.create();
		this.getE().setUid(this.getIdGeneratorService().next(this.getE().ATTACH_TYPE));
		
		// 初始化信息
		this.getE().setType  (CaseBase.TYPE_INFRACT_TRAFFIC);
		this.getE().setStatus(CaseBase.STATUS_ACTIVE);
		sourcesValue		=	this.getSourceStatuses();
		// 表单可选项的加载
		initSelects();
		
		return r; 
	}
	
	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		// 表单可选项的加载
		this.formPageOption = 	buildFormPageOption();
		statusesValue		=	this.getCaseStatuses();
		sourcesValue		=	this.getSourceStatuses();
		initSelects();
		return "form";
	}
	
	@Override
	public String save() throws Exception{
		SystemContext context = this.getSystyemContext();
		
		//设置最后更新人的信息
		Case4InfractTraffic e = this.getE();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		
		this.getCrudService().save(e);
		
		return "saveSuccess";
	}
	
	public Json json;
	public String closefile(){
		SystemContext context = this.getSystyemContext();
		
		this.getE().setStatus(CaseBase.STATUS_CLOSED);
		this.getE().setCloserId(context.getUserHistory().getActorId());
		this.getE().setCloseDate(Calendar.getInstance(Locale.CHINA));
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");   
		String closeDateStr = df.format(this.getE().getCloseDate().getTime());
		
		json = new Json();
		json.put("status", this.getE().getStatus());
		json.put("closeDate", closeDateStr);
		return "json";
	}
	
	
	// 表单可选项的加载
	public void initSelects(){
		// 加载可选车队列表
		this.motorcadeList 				= 	this.motorcadeService.createQuery().list();
		// 加载可选责任列表
		this.dutyList					=	this.optionService.findOptionItemByGroupKey(OptionConstants.IT_DUTY);
		// 加载可选性质列表
		this.properitesList				=	this.optionService.findOptionItemByGroupKey(OptionConstants.IT_PROPERITES);
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
				getText("runcase.select.source.sync"));
		statuses.put(String.valueOf(CaseBase.SOURCE_FROM_DRIVER),
				getText("runcase.select.source.fromdriver"));
		return statuses;
	}
	
	
	// 视图特殊条件
	@Override
	protected Condition getSpecalCondition() {
		if (carId != null) {
			return new EqualsCondition("carId", carId);
		} else {
			return null;
		}
	}
	

	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carId != null)
			page.setAttr("data-extras", new Json().put("carId", carId)
					.toString());
		return page;
	}
	
}
