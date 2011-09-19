/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.cert.dao.CertVehicelicenseDao;
import cn.bc.business.cert.domain.Cert4VehiceLicense;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 机动车行驶证Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertVehicelicenseDaoImpl extends HibernateCrudJpaDao<Cert4VehiceLicense> implements CertVehicelicenseDao{
	protected final Log logger =  LogFactory.getLog(getClass());

}

	