/**
 * 
 */
package cn.bc.business.sync.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.sync.domain.SyncBase;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;

/**
 * 金盾网的交通违法信息视图Action
 * 
 * @author dragon
 * 
 */
/**
 * @author rongjih
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class JinDunJTWFsAction extends SyncViewAction {
	protected final Log logger = LogFactory.getLog(JinDunJTWFsAction.class);
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isReadonly() {
		// 交通违法管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractTraffic"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|违法时间
		return new OrderCondition("b.status_", Direction.Asc).add(
				"t.happen_date", Direction.Desc);
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select b.id,b.status_,b.sync_type,b.sync_code,b.sync_from,b.sync_date");
		sql.append(",t.happen_date,t.address,t.car_type,t.car_plate_type,t.car_plate_no,t.engine_no,t.source");
		sql.append(",t.driver_name,t.decision_no,t.decision_type,t.traffic,t.break_type");
		sql.append(",t.jeom,t.penalty,t.overdue_payment");
		sql.append(" from bs_sync_jindun_jtwf t");
		sql.append(" inner join bc_sync_base b on b.id=t.id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]);
				map.put("syncType", rs[i++]);
				map.put("syncCode", rs[i++]);
				map.put("syncFrom", rs[i++]);
				map.put("syncDate", rs[i++]);
				map.put("happenDate", rs[i++]);
				map.put("address", rs[i++]);
				map.put("carType", rs[i++]);
				map.put("carPlateType", rs[i++]);
				map.put("carPlateNo", rs[i++]);
				map.put("engineNo", rs[i++]);
				map.put("source", rs[i++]);
				map.put("driverName", rs[i++]);
				map.put("decisionNo", rs[i++]);
				map.put("decisionType", rs[i++]);
				map.put("traffic", rs[i++]);
				map.put("breakType", rs[i++]);
				map.put("jeom", rs[i++]);
				map.put("penalty", rs[i++]);
				map.put("overduePayment", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("b.id", "id"));
		columns.add(new TextColumn4MapKey("b.status_", "status",
				getText("bs.sync.status"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getSyncStatuses())));
		columns.add(new TextColumn4MapKey("b.sync_code", "syncCode",
				getText("jinDunJTWF.syncCode"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.happen_date", "happenDate",
				getText("jinDunJTWF.happenDate"), 130).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("t.car_plate", "carPlateNo",
				getText("jinDunJTWF.carPlate"), 80).setSortable(true)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> map = (Map<String, Object>) context;
						return map.get("carPlateType") + "."
								+ map.get("carPlateNo");
					}
				}));
		columns.add(new TextColumn4MapKey("t.address", "address",
				getText("jinDunJTWF.address")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.source", "source",
				getText("jinDunJTWF.source"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.driver_name", "driverName",
				getText("jinDunJTWF.driverName"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.jeom", "jeom",
				getText("jinDunJTWF.jeom"), 75).setSortable(true)
				.setValueFormater(new NubmerFormater("#.#")));
		columns.add(new TextColumn4MapKey("t.penalty", "penalty",
				getText("jinDunJTWF.penalty"), 60).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("#.#")));
		columns.add(new TextColumn4MapKey("t.overdue_payment",
				"overduePayment", getText("jinDunJTWF.overduePayment"), 60)
				.setSortable(true).setValueFormater(new NubmerFormater("#.#")));
		columns.add(new TextColumn4MapKey("t.traffic", "traffic",
				getText("jinDunJTWF.traffic"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.break_type", "breakType",
				getText("jinDunJTWF.breakType"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.decision_no", "decisionNo",
				getText("jinDunJTWF.decisionNo"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.decision_type", "decisionType",
				getText("jinDunJTWF.decisionType"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("b.sync_date", "syncDate",
				getText("syncBase.syncDate"), 130).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.sync_code", "t.car_plate_no", "t.driver_name",
				"t.engine_no", "t.address", "t.source" };
	}

	@Override
	protected String getFormActionName() {
		return "jinDunJTWF";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 查看按钮
		tb.addButton(Toolbar.getDefaultOpenToolbarButton(getText("label.read")));

		if (!this.isReadonly()) {
			// "生成"按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.generate"))
					.setClick("bs.jinDunJTWFView.generate"));

			// "更多"按钮
			ToolbarMenuButton menuButton = new ToolbarMenuButton(
					getText("label.operate"))
					.setChange("bs.jinDunJTWFView.selectMenuButtonItem");
			tb.addButton(menuButton);

			// --标记为已处理
			menuButton.addMenuItem(getText("label.mark.done"), "mark."
					+ SyncBase.STATUS_DONE);
			// --标记为未处理
			menuButton.addMenuItem(getText("label.mark.new"), "mark."
					+ SyncBase.STATUS_NEW);

			// --抓取 一分公司在案车辆, 注:3为一分公司的ActorId
			menuButton.addMenuItem(getText("bs.spider.fengongsi01"),
					"jindunSpider.3");
			// --抓取 二分公司在案车辆
			menuButton.addMenuItem(getText("bs.spider.fengongsi02"),
					"jindunSpider.4");
			// --抓取 三分公司在案车辆
			menuButton.addMenuItem(getText("bs.spider.fengongsi03"),
					"jindunSpider.5");
			// --抓取 四分公司在案车辆
			menuButton.addMenuItem(getText("bs.spider.fengongsi04"),
					"jindunSpider.6");
			// --抓取 所有在案车辆
			menuButton.addMenuItem(getText("bs.spider.allUnit"),
					"jindunSpider.0");
		}

		// 状态单选按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				this.getSyncStatuses(), "status", 0,
				getText("title.click2changeSearchStatus")));

		// 搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));

		return tb;
	}

	public int unitId;// 车辆所在单位的id,0代表所有单位

	@Override
	protected int doSync(StringBuffer strMsg) {
		// 获取要抓取的车辆的id列表
		Condition condition;
		if (unitId > 0) {
			// 指定单位的在案车辆
			condition = new AndCondition().add(
					new EqualsCondition("c.status_", new Integer(0))).add(
					new EqualsCondition("a.id", new Integer(unitId)));
		} else {
			// 所有在案车辆
			condition = new EqualsCondition("c.status_", new Integer(0));
		}
		// 构建查询语句
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct c.id id,c.plate_no,c.plate_type,c.engine_no from BS_CAR c");
		sql.append(" inner join BS_MOTORCADE m on m.id=c.motorcade_id");
		sql.append(" inner join BC_IDENTITY_ACTOR a on a.id=m.unit_id");
		sqlObject.setSql(sql.toString());
		sqlObject.setArgs(null);
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> car = new LinkedHashMap<String, Object>();
				car.put("id", rs[0]);
				car.put("plateNo", rs[1]);
				car.put("plateType", rs[2]);
				car.put("engineNo", rs[3]);
				return car;
			}
		});
		List<Map<String, Object>> cars = new HibernateJpaNativeQuery<Map<String, Object>>(
				this.jpaTemplate, sqlObject).condition(condition).list();

		// 执行抓取
		return this.bsSyncService.doSync4JinDunJTWF(
				((SystemContext) this.getContext()).getUserHistory(), cars,
				strMsg);
	}
}
