/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.carman.dao.CarManRiskDao;
import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 司机人意险Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarManRiskDaoImpl extends HibernateCrudJpaDao<CarManRisk>
		implements CarManRiskDao {
	protected static final Log logger = LogFactory
			.getLog(CarManRiskDaoImpl.class);
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, Object> getCarManInfo(String identity) {
		String sql = "select id as id,name as name,cert_identity as identity from bs_carman where cert_identity=?";
		if (logger.isDebugEnabled()) {
			logger.debug("identity=" + identity + ";sql=" + sql);
		}
		try {
			return this.jdbcTemplate.queryForMap(sql, identity);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}