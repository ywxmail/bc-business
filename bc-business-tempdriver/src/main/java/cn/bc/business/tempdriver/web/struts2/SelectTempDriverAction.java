/**
 * 
 */
package cn.bc.business.tempdriver.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.BSConstants;
import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择司机Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectTempDriverAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 司机的状态，多个用逗号连接

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：最新修改日期|创建日期
		return new OrderCondition("t.modified_date", Direction.Desc).add("t.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();
		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.id,t.name,t.sex,t.birthdate,t.origin,t.region_ as region,t.address,t.new_addr as newAddress,t.phone");
		sql.append(",t.cert_identity as certIdentity,t.cert_fwzg as fwzg,t.cert_cyzg as cyzg,t.education,t.nation");
		sql.append(",t.marry,t.desc_ as desc,t.phone");
		sql.append(",t.status_ as status");
		sql.append(" FROM bs_temp_driver t");

		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id",rs[i++]);
				map.put("name",rs[i++]);
				map.put("sex",rs[i++]);
				map.put("birthdate",rs[i++]);
				map.put("origin",rs[i++]);
				map.put("region",rs[i++]);
				map.put("address",rs[i++]);
				map.put("newAddress",rs[i++]);
				map.put("phone",rs[i++]);
				map.put("certIdentity",rs[i++]);
				map.put("fwzg",rs[i++]);
				map.put("cyzg",rs[i++]);
				map.put("education",rs[i++]);
				map.put("nation",rs[i++]);
				map.put("marry",rs[i++]);
				map.put("desc",rs[i++]);
				map.put("phone",rs[i++]);
				map.put("status",rs[i++]);
		
				return map;
			}
		});
		return sqlObject;
	}
	
	/**
	 * 聘用状态值转换列表：
	 * 
	 * @return
	 */
	protected Map<String, String> getStatusValues() {
		Map<String, String> s = new LinkedHashMap<String, String>();
		s.put(String.valueOf(TempDriver.STATUS_RESERVE),
				getText("tempDriver.status.reserve"));
		s.put(String.valueOf(TempDriver.STATUS_CHECK),
				getText("tempDriver.status.check"));
		s.put(String.valueOf(TempDriver.STATUS_PASS),
				getText("tempDriver.status.pass"));
		s.put(String.valueOf(TempDriver.STATUS_GIVEUP),
				getText("tempDriver.status.giveup"));
		s.put("",getText("bs.status.all"));
		return s;
	}
	
	/**
	 * 营运状态值转换列表：
	 * 
	 * @return
	 */
	protected Map<String, String> getBusinessValues() {
		Map<String, String> s = new LinkedHashMap<String, String>();
		s.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("bs.status.active"));
		s.put(String.valueOf(BCConstants.STATUS_DRAFT),
				getText("bc.status.draft"));
		s.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("bs.status.logout"));
		return s;
	}
	
	/**
	 * 性别状态值转换列表：1=男，2=女
	 * 
	 * @return
	 */
	protected Map<String, String> getSexValues() {
		Map<String, String> s = new LinkedHashMap<String, String>();
		s.put(String.valueOf(TempDriver.SEX_MAN),
				getText("tempDriver.sex.man"));
		s.put(String.valueOf(TempDriver.SEX_WOMAN),
				getText("tempDriver.sex.woman"));
		return s;
	}
	
	/**
	 * 区域状态值转换列表：0=空，1=本市，2=本省 3=外省
	 * 
	 * @return
	 */
	protected Map<String, String> getRegionValues() {
		Map<String, String> s = new LinkedHashMap<String, String>();
		s.put(String.valueOf(TempDriver.REGION_),
				getText("tempDriver.region.empty"));
		s.put(String.valueOf(TempDriver.REGION_BEN_SHI),
				getText("tempDriver.region.benshi"));
		s.put(String.valueOf(TempDriver.REGION_BEN_SHENG),
				getText("tempDriver.region.bensheng"));
		s.put(String.valueOf(TempDriver.REGION_WAI_SHENG),
				getText("tempDriver.region.waisheng"));
		return s;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		//姓名
		columns.add(new TextColumn4MapKey("t.name", "name",
				getText("tempDriver.name"),60).setSortable(true)
				.setUseTitleFromLabel(true));
		//身份证
		columns.add(new TextColumn4MapKey("t.cert_identity", "certIdentity",
				getText("tempDriver.certIdentity"),150).setSortable(true)
				.setUseTitleFromLabel(true));
		//服务资格证
		columns.add(new TextColumn4MapKey("t.cert_fwzg", "fwzg",
				getText("tempDriver.fwzg"),80).setSortable(true)
				.setUseTitleFromLabel(true));
		//从业资格证
		columns.add(new TextColumn4MapKey("t.cert_cyzg", "cyzg",
				getText("tempDriver.cyzg"),120).setSortable(true)
				.setUseTitleFromLabel(true));
		//区域
		columns.add(new TextColumn4MapKey("t.region_", "region",
				getText("tempDriver.region"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(getRegionValues())));
		//性别
		columns.add(new TextColumn4MapKey("t.sex", "sex",
				getText("tempDriver.sex"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(getSexValues())));
		//面试日期
		columns.add(new TextColumn4MapKey("t.interview_date", "interviewDate",
				getText("tempDriver.interviewDate"), 80).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		//报名日期
		columns.add(new TextColumn4MapKey("t.register_date", "registerDate",
				getText("tempDriver.registerDate"), 80).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		//籍贯
		columns.add(new TextColumn4MapKey("t.origin", "origin",
				getText("tempDriver.origin"),150).setSortable(true)
				.setUseTitleFromLabel(true));
		//备注
		columns.add(new TextColumn4MapKey("t.desc_", "desc",
				getText("tempDriver.desc")).setSortable(true)
				.setUseTitleFromLabel(true));

		return columns;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("tempDriver.title.select");
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "t.name","t.origin","t.cert_identity","t.cert_fwzg","t.cert_cyzg" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectTempDriver");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/tempDriver/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition ac=new AndCondition();

		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				ac.add(new EqualsCondition("t.status_", new Integer(
						ss[0])));
			} else {
				ac.add(new InCondition("t.status_",
						StringUtils.stringArray2IntegerArray(ss)));
			}
		}

		return ac.isEmpty()?null:ac;
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();

		// 状态条件
		if (this.status != null && this.status.length() > 0) {
			json.put("status", status);
		}

		return json.isEmpty() ? null : json;
	}

	@Override
	protected String getClickOkMethod() {
		return "bs.tempDriverSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}

}
