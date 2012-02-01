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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

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

/**
 * 交委接口的投诉与建议信息视图Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class JiaoWeiADVICEsAction extends SyncViewAction {
	protected final Log logger = LogFactory.getLog(JiaoWeiADVICEsAction.class);
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isReadonly() {
		// 交通违章管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infractAdvice"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|违章时间
		return new OrderCondition("b.status_", Direction.Asc).add(
				"t.receive_date", Direction.Desc);
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
		sql.append(",t.subject,t.receive_date,t.unit_name,t.motorcade_name,t.car_plate,t.driver_cert,t.advisor_name,t.ticket,t.content,t.result_,t.subject,t.subject2");
		sql.append(" from bs_sync_jiaowei_ADVICE t");
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
				map.put("subject", rs[i++]);
				map.put("receiveDate", rs[i++]);
				map.put("unitName", rs[i++]);
				map.put("motorcadeName", rs[i++]);
				map.put("carPlate", rs[i++]);
				map.put("driverCert", rs[i++]);
				map.put("advisorName", rs[i++]);
				map.put("ticket", rs[i++]);
				map.put("content", rs[i++]);
				map.put("result", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("subject2", rs[i++]);
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
				getText("jiaoWeiADVICE.syncCode"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.receive_date", "receiveDate",
				getText("jiaoWeiADVICE.receiveDate"), 130).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("t.unit_name", "unitName",
				getText("jiaoWeiADVICE.unitName"), 80)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("t.motorcade_name", "motorcadeName",
				getText("jiaoWeiADVICE.motorcadeName"), 80)
				.setSortable(true));
		columns.add(new TextColumn4MapKey("t.car_plate", "carPlate",
				getText("jiaoWeiADVICE.carPlate"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.driver_cert", "driverCert",
				getText("jiaoWeiADVICE.driverCert"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.advisor_name", "advisorName",
				getText("jiaoWeiADVICE.advisorName"), 80).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.subject", "subject",
				getText("jiaoWeiADVICE.subject"),100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.subject2", "subject2",
				getText("jiaoWeiADVICE.subject2"),100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.content", "content",
				getText("jiaoWeiADVICE.content")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.sync_date", "syncDate",
				getText("syncBase.syncDate"), 130).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.sync_code", "t.car_plate", "t.driver_name",
				"t.driver_cert", "t.content", "t.advisorName" };
	}

	@Override
	protected String getFormActionName() {
		return "jiaoWeiADVICE";
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
					.setClick("bs.jiaoWeiADVICEView.generate"));

			// "更多"按钮
			ToolbarMenuButton menuButton = new ToolbarMenuButton(
					getText("label.operate"))
					.setChange("bs.jiaoWeiADVICEView.selectMenuButtonItem");
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

	public String dateType;// 同步时间范围的标记符

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
		} else {// 默认为今日
			DateUtils.setToZeroTime(fromDate);
			DateUtils.setToMaxTime(toDate);
		}

		// 执行同步
		return this.bsSyncService.doSync4JiaoWeiADVICE(
				((SystemContext) this.getContext()).getUserHistory(), fromDate,
				toDate, strMsg);
	}
}
