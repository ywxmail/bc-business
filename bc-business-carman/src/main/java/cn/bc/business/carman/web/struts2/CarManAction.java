/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.RichEntity;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 司机责任人Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarManAction extends FileEntityAction<Long, CarMan> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private IdGeneratorService idGeneratorService;
	private String MANAGER_KEY = "R_ADMIN";// 管理角色的编码
	public boolean isManager;
	public CarManService carManService;
	public String portrait;
	public Map <String,String>statusesValue;
	public OptionService optionService;
	public OptionConstants optionConstants;
	public List<OptionItem> carManHouseTypeList;//司机责任人户口性质列表
	public List<OptionItem> carManLevelList;//司机责任人等级列表
	public List<OptionItem> carManModelList;//司机责任人准驾车型列表
    
	public IdGeneratorService getIdGeneratorService() {  
        return idGeneratorService;  
    }  
    
	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}



	@Autowired  
    public void setIdGeneratorService(IdGeneratorService idGeneratorService) {  
        this.idGeneratorService = idGeneratorService;  
    }  
	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
		this.setCrudService(carManService);
	}

	@Override
	public String create() throws Exception {
		String result = super.create();
		this.getE().setStatus(RichEntity.STATUS_ENABLED);
		statusesValue=this.getEntityStatuses();
		this.getE().setUid(this.getIdGeneratorService().next(CarMan.KEY_UID));
		this.getE().setSex(1);
		carManHouseTypeList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CARMAN_HOUSETYPE);
		carManLevelList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CARMAN_LEVEL);
		carManModelList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CARMAN_MODEL);
		// 获取相片的连接
		portrait = "/bc/libs/themes/default/images/portrait/1in110x140.png";

		return result;
	}

	@Override
	public String open() throws Exception {
		String result = super.open();

		// 获取相片的连接
		//portrait = "/bc/libs/themes/default/images/portrait/1in110x140.png";

		return result;
	}

	@Override
	public String edit() throws Exception {
		String result = super.edit();
		statusesValue=this.getEntityStatuses();
		carManHouseTypeList = this.optionService
		        .findOptionItemByGroupKeyWithCurrent(optionConstants.CARMAN_HOUSETYPE,null,this.getE().getHouseType()); 
		carManLevelList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CARMAN_LEVEL);
		carManModelList = this.optionService
				.findOptionItemByGroupKey(optionConstants.CARMAN_MODEL);
		// 获取相片的连接
		portrait = "/bc/libs/themes/default/images/portrait/1in110x140.png";

		return result;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(810).setMinWidth(250)
				.setMinHeight(200);
		if (isManager()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}
	
	@Override
	protected Condition getSpecalCondition() {
		return null;
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(500).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar buildToolbar() {
		isManager = isManager();
		Toolbar tb = new Toolbar();

		if (isManager) {
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

	@Override
	protected String[] getSearchFields() {
		return new String[] {  "name",  "origin" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status", getText("carMan.status"),80)
		.setSortable(true).setValueFormater(
				new KeyValueFormater(getEntityStatuses())));
		columns.add(new TextColumn("type", getText("carMan.type"),80)
				.setSortable(true).setValueFormater(
						new KeyValueFormater(getType())));
		columns.add(new TextColumn("name", getText("carMan.name"),80)
				.setSortable(true));
		columns.add(new TextColumn("origin", getText("carMan.origin"))
				.setSortable(true));
		return columns;
	}

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}
	
	/**
	 * 获取分类值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getType() {
		Map<String, String> type = new HashMap<String, String>();
		type = new HashMap<String, String>();
		type.put(String.valueOf(CarMan.TYPE_DRIVER),
				getText("carMan.type.driver"));
		type.put(String.valueOf(CarMan.TYPE_CHARGER),
				getText("carMan.type.charger"));
		type.put(String.valueOf(CarMan.TYPE_DRIVER_AND_CHARGER),
				getText("carMan.type.driverAndCharger"));
		return type;
	}
	
	
}
