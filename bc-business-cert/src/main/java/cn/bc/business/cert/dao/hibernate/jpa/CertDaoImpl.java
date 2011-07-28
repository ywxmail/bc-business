/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import cn.bc.business.cert.dao.CertDao;
import cn.bc.business.cert.domain.Cert;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 证件Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertDaoImpl extends HibernateCrudJpaDao<Cert> implements CertDao{

}