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

import cn.bc.BCConstants;
import cn.bc.business.policy.dao.PolicyDao;
import cn.bc.business.policy.domain.Policy;
import cn.bc.core.util.DateUtils;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.log.domain.OperateLog;
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
	private ActorHistoryService actorHistoryService;

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}

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
		// 去掉时分秒
		DateUtils.setToZeroTime(instance);
		// 管理员Id
		Long adminId = this.actorHistoryService.loadByCode("admin").getId();
		String insertSql = "insert into bc_log_operate (id,type_,way,file_date,author_id,ptype,pid,operate,subject,content)"
				+ "select NEXTVAL('CORE_SEQUENCE'),?,?,?,?,?"
				+ ",p.id,?,"
				+ "'自动注销'||c.plate_type||'.'||c.plate_no||'的保单'"
				+ ",'自动注销'||c.plate_type||'.'||c.plate_no||'商业号为:'||p.commerial_no||',保险期限为:'||to_char(p.commerial_start_date,'YYYY-MM-DD')||'~'||to_char(p.commerial_end_date,'YYYY-MM-DD')||';强险单号:'||p.greenslip_no||',保险期限为:'||to_char(p.greenslip_start_date,'YYYY-MM-DD')||'~'||to_char(p.greenslip_end_date,'YYYY-MM-DD')||';责任险单号:'||p.liability_no||'的保单'"
				+ " from bs_car c left join bs_car_policy p on p.car_id = c.id where p.commerial_end_date < ?"
				+ " and p.greenslip_end_date < ? and p.status_ = ?";
		Object args[] = { OperateLog.TYPE_WORK, OperateLog.WAY_SYSTEM,
				Calendar.getInstance().getTime(), adminId, "Policy", "update",
				instance.getTime(), instance.getTime(),
				BCConstants.STATUS_ENABLED };
		int insertCount = this.jdbcTemplate.update(insertSql, args);
		if (logger.isDebugEnabled()) {
			logger.debug("insertSql=" + insertSql);
			logger.debug("insertCount=" + insertCount);
		}
		String updateSql = "update bs_car_policy set status_ = ?,logout_id = ?,logout_date= ?"
				+ " where commerial_end_date < ? and greenslip_end_date < ? and status_ = ?";
		Object args2[] = { BCConstants.STATUS_DISABLED, adminId,
				Calendar.getInstance().getTime(), instance.getTime(),
				instance.getTime(), BCConstants.STATUS_ENABLED };
		int updateCount = this.jdbcTemplate.update(updateSql, args2);
		if (logger.isDebugEnabled()) {
			logger.debug("updateSql=" + updateSql);
			logger.debug("updateCount=" + updateCount);
		}
	}
}