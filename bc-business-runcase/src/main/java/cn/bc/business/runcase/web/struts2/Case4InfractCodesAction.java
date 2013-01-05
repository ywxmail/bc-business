/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.google.gson.JsonObject;

import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.FooterButton;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 违法代码管理Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Case4InfractCodesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isReadonly() {
		// 违法代码管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.infract.code"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：代码
		return new OrderCondition("c.code", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,c.code,c.subject,c.according,c.jeom,c.penalty");
		sql.append(",c.file_date,ac.actor_name author,c.modified_date,md.actor_name modifier");
		sql.append(" from bs_case_infract_code c");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY md on md.id=c.modifier_id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY ac on ac.id=c.author_id");

		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("code", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("according", rs[i++]);
				map.put("jeom", rs[i++]);
				map.put("penalty", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("author", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("modifier", rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("c.id", "id"));
		columns.add(new TextColumn4MapKey("c.code", "code",
				getText("runcase.code.code"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.subject", "subject",
				getText("runcase.code.subject"), 165).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.jeom", "jeom",
				getText("runcase.code.jeom"), 60).setSortable(true)
				.setValueFormater(new NubmerFormater("###,###.##")));
		// 罚款金额
		columns.add(new TextColumn4MapKey("c.penalty", "penalty",
				getText("runcase.code.penalty"), 80).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.##")));
		columns.add(new TextColumn4MapKey("c.according", "according",
				getText("runcase.code.according"), 180).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("ac.actor_name", "author",
				getText("runcase.code.author"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("c.file_date", "file_date",
				getText("runcase.code.fileDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("md.actor_name", "modifier",
				getText("runcase.code.modifier"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("c.modified_date", "modified_date",
				getText("runcase.code.modifiedDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.code", "c.subject" };
	}

	@Override
	protected String getFormActionName() {
		return "case4InfractCode";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['code']";
	}

	@Override
	protected FooterButton getGridFooterImportButton() {
		// 获取默认的导入按钮设置
		FooterButton fb = this.getDefaultGridFooterImportButton();

		// 配置特殊参数
		JsonObject cfg = new JsonObject();
		cfg.addProperty("tplCode", "IMPORT_INFRACTCODE");// 模板编码
		cfg.addProperty("importAction", "bc-business/case4InfractCode/import");// 导入数据的action路径(使用相对路径)
		cfg.addProperty("headerRowIndex", 0);// 列标题所在行的索引号(0-based)
		fb.setAttr("data-cfg", cfg.toString());

		// 返回导入按钮
		return fb;
	}

}
