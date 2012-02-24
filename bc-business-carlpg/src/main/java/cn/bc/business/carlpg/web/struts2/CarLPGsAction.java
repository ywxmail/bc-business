/**
 * 
 */
package cn.bc.business.carlpg.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;

/**
 * LPG视图视图Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarLPGsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isReadonly() {
		// 车辆管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：排序号
		return new OrderCondition("order_", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select id as id,status_ as status,order_ as order,name_ as name,full_name as fullname");
		sql.append(",model as model,gp_model as gpmodel,jcf_model as jcfmodel,qhq_model as qhqmodel");
		sql.append(",psq_model as psqmodel,desc_ as desc");
		sql.append(" from bs_car_lpgmodel");
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
				map.put("order", rs[i++]);
				map.put("name", rs[i++]);
				map.put("fullname", rs[i++]);
				map.put("model", rs[i++]);
				map.put("gpmodel", rs[i++]);
				map.put("jcfmodel", rs[i++]);
				map.put("qhqmodel", rs[i++]);
				map.put("psqmodel", rs[i++]);
				map.put("desc", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("id", "id"));
		/*
		 * carlpg.order			=排序号
			carlpg.status			=状态
			carlpg.title			=LPG配置信息
			carlpg.name				=名称
			carlpg.fullname			=专用装置供应商
			carlpg.model			=专用装置品牌型号
			carlpg.gpmodel			=钢瓶品牌型号
			carlpg.jcfmodel			=集成阀品牌型号
			carlpg.qhqmodel			=汽化器品牌型号
			carlpg.psgmodel			=混合/喷射器品牌型号
			carlpg.desc				=描述
		 */
		columns.add(new TextColumn4MapKey("status_", "status", 
				getText("carlpg.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getEntityStatuses())));
		columns.add(new TextColumn4MapKey("order_", "order", 
				getText("carlpg.order"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("name_", "name", 
				getText("carlpg.name"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("full_name", "fullname", 
				getText("carlpg.fullname"), 130).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("model", "model", 
				getText("carlpg.model"), 120).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("gp_model", "gpmodel", 
				getText("carlpg.gpmodel"), 120).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("jcf_model", "jcfmodel", 
				getText("carlpg.jcfmodel"), 120).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("qhq_model", "qhqmodel", 
				getText("carlpg.qhqmodel"), 120).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("psq_model", "psqmodel", 
				getText("carlpg.psqmodel"), 150).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("desc_", "desc", 
				getText("carlpg.desc")).setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] {"name_","full_name"
				,"model","gp_model","jcf_model"
				,"qhq_model","psq_model"};
	}
	
	@Override
	protected String getFormActionName() {
		return "carLPG";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(650).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}


}