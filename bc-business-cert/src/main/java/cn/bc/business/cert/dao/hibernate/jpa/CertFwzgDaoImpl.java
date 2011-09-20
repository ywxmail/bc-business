/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.cert.dao.CertFwzgDao;
import cn.bc.business.cert.domain.Cert4FuWuZiGe;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 服务器资格Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertFwzgDaoImpl extends HibernateCrudJpaDao<Cert4FuWuZiGe> implements CertFwzgDao{
	protected final Log logger =  LogFactory.getLog(getClass());

}

	