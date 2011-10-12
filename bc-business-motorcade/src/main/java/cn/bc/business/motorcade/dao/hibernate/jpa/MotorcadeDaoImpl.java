package cn.bc.business.motorcade.dao.hibernate.jpa;

import java.util.List;

import cn.bc.business.motorcade.dao.MotorcadeDao;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.core.Entity;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

public class MotorcadeDaoImpl extends HibernateCrudJpaDao<Motorcade> implements
		MotorcadeDao {

	public List<Motorcade> findActive() {
		return this
				.createQuery()
				.condition(
						new AndCondition().add(
								new EqualsCondition("status",
										Entity.STATUS_ENABLED)).add(
								new OrderCondition("code", Direction.Asc)))
				.list();
	}
}
