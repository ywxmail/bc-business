/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.cert.dao.CertJspxDao;
import cn.bc.business.cert.domain.Cert4DriverEducation;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 驾驶培训证Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertJspxDaoImpl extends HibernateCrudJpaDao<Cert4DriverEducation> implements CertJspxDao{
	protected final Log logger =  LogFactory.getLog(getClass());

}

	