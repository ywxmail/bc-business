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

import cn.bc.BCConstants;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.LinkFormater4Id;
import cn.bc.web.formater.NubmerFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 事故理赔Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseAccidentsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接
	public Long carManId;
	public Long carId;

	@Override
	public boolean isReadonly() {
		// 司机管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.accident"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建日期
		return new OrderCondition("b.status_", Direction.Asc).add(
				"b.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id,b.status_,b.code,c.sort,b.motorcade_name,b.driver_name,b.car_plate,b.driver_cert,b.happen_date");
		sql.append(",b.address,b.driver_id,b.car_id ");
		sql.append(",c.carman_cost,c.third_loss,c.third_cost");
		sql.append(",c.car_wounding,c.third_wounding,c.agreement_payment,c.desc_ as acc_desc");
		sql.append(",c.origin ,c.duty,c.is_inner_fix");
		sql.append(",b.desc_ ,c.car_hurt,c.actual_loss");
		sql.append(",c.receiver_name,c.insurance_company");
		sql.append(",c.is_deliver,c.is_claim,c.is_pay,a.name as unitname");
		sql.append(",b.motorcade_id,b.car_id,b.driver_id");
		sql.append(" from bs_case_accident c");
		sql.append(" inner join bs_case_base b on b.id=c.id");
		sql.append(" left join bs_motorcade m on m.id=b.motorcade_id");
		sql.append(" left join bc_identity_actor a on a.id=m.unit_id");
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
				map.put("code", rs[i++]);
				map.put("sort", rs[i++]);
				map.put("motorcade_name", rs[i++]);
				map.put("driver_name", rs[i++]);
				map.put("car_plate", rs[i++]);
				map.put("driver_cert", rs[i++]);
				map.put("happen_date", rs[i++]);
				map.put("address", rs[i++]);
				map.put("driver_id", rs[i++]);
				map.put("car_id", rs[i++]);
				map.put("carman_cost", rs[i++]);// 司机拖车费 CARMAN_COST number
				map.put("third_loss", rs[i++]);// 第三者损失 THIRD_LOSS number
				map.put("third_cost", rs[i++]);// 第三者拖车费 THIRD_COST number
				map.put("car_wounding", rs[i++]);// 司机伤人 CARMAN_HURT_COUNT
														// number
				map.put("third_wounding", rs[i++]);// 第三者伤人 THIRD_HURT_COUNT
														// number
				map.put("agreement_payment", rs[i++]);// 协议赔付 AGREEMENT_PAYMENT
														// number
				map.put("acc_desc", rs[i++]);// 备注 DESC_ String
				map.put("origin", rs[i++]);// 籍贯
				map.put("duty", rs[i++]);// 责任
				map.put("is_inner_fix", rs[i++]);// 厂修
				map.put("desc_", rs[i++]);// 经过=基表备注
				map.put("car_hurt", rs[i++]);// 自车损失=车损情况
				map.put("actual_loss", rs[i++]);// 总损=实际损失
				map.put("receiver_name", rs[i++]);// 跟进人员=经办人
				map.put("insurance_company", rs[i++]);// 保险公司
				map.put("is_deliver", rs[i++]);// 送保
				map.put("is_claim", rs[i++]);// 赔付
				map.put("is_pay", rs[i++]);// 司机受款
				map.put("unitname", rs[i++]);// 分公司
				map.put("motorcade_id", rs[i++]);// 车队ID
				map.put("carId", rs[i++]);
				map.put("driverId", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("c.id", "id"));
		columns.add(new TextColumn4MapKey("c.status_", "status_",
				getText("runcase.status"), 35).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBSStatuses2())));
		// 事发时间
		columns.add(new TextColumn4MapKey("b.happen_date", "happen_date",
				getText("runcase.happenDate"), 130).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd hh:mm")));
		// 分公司
		columns.add(new TextColumn4MapKey("unitname", "unitname",
				getText("runcase.unitName"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		// 车队
		columns.add(new TextColumn4MapKey("m.name", "motorcade_name",
				getText("runcase.motorcadeName"), 60)
				.setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(
						new LinkFormater4Id(this.getContextPath()
								+ "/bc-business/motorcade/edit?id={0}",
								"motorcade") {
							@SuppressWarnings("unchecked")
							@Override
							public String getIdValue(Object context,
									Object value) {
								return StringUtils
										.toString(((Map<String, Object>) context)
												.get("motorcade_id"));
							}
						}));
		// 车牌
		if (carId == null) {
			columns.add(new TextColumn4MapKey("b.car_plate", "car_plate",
					getText("runcase.carPlate"), 80)
					.setValueFormater(new LinkFormater4Id(this.getContextPath()
							+ "/bc-business/car/edit?id={0}", "car") {
						@SuppressWarnings("unchecked")
						@Override
						public String getIdValue(Object context, Object value) {
							return StringUtils
									.toString(((Map<String, Object>) context)
											.get("carId"));
						}
					}));
		}

		if (carManId == null) {
			columns.add(new TextColumn4MapKey("b.driver_name", "driver_name",
					getText("runcase.driverName"), 60).setSortable(true)
					.setValueFormater(
							new LinkFormater4Id(this.getContextPath()
									+ "/bc-business/carMan/edit?id={0}",
									"drivers") {
								@SuppressWarnings("unchecked")
								@Override
								public String getIdValue(Object context,
										Object value) {
									return StringUtils
											.toString(((Map<String, Object>) context)
													.get("driverId"));
								}
							}));
		}
		// 服务资格证
		columns.add(new TextColumn4MapKey("b.driver_cert", "driver_cert",
				getText("runcase.driverCert"), 90).setSortable(true));
		// 籍贯
		columns.add(new TextColumn4MapKey("c.origin", "origin",
				getText("runcase.origin"), 60).setUseTitleFromLabel(true));
		// 责任
		columns.add(new TextColumn4MapKey("c.duty", "duty",
				getText("runcase.duty"), 40).setSortable(true));
		columns.add(new TextColumn4MapKey("b.address", "address",
				getText("runcase.address"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		// 经过=基表备注
		columns.add(new TextColumn4MapKey("b.desc_", "desc_",
				getText("runcase.jingguo"), 150).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("c.sort", "sort",
				getText("runcase.sort"), 40).setSortable(true));
		// 自车损失=车损情况
		columns.add(new TextColumn4MapKey("c.car_hurt", "car_hurt",
				getText("runcase.carHurt"), 80).setUseTitleFromLabel(true));
		// 总损=实际损失
		columns.add(new TextColumn4MapKey("c.actual_loss", "actual_loss",
				getText("runcase.actualLoss"), 80).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.##")));
		// 跟进人员=经办人
		columns.add(new TextColumn4MapKey("receiver_name", "receiver_name",
				getText("runcase.receiverName2"), 60)
				.setUseTitleFromLabel(true));
		// 保险公司
		columns.add(new TextColumn4MapKey("c.insurance_company",
				"insurance_company", getText("runcase.insuranceCompany"), 60)
				.setSortable(true));
		// 厂修
		columns.add(new TextColumn4MapKey("c.is_inner_fix", "is_inner_fix",
				getText("runcase.innerFix"), 40).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		// 送保
		columns.add(new TextColumn4MapKey("c.is_deliver", "is_deliver",
				getText("runcase.deliver3"), 40).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		// 赔付
		columns.add(new TextColumn4MapKey("c.is_claim", "is_claim",
				getText("runcase.claim"), 90).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		// 司机受款
		columns.add(new TextColumn4MapKey("c.is_pay", "is_pay",
				getText("runcase.pay"), 60).setSortable(true).setValueFormater(
				new BooleanFormater()));

		// 司机拖车费
		columns.add(new TextColumn4MapKey("c.carman_cost", "carman_cost",
				getText("runcase.carmanCost"), 95).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.##")));
		// 第三者损失
		columns.add(new TextColumn4MapKey("c.third_loss", "third_loss",
				getText("runcase.thirdLoss"), 75).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.##")));
		// 第三者拖车费
		columns.add(new TextColumn4MapKey("c.third_cost", "third_cost",
				getText("runcase.thirdCost"), 90).setUseTitleFromLabel(true)
				.setValueFormater(new NubmerFormater("###,###.##")));
		// 司机伤人
		columns.add(new TextColumn4MapKey("c.car_wounding",
				"car_wounding", getText("runcase.carWounding"), 60)
				.setSortable(true).setValueFormater(new NubmerFormater("###,###.##")));
		// 第三者伤人
		columns.add(new TextColumn4MapKey("c.third_wounding",
				"third_wounding", getText("runcase.thirdHurtCount"), 75)
				.setSortable(true).setValueFormater(new NubmerFormater("###,###.##")));
		// 协议赔付
		columns.add(new TextColumn4MapKey("c.agreement_payment",
				"agreement_payment", getText("runcase.agreementPayment"), 80)
				.setUseTitleFromLabel(true).setValueFormater(
						new NubmerFormater("###,###.00")));

		columns.add(new TextColumn4MapKey("b.code", "code",
				getText("runcase.caseNo3"), 160).setSortable(true));
		// 备注 DESC_ String
		columns.add(new TextColumn4MapKey("acc_desc", "acc_desc",
				getText("runcase.accdesc"), 80).setSortable(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.code", "b.motorcade_name", "b.car_plate",
				"c.sort", "b.driver_name", "b.driver_cert", "a.name" };
	}

	@Override
	protected String getFormActionName() {
		return "caseAccident";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['car_plate']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("b.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("b.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}
		// carManId条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("b.driver_id", carManId);
		}
		// carId条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("b.car_id", carId);
		}
		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition,
				carManIdCondition, carIdCondition);
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (this.status != null || this.status.length() != 0) {
			json.put("status", status);
		}
		// carManId条件
		if (carManId != null) {
			json.put("carManId", carManId);
		}
		// carId条件
		if (carId != null) {
			json.put("carId", carId);
		}
		return json.isEmpty() ? null : json;
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return super.getHtmlPageToolbar()
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getBSStatuses2(), "status", 0,
								getText("title.click2changeSearchStatus")));
	}

}
