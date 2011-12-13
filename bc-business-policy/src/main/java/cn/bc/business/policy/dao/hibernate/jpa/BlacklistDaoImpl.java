/**
 * 
 */
package cn.bc.business.policy.dao.hibernate.jpa;

import cn.bc.business.policy.dao.BlacklistDao;
import cn.bc.business.policy.domain.Policy;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 黑名单Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class BlacklistDaoImpl extends HibernateCrudJpaDao<Policy> implements BlacklistDao{

}