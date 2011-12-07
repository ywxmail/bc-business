/**
 * 
 */
package cn.bc.business.injury.dao.hibernate.jpa;

import cn.bc.business.injury.dao.InjuryDao;
import cn.bc.business.injury.domain.Injury;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 工伤Dao的hibernate jpa实现
 * 
 * @author wis
 */
public class InjuryDaoImpl extends HibernateCrudJpaDao<Injury> implements InjuryDao{
	//private static Log logger = LogFactory.getLog(InjuryDaoImpl.class);

}