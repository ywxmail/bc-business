/**
 * 
 */
package cn.bc.business.policy.dao.hibernate.jpa;

import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.policy.dao.PolicyDao;
import cn.bc.business.policy.domain.Policy;
import cn.bc.core.util.DateUtils;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 车辆保单Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class PolicyDaoImpl extends HibernateCrudJpaDao<Policy> implements
		PolicyDao {

	private static Log logger = LogFactory.getLog(PolicyDaoImpl.class);
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@SuppressWarnings("unchecked")
	public List<Policy> getPolicise(Long carId, Calendar happenTime) {
		String hql = "from Policy  where car.id=? and";
		hql += " ((? >= commerialStartDate and ? <= commerialEndDate) or (? >= greenslipStartDate and ? <= greenslipEndDate))";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
		}
		return this.getJpaTemplate().find(hql, carId, happenTime, happenTime,
				happenTime, happenTime);
	}

	public void logoutPastDuePolicy(Calendar instance) {
		@SuppressWarnings("static-access")
		String date = new DateUtils().formatCalendar2Second(instance);
		String insertSql = "insert into bc_log_operate (id,type_,way,file_date,author_id,ptype,pid,operate,subject,content)"
				+ "select NEXTVAL('CORE_SEQUENCE'),0,1,to_timestamp('"
				+ date
				+ "','YYYY-MM-DD HH24:mi:ss'),1146,'Policy',p.id,'update','自动注销'||c.plate_type||'.'||c.plate_no||'的保单','自动注销'||c.plate_type||'.'"
				+ "||c.plate_no||'商业号为:'||p.commerial_no||',保险期限为:'||to_char(p.commerial_start_date,'YYYY-MM-DD')||'~'||to_char(p.commerial_end_date,'YYYY-MM-DD')"
				+ "||';强险单号:'||p.greenslip_no||',保险期限为:'||to_char(p.greenslip_start_date,'YYYY-MM-DD')||'~'||to_char(p.greenslip_end_date,'YYYY-MM-DD')||';责任险单号:'"
				+ "||p.liability_no||'的保单' from bs_car c left join bs_car_policy p on p.car_id = c.id where p.commerial_end_date < to_timestamp('"
				+ date
				+ "','YYYY-MM-DD HH24:mi:ss')"
				+ " and p.greenslip_end_date < to_timestamp('"
				+ date
				+ "','YYYY-MM-DD HH24:mi:ss') and p.status_ = 0";

		this.jdbcTemplate.update(insertSql);
		String updateSql = "update bs_car_policy set status_ = 1,logout_id = 1146,logout_date=to_timestamp('"
				+ date
				+ "','YYYY-MM-DD HH24:mi:ss')"
				+ " where commerial_end_date < to_timestamp('"
				+ date
				+ "','YYYY-MM-DD HH24:mi:ss') and greenslip_end_date < to_timestamp('"
				+ date + "','YYYY-MM-DD HH24:mi:ss') and status_ = 0";
		this.jdbcTemplate.update(updateSql);

	}

}