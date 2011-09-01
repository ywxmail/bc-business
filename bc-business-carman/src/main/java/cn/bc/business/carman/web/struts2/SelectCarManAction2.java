/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.BSConstants;
import cn.bc.business.carman.service.CarManService;
import cn.bc.core.CrudOperations;
import cn.bc.web.struts2.AbstractGridPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 选择司机、责任人Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectCarManAction2 extends AbstractGridPageAction {
	private static final long serialVersionUID = 1L;
	public CarManService carManService;
	private String clickOkMethod = "bs.carManSelectDialog.clickOk";

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Override
	protected CrudOperations<? extends Object> getGridDataService() {
		return this.carManService;
	}

	@Override
	protected String getHtmlPageNamespace() {
		return BSConstants.NAMESPACE;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("carMan.title");
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "name";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "name" };
	}

	@Override
	protected String getGridDblRowMethod() {
		return clickOkMethod;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super
				.getHtmlPageOption()
				.setWidth(300)
				.setMinWidth(260)
				.setHeight(400)
				.setMinHeight(220)
				.setModal(true)
				.addButton(
						new ButtonOption(getText("label.ok"), null,
								clickOkMethod));
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc-business/carMan/select.js";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 添加搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));

		return tb;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = super.getGridColumns();
		columns.add(new TextColumn("name", getText("carMan.name"))
				.setSortable(true));
		return columns;
	}
}
