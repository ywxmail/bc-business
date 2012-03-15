/**
 * 
 */
package cn.bc.business.sync.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
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
 * 交委接口的交通违法信息视图Action
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
public class JiaoWeiJTWFsAction extends SyncViewAction {
	protected final Log logger = LogFactory.getLog(JiaoWeiJTWFsAction.class);
	private static final long serialVersionUID = 1L;

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
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(850);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select b.id,b.status_,b.sync_type,b.sync_code,b.sync_from,b.sync_date");
		sql.append(",t.happen_date,t.unit_name,t.motorcade_name,t.car_plate_type,t.car_plate_no,t.driver_cert,t.driver_name,t.jeom,t.content");
		sql.append(" from bs_sync_jiaowei_jtwf t");
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
				map.put("unitName", rs[i++]);
				map.put("motorcadeName", rs[i++]);
				map.put("carPlateType", rs[i++]);
				map.put("carPlateNo", rs[i++]);
				map.put("driverCert", rs[i++]);
				map.put("driverName", rs[i++]);
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
		columns.add(new TextColumn4MapKey("b.status_", "status",
				getText("bs.sync.status"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getSyncStatuses())));
		columns.add(new TextColumn4MapKey("b.sync_code", "syncCode",
				getText("jiaoWeiJTWF.syncCode"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.happen_date", "happenDate",
				getText("jiaoWeiJTWF.happenDate"), 130).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("t.unit_name", "unitName",
				getText("jiaoWeiJTWF.unitName"), 80)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("t.motorcade_name", "motorcadeName",
				getText("jiaoWeiJTWF.motorcadeName"), 80)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("t.car_plate", "carPlateNo",
				getText("jiaoWeiJTWF.carPlate"), 80).setSortable(true)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> map = (Map<String, Object>) context;
						return map.get("carPlateType") + "."
								+ map.get("carPlateNo");
					}
				}));
		columns.add(new TextColumn4MapKey("t.jeom", "jeom",
				getText("jiaoWeiJTWF.jeom"), 60).setSortable(true)
				.setValueFormater(new NubmerFormater("#.#")));
		columns.add(new TextColumn4MapKey("t.content", "content",
				getText("jiaoWeiJTWF.content")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.driver_name", "driverName",
				getText("jiaoWeiJTWF.driverName"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.driver_cert", "driverCert",
				getText("jiaoWeiJTWF.driverCert"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("b.sync_date", "syncDate",
				getText("syncBase.syncDate"), 130).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.sync_code", "t.car_plate_no", "t.driver_name",
				"t.driver_cert", "t.content" };
	}

	@Override
	protected String getFormActionName() {
		return "jiaoWeiJTWF";
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
					.setClick("bs.jiaoWeiJTWFView.generate"));

			// "更多"按钮
			ToolbarMenuButton menuButton = new ToolbarMenuButton(
					getText("label.operate"))
					.setChange("bs.jiaoWeiJTWFView.selectMenuButtonItem");
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
			// --同步上月的数据
			menuButton.addMenuItem(getText("label.sync.type.lastMonth"),
					"sync.lastMonth");
			// --同步指定期限内的数据
			menuButton.addMenuItem(getText("label.sync.type.customRange"),
					"sync.customRange");
		}

		// 状态单选按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				this.getSyncStatuses(), "status", 0,
				getText("title.click2changeSearchStatus")));

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	public String dateType;// 同步时间范围的标记符
	
	// == 指定期限内同步 代码开始  ==
	private Calendar customFromDate; //指定开始日期
	private Calendar customToDate; //指定结束日期
	
	public Calendar getCustomFromDate() {
		return customFromDate;
	}

	public void setCustomFromDate(Calendar customFromDate) {
		this.customFromDate = customFromDate;
	}

	public Calendar getCustomToDate() {
		return customToDate;
	}

	public void setCustomToDate(Calendar customToDate) {
		this.customToDate = customToDate;
	}

	@Override
	protected int doSync(StringBuffer strMsg) {
		// 计算同步的起始日期和结束日期
		Calendar fromDate = Calendar.getInstance();
		Calendar toDate = Calendar.getInstance();
		if ("thisMonth".equalsIgnoreCase(dateType)) {// 本月
			DateUtils.setToFirstDayOfMonth(fromDate);
			DateUtils.setToLastDayOfMonth(toDate);
		} else if ("lastMonth".equalsIgnoreCase(dateType)) {// 上月
			fromDate.add(Calendar.MONTH, -1);
			toDate.add(Calendar.MONTH, -1);
			DateUtils.setToFirstDayOfMonth(fromDate);
			DateUtils.setToLastDayOfMonth(toDate);
		} else if ("lastest10days".equalsIgnoreCase(dateType)) {// 最近10天
			DateUtils.setToZeroTime(fromDate);
			fromDate.add(Calendar.DAY_OF_MONTH, -10);// 10天前的一天
			DateUtils.setToMaxTime(toDate);
		} else if ("customRange".equalsIgnoreCase(dateType)) {// 同步指定期限内的数据
			fromDate = customFromDate;
			toDate = customToDate;
		} else {// 默认为今日
			DateUtils.setToZeroTime(fromDate);
			DateUtils.setToMaxTime(toDate);
		}

		// 执行同步
		return this.bsSyncService.doSync4JiaoWeiJTWF(
				((SystemContext) this.getContext()).getUserHistory(), fromDate,
				toDate, strMsg);
	}
	// == 指定期限内同步 代码结束  ==
	
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
				"name");

		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));

	}

	// ==高级搜索代码结束==

}
