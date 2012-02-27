/**
 * 
 */
package cn.bc.business.certLost.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.web.struts2.BooleanFormater4certLost;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 证照遗失视图Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CertLostsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String isReplace = "false"; // 打开视图时默认为未补办
	public Long carManId;
	public Long carId;

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建日期
		return new OrderCondition("l.file_date", Direction.Desc);
	}

	@Override
	public boolean isReadonly() {
		// 证照遗失管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.certLost"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select l.id,i.cert_name,c.code,c.plate_type,c.plate_no,l.lost_date,i.is_replace,i.replace_date");
		sql.append(",l.transactor_name,l.driver,l.driver_id,l.car_id,c.company,unit.name unit_name");
		sql.append(",m.name motorcade_name,i.lost_address,d.cert_fwzg from bs_cert_lost_item i ");
		sql.append(" left join bs_cert_lost l on l.id=i.pid");
		sql.append(" left join bs_car c on c.id=l.car_id");
		sql.append(" left join bs_carman d on d.id=l.driver_id");
		sql.append(" left join bs_motorcade m on m.id=l.motorcade_id");
		sql.append(" left join bc_identity_actor unit on unit.id=m.unit_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("cert_name", rs[i++]);
				map.put("code", rs[i++]);
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				if (map.get("plate_type") == null
						&& map.get("plate_no") == null) {
					map.put("plate", null);
				} else {
					map.put("plate", map.get("plate_type").toString() + "."
							+ map.get("plate_no").toString());
				}
				map.put("lost_date", rs[i++]);
				map.put("is_replace", rs[i++]);
				map.put("replace_date", rs[i++]);
				map.put("transactor_name", rs[i++]);
				map.put("driver", rs[i++]);
				map.put("driverId", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("company", rs[i++]);
				map.put("unit_name", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("lost_address", rs[i++]);
				map.put("cert_fwzg", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("l.id", "id"));
		columns.add(new TextColumn4MapKey("i.cert_name", "cert_name",
				getText("certLost.certName")).setSortable(true));
		columns.add(new TextColumn4MapKey("i.code", "code",
				getText("certLost.carCode"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.plate_no", "plate",
				getText("certLost.plate"), 100)
				.setValueFormater(new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/car/edit?id={0}", "car") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("carId"));
					}

					@Override
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("car") + " - " + map.get("plate");

					}
				}));
		// 公司
		columns.add(new TextColumn4MapKey("c.company", "company",
				getText("car.company"), 40).setSortable(true)
				.setUseTitleFromLabel(true));
		// 分公司
		columns.add(new TextColumn4MapKey("unit.name", "unit_name",
				getText("car.unitname"), 65).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车队
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("car.motorcade"), 65)
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
		columns.add(new TextColumn4MapKey("l.lost_date", "lost_date",
				getText("certLost.lostDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("l.lost_address", "lost_address",
				getText("certLost.lostAddress")).setSortable(true));
		columns.add(new TextColumn4MapKey("i.is_replace", "is_replace",
				getText("certLost.isReplace"), 75).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new BooleanFormater4certLost()));
		columns.add(new TextColumn4MapKey("i.replace_date", "replace_date",
				getText("certLost.replaceDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("l.driver", "driver",
				getText("certLost.driver"), 100)
				.setValueFormater(new LinkFormater4Id(this.getContextPath()
						+ "/bc-business/carMan/edit?id={0}", "driver") {
					@SuppressWarnings("unchecked")
					@Override
					public String getIdValue(Object context, Object value) {
						return StringUtils
								.toString(((Map<String, Object>) context)
										.get("driverId"));
					}

					@Override
					public String getTaskbarTitle(Object context, Object value) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return getText("certLost.driver") + " - "
								+ map.get("driver");
					}
				}));
		columns.add(new TextColumn4MapKey("d.cert_fwzg", "cert_fwzg",
				getText("certLost.certFWZG"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("l.transactor_name",
				"transactor_name", getText("certLost.handlerName"))
				.setSortable(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_type", "c.plate_no", "i.cert_name",
				"c.code", "l.driver", "l.transactor_name", "c.company",
				"unit.name", "m.name", "d.cert_fwzg" };
	}

	@Override
	protected String getFormActionName() {
		return "certLost";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(780).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate'] ";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 是否补办条件
		Condition isReplaceCondition = null;
		if (("true").equals(isReplace)) {
			isReplaceCondition = new EqualsCondition("i.is_replace", true);
		} else if (("false").equals(isReplace)) {
			isReplaceCondition = new EqualsCondition("i.is_replace", false);
		}

		// carManId条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("l.driver_id", carManId);
		}

		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("l.car_id", carId);
		}

		// 合并条件
		return ConditionUtils.mix2AndCondition(isReplaceCondition,
				carManIdCondition, carIdCondition);
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		if (carManId != null) {
			json.put("carManId", carManId);
		}
		// carId条件
		if (carId != null) {
			json.put("carId", carId);
		}
		return json.isEmpty() ? null : json;
	}

	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
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

			if (useDisabledReplaceDelete) {
				// 禁用按钮
				tb.addButton(Toolbar
						.getDefaultDisabledToolbarButton(getText("label.disabled")));
			} else {
				// 删除按钮
				tb.addButton(Toolbar
						.getDefaultDeleteToolbarButton(getText("label.delete")));
			}

		}
		// 补办结果单选按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getReplace(),
				"isReplace", 0, getText("title.click2changeSearchReplace")));
		// 搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));

		return tb;
	}

	/**
	 * 补办结果转换列表：未补办|补办|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getReplace() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put("false", getText("certLost.isReplace.no"));
		statuses.put("true", getText("certLost.isReplace.yes"));
		statuses.put("", getText("certLost.isReplace.all"));
		return statuses;
	}

}
