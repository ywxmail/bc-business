/**
 * 
 */
package cn.bc.business.sync.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.sync.domain.Sync4InfractTraffic;
import cn.bc.business.sync.service.Sync4InfractTrafficService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.sync.domain.SyncBase;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;
import cn.bc.web.ui.json.Json;

/**
 * 交委接口的交通违章信息视图Action
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
public class Sync4InfractTrafficsAction extends ViewAction<Map<String, Object>> {
	protected final Log logger = LogFactory
			.getLog(Sync4InfractTrafficsAction.class);
	private static final long serialVersionUID = 1L;
	private Sync4InfractTrafficService sync4InfractTrafficService;
	public String status = String.valueOf(Sync4InfractTraffic.STATUS_NEW); // 状态，多个用逗号连接

	@Autowired
	public void setSync4InfractTrafficService(
			Sync4InfractTrafficService sync4InfractTrafficService) {
		this.sync4InfractTrafficService = sync4InfractTrafficService;
	}

	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractTraffic"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|违章时间
		return new OrderCondition("b.status_", Direction.Asc).add(
				"t.happen_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select b.id,b.status_,b.sync_type,b.sync_code,b.sync_from,b.sync_date,b.author_id,a.actor_name");
		sql.append(",t.happen_date,t.car_plate,t.driver_cert,t.driver_name,t.jeom,t.content");
		sql.append(" from bs_sync_infract_traffic t");
		sql.append(" inner join bc_sync_base b on b.id=t.id");
		sql.append(" inner join bc_identity_actor_history a on a.id=b.author_id");
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
				map.put("sync_type", rs[i++]);
				map.put("sync_code", rs[i++]);
				map.put("sync_from", rs[i++]);
				map.put("sync_date", rs[i++]);
				map.put("author_id", rs[i++]);
				map.put("actor_name", rs[i++]);
				map.put("happen_date", rs[i++]);
				map.put("car_plate", rs[i++]);
				map.put("driver_cert", rs[i++]);
				map.put("driver_name", rs[i++]);
				map.put("jeom", rs[i++]);
				map.put("content", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("b.id", "id"));
		columns.add(new TextColumn4MapKey("b.status_", "status_",
				getText("bs.sync.status"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getSyncStatuses())));
		columns.add(new TextColumn4MapKey("t.happen_date", "happen_date",
				getText("sync4InfractTraffic.happenDate"), 90)
				.setSortable(true).setValueFormater(
						new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("t.car_plate", "car_plate",
				getText("sync4InfractTraffic.carPlate"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.jeom", "jeom",
				getText("sync4InfractTraffic.jeom"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("t.content", "content",
				getText("sync4InfractTraffic.content"))
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.sync_code", "sync_code",
				getText("sync4InfractTraffic.syncCode"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.driver_name", "driver_name",
				getText("sync4InfractTraffic.driverName"), 80)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("t.driver_cert", "driver_cert",
				getText("sync4InfractTraffic.driverCert"), 80)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("b.sync_date", "sync_date",
				getText("syncBase.syncDate"), 90).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		return columns;
	}

	private Map<String, String> getSyncStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(SyncBase.STATUS_NEW),
				getText("bs.sync.status.new"));
		statuses.put(String.valueOf(SyncBase.STATUS_DONE),
				getText("bs.sync.status.done"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.sync_code", "t.car_plate", "t.driver_name",
				"t.driver_cert", "t.content" };
	}

	@Override
	protected String getFormActionName() {
		return "sync4InfractTraffic";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(850).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['sync_code']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		return ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"b.status_");
	}

	@Override
	protected String getGridDblRowMethod() {
		return "bc.page.open";
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
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 查看按钮
		tb.addButton(Toolbar.getDefaultOpenToolbarButton(getText("label.read")));

		if (!this.isReadonly()) {
			// "生成"按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("label.generate"))
					.setClick("bs.sync4InfractTrafficsView.generate"));

			// "更多"按钮
			ToolbarMenuButton menuButton = new ToolbarMenuButton(
					getText("label.operate"))
					.setChange("bs.sync4InfractTrafficsView.selectMenuButtonItem");
			tb.addButton(menuButton);

			// --标记为已处理
			menuButton.addMenuItem(getText("label.mark.done"), "mark."
					+ SyncBase.STATUS_DONE);
			// --标记为未处理
			menuButton.addMenuItem(getText("label.mark.new"), "mark."
					+ SyncBase.STATUS_NEW);

			// --同步今天的数据
			menuButton.addMenuItem(getText("label.sync.type.today"),
					"sync.today");
			// --同步最近10天的数据
			menuButton.addMenuItem(getText("label.sync.type.lastest10days"),
					"sync.lastest10days");
			// --同步本月的数据
			menuButton.addMenuItem(getText("label.sync.type.thisMonth"),
					"sync.thisMonth");

			// // "同步"按钮
			// tb.addButton(new ToolbarButton()
			// .setIcon("ui-icon-transferthick-e-w")
			// .setText(getText("label.sync"))
			// .setClick("bs.sync4InfractTrafficsView.sync"));

			// // 删除按钮
			// tb.addButton(Toolbar
			// .getDefaultDeleteToolbarButton(getText("label.delete")));
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

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath()
				+ "/bc-business/sync/sync4InfractTraffics/view.js";
	}

	public String dateType;// 同步时间范围的标记符

	/**
	 * 同步的处理
	 * 
	 * @return
	 * @throws Exception
	 */
	public String sync() throws Exception {
		StringBuffer strMsg = new StringBuffer();// 用于保存连接错误信息

		// 计算同步的起始日期和结束日期
		Calendar fromDate;
		Calendar toDate;
		fromDate = Calendar.getInstance();
		toDate = Calendar.getInstance();
		if ("thisMonth".equalsIgnoreCase(dateType)) {// 本月
			DateUtils.setToFirstDayOfMonth(fromDate);
			DateUtils.setToLastDayOfMonth(toDate);
		} else if ("lastest10days".equalsIgnoreCase(dateType)) {// 最近10天
			DateUtils.setToZeroTime(fromDate);
			fromDate.add(Calendar.DAY_OF_MONTH, -10);// 10天前的一天
			DateUtils.setToMaxTime(toDate);
		} else {// 默认为今日
			DateUtils.setToZeroTime(fromDate);
			DateUtils.setToMaxTime(toDate);
		}

		// 执行同步处理
		int newCount;
		Json json = new Json();
		try {
			newCount = sync4InfractTrafficService.doSync(
					((SystemContext) this.getContext()).getUserHistory(),
					fromDate, toDate, strMsg);
			// newCount = syncFromWS(fromDate, toDate, strMsg);

			// 发生异常就直接退出
			if (strMsg.length() > 0) {
				json.put("success", false);
				json.put(
						"msg",
						getText("bs.sync.failed",
								new String[] { strMsg.toString() }));
			} else {
				json.put("success", true);
				json.put(
						"msg",
						getText("bs.sync.success",
								new String[] { String.valueOf(newCount) }));
			}
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg",
					getText("bs.sync.failed", new String[] { e.getMessage() }));
		}

		this.json = json.toString();
		return "json";
	}
}
