/**
 * 
 */
package cn.bc.business.web.struts2;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
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
 * 通过司机id或名字查询相关信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FindInfoByDriverAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private Long driverId;// 司机id
	public String driverName;// 司机姓名
	public Json json;// 返回的json信息
	protected JpaTemplate jpaTemplate;

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	@Override
	public String execute() throws Exception {
		// 获取数据
		List<Map<String, Object>> infos = findInfo();

		// 组装成json
		json = new Json();
		Json motorcade = new Json();
		Json car;
		Json driver = new Json();
		JsonArray cars = new JsonArray();
		if (infos != null && !infos.isEmpty()) {
			Map<String, Object> first = infos.get(0);
			// 车队信息
			motorcade.put("id", first.get("motorcadeId"));
			motorcade.put("name", first.get("motorcadeName"));
			// 车辆信息
			car = new Json();
			car.put("id", first.get("carId"));
			car.put("plateType", first.get("carPlateType"));
			car.put("plateNo", first.get("carPlateNo"));
			car.put("plate",
					first.get("carPlateType") + "." + first.get("carPlateNo"));
			car.put("registerDate",
					getDateToString(first.get("carRegisterDate")));
			car.put("bsType", first.get("carBsType"));
			cars.add(car);
			// 营运司机信息
			for (Map<String, Object> info : infos) {
				driver = new Json();
				driver.put("id", info.get("driverId"));
				driver.put("status", info.get("driverStatus"));
				driver.put("classes", info.get("driverClasses"));
				driver.put("name", info.get("driverName"));
				driver.put("sex", info.get("driverSex"));
				driver.put("cert4FWZG", info.get("driverCert4FWZG"));
				driver.put("cert4IDENTITY", info.get("driverCert4IDENTITY"));
				driver.put("origin", info.get("driverOrigin"));
				driver.put("houseType", info.get("driverHouseType"));
				driver.put("birthDate", info.get("driverBirthDate"));
				driver.put("age",
						getBirthDateToString(info.get("driverBirthDate")));

			}
		}

		json.put("cars", cars);
		json.put("motorcade", motorcade);
		json.put("driver", driver);
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
				BCConstants.STATUS_ENABLED)));
		if (driverId != null) {
			and.add(new EqualsCondition("d.id", driverId));
		} else {

			and.add(new EqualsCondition("d.name", this.driverName));

		}
		return and;
	}

	private SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select d.id driverId,d.status_ driverStatus,d.name driverName,d.sex driverSex,d.cert_fwzg driverCert4FWZG");
		sql.append(",d.cert_cyzg driverCert4IDENTITY,d.region_ driverOrigin,d.house_type driverHousetype,d.birthdate driverBirthdate");
		sql.append(",cd.car_id carId,c.plate_type carPlateType,c.plate_no carPlateNo,c.register_date carRegisterDate,c.bs_type carBsType");
		sql.append(",c.motorcade_id motorcadeId,m.name motorcadeName,cd.classes driverClasses");
		sql.append("  from bs_carman d");
		sql.append(" inner join bs_car_driver cd on cd.driver_id=d.id");
		sql.append(" inner join bs_car c on c.id=cd.car_id");
		sql.append(" inner join bs_motorcade m on m.id=c.motorcade_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				int i = 0;
				map.put("driverId", rs[i++]);
				map.put("driverStatus", rs[i++]);
				map.put("driverName", rs[i++]);
				map.put("driverSex", rs[i++]);
				map.put("driverCert4FWZG", rs[i++]);
				map.put("driverCert4IDENTITY", rs[i++]);
				map.put("driverOrigin", rs[i++]);
				map.put("driverHousetype", rs[i++]);
				map.put("driverBirthdate", rs[i++]);
				map.put("carId", rs[i++]);
				map.put("carPlateType", rs[i++]);
				map.put("carPlateNo", rs[i++]);
				map.put("carRegisterDate", rs[i++]);
				map.put("carBsType", rs[i++]);
				map.put("motorcadeId", rs[i++]);
				map.put("motorcadeName", rs[i++]);
				map.put("driverClasses", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public String getDateToString(Object object) {
		if (null != object) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			StringBuffer str = new StringBuffer(df.format(object));
			return str.toString();
		} else {
			return "";
		}
	}

	/**
	 * 计算当前岁数
	 * 
	 * @return
	 */
	public String getBirthDateToString(Object object) {
		String birthDay = getDateToString(object);
		if (birthDay.length() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			Date myDate = null;
			try {
				myDate = sdf.parse(birthDay);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long day = (date.getTime() - myDate.getTime())
					/ (24 * 60 * 60 * 1000) + 1;
			birthDay = new DecimalFormat("#,00").format(day / 365f);
			birthDay = birthDay.split(",")[0];
			// //得到当前的年份
			// String cYear = sdf.format(new Date()).substring(0,4);
			// //得到生日年份
			// String birthYear = birthDay.substring(0,4);
			// //计算当前年龄
			// int age = Integer.parseInt(cYear) - Integer.parseInt(birthYear);
			// birthDay = age+"";
		}
		return birthDay;
	}
}
