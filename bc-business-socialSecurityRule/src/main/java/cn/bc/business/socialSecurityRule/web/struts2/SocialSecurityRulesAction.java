/**
 * 
 */
package cn.bc.business.socialSecurityRule.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.socialSecurityRule.service.SocialSecurityRuleService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 
 * 
 * @author
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SocialSecurityRulesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); 
	
	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin")
				,getText("key.role.bs.socialSecurityRule"));
	}
	
	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("s.start_year",Direction.Desc)
		.add("s.start_month", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select s.id,s.area_name,s.start_year,s.start_month,s.house_type");
		sql.append(",a.actor_name as aname,s.file_date,m.actor_name as mname,s.modified_date");
		sql.append(",getsocialsecurityruledetail(s.id)");
		sql.append(" from bs_socialsecurityrule s");
		sql.append(" inner join bc_identity_actor_history a on a.id=s.author_id ");
		sql.append(" left join bc_identity_actor_history m on m.id=s.modifier_id ");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("area_name", rs[i++]);
				map.put("start_year", rs[i++]);
				map.put("start_month", rs[i++]);
				map.put("startDate", map.get("start_year")+"年"+map.get("start_month")+"月");
				map.put("house_type", rs[i++]);
				map.put("aname", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("mname", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("detail", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['area_name']+['start_year']+'年'+['start_month']+'月起'+['house_type']+'社保收费规则'";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[]{"s.area_name","s.house_type","a.actor_name"};
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("s.id", "id"));
		columns.add(new TextColumn4MapKey("s.area_name", "area_name",
						getText("socialSecurityRule.areaName"), 90)
						.setUseTitleFromLabel(true));	
		columns.add(new TextColumn4MapKey("s.start_year", "startDate",
				getText("socialSecurityRule.starDate"), 90)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("s.house_type", "house_type",
				getText("socialSecurityRule.houseType"), 80)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("", "detail",
				getText("socialSecurityRuleDetail.detail"))
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.actor_name", "aname",
				getText("socialSecurityRule.aname"), 80));
		columns.add(new TextColumn4MapKey("a.file_date", "file_date",
				getText("socialSecurityRule.fileDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("m.actor_name", "mname",
				getText("socialSecurityRule.mname"), 80));
		columns.add(new TextColumn4MapKey("m.modified_date", "modified_date",
				getText("socialSecurityRule.modifierDate"),130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}

	@Override
	protected String getFormActionName() {
		return "socialSecurityRule";
	}
	
	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}
	
	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (!this.isReadonly()) {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());

			tb.addButton(this.getDefaultDeleteToolbarButton());

		}else{
			//查看
			tb.addButton(this.getDefaultOpenToolbarButton());
		}
	
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	//高级搜索
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
	
	private SocialSecurityRuleService socialSecurityRuleService;

	@Autowired
	public void setSocialSecurityRuleService(
			SocialSecurityRuleService socialSecurityRuleService) {
		this.socialSecurityRuleService = socialSecurityRuleService;
	}
	
	public JSONArray areas;// 使用区域下拉列表信息
	public JSONArray houseTypes;// 可选户口类型信息
	public JSONArray months;// 可选月份
	public JSONArray years;// 可选年份
	
	@Override
	protected void initConditionsFrom() throws Exception {
		// 加载可选户口类型列表
		this.houseTypes =OptionItem.toLabelValues(this.socialSecurityRuleService.findHouseTypeOption());
		
		this.areas=OptionItem.toLabelValues(this.socialSecurityRuleService.findAreaOption());
		this.months=OptionItem.toLabelValues(this.getMonthList());
		this.years=OptionItem.toLabelValues(this.getYearList());
	}
	
	private List<Map<String,String>> getMonthList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		Map<String,String> map=null;
		for(int i=1;i<13;i++){
			map=new HashMap<String,String>();
			map.put("key", i+"");
			map.put("value", i+"");
			list.add(map);
		}
		return list;
	}
	
	private List<Map<String,String>> getYearList(){
		int year=Calendar.getInstance().get(Calendar.YEAR);
		year=year-5;
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		Map<String,String> map=null;
		for(int i=0;i<10;i++){
			map=new HashMap<String,String>();
			int y=year+i;
			map.put("key", y+"");
			map.put("value", y+"");
			list.add(map);
		}
		return list;
	}
	
	
}