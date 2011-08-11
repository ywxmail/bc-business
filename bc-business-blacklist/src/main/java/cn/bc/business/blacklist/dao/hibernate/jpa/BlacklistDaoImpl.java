/**
 * 
 */
package cn.bc.business.blacklist.dao.hibernate.jpa;

import cn.bc.business.blacklist.dao.BlacklistDao;
import cn.bc.business.blacklist.domain.Blacklist;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 黑名单Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class BlacklistDaoImpl extends HibernateCrudJpaDao<Blacklist> implements BlacklistDao{

}