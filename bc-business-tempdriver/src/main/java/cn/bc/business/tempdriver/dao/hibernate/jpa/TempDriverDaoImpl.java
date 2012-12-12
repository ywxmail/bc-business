package cn.bc.business.tempdriver.dao.hibernate.jpa;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.tempdriver.dao.TempDriverDao;
import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 司机招聘Dao的实现
 * 
 * @author lbj
 * 
 */
public class TempDriverDaoImpl extends HibernateCrudJpaDao<TempDriver> implements
		TempDriverDao {
	private static Log logger = LogFactory.getLog(TempDriverDaoImpl.class);
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	public boolean isUniqueCertIdentity(Long id,String certIdentity) {
		AndCondition ac=new AndCondition();
		ac.add(new EqualsCondition("certIdentity", certIdentity));
		if(id!=null){
			ac.add(new NotEqualsCondition("id",id));
		}
		return this.createQuery().condition(ac).count() < 1;
	}

	public TempDriver loadByCertIdentity(String certIdentity) {
		Condition c=new EqualsCondition("certIdentity", certIdentity);
		return this.createQuery().condition(c).singleResult();
	}

	public boolean isExistCertIdentity(String certIdentity) {
		String sql ="select count(*) from bs_temp_driver where cert_identity=?";
		Object[] args = new Object[] {certIdentity};
		this.jdbcTemplate.queryForLong(sql, args);
		
		if(logger.isDebugEnabled()){
			logger.debug("sql="+sql);
			logger.debug("args="+args.toString());
		}
		
		return this.jdbcTemplate.queryForLong(sql, args) >0;
	}

}
