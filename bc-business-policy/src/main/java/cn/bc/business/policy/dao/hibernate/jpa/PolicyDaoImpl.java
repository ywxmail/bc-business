/**
 * 
 */
package cn.bc.business.policy.dao.hibernate.jpa;

import cn.bc.business.policy.dao.PolicyDao;
import cn.bc.business.policy.domain.Policy;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 车辆保单Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class PolicyDaoImpl extends HibernateCrudJpaDao<Policy> implements PolicyDao{

}