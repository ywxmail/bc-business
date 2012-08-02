/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.BSConstants;
import cn.bc.business.web.struts2.LinkFormater4ChargerInfo;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectCarAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
	public String loadLevel;//选择视图上加载信息量登记，0或无代表默认，1~代表

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|登记日期|车队
		return new OrderCondition("c.status_", Direction.Asc).add(
				"c.register_date", Direction.Desc).add("m.name", Direction.Asc);
	}

	@Override
	protected LikeCondition getGridSearchCondition4OneField(String field,
			String value) {
		if (field.indexOf("plate_no") != -1) {
			return new LikeCondition(field, value != null ? value.toUpperCase()
					: value);
		} else {
			return super.getGridSearchCondition4OneField(field, value);
		}
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append(" c.id,c.status_,c.code,c.plate_type,c.plate_no,c.register_date");
		sql.append(",c.scrap_date,c.motorcade_id,m.name,c.company,c.bs_type,c.charger");
		if(loadLevel!=null&&loadLevel.length()>0&&loadLevel.equals("1")){
			sql.append(" ,a.name as unitCompany,a.id as unitCompanyId");
			sql.append(" ,(select bc.end_date from bs_contract bc");
			sql.append(" inner join bs_car_contract bcc on bcc.contract_id=bc.id");
			sql.append(" where bc.type_=2 and bc.status_ ="+BCConstants.STATUS_ENABLED);
			sql.append(" and bcc.car_id=c.id");
			sql.append(" ORDER BY bc.end_date DESC limit 1) as ccEndDate");
			
			sql.append(" ,(select bcp.commerial_end_date from bs_car_policy bcp");
			sql.append(" where bcp.car_id=c.id and bcp.status_ ="+BCConstants.STATUS_ENABLED);
			sql.append(" ORDER BY bcp.commerial_end_date DESC limit 1) as commerialEndDate");
			
			sql.append(" ,(select bcp.greenslip_start_date from bs_car_policy bcp");
			sql.append(" where bcp.car_id=c.id and bcp.status_ ="+BCConstants.STATUS_ENABLED);
			sql.append(" ORDER BY bcp.greenslip_end_date DESC limit 1) as greenslipStartDate");

			sql.append(" ,(select bcp.greenslip_end_date from bs_car_policy bcp");
			sql.append(" where bcp.car_id=c.id and bcp.status_ ="+BCConstants.STATUS_ENABLED);
			sql.append(" ORDER BY bcp.greenslip_end_date DESC limit 1) as greenslipEndDate");
			
			sql.append(" ,c.factory_type as factoryType,c.engine_no as engineNo,c.vin");
			sql.append(" ,c.access_count as accessCount,c.access_weight as accessWeight,c.displacement");
			sql.append(",(select string_agg(man.name,' ') from bs_carman man");
			sql.append("inner join bs_car_driver cd on man.id = cd.driver_id ");
			sql.append("where cd.car_id = c.id and cd.status_ = 0) as driverName");
			
		}
		sqlObject.setSelect(sql.toString());
		sql = new StringBuffer();
		sql.append(" bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sql.append(" inner join bc_identity_actor a on a.id = m.unit_id");
		
		sqlObject.setFrom(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status_", rs[i++]);
				map.put("code", rs[i++]);
				map.put("plate_type", rs[i++]);
				map.put("plate_no", rs[i++]);
				map.put("register_date", rs[i++]);
				map.put("scrap_date", rs[i++]);
				map.put("motorcade_id", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("company", rs[i++]);
				map.put("bs_type", rs[i++]);
				map.put("charger", rs[i++]);
				if(loadLevel!=null&&loadLevel.length()>0&&loadLevel.equals("1")){
					map.put("unitCompany", rs[i++]);
					map.put("unitCompanyId", rs[i++]);
					map.put("ccEndDate", rs[i++]);
					map.put("commerialEndDate", rs[i++]);
					map.put("greenslipStartDate", rs[i++]);
					map.put("greenslipEndDate", rs[i++]);
					map.put("factoryType", rs[i++]);
					map.put("engineNo", rs[i++]);
					map.put("vin", rs[i++]);
					map.put("accessCount", rs[i++]);
					map.put("accessWeight", rs[i++]);
					map.put("displacement", rs[i++]);
					map.put("driverName", rs[i++]);
					
					//计算预计交车日期

					List<Timestamp> tempList=new ArrayList<Timestamp>();
					
					if(map.get("ccEndDate")!=null)
						tempList.add((Timestamp)map.get("ccEndDate"));
					
					if(map.get("commerialEndDate")!=null)
						tempList.add((Timestamp)map.get("commerialEndDate"));
					
					if(map.get("greenslipEndDate")!=null)
						tempList.add((Timestamp)map.get("greenslipEndDate"));
					
					if(tempList.size()==0){
						map.put("predictReturnDate", null);
					}else if(tempList.size()==1){
						map.put("predictReturnDate",tempList.get(0));
					}else if(tempList.size()==2){
						if(tempList.get(0).after(tempList.get(1))){
							map.put("predictReturnDate",tempList.get(1));
						}else
							map.put("predictReturnDate",tempList.get(0));
					}else{
						//排序	
						for(int j=0;j<tempList.size();j++){
							for(int k=0;k+1<tempList.size();k++){
								if(tempList.get(k).after(tempList.get(k+1))){
									tempList.add(k, tempList.get(k+1));
									tempList.remove(k+2);
								}
							}
						}
						map.put("predictReturnDate",tempList.get(0));
					}
				}
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
				getText("car.code"), 50).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.plate_no", "plate_no",
				getText("car.plate"), 80).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> car = (Map<String, Object>) context;
						return car.get("plate_type") + "."
								+ car.get("plate_no");
					}
				}));
		columns.add(new TextColumn4MapKey("c.register_date", "register_date",
				getText("car.registerDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c.scrap_date", "scrap_date",
				getText("car.scrapDate"), 100).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("car.motorcade"),80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.bs_type", "bs_type",
				getText("car.businessType"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.charger", "charger",
				getText("car.charger"), 120).setUseTitleFromLabel(true)
				.setValueFormater(new LinkFormater4ChargerInfo(this
						.getContextPath())));
		columns.add(new TextColumn4MapKey("c.company", "company",
				getText("selectCar.company"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new HiddenColumn4MapKey("motorcadeId", "motorcade_id"));
		columns.add(new HiddenColumn4MapKey("motorcadeName", "motorcade_name"));
		
		if(loadLevel!=null&&loadLevel.length()>0&&loadLevel.equals("1")){
			columns.add(new HiddenColumn4MapKey("unitCompany", "unitCompany"));
			columns.add(new HiddenColumn4MapKey("unitCompanyId", "unitCompanyId"));
			//预计交车日期
			columns.add(new HiddenColumn4MapKey("predictReturnDate", "predictReturnDate"));
			columns.add(new HiddenColumn4MapKey("ccEndDate", "ccEndDate"));
			columns.add(new HiddenColumn4MapKey("commerialEndDate", "commerialEndDate"));
			columns.add(new HiddenColumn4MapKey("greenslipStartDate", "greenslipStartDate"));
			columns.add(new HiddenColumn4MapKey("greenslipEndDate", "greenslipEndDate"));
			columns.add(new HiddenColumn4MapKey("factoryType", "factoryType"));
			columns.add(new HiddenColumn4MapKey("engineNo", "engineNo"));
			columns.add(new HiddenColumn4MapKey("vin", "vin"));
			columns.add(new HiddenColumn4MapKey("accessCount", "accessCount"));
			columns.add(new HiddenColumn4MapKey("accessWeight", "accessWeight"));
			columns.add(new HiddenColumn4MapKey("displacement", "displacement"));
			columns.add(new HiddenColumn4MapKey("driverName", "driverName"));
		}
		return columns;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("car.title.selectCar");
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "c.plate_no", "m.name" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['plate_type'] + '.' + ['plate_no']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectCar");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/car/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				return new EqualsCondition("c.status_", new Integer(ss[0]));
			} else {
				return new InCondition("c.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		} else {
			return null;
		}
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		if(status!=null&&status.length()>0)
			json.put("status", status);
		if(loadLevel!=null&&loadLevel.length()>0)
			json.put("loadLevel", loadLevel);
		return json;
		
	}

	@Override
	protected String getClickOkMethod() {
		return "bs.carSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BSConstants.NAMESPACE;
	}
}
