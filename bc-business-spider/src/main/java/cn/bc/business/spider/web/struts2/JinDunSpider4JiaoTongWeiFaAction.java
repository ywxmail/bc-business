/**
 * 
 */
package cn.bc.business.spider.web.struts2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.spider.service.BsSpiderService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 车辆视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class JinDunSpider4JiaoTongWeiFaAction extends
		ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String carIds;
	public BsSpiderService bsSpiderService;

	@Autowired
	public void setBsSpiderService(BsSpiderService bsSpiderService) {
		this.bsSpiderService = bsSpiderService;
	}

	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractTraffic"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("c.id", "id"));
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("car.motorcade"), 80)
				.setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/motorcade/edit?id={0}",
								"motorcade") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("motorcade_id"));
							}
						}));
		columns.add(new TextColumn4MapKey("c.plate_no", "plate_no",
				getText("car.plate"), 80).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> car = (Map<String, Object>) context;
						return car.get("plate_type") + "."
								+ car.get("plate_no");
					}
				}));
		columns.add(new TextColumn4MapKey("单号", "单号", getText("car.1"), 140)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("违法时间", "违法时间", getText("car.1"), 130)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("违法地点", "违法地点", getText("car.2"))
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("违法来源", "违法来源", getText("car.3"), 80));
		columns.add(new TextColumn4MapKey("违法记分数", "违法记分数", getText("car.4"),
				100));
		columns.add(new TextColumn4MapKey("当事人", "当事人", getText("car.5"), 60));
		columns.add(new TextColumn4MapKey("罚款金额", "罚款金额", getText("car.6"), 60));
		columns.add(new TextColumn4MapKey("滞纳金", "滞纳金", getText("car.7"), 60));
		columns.add(new TextColumn4MapKey("交通方式", "交通方式", getText("car.8"), 60));
		columns.add(new TextColumn4MapKey("违法行为", "违法行为", getText("car.9"), 60));

		columns.add(new TextColumn4MapKey("c.bs_type", "bs_type",
				getText("car.businessType"), 100));
		columns.add(new TextColumn4MapKey("c.register_date", "register_date",
				getText("car.registerDate"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("c.engine_no", "engine_no",
				getText("car.engineNo"), 80));
		return columns;
	}

	@Override
	protected Grid getHtmlPageGrid() {
		return super.getHtmlPageGrid().setDblClickRow(null);
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(750).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate_type'] + '.' + ['plate_no']";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 生成投诉单按钮
		tb.addButton(new ToolbarButton().setIcon("ui-icon-transfer-e-w")
				.setText("生成投诉单")
				.setClick("bs.jinDunSpider4JiaoTongWeiFa.create"));

		return tb;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath()
				+ "/bc-business/spider/jinDunSpider4JiaoTongWeiFa.js";
	}

	@Override
	protected String[] getGridSearchFields() {
		return null;
	}

	@Override
	protected String getFormActionName() {
		return "jd";
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		return null;
	}

	@Override
	protected List<Map<String, Object>> findList() {
		Map<String, List<Map<String, Object>>> map = bsSpiderService
				.findJinDunJiaoTongWeiZhang(carIds);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Entry<String, List<Map<String, Object>>> e : map.entrySet()) {
			list.addAll(e.getValue());
		}
		return list;
	}
}
