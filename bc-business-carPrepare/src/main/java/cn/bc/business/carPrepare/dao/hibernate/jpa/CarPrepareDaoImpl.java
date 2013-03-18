/**
 * 
 */
package cn.bc.business.carPrepare.dao.hibernate.jpa;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.carPrepare.dao.CarPrepareDao;
import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 出车准备Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class CarPrepareDaoImpl extends HibernateCrudJpaDao<CarPrepare>
		implements CarPrepareDao {
	private static Log logger = LogFactory.getLog(CarPrepareDaoImpl.class);
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
}