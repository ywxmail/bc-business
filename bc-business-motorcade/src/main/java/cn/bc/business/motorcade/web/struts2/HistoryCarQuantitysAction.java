/**
 * 
 */
package cn.bc.business.motorcade.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.BSConstants;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 查看历史车辆数Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class HistoryCarQuantitysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public Long motorcade_id;

	@Override
	public boolean isReadonly() {
		// 车队管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.motorcade"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：年 月 日
		return new OrderCondition("a.year_", Direction.Desc).add("a.month_",Direction.Desc).add("a.day_",Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,a.year_ as year,a.month_ as month,a.day_ as day,a.quantity");
		sql.append(",b.name as mtname,c.actor_name as mname,a.modified_date as mdate,u.name as unitName");
		sql.append(" from bs_motorcade_carquantity a");
		sql.append(" inner join bs_motorcade b on b.id=a.motorcade_id");
		sql.append(" inner join bc_identity_actor u on u.id=b.unit_id");
		sql.append(" left join bc_identity_actor_history c on c.id=a.modifier_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("year", rs[i++]);
				map.put("month", rs[i++]);
				map.put("day", rs[i++]);
				map.put("quantity", rs[i++]);
				map.put("mtname", rs[i++]);
				map.put("mname", rs[i++]);
				map.put("mdate", rs[i++]);
				map.put("unitName", rs[i++]);
				String date=map.get("year").toString()
							+"-"
							+map.get("month").toString()
							+"-"
							+map.get("day").toString();
				map.put("date", date);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("m.id", "id"));
		columns.add(new TextColumn4MapKey("u.name", "unitName",
				getText("motorcade.unit"), 85).setSortable(true)
				.setUseTitleFromLabel(true));
		//车队名称
		columns.add(new TextColumn4MapKey("b.name", "mtname",
				getText("motorcade.name"), 85).setSortable(true)
				.setUseTitleFromLabel(true));
		//日期
		columns.add(new TextColumn4MapKey("", "date",
				getText("historyCarQuantity.date"),100).setSortable(true)
				.setUseTitleFromLabel(true));
		//车辆数
		columns.add(new TextColumn4MapKey("a.quantity", "quantity",
				getText("historyCarQuantity.carquantity"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		//最后修改人
		columns.add(new TextColumn4MapKey("c.actor_name", "mname",
				getText("historyCarQuantity.modifier"), 80).setSortable(true));
		//最后修改时间
		columns.add(new TextColumn4MapKey("a.modified_date", "mdate",
				getText("historyCarQuantity.modifierDate")).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.name", "a.year_" , "a.month_", "a.day_","c.actor_name","u.name"};
	}

	@Override
	protected String getFormActionName() {
		return "historyCarQuantity";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(700).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['mtname'] + ['date']+'车辆数'";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if(motorcade_id != null){
			return new EqualsCondition("a.motorcade_id", motorcade_id);
		}
		return null;
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		if(motorcade_id != null){
			json.put("motorcade_id", motorcade_id);
		}
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if(!isReadonly()){
			tb.addButton(this.getDefaultCreateToolbarButton());
			tb.addButton(this.getDefaultEditToolbarButton());
			tb.addButton(this.getDefaultDeleteToolbarButton());
		}else{
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	
	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return false;
	}


	public JSONArray motorcadeList;// 车队名称的下拉列

	@Override
	protected void initConditionsFrom() throws Exception {
		
	}

	@Override
	public String getAdvanceSearchConditionsJspPath() {
		return  BSConstants.NAMESPACE + "/motorcade/historyCarQuantity";
	}
	
	// ==高级搜索代码结束==
}
