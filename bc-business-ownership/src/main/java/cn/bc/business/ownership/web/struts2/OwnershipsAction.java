/**
 * 
 */
package cn.bc.business.ownership.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 车辆经营权视图Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OwnershipsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
	public Long carId;// 车辆Id
	private OptionService optionService;

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 车辆经营权管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.ownership"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：车辆状态|创建日期
		return new OrderCondition("c.status_", Direction.Asc).add(
				"c.register_date", Direction.Desc)
				.add("c.code", Direction.Desc);

	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.company,m.name,c.plate_type,c.plate_no,c.cert_no2,o.nature,o.situation,o.owner_");
		sql.append(",c.code,c.bs_type,c.factory_type,c.register_date,o.file_date,ac.actor_name author");
		sql.append(",o.modified_date,md.actor_name modifier,c.id");
		sql.append(" from bs_car c");
		sql.append(" left join bs_car_ownership o on c.id=o.car_id");
		sql.append(" left join bs_motorcade m on m.id=c.motorcade_id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY md on md.id=o.modifier_id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY ac on ac.id=o.author_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("company", rs[i++]);// 公司
				map.put("motorcade_name", rs[i++]);// 车队
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("plate", map.get("plate_type").toString() + "."
						+ map.get("plate_no").toString());
				map.put("cert_no2", rs[i++]);// 经营权证号
				map.put("nature", rs[i++]);
				map.put("situation", rs[i++]);
				map.put("owner_", rs[i++]);
				map.put("code", rs[i++]);// 自编号
				map.put("bs_type", rs[i++]);
				map.put("factory_type", rs[i++]);
				map.put("registeDate", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("author", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("modifier", rs[i++]);
				map.put("carId", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("o.id", "id"));
		columns.add(new TextColumn4MapKey("c.company", "company",
				getText("car.company"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("car.motorcade"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.plate_no", "plate",
				getText("car.plate"), 80).setValueFormater(new LinkFormater4Id(
				this.getContextPath() + "/bc-business/car/edit?id={0}", "car") {
			@SuppressWarnings("unchecked")
			@Override
			public String getIdValue(Object context, Object value) {
				return StringUtils.toString(((Map<String, Object>) context)
						.get("carId"));
			}

			@Override
			public String getTaskbarTitle(Object context, Object value) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) context;

				return getText("car") + " - " + map.get("plate");

			}
		}));
		columns.add(new TextColumn4MapKey("c.cert_no2", "cert_no2",
				getText("car.certNo2"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("o.nature", "nature",
				getText("ownership.nature"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("o.situation", "situation",
				getText("ownership.situation"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.owner_", "owner_",
				getText("ownership.owner"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("car.code"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.bs_type", "bs_type",
				getText("car.businessType"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.factory_type", "factory_type",
				getText("car.factoryType"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.registeDate", "registeDate",
				getText("car.registerDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("ac.actor_name", "author",
				getText("ownership.author"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("o.file_date", "file_date",
				getText("ownership.fileDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("md.actor_name", "modifier",
				getText("ownership.modifier"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("o.modified_date", "modified_date",
				getText("ownership.modifiedDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.company", "m.name", "c.plate_no",
				"c.cert_no2", "o.nature", "o.situation", "o.owner_", "c.code",
				"c.bs_type", "c.factory_type", "ac.actor_name", "md.actor_name" };
	}

	@Override
	protected String getFormActionName() {
		return "ownership";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		return ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"c.status_");
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}

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
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
		} else {
			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());
			// 批量修改
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText("批量修改")
					.setClick("bc.business.piLiangXiuGai.create"));

			// 取消删除按钮
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		// 状态单选按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getBSStatuses1(),
				"status", 0, getText("title.click2changeSearchStatus")));

		return tb;
	}

	protected String getHtmlPageJs() {
		return this.getContextPath()
				+ "/bc-business/ownership/piliangXiuGai.js";

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
	public JSONArray natures;// 经营权性质
	public JSONArray situations;// 经营权情况
	public JSONArray owners;// 车辆产权

	@Override
	protected void initConditionsFrom() throws Exception {
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.OWNERSHIP_NATURE,
						OptionConstants.OWNERSHIP_OWNER,
						OptionConstants.OWNERSHIP_SITUATION });
		// 经营权性质
		natures = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.OWNERSHIP_NATURE));
		// 经营权情况
		situations = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.OWNERSHIP_SITUATION));
		// 车辆产权
		owners = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.OWNERSHIP_OWNER));
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
