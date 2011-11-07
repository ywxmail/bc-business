/**
 * 
 */
package cn.bc.business.web.struts2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Controller;

import cn.bc.core.Entity;
import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 通过车辆id或车牌号查询相关信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FindInfoByCarAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog(FindInfoByCarAction.class);
	private Long carId;// 车辆id
	public String carPlate;// 车牌号，如"粤A.E1P11"，如果指定了carId将忽略该参数
	public Json json;// 返回的json信息
	protected JpaTemplate jpaTemplate;

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	@Override
	public String execute() throws Exception {
		// 获取数据
		List<Map<String, Object>> infos = findInfo();

		// 组装成json
		json = new Json();
		Json motorcade = new Json();
		Json car = new Json();
		Json driver;
		JsonArray drivers = new JsonArray();
		if (infos != null && !infos.isEmpty()) {
			Map<String, Object> first = infos.get(0);
			//车队信息
			motorcade.put("id", first.get("motorcadeId"));
			motorcade.put("name", first.get("motorcadeName"));
			//车辆信息
			car.put("id", first.get("carId"));
			car.put("status", first.get("carStatus"));
			car.put("plateType", first.get("carPlateType"));
			car.put("plateNo", first.get("carPlateNo"));
			car.put("plate",
					first.get("carPlateType") + "." + first.get("carPlateNo"));
			
			//营运司机信息
			for (Map<String, Object> info : infos) {
				driver = new Json();
				driver.put("id", info.get("driverId"));
				driver.put("classes", info.get("driverClasses"));
				driver.put("name", info.get("driverName"));
				driver.put("sex", info.get("driverSex"));
				driver.put("cert4FWZG", info.get("driverCert4FWZG"));
				drivers.add(driver);
			}
		}

		json.put("car", car);
		json.put("motorcade", motorcade);
		json.put("drivers", drivers);
		return "json";
	}

	private List<Map<String, Object>> findInfo() {
		Query<Map<String, Object>> q = new HibernateJpaNativeQuery<Map<String, Object>>(
				jpaTemplate, getSqlObject());
		return q.condition(this.getCondition()).list();
	}

	private Condition getCondition() {
		AndCondition and = new AndCondition();
		and.add(new OrderCondition("cd.classes", Direction.Asc));
		and.add(new EqualsCondition("cd.status_", new Integer(
				Entity.STATUS_ENABLED)));
		if (carId != null) {
			and.add(new EqualsCondition("c.id", carId));
		} else {
			String[] plates = this.carPlate.split(".");
			and.add(new EqualsCondition("c.plate_type", plates[0])).add(
					new EqualsCondition("c.plate_no", plates[1]));
		}
		return and;
	}

	private SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select c.id carId,c.status_ carStatus,c.plate_type carPlateType,c.plate_no carPlateNo");
		sql.append(",c.motorcade_id motorcadeId,m.name motorcadeName");
		sql.append(",cd.classes driverClasses,cd.driver_id driverId,d.name driverName,d.sex driverSex,d.cert_fwzg driverCert4FWZG");
		sql.append(" from bs_car c");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sql.append(" inner join bs_car_driver cd on cd.car_id=c.id");
		sql.append(" inner join bs_carman d on d.id=cd.driver_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				int i = 0;
				map.put("carId", rs[i++]);
				map.put("carStatus", rs[i++]);
				map.put("carPlateType", rs[i++]);
				map.put("carPlateNo", rs[i++]);
				map.put("motorcadeId", rs[i++]);
				map.put("motorcadeName", rs[i++]);
				map.put("driverClasses", rs[i++]);
				map.put("driverId", rs[i++]);
				map.put("driverName", rs[i++]);
				map.put("driverSex", rs[i++]);
				map.put("driverCert4FWZG", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}
}