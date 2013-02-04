package cn.bc.business.motorcade.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.business.motorcade.dao.HistoryCarQuantityDao;
import cn.bc.business.motorcade.domain.HistoryCarQuantity;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 删除历史车辆数
 * 
 * @author zxr
 * 
 */
public class HistoryCarQuantityDaoImpl extends
		HibernateCrudJpaDao<HistoryCarQuantity> implements
		HistoryCarQuantityDao {
	private static Log logger = LogFactory
			.getLog(HistoryCarQuantityDaoImpl.class);
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void deleteByMotorcade(Serializable motorcadeId) {
		String hql = "delete HistoryCarQuantity a where a.motorcade.id=?";
		List<Object> args = new ArrayList<Object>();
		args.add(motorcadeId);
		this.executeUpdate(hql, args);
	}

	public int doRecordHistoryCarQuantity4Day() {
		String insertSql = "insert into bs_motorcade_carquantity(id, motorcade_id, quantity, year_, month_, day_";
		insertSql += ", file_date, author_id, modified_date, modifier_id)\r\n";
		insertSql += "	select nextval('hibernate_sequence'),c.motorcade_id,count(*)\r\n";
		insertSql += "	,date_part('year',current_date),date_part('month',current_date),date_part('day',current_date)\r\n";
		insertSql += "	,LOCALTIMESTAMP,(select h.id from bc_identity_actor_history h where current=true and actor_code='admin')\r\n";
		insertSql += "	,LOCALTIMESTAMP,(select h.id from bc_identity_actor_history h where current=true and actor_code='admin')\r\n";
		insertSql += "	from bs_car c\r\n";
		insertSql += "	where c.status_=0\r\n";
		insertSql += "	and not exists (\r\n";
		insertSql += "		select 0 from bs_motorcade_carquantity mq \r\n";
		insertSql += "		where mq.motorcade_id=c.motorcade_id\r\n";
		insertSql += "		and mq.year_=date_part('year',current_date)\r\n";
		insertSql += "		and mq.month_=date_part('month',current_date)\r\n";
		insertSql += "		and mq.day_=date_part('day',current_date)\r\n";
		insertSql += "	)\r\n";
		insertSql += "	group by c.motorcade_id";
		int insertCount = this.jdbcTemplate.update(insertSql);
		if (logger.isDebugEnabled()) {
			logger.debug("insertSql=" + insertSql);
			logger.debug("insertCount=" + insertCount);
		}
		return insertCount;
	}

	public HistoryCarQuantity loadByDate(Long motorcadeId, Calendar date) {
		AndCondition c = new AndCondition();
		c.add(new EqualsCondition("motorcade.id", motorcadeId));
		c.add(new EqualsCondition("year", date.get(Calendar.YEAR)));
		c.add(new EqualsCondition("month", date.get(Calendar.MONTH) + 1));
		c.add(new EqualsCondition("day", date.get(Calendar.DATE)));
		return this.createQuery().condition(c).singleResult();
	}
}
