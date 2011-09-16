/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.cert.dao.CertIdentityDao;
import cn.bc.business.cert.domain.Cert4Identity;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 居民身份证证件Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertIdentityDaoImpl extends HibernateCrudJpaDao<Cert4Identity> implements CertIdentityDao{
	protected final Log logger =  LogFactory.getLog(getClass());

}

	