/**
 * 
 */
package cn.bc.business.tempDriver.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.tempDriver.domain.TempDriver;
import cn.bc.business.tempDriver.domain.TempDriverWorkFlow;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.query.condition.impl.QlCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 司机招聘信息的视图Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TempDriversAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isReadonly() {
		// 司机招聘管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.tempDriver"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建日期
		return new OrderCondition("t.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();
		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.id,t.name,t.sex,t.birthdate,t.origin,t.region_ as region,t.address,t.new_addr as newAddress,t.phone");
		sql.append(",t.cert_identity as certIdentity,t.cert_fwzg as fwzg,t.cert_cyzg as cyzg,t.education,t.nation");
		sql.append(",t.pic_path as picPath,t.marry,t.desc_ as desc,t.phone");
		sql.append(",w.offer_status as ostatus");
		sql.append(",t.file_date,u.actor_name as aname,t.modified_date,m.actor_name as mname");
		sql.append(" FROM bs_temp_driver t");
		sql.append(" LEFT JOIN bs_temp_driver_workflow w on w.pid=t.id");
		sql.append(" INNER JOIN bc_identity_actor_history u on u.id=t.author_id");
		sql.append(" LEFT JOIN bc_identity_actor_history m on m.id=t.modifier_id");

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
				map.put("picPath",rs[i++]);
				map.put("marry",rs[i++]);
				map.put("desc",rs[i++]);
				map.put("phone",rs[i++]);
				map.put("ostatus",rs[i++]);
				map.put("file_date",rs[i++]);
				map.put("aname",rs[i++]);
				map.put("modified_date",rs[i++]);
				map.put("mname",rs[i++]);
				return map;
			}
		});
		return sqlObject;
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
	
	/**
	 * 录用状态值转换列表：0=审核中，1=录用，2=不录用
	 * 
	 * @return
	 */
	protected Map<String, String> getOfferStatusValues() {
		Map<String, String> s = new LinkedHashMap<String, String>();
		s.put(String.valueOf(TempDriverWorkFlow.OFFER_STATUS_CHECK),
				getText("tempDriverWorkFlow.offerStatus.check"));
		s.put(String.valueOf(TempDriverWorkFlow.OFFER_STATUS_PASS),
				getText("tempDriverWorkFlow.offerStatus.pass"));
		s.put(String.valueOf(TempDriverWorkFlow.OFFER_STATUS_NOPASS),
				getText("tempDriverWorkFlow.offerStatus.noPass"));
		s.put("","未参与");
		return s;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		//录用状态
		columns.add(new TextColumn4MapKey("w.offer_status", "ostatus",
				getText("tempDriverWorkFlow.newOfferStatus"), 80).setSortable(true)
				.setValueFormater(new KeyValueFormater(getOfferStatusValues())));
		//姓名
		columns.add(new TextColumn4MapKey("t.name", "name",
				getText("tempDriver.name"),60).setSortable(true)
				.setUseTitleFromLabel(true));
		//性别
		columns.add(new TextColumn4MapKey("t.sex", "sex",
				getText("tempDriver.sex"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(getSexValues())));
		//出生日期
		columns.add(new TextColumn4MapKey("t.birthdate", "birthdate",
				getText("tempDriver.birthdate"), 90).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		//籍贯
		columns.add(new TextColumn4MapKey("t.origin", "origin",
				getText("tempDriver.origin"),150).setSortable(true)
				.setUseTitleFromLabel(true));
		//区域
		columns.add(new TextColumn4MapKey("t.region_", "region",
				getText("tempDriver.region"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(getRegionValues())));
		//身份证地址
		columns.add(new TextColumn4MapKey("t.cert_identity", "certIdentity",
				getText("tempDriver.certIdentity"),150).setSortable(true)
				.setUseTitleFromLabel(true));
		//服务资格证
		columns.add(new TextColumn4MapKey("t.cert_fwzg", "fwzg",
				getText("tempDriver.fwzg"),80).setSortable(true)
				.setUseTitleFromLabel(true));
		//电话号码
		columns.add(new TextColumn4MapKey("t.phone", "phone",
				getText("tempDriver.phone"),100).setSortable(true)
				.setUseTitleFromLabel(true));
		//从业资格证
		columns.add(new TextColumn4MapKey("t.cert_cyzg", "cyzg",
				getText("tempDriver.cyzg"),120).setSortable(true)
				.setUseTitleFromLabel(true));
		//学历
		columns.add(new TextColumn4MapKey("t.education", "education",
				getText("tempDriver.education"),60).setSortable(true)
				.setUseTitleFromLabel(true));
		//民族
		columns.add(new TextColumn4MapKey("t.nation", "nation",
				getText("tempDriver.nation"),100).setSortable(true)
				.setUseTitleFromLabel(true));
		//婚姻状况
		columns.add(new TextColumn4MapKey("t.marry", "marry",
				getText("tempDriver.marry"),80).setSortable(true)
				.setUseTitleFromLabel(true));
		//备注
		columns.add(new TextColumn4MapKey("t.desc_", "desc",
				getText("tempDriver.desc")).setSortable(true)
				.setUseTitleFromLabel(true));
		
		columns.add(new TextColumn4MapKey("u.actor_name", "uname",
				getText("label.authorName"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.file_date", "fileDate",
				getText("label.fileDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.actor_name", "mname",
				getText("tempDriver.modifier"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("t.modified_date", "modified_date",
				getText("tempDriver.modifiedDate"), 120).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "t.name","t.origin","t.address","t.new_addr" ,"t.cert_identity"
				,"t.cert_fwzg","t.cert_cyzg","t.education","t.nation", "t.marry","u.actor_name","m.actor_name"};
	}

	@Override
	protected String getFormActionName() {
		return "tempDriver";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(700).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition andCondition = new AndCondition();
		//过滤旧的流程
		andCondition.add(new QlCondition("NOT EXISTS(select 1 from bs_temp_driver_workflow w2 where w2.pid=t.id and w.start_time>w2.start_time)"));
		return andCondition;
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(getDefaultOpenToolbarButton());
		} else {
			// 新建按钮
			tb.addButton(getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(getDefaultEditToolbarButton());

		}
		
		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());
		
		return tb;
	}
}
