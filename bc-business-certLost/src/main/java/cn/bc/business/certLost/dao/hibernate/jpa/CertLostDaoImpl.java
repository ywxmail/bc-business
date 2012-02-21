/**
 * 
 */
package cn.bc.business.certLost.dao.hibernate.jpa;

import cn.bc.business.certLost.dao.CertLostDao;
import cn.bc.business.certLost.domain.CertLost;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 证照遗失Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class CertLostDaoImpl extends HibernateCrudJpaDao<CertLost> implements
		CertLostDao {

}