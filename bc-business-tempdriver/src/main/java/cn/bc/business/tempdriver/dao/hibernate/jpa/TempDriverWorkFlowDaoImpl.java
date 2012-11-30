package cn.bc.business.tempdriver.dao.hibernate.jpa;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.tempdriver.dao.TempDriverWorkFlowDao;
import cn.bc.business.tempdriver.domain.TempDriverWorkFlow;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 司机招聘流程记录Dao的实现
 * 
 * @author lbj
 * 
 */
public class TempDriverWorkFlowDaoImpl extends HibernateCrudJpaDao<TempDriverWorkFlow> implements
		TempDriverWorkFlowDao {
	private static Log logger = LogFactory.getLog(TempDriverWorkFlowDaoImpl.class);
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public TempDriverWorkFlow loadByProcInstId(String procInstId) {
		Condition c =new EqualsCondition("procInstId", procInstId);;
		return this.createQuery().condition(c).singleResult();
	}

	public void update(String procInstId, int offerStatus, int tempDriverStatus) {
		String sql = "update bs_temp_driver_workflow set offer_status = ? where proc_inst_id = ?";
		this.jdbcTemplate.update(sql, new Object[] { offerStatus,procInstId});
		
		sql = "update bs_temp_driver set status_ = ? where id = (select pid from bs_temp_driver_workflow where proc_inst_id = ?)";
		this.jdbcTemplate.update(sql, new Object[] { tempDriverStatus,procInstId});
	}
}
