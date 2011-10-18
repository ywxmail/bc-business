/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Controller;

import cn.bc.business.BSConstants;
import cn.bc.core.RichEntity;
import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.orm.hibernate.jpa.RowMapper;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.AbstractGridPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 车辆视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarViewAction extends AbstractGridPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status; // 车辆的状态，多个用逗号连接
	private JpaTemplate jpaTemplate;

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	@Override
	protected Query<Map<String, Object>> getQuery() {
		HibernateJpaNativeQuery<Map<String, Object>> query = new HibernateJpaNativeQuery<Map<String, Object>>(
				jpaTemplate);

		// 构建查询语句
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.status_,c.plate_type,c.plate_no,c.driver,c.charger,c.factory_type,c.factory_model,c.cert_no2");
		sql.append(",c.register_date,c.bs_type,c.code,c.origin_no,c.vin,m.id,m.name");
		sql.append(" from bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sql.append("");
		query.setSql(sql.toString());

		// 数据映射器
		query.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status_", rs[i++]);
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("driver", rs[i++]);
				map.put("charger", rs[i++]);
				map.put("factory_type", rs[i++]);
				map.put("factory_model", rs[i++]);
				map.put("cert_no2", rs[i++]);
				map.put("register_date", rs[i++]);
				map.put("bs_type", rs[i++]);
				map.put("code", rs[i++]);
				map.put("origin_no", rs[i++]);
				map.put("vin", rs[i++]);
				map.put("motorcade_id", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				return map;
			}
		});
		return query;
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}

	@Override
	protected String getFormActionName() {
		return "car";
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn(true, "['plate_type']+'.'+['plate_no']")
				.setId("id").setValueExpression("['id']"));
		// columns.add(new TextColumn("['id']", "ID", 40));
		columns.add(new TextColumn4MapKey("status_", getText("car.status"), 50)
				.setSortable(true).setValueFormater(
						new EntityStatusFormater(getEntityStatuses())));
		columns.add(new TextColumn4MapKey("plate_no", getText("car.plate"), 80)
				.setUseTitleFromLabel(true).setValueFormater(
						new AbstractFormater<String>() {
							@SuppressWarnings("unchecked")
							@Override
							public String format(Object context, Object value) {
								Map<String, Object> car = (Map<String, Object>) context;
								return car.get("plate_type") + "."
										+ car.get("plate_no");
							}
						}));
		columns.add(new TextColumn4MapKey("driver", getText("car.carMan"), 150));
		columns.add(new TextColumn4MapKey("charger", getText("car.charger")));
		columns.add(new TextColumn4MapKey("motorcade_name",
				getText("car.motorcade"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("register_date",
				getText("car.registerDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		columns.add(new TextColumn4MapKey("cert_no2", getText("car.certNo2"),
				100));
		columns.add(new TextColumn4MapKey("bs_type",
				getText("car.businessType"), 100));
		columns.add(new TextColumn4MapKey("factory_type",
				getText("car.factory"), 120)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						// 从上下文取出元素Map
						Map<String, Object> car = (Map<String, Object>) context;
						if (car.get("factory_type") != null
								&& car.get("factory_model") != null) {
							return car.get("factory_type") + " "
									+ car.get("factory_model");
						} else if (car.get("factory_model") != null) {
							return car.get("factory_model").toString();
						} else if (car.get("factory_type") != null) {
							return car.get("factory_type") + " ";
						} else {
							return "";
						}
					}
				}));
		columns.add(new TextColumn4MapKey("code", getText("car.code"), 60)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("origin_no", getText("car.originNo"),
				100).setSortable(true));
		columns.add(new TextColumn4MapKey("vin", getText("car.vin"), 120));
		return columns;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(550).setMinHeight(300);
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_type", "c.plate_no", "c.factory_type",
				"c.driver", "c.cert_no2", "c.charger" };
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate_type'] + '.' + ['plate_no']";
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("car.title");
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				return new EqualsCondition("c.status_", new Integer(ss[0]));
			} else {
				return new InCondition("c.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		} else {
			return null;
		}
	}

	@Override
	protected Json getGridExtrasData() {
		if (this.status == null || this.status.length() == 0) {
			return null;
		} else {
			Json json = new Json();
			json.put("status", status);
			return json;
		}
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.read")));
		} else {
			// 新建按钮
			tb.addButton(Toolbar
					.getDefaultCreateToolbarButton(getText("label.create")));

			// 编辑按钮
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.edit")));

			// 删除按钮
			tb.addButton(Toolbar
					.getDefaultDeleteToolbarButton(getText("label.delete")));
		}

		// 搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));

		return tb;
	}

	/**
	 * 获取Entity的状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(RichEntity.STATUS_DISABLED),
				getText("entity.status.disabled"));
		statuses.put(String.valueOf(RichEntity.STATUS_ENABLED),
				getText("entity.status.enabled"));
		statuses.put(String.valueOf(RichEntity.STATUS_DELETED),
				getText("entity.status.deleted"));
		return statuses;
	}
}
