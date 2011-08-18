package cn.bc.business.car.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;

import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 选择车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectCarAction extends  FileEntityAction<Long, Car> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	// private static Log logger = LogFactory.getLog(CarAction.class);
		private static 	final long 		serialVersionUID 	= 1L;
		private String 					MANAGER_KEY 		= "R_ADMIN";// 管理角色的编码
		public 	boolean 				isManager;
		@SuppressWarnings("unused")
		private CarService 				carService;
		

		@Autowired
		public void setCarService(CarService carService) {
			this.carService = carService;
			this.setCrudService(carService);
		}
		
		/** 页面需要另外加载的js文件，逗号连接多个文件 */
		protected String getJs() {
			return " /bc-business/car/selectCar.js";
		}


		@Override
		protected OrderCondition getDefaultOrderCondition() {
			return new OrderCondition("registerDate", Direction.Desc);
		}

		
		@Override
		protected Toolbar buildToolbar() {
			isManager = isManager();
			Toolbar tb = new Toolbar();
			tb.addStyle("height", "30px");
			
			// 搜索按钮
			tb.addButton(getDefaultSearchToolbarButton());

			return tb;
		}

		@Override
		protected String[] getSearchFields() {
			return new String[] { "plateNo" };
		}

		@Override
		protected List<Column> buildGridColumns() {
			// 是否本模块管理员
			isManager = isManager();

			List<Column> columns = super.buildGridColumns();
			
			columns.add(new TextColumn("plateNo", getText("car.plate"))
			.setValueFormater(new AbstractFormater() {
				@Override
				public String format(Object context, Object value) {
					Car car = (Car) context;
					return car.getPlateType() + " " + car.getPlateNo();
				}
			}));
			return columns;
		}

		// 判断当前用户是否是本模块管理员
		private boolean isManager() {
			return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
		}
		

		@Override
		protected GridData buildGridData(List<Column> columns) {
			return super.buildGridData(columns).setRowLabelExpression("plateNo");
		}
		// 设置页面的尺寸
		@Override
		protected PageOption buildListPageOption() {
			return super
					.buildListPageOption()
					.setWidth(370)
					.setMinWidth(260)
					.setHeight(340)
					.setMinHeight(220)
					.setModal(true)
					.addButton(
							new ButtonOption(getText("selectCar.clickOk"), null,
									"bc.business.selectCar.clickOk"));
		}
		
		 //获取Action名
		protected String getEntityConfigName() {
			return "SelectCar";
		}
		/** 构建视图页面的表格 */
		protected Grid buildGrid() {
			List<Column> columns = this.buildGridColumns();

			// id列
			Grid grid = new Grid();
			grid.setGridHeader(this.buildGridHeader(columns));
			grid.setGridData(this.buildGridData(columns));
			grid.setRemoteSort("true"
					.equalsIgnoreCase(getText("app.grid.remoteSort")));
			grid.setColumns(columns);
			// name属性设为bean的名称
			grid.setName(getText(StringUtils.uncapitalize(getEntityConfigName())));
			// 多选及双击行编辑
			grid.setSingleSelect(false).setDblClickRow("bc.business.selectCar.dblickOk");
			// 分页条
			grid.setFooter(buildGridFooter(grid));
			return grid;
		}
		
}
