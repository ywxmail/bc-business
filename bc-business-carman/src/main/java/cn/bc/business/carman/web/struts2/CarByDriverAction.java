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

import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarByDriverService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.RichEntity;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 司机营运车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarByDriverAction extends FileEntityAction<Long, CarByDriver> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = "R_ADMIN";// 管理角色的编码
	public boolean isManager;
	public CarByDriverService carByDriverService;
	public String portrait;
	public Map statusesValue;
 
	@Autowired
	public void setCarByDriverService(CarByDriverService carByDriverService) {
		this.carByDriverService = carByDriverService;
		this.setCrudService(carByDriverService);
	}

	/*@Override
	public String create() throws Exception {
		String result = super.create();
		
		return result;
	}*/

	/*@Override
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
		// 获取相片的连接
		portrait = "/bc/libs/themes/default/images/portrait/1in110x140.png";

		return result;
	}*/

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(500).setMinWidth(250)
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

	/*@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("orderNo", Direction.Desc);
	}*/

	@Override
	protected Condition getSpecalCondition() {
		return null;
	}

	/*// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(500).setMinWidth(300)
				.setHeight(500).setMinHeight(100);
	}*/

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

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "name", "origin" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("car.plateNo", getText("carByDriver.car.plateNo"))
		.setSortable(true));
		columns.add(new TextColumn("classes", getText("carByDriver.classes"))
				.setSortable(true));
		columns.add(new TextColumn("startDate"+"~"+"endDate", getText("carByDriver.timeInterva"))
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
	 *//*
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
	
	// 删除
		public String delete() throws Exception {
			
			if (this.getId() != null) {// 删除一条
				Map<String,Object> attrs = new HashMap<String,Object>();
				attrs.put("status", RichEntity.STATUS_DELETED);
				this.getCrudService().update(this.getId(), attrs);
			} else {// 删除一批
				if (this.getIds() != null && this.getIds().length() > 0) {
					Long[] ids = cn.bc.core.util.StringUtils
							.stringArray2LongArray(this.getIds().split(","));
					
					Map<String,Object> attrs = new HashMap<String,Object>();
					attrs.put("status", RichEntity.STATUS_DELETED);
					this.getCrudService().update(ids, attrs);
				} else {
					throw new CoreException("must set property id or ids");
				}
			}
			return "deleteSuccess";
		}*/
	
}
