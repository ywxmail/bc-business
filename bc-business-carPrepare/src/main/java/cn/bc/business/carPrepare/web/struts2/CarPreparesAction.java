/**
 * 
 */
package cn.bc.business.carPrepare.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 出车准备 Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarPreparesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆保单的状态，多个用逗号连接
	public Long carId;
	public Long main;// 主体： 0-当前版本,1-历史版本
	public Long policyId;// 合同ID

	@Override
	public boolean isReadonly() {
		// 出车准备管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.carPrepare"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("p.status_", Direction.Asc).add(
				"p.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select p.id,p.status_,p.code,p.plan_date,p.c1_plate_type,p.c1_plate_no from bs_car_prepare p");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status_", rs[i++]);
				map.put("code", rs[i++]);
				map.put("plan_date", rs[i++]);
				map.put("c1_plate_type", rs[i++]);
				map.put("c1_plate_no", rs[i++]);
				map.put("plate", map.get("c1_plate_type").toString() + "."
						+ map.get("c1_plate_no").toString());
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("p.id", "id"));
		// 状态
		columns.add(new TextColumn4MapKey("p.status_", "status_",
				getText("carPrepare.status"), 40).setSortable(true)
				.setValueFormater(
						new EntityStatusFormater(this.getCarPrepareStatuses())));
		columns.add(new TextColumn4MapKey("p.code", "code",
				getText("carPrepare.code"), 75).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("p.plan_date", "plan_date",
				getText("carPrepare.planDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		// // 公司
		// columns.add(new TextColumn4MapKey("c.company", "company",
		// getText("car.company"), 40).setSortable(true)
		// .setUseTitleFromLabel(true));
		// // 分公司
		// columns.add(new TextColumn4MapKey("unit_name", "unit_name",
		// getText("car.unitname"), 65).setSortable(true)
		// .setUseTitleFromLabel(true));
		// // 车队
		// columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
		// getText("car.motorcade"), 65)
		// .setSortable(true)
		// .setUseTitleFromLabel(true)
		// .setValueFormater(
		// new LinkFormater4Id(this.getContextPath()
		// + "/bc-business/motorcade/edit?id={0}",
		// "motorcade") {
		// @SuppressWarnings("unchecked")
		// @Override
		// public String getIdValue(Object context,
		// Object value) {
		// return StringUtils
		// .toString(((Map<String, Object>) context)
		// .get("motorcade_id"));
		// }
		// }));
		// // 自编号
		// columns.add(new TextColumn4MapKey("c.code", "code",
		// getText("car.code"), 75).setSortable(true)
		// .setUseTitleFromLabel(true));
		// 车号
		if (carId == null) {// 车辆页签时不需显示车牌号码
			columns.add(new TextColumn4MapKey("p.plate_no", "plate",
					getText("carPrepare.C1Plate"), 80)
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
						public String getTaskbarTitle(Object context,
								Object value) {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = (Map<String, Object>) context;
							return getText("car") + " - " + map.get("plate");

						}
					}));
		}
		// // 车辆登记日期
		// columns.add(new TextColumn4MapKey("c.register_date", "registeDate",
		// getText("policy.carRegisteDate"), 100).setSortable(true)
		// .setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "p.c1_plate_no" };
	}

	@Override
	protected String getFormActionName() {
		return "carPrepare";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("p.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("p.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("c.id", carId);
		}
		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition, carIdCondition);

	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (this.status == null || this.status.length() == 0) {
			json.put("status", status);
		}

		// carId条件
		if (carId != null) {
			json.put("carId", carId);
		}
		return json.isEmpty() ? null : json;
	}

	/**
	 * 状态值转换列表：待更新|更新中|已完成|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getCarPrepareStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(CarPrepare.STATUS_STAYUPDATED),
				getText("carPrepare.status.stayUpdate"));
		statuses.put(String.valueOf(CarPrepare.STATUS_INTHEUPDATE),
				getText("carPrepare.status.intheUpdate"));
		statuses.put(String.valueOf(CarPrepare.STATUS_COMPLETED),
				getText("carPrepare.status.completed"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
		} else {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());
			// 删除按钮
			tb.addButton(this.getDefaultDeleteToolbarButton());
		}
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				getCarPrepareStatuses(), "status", 0,
				getText("title.click2changeSearchClasses")));
	}

	@Override
	protected String getGridDblRowMethod() {
		return "bc.page.open";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	private MotorcadeService motorcadeService;
	private ActorService actorService;

	@Autowired
	public void setActorService(
			@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	public JSONArray motorcades;// 车队的下拉列表信息
	public JSONArray units;// 分公司的下拉列表信息

	@Override
	protected void initConditionsFrom() throws Exception {
		// 可选分公司列表
		units = OptionItem.toLabelValues(this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT }, (Integer[]) null), "name",
				"id");

		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));
	}

	// ==高级搜索代码结束==
}
